package eu.tsystems.mms.tic.testframework.qc11connector.util;

import eu.tsystems.mms.tic.testframework.qc11connector.constants.AssertionMessages;
import eu.tsystems.mms.tic.testframework.qc11connector.constants.QCConstants;
import eu.tsystems.mms.tic.testframework.qcconnector.constants.QCTestStatus;
import eu.tsystems.mms.tic.testframework.qcconnector.constants.QCTestUnderTest;
import eu.tsystems.mms.tic.testframework.qcrest.clients.QcRestClient;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.QcTest;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestSetTest;
import org.testng.Assert;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * This class contains some utilty methods for QC 11 Tests.
 *
 * @author rnhb
 */
public class QC11TestUtils extends CommonQCTestUtils {

    /**
     * Check if the passed test was synchronized in the last minutes. Otherwise we could get false results from running
     * the integration tests on stale synchronizations of tests under test.
     *
     * @param test .
     */
    public static void assertRecentSynchronizationUnitTest(TestSetTest test) {
        final QcTest testinstance = test.getTest();
        if (testinstance == null) {
            Assert.fail("Could not get test instance from testsettest. Please look for previous (rest-) error messages.");
        }
        assertRecentSynchronization(testinstance.getName(), getTimestampOfTestSetTestRun(test), QCConstants.QCREST_SYNCINTERVAL);
    }

    /**
     * Assert the recent synchronization system test
     *
     * @param test .
     */
    public static void assertRecentSynchronizationSystemTest(TestSetTest test) {
        final QcTest testinstance = test.getTest();
        if (testinstance == null) {
            Assert.fail("Could not get test instance from testsettest. Please look for previous (rest-) error messages.");
        }
        assertRecentSynchronization(testinstance.getName(), getTimestampOfTestSetTestRun(test), QCConstants.SYSTEMTEST_SYNCINTERVAL);
    }

    /**
     * Assert that a Test has a certain status.
     *
     * @param test           .
     * @param expectedStatus .
     */
    public static void assertStatusOfTestInQC(TestSetTest test, QCTestStatus expectedStatus) {
        final QcTest testinstance = test.getTest();
        if (testinstance == null) {
            Assert.fail("Could not get test instance from testsettest. Please look for previous (rest-) error messages.");
        }
        assertStatusOfTestInQC(testinstance.getName(), test.getStatus(), expectedStatus);
    }

    /**
     * Assert that a Test has a certain status.
     *
     * @param testSetFolder  .
     * @param test           .
     * @param expectedStatus .
     */
    public static void assertStatusOfTestInQC(String testSetFolder, QCTestUnderTest test, QCTestStatus expectedStatus) {
        List<TestSetTest> tests = QC11TestUtils.getTestsOfQCTestSet(testSetFolder);
        TestSetTest testSetTest = QC11TestUtils.getTest(tests, test.getTestName());
        assertStatusOfTestInQC(testSetTest.getTest().getName(), testSetTest.getStatus(), expectedStatus);
    }

    /**
     * Get a Test out of a collection of test by its name.
     *
     * @param tests    .
     * @param testName the name of the test
     */
    public static TestSetTest getTest(Iterable<TestSetTest> tests, String testName) {
        for (TestSetTest test : tests) {
            final QcTest testinstance = test.getTest();
            if (testinstance == null) {
                Assert.fail("Could not get test instance from testsettest. Please look for previous (rest-) error messages.");
            }
            if (testinstance.getName().equals(testName)) {
                return test;
            }
        }
        Assert.fail(AssertionMessages.testNotFoundInTestSet(testName, tests.iterator().next().getTestSet().getName()));
        return null;
    }

    public static List<TestSetTest> getTestsOfQCTestSet(String testSetName, String testSetFolder) {

        List<TestSetTest> tests = null;
        try {
            tests = QcRestClient.getTestSetTests(testSetName, testSetFolder);
        } catch (final IOException e) {
            Assert.fail(AssertionMessages.couldNotFindTestSet(testSetName, testSetFolder));
        }
        if (tests.size() == 0) {
            logger.warn(AssertionMessages.testSetIsEmpty(testSetName));
        }
        return tests;
    }

    public static List<TestSetTest> getTestsOfQCTestSet(String fullPathWithTestSetName) {
        int lastBackslash = fullPathWithTestSetName.lastIndexOf("\\");
        return getTestsOfQCTestSet(fullPathWithTestSetName.substring(lastBackslash + 1), fullPathWithTestSetName.substring(0, lastBackslash));
    }

    public static List<TestSetTest> getTestSetTestsFromTestSetPath(String path) {

        try {
            String[] splittedPath = splitFullTestSetPath(path);
            List<TestSetTest> testSetTests = QcRestClient.getTestSetTests(splittedPath[0], splittedPath[1]);
            return testSetTests;
        } catch (IOException e) {
            Assert.fail(AssertionMessages.testSetNotExisting(path));
        }
        return null;
    }

    public static LinkedList<String> getTestNamesOfTestSet(Iterable<TestSetTest> testSetTests) {
        LinkedList<String> names = new LinkedList<String>();
        for (TestSetTest test : testSetTests) {
            final QcTest testinstance = test.getTest();
            if (testinstance == null) {
                Assert.fail("Could not get test instance from testsettest. Please look for previous (rest-) error messages.");
            }
            names.add(testinstance.getName());
        }
        return names;
    }

    /**
     * Get time of latest TestRun of a TestSetTest in milliseconds.
     *
     * @param test A TestSetTest
     *
     * @return Time of last run in milli seconds.
     */
    public static long getTimestampOfTestSetTestRun(final TestSetTest test) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            date = sdf.parse(test.getExecutionDate() + " " + test.getExecutionTime());
        } catch (ParseException e) {
            return 0;
        }
        return date.getTime();
    }

    public static void assertRecentSynchronizationAndCorrectStatusOfSystemTest(String testSetFolder,
                                                                               QCTestUnderTest test, QCTestStatus status) {
        List<TestSetTest> tests = QC11TestUtils.getTestsOfQCTestSet(testSetFolder);
        TestSetTest testSetTest = QC11TestUtils.getTest(tests, test.getTestName());
        assertRecentSynchronizationSystemTest(testSetTest);
        assertStatusOfTestInQC(testSetTest, status);
    }
}
