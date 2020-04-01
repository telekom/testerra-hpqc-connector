package eu.tsystems.mms.tic.testframework.qc11connector.test;

import eu.tsystems.mms.tic.testframework.connectors.util.TestFileUtils;
import eu.tsystems.mms.tic.testframework.qc11connector.constants.QCConstants;
import eu.tsystems.mms.tic.testframework.qc11connector.constants.Testframework;
import eu.tsystems.mms.tic.testframework.qc11connector.test.abstracts.CommonQCSync3SynchronizerTest;
import eu.tsystems.mms.tic.testframework.qcconnector.constants.ErrorMessages;
import eu.tsystems.mms.tic.testframework.qcconnector.constants.QCTestStatus;
import eu.tsystems.mms.tic.testframework.qcconnector.constants.QCTestUnderTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.LinkedList;

/**
 * This class contains tests that check the behaviour of the QC Synchronization with type 3. The testsUnderTest of the
 * referenced classes are executed and synchronized with qc according to their QCTestset annotation.
 *
 * @author rnhb
 */
public class QCSync3SynchronizerTest extends CommonQCSync3SynchronizerTest {

    private QC11SynchronizerTest qc11SynchronizerTest;

    /**
     * test the qc synchronizer and creates results
     */
    @BeforeClass
    public void init() {
        LinkedList<Class<?>> classesContainingTestsUnderTest = getClassesContainingTestsUnderTest();
        String testSetPath = QCConstants.QC_TESTSUNDERTEST_FOLDER + QCConstants.QCSYNC3_TESTSET_NAME;
        qc11SynchronizerTest = new QC11SynchronizerTest(classesContainingTestsUnderTest, testSetPath);
        qc11SynchronizerTest.createTestResults();

    }

    @Override
    protected void synchronizeTestAndAssertStatus(QCTestUnderTest testUnderTest, QCTestStatus status, Testframework framework) {
        qc11SynchronizerTest.synchronizeTestAndAssertStatus(testUnderTest, status, framework);
    }

    @Override
    protected void synchronizeTestRun(QCTestUnderTest testUnderTest, Testframework framework) {
        qc11SynchronizerTest.synchronizeTestRun(testUnderTest, framework);
    }

    /**
     * Check if a Test is synchronized to the TestSet with a name specified by its own Method-Annotation.
     */
    @Test
    public void testT18_correctMethodAnnotationTestNG() {
        synchronizeTestAndAssertStatus(QCTestUnderTest.QCSYNC3_CORRECTTESTNAME, QCTestStatus.PASSED,
                Testframework.TESTNG);
    }

    /**
     * Check behaviour of a Test with a not existing Testname specified by its own Method-Annotation.
     */
    @Test
    public void testT20_wrongMethodAnnotationTestNG() {


        synchronizeTestRun(QCTestUnderTest.QCSYNC3_WRONGTESTNAME, Testframework.TESTNG);


        TestFileUtils.assertEntryInLogFile(
                ErrorMessages.noTestMethodFoundInQC(QCTestUnderTest.QCSYNC3_WRONGTESTNAME.testName,
                        QCConstants.QC_TESTSUNDERTEST_FOLDER + QCConstants.QCSYNC3_TESTSET_NAME));
    }
}
