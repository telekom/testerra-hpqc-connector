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

import eu.tsystems.mms.tic.testframework.qc11connector.util.QC11TestUtils;
import eu.tsystems.mms.tic.testframework.qcconnector.constants.QCTestStatus;
import eu.tsystems.mms.tic.testframework.qcconnector.constants.QCTestUnderTest;
import eu.tsystems.mms.tic.testframework.qcconnector.synchronize.QualityCenterSyncUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test of QualityCenterSyncUtils with manual synchronize to QC.
 *
 * @author sepr
 */
public class QCSyncUtilsTest extends AbstractQcTest {

    /**
     * Name of testUnderTest
     */
    private final String name = "testTest1";
    /**
     * Name of testUnderTest
     */
    private final String nameNegative = "test3";
    /**
     * qcPath to sync to
     */
    private final String qcPath = "Root\\Xeta\\Xeta 3.2\\TestSetsUnderTest\\manual\\Sample";

    /**
     * positive test
     */
    @Test
    public void testManualSync() {
        Assert.assertTrue(QualityCenterSyncUtils.syncTestRun(qcPath, name, "Passed", null));
        QC11TestUtils.assertStatusOfTestInQC(qcPath, QCTestUnderTest.QCSYNCPROG_TEST1, QCTestStatus.PASSED);
    }

    /**
     * negative test with wrong qcPath
     */
    @Test
    public void testManualSyncWrongPath() {
        Assert.assertFalse(QualityCenterSyncUtils.syncTestRun(qcPath + "fdg", nameNegative, "Passed", null));
    }

    /**
     * negative test with wrong name
     */
    @Test
    public void testManualSyncWrongName() {
        Assert.assertFalse(QualityCenterSyncUtils.syncTestRun(qcPath, nameNegative + "dgsdg", "Passed", null));
        QC11TestUtils.assertStatusOfTestInQC(qcPath, QCTestUnderTest.QCSYNCPROG_TEST2, QCTestStatus.NORUN);
    }

    /**
     * positive test but no status
     */
    @Test
    public void testManualSyncWrongNoStatus() {
        Assert.assertTrue(QualityCenterSyncUtils.syncTestRun(qcPath, name, null, null));
        QC11TestUtils.assertStatusOfTestInQC(qcPath, QCTestUnderTest.QCSYNCPROG_TEST1, QCTestStatus.PASSED);
    }

}
