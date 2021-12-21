package eu.tsystems.mms.tic.testframework.qc11connector.testsundertest;

import eu.tsystems.mms.tic.testframework.qc11connector.constants.QCConstants;
import eu.tsystems.mms.tic.testframework.testmanagement.annotation.QCTestset;
import org.testng.Assert;

/**
 * Sample test with wrong QCTestSet class annotation for integration-test of QCSync Type 3.
 *
 * @author rnhb
 */
@QCTestset(QCConstants.NOT_EXISTING_FOLDER + QCConstants.NOT_EXISTING_TESTSET)
public class WrongClassAnnotationTest {

    /**
     * Test under test for test wrongClassAnnotation
     */
    @org.testng.annotations.Test
    public void wrongClassAnnotation() {
        Assert.assertTrue(true);
    }

    /**
     * Test under test for test methodOverridesClassAnnotation Path in annotation is correct and must override the wrong
     * path in class annotation
     */
    @QCTestset(QCConstants.QC_TESTSUNDERTEST_FOLDER + QCConstants.QCSYNC3_TESTSET_NAME)
    @org.testng.annotations.Test
    public void correctMethodAnnotationOverridesWrongClassAnnotation() {
        Assert.assertTrue(true);
    }

}
