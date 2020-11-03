package eu.tsystems.mms.tic.testframework.qc11connector.test.abstracts;

import eu.tsystems.mms.tic.testframework.connectors.util.TestFileUtils;
import eu.tsystems.mms.tic.testframework.exceptions.SystemException;
import eu.tsystems.mms.tic.testframework.qc11connector.constants.QCConstants;
import eu.tsystems.mms.tic.testframework.qc11connector.constants.Testframework;
import eu.tsystems.mms.tic.testframework.qcconnector.constants.ErrorMessages;
import eu.tsystems.mms.tic.testframework.qcconnector.constants.QCTestStatus;
import eu.tsystems.mms.tic.testframework.qcconnector.constants.QCTestUnderTest;
import org.testng.annotations.Test;

import java.util.LinkedList;

/**
 * User: rnhb Date: 15.01.14
 */
public abstract class CommonQCSync3SynchronizerTest {

    protected Class<?> noClassAnnotation = eu.tsystems.mms.tic.testframework.qc11connector.testsundertest.qcsync3.NoClassAnnotationTest.class;
    protected Class<?> correctClassAnnotation = eu.tsystems.mms.tic.testframework.qc11connector.testsundertest.qcsync3.CorrectClassAnnotationTest.class;
    protected Class<?> wrongClassAnnotation = eu.tsystems.mms.tic.testframework.qc11connector.testsundertest.qcsync3.WrongClassAnnotationTest.class;

    protected LinkedList<Class<?>> getClassesContainingTestsUnderTest() {

        LinkedList<Class<?>> classesContainingTestsUnderTest = new LinkedList<Class<?>>();
        classesContainingTestsUnderTest.add(noClassAnnotation);
        classesContainingTestsUnderTest.add(correctClassAnnotation);
        classesContainingTestsUnderTest.add(wrongClassAnnotation);
        return classesContainingTestsUnderTest;
    }

    protected abstract void synchronizeTestAndAssertStatus(QCTestUnderTest testUnderTest, QCTestStatus status, Testframework framework);

    protected abstract void synchronizeTestRun(QCTestUnderTest testUnderTest, Testframework framework);

    /**
     * Check if the synchronization of a successful Test is correct.
     */
    @Test
    public void testT02_synchronizeSuccessfulTestTestNG() {
        synchronizeTestAndAssertStatus(QCTestUnderTest.QCSYNC3_SUCCESSFULTEST, QCTestStatus.PASSED,
                Testframework.TESTNG);
    }

    /**
     * Check if the synchronization of a failing Test is correct.
     */
    @Test
    public void testT04_synchronizeFailingTestTestNG() {
        synchronizeTestAndAssertStatus(QCTestUnderTest.QCSYNC3_FAILINGTEST, QCTestStatus.FAILED, Testframework.TESTNG);
    }

    /**
     * Check if a Test is synchronized to the TestSet specified by its Class-Annotation.
     */
    @Test
    public void testT06_correctClassAnnotationTestNG() {
        synchronizeTestAndAssertStatus(QCTestUnderTest.QCSYNC3_CORRECTCLASS, QCTestStatus.PASSED, Testframework.TESTNG);
    }

    /**
     * Check behaviour of a Test with a not existing TestSet specified by its Class-Annotation.
     */
    @Test
    public void testT08_wrongClassAnnotationTestNG() throws SystemException {
        synchronizeTestRun(QCTestUnderTest.QCSYNC3_WRONGCLASS, Testframework.TESTNG);
        TestFileUtils.assertEntryInLogFile(ErrorMessages.wrongQCTestSetAnnotation(QCConstants.NOT_EXISTING_PATH,
                wrongClassAnnotation.getName()));
    }

    /**
     * Check if a Test is synchronized to the TestSet specified by its own Method-Annotation.
     */
    @Test
    public void testT10_correctMethodAnnotationTestNG() {
        synchronizeTestAndAssertStatus(QCTestUnderTest.QCSYNC3_CORRECTMETHOD, QCTestStatus.PASSED,
                Testframework.TESTNG);
    }

    /**
     * Check behaviour of a Test with a not existing TestSet specified by its own Method-Annotation.
     */
    @Test
    public void testT12_wrongMethodAnnotationTestNG() {
        synchronizeTestRun(QCTestUnderTest.QCSYNC3_WRONGMETHOD, Testframework.TESTNG);
        TestFileUtils.assertEntryInLogFile(ErrorMessages.wrongQCTestSetAnnotation(QCConstants.NOT_EXISTING_PATH,
                noClassAnnotation.getName()));
    }

    /**
     * Check behaviour of a Test overriding its wrong Class-Annotation with an existing TestSet specified by its own
     * Method-Annotation.
     */
    @Test
    public void testT14_correctMethodAnnotationOverridesWrongClassAnnotationTestNG() {
        synchronizeTestAndAssertStatus(QCTestUnderTest.QCSYNC3_METHODCORRECTOVERRIDE, QCTestStatus.PASSED,
                Testframework.TESTNG);
    }

    /**
     * Check behaviour of a Test overriding its correct Class-Annotation with a not existing TestSet specified by its
     * own Method-Annotation.
     */
    @Test
    public void testT16_wrongMethodAnnotationOverridesCorrectClassAnnotationTestNG() {
        synchronizeTestRun(QCTestUnderTest.QCSYNC3_METHODWRONGOVERRIDE, Testframework.TESTNG);
        TestFileUtils.assertEntryInLogFile(ErrorMessages.wrongQCTestSetAnnotation(QCConstants.NOT_EXISTING_PATH,
                correctClassAnnotation.getName()));
    }

    /**
     * Check default behaviour: No instance count provided in annotation
     */
    @Test
    public void testT17_correctTestNameAnnotation() {
        synchronizeTestAndAssertStatus(QCTestUnderTest.QCSYNC3_CORRECTTESTNAME_WITHOUTINSTANCE, QCTestStatus.PASSED, Testframework.TESTNG);
    }

    /**
     * Instance Count 1 on annotation provided.
     */
    @Test
    public void testT19_correctTestNameAnnotationWithInstanceCount() {
        synchronizeTestAndAssertStatus(QCTestUnderTest.QCSYNC3_CORRECTTESTNAME_WITHINSTANCE, QCTestStatus.PASSED, Testframework.TESTNG);
    }

    /**
     * Instance Count 2 on annotation provided with different test result.
     */
    @Test
    public void testT21_correctTestNameAnnotationWithInstanceCountTwo() {
        synchronizeTestAndAssertStatus(QCTestUnderTest.QCSYNC3_CORRECTTESTNAME_WITHINSTANCE_TWO, QCTestStatus.FAILED, Testframework.TESTNG);
    }

}
