/*
 * Testerra
 *
 * (C) 2013, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.qcconnector.annotation.QCPathUtil;
import eu.tsystems.mms.tic.testframework.qcconnector.annotation.TMInfoContainer;
import eu.tsystems.mms.tic.testframework.qcconnector.constants.ErrorMessages;
import eu.tsystems.mms.tic.testframework.qcconnector.constants.QCFieldValues;
import eu.tsystems.mms.tic.testframework.qcconnector.exceptions.MissingQcTestSetAnnotationException;
import eu.tsystems.mms.tic.testframework.qcconnector.exceptions.TesterraQcResultSyncException;
import eu.tsystems.mms.tic.testframework.qcrest.clients.QcRestClient;
import eu.tsystems.mms.tic.testframework.qcrest.clients.RestConnector;
import eu.tsystems.mms.tic.testframework.qcrest.clients.UtilClient;
import eu.tsystems.mms.tic.testframework.qcrest.constants.QCProperties;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.Attachment;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.QcTest;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestRun;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestSet;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestSetTest;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.model.context.Video;
import eu.tsystems.mms.tic.testframework.testmanagement.annotation.QCTestname;
import io.cucumber.testng.PickleWrapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * A helper class containing methods for Synchronizer.
 */
public final class QualityCenterSyncUtils {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(QualityCenterSyncUtils.class);

    /**
     * Mapping between Methods an TestSetTests.
     */
    private static Map<Method, TestSetTest> testMapping;

    /**
     * Cache for TestSetTests of a TestSet.
     */
    private static Map<TestSet, List<TestSetTest>> testSetCache;

    /**
     * Thread local list of attachments, that will be added to the next synced run
     */
    private static ThreadLocal<List<File>> additionalRunAttachments = new ThreadLocal<List<File>>();


    static {
        testMapping = new HashMap<Method, TestSetTest>();
        testSetCache = new HashMap<TestSet, List<TestSetTest>>();
    }

    /**
     * Protected constructor to hide the public one since this is a static only class.
     */
    private QualityCenterSyncUtils() {
    }

    /**
     * @return the testMapping
     */
    public static Map<Method, TestSetTest> getTestMapping() {
        return testMapping;
    }

    /**
     * Synchronize to QC with syncType 3.
     *
     * @param clazz Class of test.
     * @param method of test.
     * @param methodName Name of testMethod.
     * @param run RunWrapper for TestRun.
     * @param result result containing testParameters
     * @return Id of added run or 0 if it could not be added.
     * @throws TesterraQcResultSyncException sync error
     */
    public static int syncWithSyncType3(final Class<?> clazz, final Method method, final String methodName,
                                        final TestRun run, final ITestResult result) throws TesterraQcResultSyncException {
        int id = readAnnotationAndSync(clazz, method, methodName, run, result);
        if (id == 0) {
            throw new TesterraQcResultSyncException("No id returned. See previous logs.");
        } else {
            return id;
        }
    }

    /**
     * Adds the Testrun to given Testset with RunClient.
     *
     * @param test The TestSetTest.
     * @param run The TestRun.
     * @return Id of added run or 0 if it could not be added.
     */
    private static int addTestRunToTestSet(final TestSetTest test, final TestRun run) {
        int runId = 0;
        try {
            runId = QcRestClient.addTestRunToTestSet(test, run);
            if (runId != 0) {
                LOGGER.info("Added TestRun to QC for TestSetTest " + test);
            } else {
                LOGGER.error("TestRun was not added by RestClient. Try a second time");
                runId = QcRestClient.addTestRunToTestSet(test, run);
                if (runId != 0) {
                    LOGGER.info("Added TestRun to QC for TestSetTest " + test);
                } else {
                    LOGGER.error("TestRun for TestSetTest " + test
                            + " could not be added by RestClient (2 attempts).");
                }
            }
        } catch (final Exception io) {
            LOGGER.error("Error adding TestRun through webservice. Trying second time.", io);
            try {
                runId = QcRestClient.addTestRunToTestSet(test, run);
                if (runId != 0) {
                    LOGGER.info("Added TestRun to QC for TestSetTest " + test);
                } else {
                    LOGGER.error("TestRun for TestSetTest " + test
                            + " could not be added two times by RestClient.");
                }
            } catch (final Exception io2) {
                final StringBuilder error = new StringBuilder();
                error.append("An error occurred while saving the test results for test ");
                error.append(test.getTest().getName());
                error.append(" from test set ");
                error.append(test.getTestSet());
                error.append(".");
                LOGGER.error(error.toString(), io2);
            }
        }
        return runId;
    }

