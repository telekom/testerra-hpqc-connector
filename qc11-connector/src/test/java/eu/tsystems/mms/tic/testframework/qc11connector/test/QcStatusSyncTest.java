package eu.tsystems.mms.tic.testframework.qc11connector.test;

import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.testmanagement.annotation.QCTestset;
import eu.tsystems.mms.tic.testframework.utils.UITestUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created on 07.03.2022
 *
 * @author mgn
 */
@QCTestset("Root\\Testerra\\QCSyncResultTests\\QcSyncResultTests")
public class QcStatusSyncTest extends TesterraTest {

    // It results in 2 screenshots are synced to QC
    @Test
    public void testT01_QcSyncResultFailed() {
        WebDriverManager.getWebDriver();
        UITestUtils.takeScreenshots(true);
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
