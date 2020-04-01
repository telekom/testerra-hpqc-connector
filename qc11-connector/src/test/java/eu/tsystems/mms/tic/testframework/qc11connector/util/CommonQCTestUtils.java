package eu.tsystems.mms.tic.testframework.qc11connector.util;

import eu.tsystems.mms.tic.testframework.qc11connector.constants.AssertionMessages;
import eu.tsystems.mms.tic.testframework.qcconnector.constants.QCTestStatus;
import eu.tsystems.mms.tic.testframework.qcconnector.constants.QCTestUnderTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * User: rnhb Date: 12.12.13
 */
public class CommonQCTestUtils {

    protected static final Logger logger = LoggerFactory.getLogger(CommonQCTestUtils.class);

    /**
     * @param classToSearch .
     * @param annotation    .
     *
     * @return names
     */
    public static LinkedList<String> getMethodsAnnotatedWith(Class classToSearch, Class annotation) {
        LinkedList<String> names = new LinkedList<String>();
        for (Method method : classToSearch.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotation)) {
                names.add(method.getName());
            }
        }
        return names;
    }

    /**
     * gets the test set name from full path
     *
     * @param fullPath .
     *
     * @return .
     */
    public static String getTestSetNameFromFullPath(String fullPath) {
        return fullPath.substring(fullPath.lastIndexOf("\\") + 1);
    }

    /**
     * @param fullPath .
     *
     * @return .
     */
    public static String getFoldersFromFullPath(String fullPath) {
        return fullPath.substring(0, fullPath.lastIndexOf("\\"));
    }

    /**
     * @param fullPath .
     *
     * @return .
     */
    public static String[] splitFullTestSetPath(String fullPath) {
        return new String[]{getTestSetNameFromFullPath(fullPath), getFoldersFromFullPath(fullPath)};
    }

    /**
     * Check if the passed test was synchronized in the last minutes. Otherwise we could get false results from running
     * the integration tests on stale synchronizations of tests under test.
     *
     * @param testName     .
     * @param testRunTime  .
     * @param syncInterval .
     */
    public static void assertRecentSynchronization(String testName, long testRunTime, long syncInterval) {
        long diff = System.currentTimeMillis() - testRunTime;
        logger.debug("Test \"" + testName + "\" was synchronized " + (diff / 1000) + "s ago.");
        Assert.assertTrue(diff < syncInterval,
                AssertionMessages.staleSynchronization(testName, syncInterval, diff));
    }

    /**
     * Assert that a Test has a certain status.
     *
     * @param testName       .
     * @param actualStatus   .
     * @param expectedStatus .
     */
    public static void assertStatusOfTestInQC(String testName, String actualStatus, QCTestStatus expectedStatus) {
        Assert.assertEquals(actualStatus, expectedStatus.toString(), AssertionMessages.wrongStatus(testName));
    }

    /**
     * Assert that a testUnderTests is contained. Mainly useful for provider tests, where the methods have to be found
     * and mapped.
     *
     * @param test    the test to check containment for
     * @param methods list of methods to search through
     */
    public static void assertTestIsContained(QCTestUnderTest test, Iterable<Method> methods) {
        boolean methodFound = false;
        for (Method method : methods) {
            if (method.getName().equals(test.getMethodName())) {
                methodFound = true;
                break;
            }
        }
        Assert.assertTrue(methodFound, AssertionMessages.failedToProvideTest(test.getTestName()));
    }

    /**
     * Assert that a testUnderTests is not contained. Mainly useful for provider tests, where the methods have to be
     * found and mapped.
     *
     * @param test    the test to check containment for
     * @param methods list of methods to search through
     */
    public static void assertTestIsNotContained(QCTestUnderTest test, Iterable<Method> methods) {
        boolean methodFound = false;
        for (Method method : methods) {
            if (method.getName().equals(test.getMethodName())) {
                methodFound = true;
                break;
            }
        }
        Assert.assertFalse(methodFound, AssertionMessages.failedToProvideTest(test.getTestName()));
    }

    /**
     * @param testngTests .
     * @param junitTests  .
     */
    public static void logTestsToRun(List<Method> testngTests, List<Method> junitTests) {
        logger.debug("Calculated Tests to run, found the following Methods.");
        String out = "TestNG: ";
        for (Method method : testngTests) {
            out += method.getName() + ",";
        }
        logger.debug(out.substring(0, out.length() - 1));
        out = "JUnit: ";
        for (Method method : junitTests) {
            out += method.getName() + ",";
        }
        logger.debug(out.substring(0, out.length() - 1));

    }
}
