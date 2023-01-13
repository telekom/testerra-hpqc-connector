package eu.tsystems.mms.tic.testframework.qc11connector.test;

import eu.tsystems.mms.tic.testframework.qcconnector.annotation.QCTestname;
import eu.tsystems.mms.tic.testframework.qcconnector.annotation.QCTestset;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import eu.tsystems.mms.tic.testframework.utils.UITestUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created on 07.03.2022
 *
 * @author mgn
 */
@QCTestset("Root\\Testerra\\QCSyncResultTests\\QcSyncResultTests")
public class QcStatusSyncTest extends TesterraTest implements WebDriverManagerProvider {

    // It results in 2 screenshots are synced to QC
    @Test
    public void testT01_QcSyncResultFailed() {
        WEB_DRIVER_MANAGER.getWebDriver();
        UITestUtils.takeScreenshots(true);
        Assert.assertEquals(1, 2);
    }

    @QCTestname(value = "T02_QcSyncResultPassed")
    @Test
    public void testT02_QcSyncResultPassed() {
        TimerUtils.sleep(2_000, "Waiter to get test duration.");
        Assert.assertEquals(1, 1);
    }

    @QCTestname(value = "T02_QcSyncResultPassed", instanceCount = 2)
    @Test
    public void testT02a_QcSyncResultPassed() {
        Assert.assertEquals(1, 1);
    }

    @Test(dependsOnMethods = "testT01_QcSyncResultFailed")
    public void testT03_QcSyncResultSkipped() {

    }

    /**
     * Test method names in Quality Center can contain spaces.
     */
    @QCTestname("T04 QcSyncResult Passed Spaces")
    @Test
    public void testT04_QcSyncResultPassedSpaces() {
    }

    @QCTestname(testId = 2264, instanceCount = 2)
    @Test
    public void testT04a_QcSyncResultPassedSpacesPerId() {
    }


}
