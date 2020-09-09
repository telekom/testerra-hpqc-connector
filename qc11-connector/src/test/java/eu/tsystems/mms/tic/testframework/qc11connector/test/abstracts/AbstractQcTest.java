/*
 * Created on 15.06.2015
 *
 * Copyright(c) 2011 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.qc11connector.test.abstracts;

import eu.tsystems.mms.tic.testframework.common.TesterraCommons;
import eu.tsystems.mms.tic.testframework.qc11connector.constants.QCConstants;
import eu.tsystems.mms.tic.testframework.qcrest.clients.QcRestClient;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestRun;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestSet;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestSetTest;
import java.io.IOException;
import java.util.List;
import org.testng.annotations.AfterSuite;

/**
 * Common super class for tests with after suite method.
 *
 * @author sepr
 */
public abstract class AbstractQcTest {

    static {
        TesterraCommons.init();
    }

    /**
     * Remove ever growing stack of test runs in testsetundertest.
     *
     * @throws IOException Exception during rest call.
     */
    @AfterSuite
    public void cleanUp() throws Exception {
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
                        QcRestClient.removeTestRun(run.getId());
                    }
                }
            }
        }
    }

}
