/*
 * Testerra
 *
 * (C) 2013, Stefan Prasse, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package eu.tsystems.mms.tic.testframework.qcconnector.synchronize;

import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.connectors.util.AbstractCommonSynchronizer;
import eu.tsystems.mms.tic.testframework.connectors.util.SyncType;
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.exceptions.SystemException;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.qcconnector.constants.QCFieldValues;
import eu.tsystems.mms.tic.testframework.qcconnector.exceptions.MissingQcTestSetAnnotationException;
import eu.tsystems.mms.tic.testframework.qcconnector.exceptions.TesterraQcResultSyncException;
import eu.tsystems.mms.tic.testframework.qcrest.constants.QCProperties;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.Attachment;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestRun;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import org.apache.commons.io.FileUtils;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * A helper class for Quality Center. Automatically handles the test run result synchronization by listening to the
 * Surefire-JUnit-Maven and Surefire-Testng-Maven test runs.
 *
 * @author mrgi, mibu, sepr
 */
public class QualityCenterTestResultSynchronizer extends AbstractCommonSynchronizer {

    private boolean isSyncActive = false;

    @Subscribe
    @Override
    public void onMethodEnd(MethodEndEvent event) {
        super.onMethodEnd(event);
    }

    /**
     * If true, upload the screenshot, taken by failure, to QC TestRun.
     */
    private static boolean uploadScreenshot;

    /**
     * Public constructor. Creates a new <code>QualityCenterTestResultSynchronizer</code> object.
     *
     * @throws SystemException thrown if no connection to Quality Center can established.
     */
    public QualityCenterTestResultSynchronizer() throws SystemException {
        this.init();
    }

    /**
     * Inititalize fields.
     *
     * @throws SystemException .
     */
    private void init() throws SystemException {
        log().info("Initializing " + this.getClass().getSimpleName() + " over QCRestService");
        try {

            PropertyManager.loadProperties("qcconnection.properties");
            this.isSyncActive = PropertyManager.getBooleanProperty(QCProperties.SYNC_ACTIVE, false);

            if (!this.isSyncActive) {
                log().info("QC Synchronization turned off.");
                return;
            }

            syncType = SyncType.ANNOTATION;
            uploadScreenshot = PropertyManager.getBooleanProperty("uploadAutomaticScreenshot", false);

        } catch (final Exception e) {
            final StringBuilder error = new StringBuilder();
            error.append("An error occurred while communicating with QC: ");
            if (e.getMessage() != null) {
                error.append(e.getMessage());
            } else {
                error.append("no message");
            }

            throw new SystemException(error.toString(), e);
        }
    }

    /**
     * Interface for test methods.
     *
     * @param result r
     * @return run
     */
    public TestRun helpCreateTestRun(final ITestResult result) {
        return createTestRun(result);
    }

    /**
     * Creates a TestRun based on the data given by the result.
     *
     * @param result The result given to the listener methods.
     * @return A TestRun instance.
     */
    private TestRun createTestRun(final ITestResult result) {

        final TestRun testRun = new TestRun();
        testRun.setName(result.getMethod().getMethodName());
        // final Date date = new Date(result.getStartMillis());
        final Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        testRun.setExecutionTime(sdf.format(date));
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        testRun.setExecutionDate(sdf.format(date));
        if (!result.isSuccess()) {
            testRun.setStatus("Failed");
        } else {
            testRun.setStatus("Passed");
        }

        // user fields
        QualityCenterSyncUtils.addQCUserFields(testRun);

        // Add Attachments
        final List<File> attachments = QualityCenterSyncUtils.getTestAttachments(result);
        if (!attachments.isEmpty()) {
            for (final File attachment : attachments) {
                Attachment att;
                try {
                    if (attachment.isFile()) {
                        att = new Attachment();
                        att.setName(attachment.getName());
                        att.setRefType("File");
                        att.setEntityName("RUN");
                        att.setContent(FileUtils.readFileToByteArray(attachment));
                        testRun.addAttachments(att);
                    }
                } catch (final IOException e) {
                    log().error("Error setting content of attachment from File.");
                }
            }
        }

        return testRun;
    }

    /**
     * Sync a TestRun with the appropiate SyncType. Safe the id of the synced run to allow uploads of screenshots to the
     * run.
     *
     * @param result TestResult containing Test infos.
     * @param run TestRun to sync.
     */
    public void syncTestRun(final ITestResult result, final TestRun run) {
        if (this.isSyncActive) {
            pSyncTestRun(result, run);
        }
    }

    /**
     * Sync a TestRun with the appropiate SyncType. Safe the id of the synced run to allow uploads of screenshots to the
     * run.
     *
     * @param result TestResult containing Test infos.
     * @param run TestRun to sync.
     */
    private void pSyncTestRun(final ITestResult result, final TestRun run) {

        Optional<MethodContext> methodContext = ExecutionContextController.getMethodContextForThread();

        try {
            int runId = 0;

            // Should not happen.
            if (syncType == SyncType.ANNOTATION) {
                final Class<?> clazz = result.getTestClass().getRealClass();
                final Method method = result.getMethod().getConstructorOrMethod().getMethod();

                // do not synchronize non-test methods
                if (!result.getMethod().isTest()) {
                    return;
                }

                final String methodName = result.getMethod().getMethodName();
                runId = QualityCenterSyncUtils.syncWithSyncType3(clazz, method, methodName, run, result);
            }

            if (runId > 0) {
                // Save the runId as a result Attribute.
                int finalRunId = runId;
                methodContext.ifPresent(context -> {
                    context.getTestNgResult().ifPresent(currentResult -> currentResult.setAttribute("RunId", finalRunId));
                });
            } else {
                throw new TesterraQcResultSyncException("Error during sync occured. See previous logs.");
            }
            methodContext.ifPresent(context -> {
                log().info("Synchronization to QualityCenter / ALM successful.", context, Loggable.prompt);
            });

        } catch (MissingQcTestSetAnnotationException xmqe) {
            methodContext.ifPresent(context -> {
                log().warn("Found missing QCTestSet annotation when QCSync is active.", context, Loggable.prompt);
            });

        } catch (TesterraQcResultSyncException xse) {
            log().error(xse.getMessage());
            methodContext.ifPresent(context -> {
                log().error("Synchronization to QualityCenter / ALM failed.", context, Loggable.prompt);
            });

        }
        QCFieldValues.resetThreadLocalFields();
    }

    @Override
    protected void pOnTestSuccess(MethodEndEvent event) {

        if (isSyncActive) {
            final TestRun run = createTestRun(event.getTestResult());
            syncTestRun(event.getTestResult(), run);
        }
    }

    @Override
    protected void pOnTestFailure(MethodEndEvent event) {

        if (isSyncActive) {
            final TestRun run = createTestRun(event.getTestResult());
            syncTestRun(event.getTestResult(), run);
        }
    }

    @Override
    public void setSyncType(final SyncType syncType) {

        log().info("QC sync type: " + syncType.name());
        QualityCenterTestResultSynchronizer.syncType = syncType;
    }
}
