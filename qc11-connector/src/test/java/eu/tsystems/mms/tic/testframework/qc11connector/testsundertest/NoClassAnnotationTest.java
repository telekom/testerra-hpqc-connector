/*
 * Created on 23.02.2012
 *
 * Copyright(c) 2011 - 2011 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.qc11connector.testsundertest;

import eu.tsystems.mms.tic.testframework.qc11connector.constants.QCConstants;
import eu.tsystems.mms.tic.testframework.testmanagement.annotation.QCTestname;
import eu.tsystems.mms.tic.testframework.testmanagement.annotation.QCTestset;
import org.testng.Assert;

/**
 * Sample test with QCTestSet method-annotations for integration-tests of QCSync Type 3.
 *
 * @author rnhb
 */
public class NoClassAnnotationTest {

    /**
     *
     */
    @QCTestset(QCConstants.QC_TESTSUNDERTEST_FOLDER + QCConstants.QCSYNC3_TESTSET_NAME)
    @org.testng.annotations.Test
    public void correctMethodAnnotation() {
        Assert.assertTrue(true);
    }

    /**
     *
     */
    @QCTestset(QCConstants.NOT_EXISTING_FOLDER + QCConstants.NOT_EXISTING_TESTSET)
    @org.testng.annotations.Test
    public void wrongMethodAnnotation() {
        Assert.assertTrue(true);
    }

    @org.testng.annotations.Test
    public void noAnnotation() {
        Assert.assertTrue(true);
    }

    @org.testng.annotations.Test
    @QCTestset(QCConstants.QC_TESTSUNDERTEST_FOLDER + QCConstants.QCSYNC3_TESTSET_NAME)
    @QCTestname("correctTestNameAnnotation")
    public void someNotKnownTestname() {
        Assert.assertTrue(true);
    }

    @QCTestset(QCConstants.QC_TESTSUNDERTEST_FOLDER + QCConstants.QCSYNC3_TESTSET_NAME)
    @org.testng.annotations.Test
    @QCTestname(value = "correctTestNameAnnotationWithoutInstanceCount")
    public void correctTestNameAnnotationWithoutInstanceCount() {
        Assert.assertTrue(true);
    }

    @QCTestset(QCConstants.QC_TESTSUNDERTEST_FOLDER + QCConstants.QCSYNC3_TESTSET_NAME)
    @org.testng.annotations.Test
    @QCTestname(value = "correctTestNameAnnotationWithInstanceCount", instanceCount = 1)
    public void correctTestNameAnnotationWithInstanceCount() {
        Assert.assertTrue(true);
    }

    @QCTestset(QCConstants.QC_TESTSUNDERTEST_FOLDER + QCConstants.QCSYNC3_TESTSET_NAME)
    @org.testng.annotations.Test
    @QCTestname(value = "correctTestNameAnnotationWithInstanceCount", instanceCount = 2)
    public void correctTestNameAnnotationWithInstanceCountTwo() {
        Assert.assertTrue(false, "Error.");
    }
}
