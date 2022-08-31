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
package eu.tsystems.mms.tic.testframework.qcrest.test.client;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.qcrest.clients.QcRestClient;
import eu.tsystems.mms.tic.testframework.qcrest.clients.RestConnector;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestRun;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestSetTest;
import java.io.IOException;
import java.util.List;

import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.testng.annotations.AfterSuite;

/**
 * abstract class for all tests
 *
 * @author sepr
 */
public abstract class AbstractTest extends TesterraTest implements Loggable {
    /** The test folders path. **/
    protected static final String TESTSET_PATH = "Root\\Testerra\\QCRestClient";
    /** Name of TestSet. */
    protected static final String TESTSET = "TestSetUnderTest";
    /** Name of tests in testlab */
    protected static final String TEST = "testUnderTest";
    /** Name of tests in testlab */
    protected static final String STRESSTEST = "stressTestUnderTest";

    /**
     * logout from rest service
     *
     * @throws IOException Error executing Rest request.
     */
    @AfterSuite
    public void cleanUp() throws Exception {
        // Deactivated to keep test results in QC
        // cleanUpRuns();
        RestConnector.getInstance().logout();
    }

    /**
     * Recreate TestUnderTest in TestSet to cleanup runs.
     *
     * @throws IOException Error executing Rest request.
     */
    private void cleanUpRuns() throws Exception {
        final TestSetTest tsTest = QcRestClient.getTestSetTest(TEST, TESTSET, TESTSET_PATH);

        final List<TestRun> lTestRuns = QcRestClient.getTestRuns(tsTest);
        for (final TestRun testRun : lTestRuns) {
            QcRestClient.removeTestRun(testRun.getId());
        }
        final TestSetTest tsStressTest = QcRestClient.getTestSetTest(STRESSTEST, TESTSET, TESTSET_PATH);

        final List<TestRun> lStressTestRuns = QcRestClient.getTestRuns(tsStressTest);
        for (final TestRun testRun : lStressTestRuns) {
            QcRestClient.removeTestRun(testRun.getId());
        }
    }

}
