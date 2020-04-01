/*
 * Created on 23.02.2012
 *
 * Copyright(c) 2011 - 2011 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.qc11connector.testsundertest.qcsync3;

import eu.tsystems.mms.tic.testframework.qc11connector.constants.QCConstants;
import eu.tsystems.mms.tic.testframework.testmanagement.annotation.QCTestname;
import eu.tsystems.mms.tic.testframework.testmanagement.annotation.QCTestset;
import org.testng.Assert;

/**
 * Sample test with QCTestSet class-annotation for integration-tests of QCSync Type 3.
 *
 * @author rnhb
 */
@QCTestset(QCConstants.QC_TESTSUNDERTEST_FOLDER + QCConstants.QCSYNC3_TESTSET_NAME)
public class CorrectClassAnnotationTest {


    /**
     * Test under test for unit-test correctClassAnnotation. The integration-test will also
     * control the methodname-mapping (e.g. cutting of 'test_').
     */
    @org.testng.annotations.Test
    public void correctClassAnnotation() {
        Assert.assertTrue(true);
    }

    /**
     * Test under test for test wrongMethodAnnotationOverridesCorrectClassAnnotation
     */
    @QCTestset(QCConstants.NOT_EXISTING_FOLDER + QCConstants.NOT_EXISTING_TESTSET)
    @org.testng.annotations.Test
    public void wrongMethodAnnotationOverridesCorrectClassAnnotation() {
        Assert.assertTrue(true);
    }

    /**
     *
     */
    @org.testng.annotations.Test
    public void failingTest() {
        Assert.assertTrue(false);
    }

    /**
     *
     */
    @org.testng.annotations.Test
    public void successfulTest() {
        Assert.assertTrue(true);
    }

    @org.testng.annotations.Test
    @QCTestname("notExistingTestNameAnnotation")
    public void someNotKnownTestnameWithWrongAnnotation() {
        Assert.assertTrue(true);
    }
}
