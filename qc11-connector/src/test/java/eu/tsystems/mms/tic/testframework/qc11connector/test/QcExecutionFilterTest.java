/*
 * Created on 06.11.2014
 *
 * Copyright(c) 2011 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.qc11connector.test;

import eu.tsystems.mms.tic.testframework.qc11connector.constants.QCConstants;
import eu.tsystems.mms.tic.testframework.qc11connector.test.abstracts.AbstractQcTest;
import eu.tsystems.mms.tic.testframework.qc11connector.testsundertest.qcsync3.CorrectClassAnnotationTest;
import eu.tsystems.mms.tic.testframework.qcconnector.constants.QCTestUnderTest;
import eu.tsystems.mms.tic.testframework.qcconnector.synchronize.QualityCenterSyncUtils;
import eu.tsystems.mms.tic.testframework.qcrest.constants.QCProperties;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestSetTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.LinkedList;

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

        System.setProperty(QCProperties.QCEXECUTIONFILTER, "exclude:status:failed");
        String testSetPath = QCConstants.QC_TESTSUNDERTEST_FOLDER + QCConstants.QCSYNC3_TESTSET_NAME;
        LinkedList<Class<?>> classesContainingTestsUnderTest = new LinkedList<Class<?>>();
        classesContainingTestsUnderTest.add(CorrectClassAnnotationTest.class);
        QC11SynchronizerTest qc11SynchronizerTest = new QC11SynchronizerTest(classesContainingTestsUnderTest, testSetPath);
        qc11SynchronizerTest.createTestResults();
        TestSetTest failingTest = qc11SynchronizerTest.getTestSetTest(QCTestUnderTest.QCSYNC3_FAILINGTEST);
        Assert.assertFalse(QualityCenterSyncUtils.matchesExecutionFilter(failingTest), "Failing Test should not run");
        TestSetTest successfulTest = qc11SynchronizerTest.getTestSetTest(QCTestUnderTest.QCSYNC3_SUCCESSFULTEST);
        Assert.assertTrue(QualityCenterSyncUtils.matchesExecutionFilter(successfulTest), "Failing Test should not run");
    }

}
