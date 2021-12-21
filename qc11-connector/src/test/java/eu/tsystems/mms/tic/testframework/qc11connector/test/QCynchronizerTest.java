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
package eu.tsystems.mms.tic.testframework.qc11connector.test;

import eu.tsystems.mms.tic.testframework.exceptions.SystemException;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.qc11connector.constants.QCConstants;
import eu.tsystems.mms.tic.testframework.qc11connector.constants.Testframework;
import eu.tsystems.mms.tic.testframework.qc11connector.testsundertest.CorrectClassAnnotationTest;
import eu.tsystems.mms.tic.testframework.qc11connector.testsundertest.NoClassAnnotationTest;
import eu.tsystems.mms.tic.testframework.qc11connector.testsundertest.WrongClassAnnotationTest;
import eu.tsystems.mms.tic.testframework.qc11connector.util.QCSynTestHelper;
import eu.tsystems.mms.tic.testframework.qcconnector.constants.ErrorMessages;
import eu.tsystems.mms.tic.testframework.qcconnector.constants.QCTestStatus;
import eu.tsystems.mms.tic.testframework.qcconnector.constants.QCTestUnderTest;
import eu.tsystems.mms.tic.testframework.report.model.context.LogMessage;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.Optional;

/**
 * This class contains tests that check the behaviour of the QC Synchronization. The testsUnderTest of the
 * referenced classes are executed and synchronized with qc according to their QCTestset annotation.
 */
public class QCynchronizerTest extends TesterraTest implements Loggable {

    private QCSynTestHelper qc11SynchronizerTest;

    protected Class<?> noClassAnnotation = NoClassAnnotationTest.class;
    protected Class<?> correctClassAnnotation = CorrectClassAnnotationTest.class;
    protected Class<?> wrongClassAnnotation = WrongClassAnnotationTest.class;

    protected LinkedList<Class<?>> getClassesContainingTestsUnderTest() {

        LinkedList<Class<?>> classesContainingTestsUnderTest = new LinkedList<Class<?>>();
        classesContainingTestsUnderTest.add(noClassAnnotation);
        classesContainingTestsUnderTest.add(correctClassAnnotation);
        classesContainingTestsUnderTest.add(wrongClassAnnotation);
        return classesContainingTestsUnderTest;
    }

    /**
     * test the qc synchronizer and creates results
     */
    @BeforeClass
    public void init() {
        LinkedList<Class<?>> classesContainingTestsUnderTest = getClassesContainingTestsUnderTest();
        String testSetPath = QCConstants.QC_TESTSUNDERTEST_FOLDER + QCConstants.SYNC_TESTSET_NAME;
        qc11SynchronizerTest = new QCSynTestHelper(classesContainingTestsUnderTest, testSetPath);
        qc11SynchronizerTest.createTestResults();

    }

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
        TestStep.begin("current_test");
        synchronizeTestRun(QCTestUnderTest.QCSYNC3_WRONGCLASS, Testframework.TESTNG);
        this.findInMethodLogs(ErrorMessages.wrongQCTestSetAnnotation(QCConstants.NOT_EXISTING_PATH, wrongClassAnnotation.getName()));
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
        this.findInMethodLogs(ErrorMessages.wrongQCTestSetAnnotation(QCConstants.NOT_EXISTING_PATH, noClassAnnotation.getName()));
    }

    /**
     * Check behaviour of a Test overriding its wrong Class-Annotation with an existing TestSet specified by its own
     * Method-Annotation.
     */
    @Test
    public void testT14_correctMethodAnnotationOverridesWrongClassAnnotationTestNG() {
        synchronizeTestAndAssertStatus(QCTestUnderTest.QCSYNC3_METHODCORRECTOVERRIDE, QCTestStatus.PASSED, Testframework.TESTNG);
    }

    /**
     * Check behaviour of a Test overriding its correct Class-Annotation with a not existing TestSet specified by its
     * own Method-Annotation.
     */
    @Test
    public void testT16_wrongMethodAnnotationOverridesCorrectClassAnnotationTestNG() {
        synchronizeTestRun(QCTestUnderTest.QCSYNC3_METHODWRONGOVERRIDE, Testframework.TESTNG);
        this.findInMethodLogs(ErrorMessages.wrongQCTestSetAnnotation(QCConstants.NOT_EXISTING_PATH, correctClassAnnotation.getName()));
    }

    /**
     * Check default behaviour: No instance count provided in annotation
     */
    @Test
    public void testT17_correctTestNameAnnotation() {
        synchronizeTestAndAssertStatus(QCTestUnderTest.QCSYNC3_CORRECTTESTNAME_WITHOUTINSTANCE, QCTestStatus.PASSED, Testframework.TESTNG);
    }

    /**
     * Check if a Test is synchronized to the TestSet with a name specified by its own Method-Annotation.
     */
    @Test
    public void testT18_correctMethodAnnotationTestNG() {
        synchronizeTestAndAssertStatus(QCTestUnderTest.QCSYNC3_CORRECTTESTNAME, QCTestStatus.PASSED, Testframework.TESTNG);
    }

    /**
     * Instance Count 1 on annotation provided.
     */
    @Test
    public void testT19_correctTestNameAnnotationWithInstanceCount() {
        synchronizeTestAndAssertStatus(QCTestUnderTest.QCSYNC3_CORRECTTESTNAME_WITHINSTANCE, QCTestStatus.PASSED, Testframework.TESTNG);
    }

    /**
     * Check behaviour of a Test with a not existing Testname specified by its own Method-Annotation.
     */
    @Test
    public void testT20_wrongMethodAnnotationTestNG() {
        synchronizeTestRun(QCTestUnderTest.QCSYNC3_WRONGTESTNAME, Testframework.TESTNG);
        this.findInMethodLogs(ErrorMessages.noTestMethodFoundInQC(QCTestUnderTest.QCSYNC3_WRONGTESTNAME.testName, QCConstants.QC_TESTSUNDERTEST_FOLDER + QCConstants.SYNC_TESTSET_NAME));
    }

    /**
     * Instance Count 2 on annotation provided with different test result.
     */
    @Test
    public void testT21_correctTestNameAnnotationWithInstanceCountTwo() {
        synchronizeTestAndAssertStatus(QCTestUnderTest.QCSYNC3_CORRECTTESTNAME_WITHINSTANCE_TWO, QCTestStatus.FAILED, Testframework.TESTNG);
    }

    private void synchronizeTestRun(QCTestUnderTest testUnderTest, Testframework framework) {
        qc11SynchronizerTest.synchronizeTestRun(testUnderTest, framework);
    }

    private void synchronizeTestAndAssertStatus(QCTestUnderTest testUnderTest, QCTestStatus status, Testframework framework) {
        qc11SynchronizerTest.synchronizeTestAndAssertStatus(testUnderTest, status, framework);
    }

    private void findInMethodLogs(String value) {
        MethodContext methodContext = ExecutionContextController.getMethodContextForThread().get();
        Optional<LogMessage> first = methodContext.readTestSteps()
                .flatMap(testStep -> testStep.getTestStepActions().stream())
                .flatMap(testStepAction -> testStepAction.readEntries(LogMessage.class))
                .filter(elem -> elem.getMessage().contains(value))
                .findFirst();

        Assert.assertTrue(first.isPresent(), value + " should part of log messages.");
    }

}