    /**
     * Reads the QCTestset path from Annotation and synchronize result to QC.
     *
     * @param clazz The class to search the annotation.
     * @param method The method to read the annotation.
     * @param methodName The name of the Testmethod.
     * @param run The TestRun.
     * @param result result containing testParameters
     * @return Id of added run or 0 if it could not be added.
     */
    private static int readAnnotationAndSync(final Class<?> clazz, final Method method, final String methodName,
                                             final TestRun run, final ITestResult result) {
        int runId = 0;
        final TestSetTest test = getTestSetTestForAnnotation(clazz, method, methodName, result);
        if (test != null) {
            try {
                runId = addTestRunToTestSet(test, run);
            } catch (final Exception e) {
                final StringBuilder error = new StringBuilder();
                error.append("An error occurred while communicating with Quality Center: ");
                error.append(e.getMessage());
                LOGGER.error(error.toString(), e);
            }
        }
        return runId;
    }

    /**
     * Get the TestSetTest for the TestSet annotated on this test method.
     *
     * @param clazz Test class.
     * @param method Test method
     * @param methodName Method name.
     * @param result TestResult.
     * @return QC TestSetTest
     */
    public static TestSetTest getTestSetTestForAnnotation(final Class<?> clazz, final Method method,
                                                          final String methodName,
                                                          final ITestResult result) {
        TestSetTest matchingTest = null;
        final String testSetPath = QCPathUtil.getQCTestsetPath(clazz, method, result);

        if (testSetPath != null) {

            final String testSetFolder = testSetPath.substring(0, testSetPath.lastIndexOf('\\'));
            final String testSetName = testSetPath.substring(testSetPath.lastIndexOf('\\') + 1, testSetPath.length());
            List<TestSetTest> testSetTests = null;

            try {

//                String qcTestName = methodName;
                final String qcTestNameAnnotation = getTestnameFromAnnotation(method);
                final boolean isInstanceCountAnnotation = isInstanceCountAnnotationPresent(method);
                final String qcTestNameFromScenario = getTestnameFromScenario(result);

//                if (qcTestNameAnnotation != null) {
//                    qcTestName = qcTestNameAnnotation;
//                }

                String qcTestName = qcTestNameAnnotation != null ? qcTestNameAnnotation
                        : (qcTestNameFromScenario != null ? qcTestNameFromScenario : methodName);

                LOGGER.info("Looking up TestSetTest " + testSetPath + " - " + qcTestName + "\nfor Test: " +
                        result.getTestClass().getRealClass().getSimpleName() + "#" + result.getName());
                TestSet testSet = QcRestClient.getTestSet(testSetName, testSetFolder);
                if (testSet != null) {
                    if (!testSetCache.containsKey(testSet)) {
                        testSetCache.put(testSet, QcRestClient.getTestSetTests(testSetName, testSetFolder));
                    } else {
                        LOGGER.debug("TestSetTest of TestSet cached: " + testSetName);
                    }
                    testSetTests = testSetCache.get(testSet);

                    for (final TestSetTest test : testSetTests) {

                        // Determine valid name. Use instance name instead of test method name whne instance count is present on annotation.
                        String qcTestMethodName;

                        if (isInstanceCountAnnotation) {
                            qcTestMethodName = test.getTestInstanceName();
                        } else {
                            QcTest qcTest = test.getTest();
                            qcTestMethodName = qcTest.getName();
                        }

                        if (qcTestMethodName.equalsIgnoreCase(qcTestName)) {
                            matchingTest = test;
                            break;
                        }
                    }
                    if (matchingTest == null) {
                        String mName;
                        for (final TestSetTest test : testSetTests) {
                            String qcTestMethodName = test.getTest().getName();
                            qcTestMethodName = cutTestFromString(qcTestMethodName, methodName);
                            mName = cutTestFromString(methodName, qcTestMethodName);
                            if (qcTestMethodName.equalsIgnoreCase(mName)) {
                                matchingTest = test;
                                break;
                            }
                        }
                    }
                    if (matchingTest == null) {
                        final StringBuilder error = new StringBuilder();
                        error.append("No method ");
                        error.append(qcTestName);
                        error.append(" found in Testset ");
                        error.append(testSetPath);
                        error.append(". Could not synchronize with QC! ");
                        LOGGER.error(error.toString());
                    }
                } else {
                    String errorMsg = ErrorMessages.wrongQCTestSetAnnotation(testSetPath, clazz.getName());
                    LOGGER.error(errorMsg);
                }
            } catch (final Exception e) {
                String msg = "An error occurred while communicating with Quality Center: ";
                if (e.getMessage() == null) {
                    LOGGER.error(msg + "No error message. See stacktrace for information", e);
                } else if (e.getMessage().contains("The TestSet \"" + testSetName + "\" was not found.")) {
                    String errorMsg = ErrorMessages.wrongQCTestSetAnnotation(testSetPath, clazz.getName());
                    LOGGER.error(errorMsg);
                } else {
                    LOGGER.error(msg + e.getMessage());
                }
            }
        } else {
            final StringBuilder warning = new StringBuilder();
            warning.append("No QCTestset Annotation on testmethod ");
            warning.append(method.getName());
            warning.append(". Could not synchronize with QC! ");
            LOGGER.warn(warning.toString());
            // return -1; // No SyncError in this case
            throw new MissingQcTestSetAnnotationException(warning.toString());
        }
        return matchingTest;
    }

