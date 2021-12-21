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

import eu.tsystems.mms.tic.testframework.qc11connector.constants.QCConstants;
import eu.tsystems.mms.tic.testframework.qc11connector.testsundertest.CorrectClassAnnotationTest;
import eu.tsystems.mms.tic.testframework.qc11connector.util.QCSynTestHelper;
import eu.tsystems.mms.tic.testframework.qcconnector.constants.QCTestUnderTest;
import eu.tsystems.mms.tic.testframework.qcconnector.synchronize.QualityCenterSyncUtils;
import eu.tsystems.mms.tic.testframework.qcrest.constants.QCProperties;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestSetTest;
import java.util.LinkedList;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test exectuion filter property.
 *
 * @author sepr
 */
public class QcExecutionFilterTest extends AbstractQcTest {

    /**
     * Test that only filtered test are run.
     */
    @Test
    public void testExecutionFilterSyncType3() {

        System.setProperty(QCProperties.EXECUTION_FILTER, "exclude:status:failed");
        String testSetPath = QCConstants.QC_TESTSUNDERTEST_FOLDER + QCConstants.QCSYNC3_TESTSET_NAME;
        LinkedList<Class<?>> classesContainingTestsUnderTest = new LinkedList<Class<?>>();
        classesContainingTestsUnderTest.add(CorrectClassAnnotationTest.class);
        QCSynTestHelper qc11SynchronizerTest = new QCSynTestHelper(classesContainingTestsUnderTest, testSetPath);
        qc11SynchronizerTest.createTestResults();
        TestSetTest failingTest = qc11SynchronizerTest.getTestSetTest(QCTestUnderTest.QCSYNC3_FAILINGTEST);
        Assert.assertFalse(QualityCenterSyncUtils.matchesExecutionFilter(failingTest), "Failing Test should not run");
        TestSetTest successfulTest = qc11SynchronizerTest.getTestSetTest(QCTestUnderTest.QCSYNC3_SUCCESSFULTEST);
        Assert.assertTrue(QualityCenterSyncUtils.matchesExecutionFilter(successfulTest), "Failing Test should not run");
    }

}
