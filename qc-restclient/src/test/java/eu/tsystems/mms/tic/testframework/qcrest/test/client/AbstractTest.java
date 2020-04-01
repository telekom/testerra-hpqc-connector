/*
 * Created on 11.08.2014
 *
 * Copyright(c) 2011 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.qcrest.test.client;

import eu.tsystems.mms.tic.testframework.qcrest.clients.QcRestClient;
import eu.tsystems.mms.tic.testframework.qcrest.clients.RestConnector;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestRun;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestSetTest;
import org.testng.annotations.AfterSuite;

import java.io.IOException;
import java.util.List;

/**
 * abstract class for all tests
 *
 * @author sepr
 */
public abstract class AbstractTest {
    /** The test folders path. **/
    protected static final String TESTSET_PATH = "Root\\Xeta\\QC WebServiceClient";
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
    public void cleanUp() throws IOException {
        cleanUpRuns();
        RestConnector.getInstance().logout();
    }

    /**
     * Recreate TestUnderTest in TestSet to cleanup runs.
     * 
     * @throws IOException Error executing Rest request.
     */
    private void cleanUpRuns() throws IOException {
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
