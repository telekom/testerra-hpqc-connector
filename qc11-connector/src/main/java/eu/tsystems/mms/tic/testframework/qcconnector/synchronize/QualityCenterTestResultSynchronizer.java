/*
 * Created on 05.04.2011
 *
 * Copyright(c) 2011 - 2011 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.qcconnector.synchronize;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.connectors.util.AbstractCommonSynchronizer;
import eu.tsystems.mms.tic.testframework.connectors.util.SyncType;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.qcconnector.constants.QCFieldValues;
import eu.tsystems.mms.tic.testframework.qcconnector.exceptions.TesterraMissingQcTestSetAnnotationException;
import eu.tsystems.mms.tic.testframework.qcconnector.exceptions.TesterraQcResultSyncException;
import eu.tsystems.mms.tic.testframework.qcrest.constants.QCProperties;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.Attachment;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestRun;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * A helper class for Quality Center. Automatically handles the test run result synchronization by listening to the
 * Surefire-JUnit-Maven and Surefire-Testng-Maven test runs.
 *
 * @author mrgi, mibu, sepr
 */
public class QualityCenterTestResultSynchronizer extends AbstractCommonSynchronizer {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(QualityCenterTestResultSynchronizer.class);

    static {
        LOGGER.info("Initializing " + QualityCenterTestResultSynchronizer.class.getSimpleName()
                + " over QCRestService");
        init();
    }

    /**
     * If true, upload the screenshot, taken by failure, to QC TestRun.
     */
    private static boolean uploadScreenshot;

    /**
     * Public constructor. Creates a new <code>QualityCenterTestResultSynchronizer</code> object.
     *
     * @throws TesterraSystemException thrown if no connection to Quality Center can established.
     */
    public QualityCenterTestResultSynchronizer() throws TesterraSystemException {
    }

    /**
     * Inititalize fields.
     *
     * @throws TesterraSystemException .
     */
    private static void init() throws TesterraSystemException {
        try {

            PropertyManager.loadProperties("qcconnection.properties");
            isSyncActive = PropertyManager.getBooleanProperty(QCProperties.SYNCACTIVATED, true);

            if (!isSyncActive) {
                LOGGER.info("QC Synchronization turned off.");
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

            throw new TesterraSystemException(error.toString(), e);
        }
    }

    /**
     * Interface for test methods.
     *
     * @param result r
     *
     * @return run
     */
    public TestRun helpCreateTestRun(final ITestResult result) {
        return createTestRun(result);
    }

    /**
     * Creates a TestRun based on the data given by the result.
     *
     * @param result The result given to the listener methods.
     *
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
                    LOGGER.error("Error setting content of attachment from File.");
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
     * @param run    TestRun to sync.
     */
    public void syncTestRun(final ITestResult result, final TestRun run) {
        pSyncTestRun(result, run);
    }

    /**
     * Sync a TestRun with the appropiate SyncType. Safe the id of the synced run to allow uploads of screenshots to the
     * run.
     *
     * @param result TestResult containing Test infos.
     * @param run    TestRun to sync.
     */
    private void pSyncTestRun(final ITestResult result, final TestRun run) {

        try {
            int runId = 0;

            // Should not happen.
            if (syncType == SyncType.ANNOTATION) {
                final Class<?> clazz = result.getTestClass().getRealClass();
                final Method method = result.getMethod().getConstructorOrMethod().getMethod();
                final String methodName = result.getMethod().getMethodName();
                runId = QualityCenterSyncUtils.syncWithSyncType3(clazz, method, methodName, run, result);
            }

            if (runId > 0) {
                // Save the runId as a result Attribute.
                ExecutionContextController.getCurrentTestResult().setAttribute("RunId", runId);
            } else {
                throw new TesterraQcResultSyncException("Error during sync occured. See previous logs.");
            }

            ExecutionContextController.getCurrentMethodContext().infos.add("Synchronization to QualityCenter / ALM successful.");
        } catch (TesterraMissingQcTestSetAnnotationException xmqe) {
            LOGGER.warn("Found missing QCTestSet annotation when QCSync is active.");
        } catch (TesterraQcResultSyncException xse) {
            LOGGER.error("Sync failed.", xse);
            ExecutionContextController.getCurrentMethodContext().addPriorityMessage("Synchronization to QualityCenter / ALM failed.");
        }
        QCFieldValues.resetThreadLocalFields();
    }

    @Override
    protected void pOnTestSuccess(final ITestResult result) {

        if (isSyncActive) {
            final TestRun run = createTestRun(result);
            syncTestRun(result, run);
        }
    }

    @Override
    protected void pOnTestFailure(final ITestResult result) {

        if (isSyncActive) {
            final TestRun run = createTestRun(result);
            syncTestRun(result, run);
        }
    }

    @Override
    public void setSyncType(final SyncType syncType) {

        LOGGER.info("QC sync type: " + syncType.name());
        QualityCenterTestResultSynchronizer.syncType = syncType;
    }
}
