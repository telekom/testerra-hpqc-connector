package eu.tsystems.mms.tic.testframework.qcconnector.constants;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * User: rnhb Date: 19.12.13
 */
public enum QCTestUnderTest {

    QCSYNC3_SUCCESSFULTEST("successfulTest"),
    QCSYNC3_FAILINGTEST("failingTest"),
    QCSYNC3_METHODCORRECTOVERRIDE("correctMethodAnnotationOverridesWrongClassAnnotation"),
    QCSYNC3_METHODWRONGOVERRIDE("wrongMethodAnnotationOverridesCorrectClassAnnotation"),
    QCSYNC3_CORRECTMETHOD("correctMethodAnnotation"),
    QCSYNC3_WRONGMETHOD("wrongMethodAnnotation"),
    QCSYNC3_CORRECTTESTNAME("correctTestNameAnnotation", "someNotKnownTestname"),
    QCSYNC3_CORRECTTESTNAME_WITHOUTINSTANCE("correctTestNameAnnotationWithoutInstanceCount", "correctTestNameAnnotationWithoutInstanceCount"),
    QCSYNC3_CORRECTTESTNAME_WITHINSTANCE("correctTestNameAnnotationWithInstanceCount", "correctTestNameAnnotationWithInstanceCount", 1),
    QCSYNC3_CORRECTTESTNAME_WITHINSTANCE_TWO("correctTestNameAnnotationWithInstanceCount", "correctTestNameAnnotationWithInstanceCountTwo", 2),
    QCSYNC3_WRONGTESTNAME("notExistingTestNameAnnotation", "someNotKnownTestnameWithWrongAnnotation"),
    QCSYNC3_CORRECTCLASS("correctClassAnnotation"),
    QCSYNCPROG_TEST1("testTest1"),
    QCSYNCPROG_TEST2("test3"),
    QCSYNC3_WRONGCLASS("wrongClassAnnotation");

    /**
     * The name of the TestSetTest in QC.
     */
    public final String testName;
    /**
     * The name of the method that is connected to the test.
     */
    public final String methodName;
    /**
     * The instance count.
     */
    public final int instanceCount;


    private QCTestUnderTest(String testName, String methodName, int instanceCount) {
        this.testName = testName;
        this.methodName = methodName;
        this.instanceCount = instanceCount;
    }

    private QCTestUnderTest(String testName, String methodName) {
        this(testName, methodName, 0);
    }

    private QCTestUnderTest(String testName) {
        this(testName, testName);
    }

    @Override
    public String toString() {
        return getTestName();
    }

    /**
     * gets the test names for the test sets
     *
     * @return test names
     */
    public static LinkedList<QCTestUnderTest> getTestSetTestNames() {
        return new LinkedList<>(Arrays.asList(QCTestUnderTest.values()));
    }

    /**
     * @return the testName
     */
    public String getTestName() {
        return testName;
    }

    /**
     * @return the methodName
     */
    public String getMethodName() {
        return methodName;
    }

    public int getInstanceCount() {
        return instanceCount;
    }
}
