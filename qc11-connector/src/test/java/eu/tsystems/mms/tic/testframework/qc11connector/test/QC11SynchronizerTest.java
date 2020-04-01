package eu.tsystems.mms.tic.testframework.qc11connector.test;

import eu.tsystems.mms.tic.testframework.connectors.util.SyncType;
import eu.tsystems.mms.tic.testframework.qc11connector.constants.AssertionMessages;
import eu.tsystems.mms.tic.testframework.qc11connector.constants.Testframework;
import eu.tsystems.mms.tic.testframework.qc11connector.test.abstracts.AbstractCommonSynchronizerTest;
import eu.tsystems.mms.tic.testframework.qc11connector.util.CommonQCTestUtils;
import eu.tsystems.mms.tic.testframework.qc11connector.util.QC11TestUtils;
import eu.tsystems.mms.tic.testframework.qc11connector.util.QCConsistencyChecker;
import eu.tsystems.mms.tic.testframework.qcconnector.constants.QCTestStatus;
import eu.tsystems.mms.tic.testframework.qcconnector.constants.QCTestUnderTest;
import eu.tsystems.mms.tic.testframework.qcconnector.synchronize.QualityCenterSyncUtils;
import eu.tsystems.mms.tic.testframework.qcconnector.synchronize.QualityCenterTestResultSynchronizer;
import eu.tsystems.mms.tic.testframework.qcrest.clients.QcRestClient;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.QcTest;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestSetTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestResult;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * User: rnhb Date: 18.12.13
 */
public class QC11SynchronizerTest extends AbstractCommonSynchronizerTest {

    private static Logger logger = LoggerFactory.getLogger(QC11SynchronizerTest.class);

    /**
     * The synchronizer to test.
     */
    private static QualityCenterTestResultSynchronizer synchronizer;

    /**
     * List with TestSetTest. If we want to check TestSetTests from QC, we load them into this List.
     */
    private List<TestSetTest> testSetTests;

    /**
     * the qc11 synchronizer test
     *
     * @param classesContainingTestsUnderTest .
     * @param testSetPath                     .
     */
    public QC11SynchronizerTest(LinkedList<Class<?>> classesContainingTestsUnderTest, String testSetPath) {
        this.classesContainingTestsUnderTest = classesContainingTestsUnderTest;
        this.testSetPath = testSetPath;
        logger.debug("Initializing synchronizer for TestSet " + testSetPath);
        if (synchronizer == null) {
            synchronizer = new QualityCenterTestResultSynchronizer();
        }
        synchronizer.setSyncType(SyncType.getSyncMethod());
    }

    @Override
    protected void checkConsistencyWithQC() {
        checkIfTestSetExists();
        updateTestSetTestList();
        QCConsistencyChecker.checkConsistencyWithQC(testSetPath, QC11TestUtils.getTestNamesOfTestSet(testSetTests));
    }

    private void checkIfTestSetExists() {
        String[] splittedPath = CommonQCTestUtils.splitFullTestSetPath(testSetPath);
        try {
            boolean exists = QcRestClient.isExistingTestSet(splittedPath[0], splittedPath[1]);
            Assert.assertTrue(exists, AssertionMessages.testSetNotExisting(testSetPath));
        } catch (IOException e) {
            Assert.fail(AssertionMessages.errorConnectingToQC());
        }
    }

    /**
     * gets the testSetTest
     *
     * @param test .
     *
     * @return testSetTest
     */
    public TestSetTest getTestSetTest(QCTestUnderTest test) {

        for (TestSetTest testSetTest : testSetTests) {

            final QcTest testinstance = testSetTest.getTest();
            final String qcTestInstanceName = testSetTest.getTestInstanceName();
            final String localTestInstanceName = String.format("%s [%s]", test.getTestName(), test.getInstanceCount());

            if (testinstance == null) {
                Assert.fail("Could not get test instance from testsettest. Please look for previous (rest-) error messages.");
            }

            if (test.getInstanceCount() > 0) {
                // Instance Count MUST match, otherwise returning null
                if (qcTestInstanceName.equalsIgnoreCase(localTestInstanceName)) {
                    return testSetTest;
                }

            } else {

                // Instance count is ignored.
                if (test.toString().equals(testinstance.getName())) {
                    return testSetTest;
                }
            }
        }
        //        Assert.fail(AssertionMessages.testNotFoundInTestSet(test.toString(), testSetPath));
        return null;
    }

    private void updateTestSetTestList() {
        testSetTests = QC11TestUtils.getTestSetTestsFromTestSetPath(testSetPath);
    }

    /**
     * synchronizes test runs
     *
     * @param test      .
     * @param framework .
     */
    protected void synchronizeTestRun(QCTestUnderTest test, Testframework framework) {
        logger.debug("Synchronizing" + framework + "-Test " + test.getTestName());
        switch (framework) {
            case TESTNG:
                ITestResult result = getITestResult(test);
                addTestMapping(result, test);
                synchronizer.syncTestRun(result, synchronizer.helpCreateTestRun(result));
                break;
        }
    }

    protected void synchronizeTestAndAssertStatus(QCTestUnderTest test, QCTestStatus status, Testframework framework) {
        synchronizeTestRun(test, framework);
        updateTestSetTestList();
        TestSetTest testSetTest = getTestSetTest(test);
        QC11TestUtils.assertRecentSynchronizationUnitTest(testSetTest);
        QC11TestUtils.assertStatusOfTestInQC(testSetTest, status);
    }

    private void addTestMapping(ITestResult result, QCTestUnderTest test) {
        TestSetTest testSetTest = getTestSetTest(test);
        Method method = result.getMethod().getConstructorOrMethod().getMethod();
        QualityCenterSyncUtils.addTestMapping(method, testSetTest);
    }
}