    /**
     * Read QCTestname annotation from test method.
     *
     * @return Found value or null.
     */
    private static String getTestnameFromAnnotation(Method method) {

        String methodName = method.getName();
        String testname = null;
        int instanceCount;

        // Get the annotation.
        final QCTestname qcTestname = getQcTestnameAnnotation(method);

        // Get the test path info.
        if (qcTestname != null) {
            if (StringUtils.isNotEmpty(qcTestname.value())) {

                testname = qcTestname.value().trim();
                instanceCount = qcTestname.instanceCount(); // default = 0 --> No instance count necessary

                if (instanceCount > 0) {
                    LOGGER.debug("Instance count set on QCTestname annotation.");
                    testname = String.format("%s [%s]", testname, instanceCount);
                }

                testname = PropertyManager.getPropertiesParser().parseLine(testname);
                LOGGER.debug(String.format("Found QCTestname annotation with value %s for method %s", testname, methodName));
            }
        }
        return testname;
    }

    private static boolean isInstanceCountAnnotationPresent(final Method method) {

        final QCTestname qcTestnameAnnotation = getQcTestnameAnnotation(method);

        if (qcTestnameAnnotation != null) {
            return qcTestnameAnnotation.instanceCount() > 0;
        }

        return false;
    }

    private static QCTestname getQcTestnameAnnotation(final Method method) {

        QCTestname annotation = null;

        if (method.isAnnotationPresent(QCTestname.class)) {
            annotation = method.getAnnotation(QCTestname.class);
        }

        return annotation;
    }

    /**
     * Try to find out the name from @QCTestname marker of a Cucumber annotation
     *
     * @param result
     * @return
     */
    private static String getTestnameFromScenario(ITestResult result) {
        Optional<PickleWrapper> first = Arrays.stream(result.getParameters())
                .filter(o -> o instanceof PickleWrapper)
                .map(e -> (PickleWrapper) e)
                .findFirst();
        if (first.isPresent()) {
            List<String> tags = first.get().getPickle().getTags();
            AtomicReference<String> value = new AtomicReference<>();
            tags.stream()
                    .filter(e -> e.toLowerCase().contains("qctestname"))
                    .findFirst()
                    .ifPresent(tag -> {
                        value.set(StringUtils.substringBetween(tag, "\""));
                    });
            return StringUtils.isNotBlank(value.get()) ? value.get() : null;
        }
        return null;
    }

    /**
     * Gets the script specified by the user field for the given test.
     *
     * @param test The test set test containing the script name.
     * @return The script name of the unit test method associated with the given test.
     */
    public static String getScriptname(final TestSetTest test) {
        final String fieldIndex = UtilClient.getIndexOfUserLabel("ta_scriptname", "test");
        return test.getTest().getUserField(fieldIndex);
    }

