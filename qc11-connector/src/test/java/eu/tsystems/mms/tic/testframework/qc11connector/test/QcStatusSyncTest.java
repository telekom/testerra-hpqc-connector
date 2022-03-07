package eu.tsystems.mms.tic.testframework.qc11connector.test;

import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.testmanagement.annotation.QCTestset;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created on 07.03.2022
 *
 * @author mgn
 */
@QCTestset("Root\\Testerra\\QCSyncResultTests\\QcSyncResultTests")
public class QcStatusSyncTest extends TesterraTest {

    @Test
    public void testT01_QcSyncResultFailed() {
        Assert.assertEquals(1,2);
    }

    @Test
    public void testT02_QcSyncResultPassed() {
        Assert.assertEquals(1,1);
    }


    @Test(dependsOnMethods = "testT01_QcSyncResultFailed")
    public void testT03_QcSyncResultSkipped() {

    }


}
