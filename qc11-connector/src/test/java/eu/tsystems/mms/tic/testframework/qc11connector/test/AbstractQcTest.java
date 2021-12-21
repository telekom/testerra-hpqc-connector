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

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.qc11connector.constants.QCConstants;
import eu.tsystems.mms.tic.testframework.qcrest.clients.QcRestClient;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestRun;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestSet;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestSetTest;
import java.io.IOException;
import java.util.List;

import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.testng.annotations.AfterSuite;

/**
 * Common super class for tests with after suite method.
 *
 * @author sepr
 */
public abstract class AbstractQcTest extends TesterraTest implements Loggable {

    /**
     * Remove ever growing stack of test runs in testsetundertest.
     *
     * @throws IOException Exception during rest call.
     */
    @AfterSuite
    public void cleanUp() throws Exception {
        log().info("Cleanup created testruns");
        String[] testsets = new String[]{QCConstants.QCSYNC3_TESTSET_NAME};
        for (String testSet : testsets) {
            TestSet ts = QcRestClient.getTestSet(testSet, QCConstants.QC_TESTSUNDERTEST_FOLDER);
            for (TestSetTest tst : QcRestClient.getTestSetTests(ts)) {
                List<TestRun> testRuns = QcRestClient.getTestRuns(tst);
                boolean first = true;
                for (TestRun run : testRuns) {
                    if (first) {
                        // Keep one run in test, as some other tests need at least one run
                        // (qcconnector Tests)
                        first = false;
                    } else {
                        log().info("Remove testrun with ID " + run.getId());
                        QcRestClient.removeTestRun(run.getId());
                    }
                }
            }
        }
    }

}
