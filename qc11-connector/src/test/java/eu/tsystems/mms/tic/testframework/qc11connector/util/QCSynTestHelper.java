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
package eu.tsystems.mms.tic.testframework.qc11connector.util;

import eu.tsystems.mms.tic.testframework.connectors.util.SyncType;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.qc11connector.constants.AssertionMessages;
import eu.tsystems.mms.tic.testframework.qc11connector.constants.Testframework;
import eu.tsystems.mms.tic.testframework.qcconnector.constants.QCTestStatus;
import eu.tsystems.mms.tic.testframework.qcconnector.constants.QCTestUnderTest;
import eu.tsystems.mms.tic.testframework.qcconnector.synchronize.QualityCenterSyncUtils;
import eu.tsystems.mms.tic.testframework.qcconnector.synchronize.QualityCenterTestResultSynchronizer;
import eu.tsystems.mms.tic.testframework.qcrest.clients.QcRestClient;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.QcTest;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestSetTest;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.TestNG;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class QCSynTestHelper implements Loggable {

    /**
     * Listener saves all testng testresults.
     */
    protected final TestNgResultCatcher testngListener = new TestNgResultCatcher();

    protected LinkedList<Class<?>> classesContainingTestsUnderTest;
    protected String testSetPath;

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
     * @param testSetPath .
     */
    public QCSynTestHelper(LinkedList<Class<?>> classesContainingTestsUnderTest, String testSetPath) {
        this.classesContainingTestsUnderTest = classesContainingTestsUnderTest;
        this.testSetPath = testSetPath;
        log().debug("Initializing synchronizer for TestSet " + testSetPath);
        if (synchronizer == null) {
            synchronizer = new QualityCenterTestResultSynchronizer();
        }
        synchronizer.setSyncType(SyncType.getSyncMethod());
    }

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
        } catch (Exception e) {
            Assert.fail(AssertionMessages.errorConnectingToQC());
        }
    }

    /**
     * gets the testSetTest
     *
     * @param test .
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
     * @param test .
     * @param framework .
     */
    public void synchronizeTestRun(QCTestUnderTest test, Testframework framework) {
        log().debug("Synchronizing" + framework + "-Test " + test.getTestName());
        switch (framework) {
            case TESTNG:
                ITestResult result = getITestResult(test);
                addTestMapping(result, test);
                synchronizer.syncTestRun(result, synchronizer.helpCreateTestRun(result));
                break;
        }
    }

    public void synchronizeTestAndAssertStatus(QCTestUnderTest test, QCTestStatus status, Testframework framework) {
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

    /**
     * Some basic functionality common to all Unit-Tests for QCSynchronizer. There exist a number of TestsUnderTest that
     * should be run and detected by the listeners of both testNG and jUnit. Furthermore, these tests have to exist in a
     * certain QC TestSet, to validate synchronization. These prerequisites are checked here, otherwise we could get
     * false positives/negatives.
     */
    protected void runTestsUnderTest() {

        TestNG testng = new TestNG();
        testng.addListener(testngListener);

        final Class<?>[] classes = new Class[classesContainingTestsUnderTest.size()];
        for (int i = 0; i < classesContainingTestsUnderTest.size(); i++) {
            final Class<?> aClass = classesContainingTestsUnderTest.get(i);
            classes[i] = aClass;
        }

        testng.setTestClasses(classes);
        testng.run();
    }

    public void createTestResults() {
        log().debug("Running tests under test to get results which can be synchronized.");
        runTestsUnderTest();
        checkConsistencyWithQC();
    }

    protected ITestResult getITestResult(QCTestUnderTest test) {

        for (ITestResult result : testngListener.getPassedTests()) {
            if (result.getName().equals(test.getMethodName())) {
                return result;
            }
        }

        for (ITestResult result : testngListener.getFailedTests()) {
            if (result.getName().equals(test.getMethodName())) {
                return result;
            }
        }

        Assert.fail(AssertionMessages.testUnderTestNotRunOrHeardTestNG(test.getMethodName()));
        return null;
    }
}