    /**
     * Add an entry to the TestMapping.
     *
     * @param method local TestMethod that matches QC TestSetTest.
     * @param testSetTest QCTestSetTest that is executed.
     */
    public static void addTestMapping(final Method method, final TestSetTest testSetTest) {
        pAddTestMapping(method, testSetTest);
    }

    /**
     * Add an entry to the TestMapping.
     *
     * @param method local TestMethod that matches QC TestSetTest.
     * @param testSetTest QCTestSetTest that is executed.
     */
    private static void pAddTestMapping(final Method method, final TestSetTest testSetTest) {

        testMapping.put(method, testSetTest);
        String path = null;

        if (testSetTest != null) {
            path = testSetTest.getTestSet().getTestSetFolder().getPath() + "\\" + testSetTest.getTestSet().getName() + "\\" +
                    testSetTest.getTest().getName();
        }

        TMInfoContainer.savePath(method.getDeclaringClass().getName(), method.getName(), path);
    }

    /**
     * Check TestSetTest against execution filter.
     *
     * @param testSetTest testSetTest to verify
     * @return true if test should be executed, false otherwise.
     */
    public static boolean matchesExecutionFilter(final TestSetTest testSetTest) {
        return pMatchesExecutionFilter(testSetTest);
    }

    /**
     * Check TestSetTest against execution filter.
     *
     * @param testSetTest testSetTest to verify
     * @return true if test should be executed, false otherwise.
     */
    private static boolean pMatchesExecutionFilter(final TestSetTest testSetTest) {

        final String filterProperty = PropertyManager.getProperty(QCProperties.EXECUTION_FILTER, null);
        if (StringUtils.isEmpty(filterProperty)) {
            return true;
        }
        final String[] splittedProps = filterProperty.split(":");
        if (splittedProps.length < 3) {
            LOGGER.warn("Execution filter has not the expected format: e.g. exclude:status:passed");
            return true;
        }
        final boolean include = "include".equalsIgnoreCase(splittedProps[0]);
        final String filterType = splittedProps[1];
        String argument = "";
        for (int i = 2; i < splittedProps.length; i++) {
            // re add : of third argument if splitted
            argument = ":" + argument + splittedProps[i];
        }
        argument = argument.substring(1);
        if ("status".equalsIgnoreCase(filterType)) {
            try {
                final List<TestRun> lastRun = QcRestClient.getXTestRuns(testSetTest, 1);
                if (lastRun.size() == 0) {
                    LOGGER.error("No run found. Test will " +
                            (include ? "not" : "") + " be executed.");
                    return !include;
                }
                final TestRun testRun = lastRun.get(0);
                final boolean expected = testRun.getStatus().equalsIgnoreCase(argument);
                LOGGER.info("Last run status was: " + testRun.getStatus() + ". Expected: " + argument);
                LOGGER.info("Test will  be " + ((expected == include) ? "included." : "excluded."));
                return expected == include;
            } catch (Exception e) {
                LOGGER.error("Error getting last run for testSet. Test will " +
                        (include ? "not" : "") + " be executed.");
                return !include;
            }
        } else {
            LOGGER.error("Only filter for 'status' implemented yet. " + filterType + " unknown.");
            return true;
        }
    }

    /**
     * Add run fields from qcconnection.properties to run.
     *
     * @param testRun TestRun to fill fields with
     */
    public static void addQCUserFields(TestRun testRun) {
        pAddQCUserFields(testRun);
    }

    /**
     * Add run fields from qcconnection.properties to run.
     *
     * @param testRun TestRun to fill fields with
     */
    private static void pAddQCUserFields(TestRun testRun) {
        // Set additional fields
        try {
            final Map<String, String> additionalFields = QCFieldValues.getAllFieldsToAdd();
            for (Entry<String, String> entry : additionalFields.entrySet()) {
                String name = QCFieldValues.getFieldNameForLabel(entry.getKey());
                if (name == null) {
                    name = entry.getKey();
                }
                testRun.setFieldValue(name, entry.getValue());
            }
        } catch (Exception e) {
            LOGGER.error("Error adding fields to QC TestRun", e);
        }
    }

