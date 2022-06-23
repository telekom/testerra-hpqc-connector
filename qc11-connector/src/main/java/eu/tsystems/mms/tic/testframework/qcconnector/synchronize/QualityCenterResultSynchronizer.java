package eu.tsystems.mms.tic.testframework.qcconnector.synchronize;

import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.events.TestStatusUpdateEvent;
import eu.tsystems.mms.tic.testframework.exceptions.SystemException;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.qcconnector.constants.QCFieldValues;
import eu.tsystems.mms.tic.testframework.qcconnector.exceptions.MissingQcTestSetAnnotationException;
import eu.tsystems.mms.tic.testframework.qcconnector.exceptions.TesterraQcResultSyncException;
import eu.tsystems.mms.tic.testframework.qcrest.constants.QCProperties;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.Attachment;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestRun;
import eu.tsystems.mms.tic.testframework.report.Status;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on 09.03.2022
 *
 * @author mgn
 */
public class QualityCenterResultSynchronizer implements TestStatusUpdateEvent.Listener, Loggable {

    private boolean isSyncActive = false;

    /**
     * If true, upload the screenshot, taken by failure, to QC TestRun.
     */
    private static boolean uploadScreenshot;

    public QualityCenterResultSynchronizer() {
        this.init();
    }

    @Subscribe
    @Override
    public void onTestStatusUpdate(TestStatusUpdateEvent event) {
        if (!this.isSyncActive) {
            return;
        }

        MethodContext methodContext = event.getMethodContext();
        String qcStatus = "No run";

        Status status = methodContext.getStatus();
        switch (status) {
            case PASSED:
            case REPAIRED:
            case RECOVERED:
                qcStatus = "Passed";
                break;
            case FAILED:
            case FAILED_EXPECTED:
                qcStatus = "Failed";
                break;
            case RETRIED:
                log().info("Method " + methodContext.getName() + " was retried and will not sync.");
                return;
            case SKIPPED:
                qcStatus = "N/A";
                break;
            case NO_RUN:
                return;
            default:
                log().info(String.format("Method state %s of %s cannot handle.", status.toString(), methodContext.getName()));
                return;
        }
        final TestRun run = createTestRun(qcStatus, methodContext);
        this.syncTestRun(run, methodContext);
    }

    /**
     * Sync a TestRun with the appropiate SyncType. Safe the id of the synced run to allow uploads of screenshots to the
     * run.
     *
     * @param run TestRun to sync.
     * @param methodContext The current method context
     */
    public void syncTestRun(final TestRun run, MethodContext methodContext) {
        if (!this.isSyncActive) {
            return;
        }

        try {
            int runId = 0;
            ITestResult result = methodContext.getTestNgResult().get();

            // do not synchronize non-test methods
            if (!result.getMethod().isTest()) {
                return;
            }

            runId = QualityCenterSyncUtils.syncResult(methodContext, run);

            if (runId > 0) {
                // Save the runId as a result Attribute.
                int finalRunId = runId;
                methodContext.getTestNgResult().ifPresent(currentResult -> currentResult.setAttribute("RunId", finalRunId));
            } else {
                throw new TesterraQcResultSyncException("Error during sync occured. See previous logs.");
            }
            log().info("Synchronization to QualityCenter / ALM successful.", methodContext, Loggable.prompt);

        } catch (MissingQcTestSetAnnotationException xmqe) {
            log().warn("Found missing QCTestSet annotation when QCSync is active.", methodContext, Loggable.prompt);
        } catch (TesterraQcResultSyncException xse) {
            log().error(xse.getMessage());
            log().error("Synchronization to QualityCenter / ALM failed.", methodContext, Loggable.prompt);
        }
        QCFieldValues.resetThreadLocalFields();
    }

    private TestRun createTestRun(String qcTestStatus, MethodContext methodContext) {
        final TestRun testRun = new TestRun();

        testRun.setName(methodContext.getName());
        // final Date date = new Date(result.getStartMillis());
        final Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        testRun.setExecutionTime(sdf.format(date));
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        testRun.setExecutionDate(sdf.format(date));
        testRun.setStatus(qcTestStatus);

        QualityCenterSyncUtils.addQCUserFields(testRun);

        // Add Attachments
        final List<File> attachments = QualityCenterSyncUtils.getTestAttachments(methodContext);
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

        // Add Error messages to comment
        AtomicInteger i = new AtomicInteger(0);
        methodContext.readErrors()
//                .filter(ErrorContext::isNotOptional)
                .forEach(errorContext -> {
                    Throwable throwable = errorContext.getThrowable();
                    StringBuffer comment = new StringBuffer();
                    comment.append("Error " + i.incrementAndGet() + ")\n");
//                    comment.append(throwable.toString());
                    Optional<Fails> fails = methodContext.getFailsAnnotation();
                    if (fails.isPresent() && StringUtils.isNotBlank(fails.get().description())) {
                        comment.append("Known issue: " + fails.get().description());
                    }
                    final String stackTrace = ExceptionUtils.getStackTrace(throwable);
                    comment.append(stackTrace);
                    testRun.setComment(comment.toString());
                });

        return testRun;
    }

    private void init() throws SystemException {
        log().info("Initializing " + this.getClass().getSimpleName() + " over QCRestService");
        try {

            PropertyManager.loadProperties("qcconnection.properties");
            this.isSyncActive = PropertyManager.getBooleanProperty(QCProperties.SYNC_ACTIVE, false);

            if (!this.isSyncActive) {
                log().info("QC Synchronization turned off.");
                return;
            }

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
}
