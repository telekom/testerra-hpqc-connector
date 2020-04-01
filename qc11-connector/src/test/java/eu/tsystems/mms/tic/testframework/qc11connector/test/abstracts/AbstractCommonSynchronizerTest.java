package eu.tsystems.mms.tic.testframework.qc11connector.test.abstracts;

import eu.tsystems.mms.tic.testframework.qc11connector.constants.AssertionMessages;
import eu.tsystems.mms.tic.testframework.qc11connector.util.TestNgResultCatcher;
import eu.tsystems.mms.tic.testframework.qcconnector.constants.QCTestUnderTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.TestNG;

import java.util.LinkedList;

/**
 * User: rnhb Date: 18.12.13
 */
public abstract class AbstractCommonSynchronizerTest {

    /**
     * Listener saves all testng testresults.
     */
    protected final TestNgResultCatcher testngListener = new TestNgResultCatcher();

    protected LinkedList<Class<?>> classesContainingTestsUnderTest;
    protected String testSetPath;

    private static Logger logger = LoggerFactory.getLogger(AbstractCommonSynchronizerTest.class);

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

        logger.debug("Running tests under test to get results which can be synchronized.");
        runTestsUnderTest();
        checkConsistencyWithQC();
    }

    protected abstract void checkConsistencyWithQC();

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