    /**
     * @param methodContext The current method context
     * @return List of screenshots and screencasts to upload.
     */
    public static List<File> getTestAttachments(MethodContext methodContext) {
        List<File> attachments = new LinkedList<File>();

        if (methodContext == null) {
            return attachments;
        }

        final boolean isUploadScreenshots = PropertyManager.getBooleanProperty(QCProperties.UPLOAD_SCREENSHOTS, false);
        final boolean isUploadVideos = PropertyManager.getBooleanProperty(QCProperties.UPLOAD_VIDEOS, false);

        if (isUploadScreenshots) {
            methodContext.readTestSteps().forEach(testStep -> {
                testStep.readActions().forEach(testStepAction -> {
                    testStepAction.readEntries(Screenshot.class).forEach(screenshot -> {
                        attachments.add(screenshot.getScreenshotFile());
                    });
                });
            });
        }

        // Upload videos...
        if (isUploadVideos) {
            methodContext.readSessionContexts().forEach(sessionContext -> {
                Optional<Video> video = sessionContext.getVideo();
                video.ifPresent(value -> attachments.add(value.getVideoFile()));
            });
        }

        LOGGER.debug("Following attachments added to testrun for {}: {}", methodContext.getName(),
                attachments.stream().map(File::getName).collect(Collectors.joining(",")));

        return attachments;
    }

    private static String cutTestFromString(final String stringToCut, final String stringToCompare) {
        final String TEST = "test";
        final String TEST2 = "test_";
        if (!stringToCut.equalsIgnoreCase(stringToCompare)) {

            if (stringToCut.length() > TEST2.length() && stringToCut.matches("[Tt]est_.*")
                    && stringToCut.substring(TEST2.length()).equalsIgnoreCase(stringToCompare)) {

                return stringToCut.substring(TEST2.length());
            }
            if (stringToCut.length() > TEST.length() && stringToCut.matches("[Tt]est.*")
                    && stringToCut.substring(TEST.length()).equalsIgnoreCase(stringToCompare)) {

                return stringToCut.substring(TEST.length());
            }
        }
        return stringToCut;
    }

    /**
     * Creates a testrun for the given test in the specified TestSet.
     * <p>
     * // TODO: Check if can be removed, only used in tests
     *
     * @param qcPath path to TestSet
     * @param testInstanceName name of test to create run for
     * @param status Status of test
     * @param attachments List of possible attchments
     * @return true if run could be synced.
     */
    public static boolean syncTestRun(final String qcPath, final String testInstanceName, final String status,
                                      List<File> attachments) {
        return pSyncTestRun(qcPath, testInstanceName, status, attachments);
    }

    /**
     * Creates a testrun for the given test in the specified TestSet.
     *
     * @param qcPath path to TestSet
     * @param testInstanceName name of test to create run for
     * @param status Status of test
     * @param attachments List of possible attchments
     * @return true if run could be synced.
     */
    private static boolean pSyncTestRun(final String qcPath, final String testInstanceName, final String status, List<File> attachments) {

        TestRun testRun = createTestRun(testInstanceName, status, attachments);
        if (!qcPath.contains("\\")) {
            LOGGER.error("Could not anaylze qcPath. \\ between testSetFolder and testSetName is missing.");
            LOGGER.error("run for " + testInstanceName + " not synced");
            return false;
        }

        int splitIndex = qcPath.lastIndexOf("\\");
        TestSetTest testSetTest;
        try {
            testSetTest = QcRestClient.getTestSetTest(testInstanceName, qcPath.substring(splitIndex + 1), qcPath.substring(0, splitIndex));
            int id = QcRestClient.addTestRunToTestSet(testSetTest, testRun);
            LOGGER.info("Synced testRun with id: " + id);
            return id > 0;
        } catch (Exception e) {
            LOGGER.error("Error creating TestRun.", e);
            LOGGER.error("run for " + testInstanceName + " not synced");
        } finally {
            RestConnector.getInstance().logout();
        }
        return false;
    }

    /**
     * Creates a TestRun.
     *
     * @param testInstanceName name of test to create run for
     * @param status Status of test
     * @param attachments List of possible attchments
     * @return A TestRun instance.
     */
    private static TestRun createTestRun(final String testInstanceName, final String status, List<File> attachments) {

        final TestRun testRun = new TestRun();
        testRun.setName("QcSyncUtils: " + testInstanceName);
        final Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        testRun.setExecutionTime(sdf.format(date));
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        testRun.setExecutionDate(sdf.format(date));
        testRun.setStatus(status);

        // user fields
        addQCUserFields(testRun);

        // Add Attachments
        if (attachments != null && !attachments.isEmpty()) {
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
}
