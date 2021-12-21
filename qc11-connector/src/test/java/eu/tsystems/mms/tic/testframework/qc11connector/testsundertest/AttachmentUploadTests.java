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
package eu.tsystems.mms.tic.testframework.qc11connector.testsundertest;

import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.qcrest.constants.QCProperties;
import eu.tsystems.mms.tic.testframework.utils.UITestUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * Test used to verify their screenshots or screencasts are uploaded.
 *
 * @author sepr
 */
public class AttachmentUploadTests {

    /**
     * Enable Screencaster
     *
     * @param method TestMethod that has been started.
     */
    @BeforeMethod
    public void before(final Method method) {
        System.clearProperty(QCProperties.UPLOAD_SCREENSHOTS_PASSED);
        System.clearProperty(QCProperties.UPLOAD_SCREENSHOTS_FAILED);
        System.clearProperty(TesterraProperties.SCREENCASTER_ACTIVE);
//        if (!method.getName().contains("Screenshot")) {
//            final FirefoxProfile profile = new FirefoxProfile();
//            WebDriverManager.setGlobalExtraCapability(FirefoxDriver.PROFILE, profile);
//            WebDriver driver = WebDriverManager.getWebDriver();
//        }
    }

    /**
     * Reset properties after all tests have been run
     */
    @AfterTest
    public void afterTest() {
        System.clearProperty(QCProperties.UPLOAD_SCREENSHOTS_PASSED);
        System.clearProperty(QCProperties.UPLOAD_SCREENSHOTS_FAILED);
    }

    /**
     * Test using property qc.test.passed.upload.screenshots
     */
    @Test
    public void testScreenshotUploadAllPassed() {
        System.setProperty(QCProperties.UPLOAD_SCREENSHOTS_PASSED, "true");
        final WebDriver driver = WebDriverManager.getWebDriver();
        UITestUtils.takeScreenshot(driver, false);
        UITestUtils.takeScreenshot(driver, false);
        UITestUtils.takeScreenshot(driver, false);
    }

    /**
     * Test using property qc.test.failed.upload.screenshots
     */
    @Test
    public void testScreenshotUploadAllFailed() {
        System.setProperty(QCProperties.UPLOAD_SCREENSHOTS_FAILED, "true");
        final WebDriver driver = WebDriverManager.getWebDriver();
        UITestUtils.takeScreenshot(driver, false);
        Assert.fail();
    }

//    /**
//     * Test using property qc.test.failed.upload.automatic.screenshot
//     */
//    @org.testng.annotations.Test
//    public void testScreenshotUploadAutoFailed() {
//        final WebDriver driver = WebDriverManager.getWebDriver();
//        UITestUtils.takeScreenshot(driver, false);
//        Assert.fail();
//    }

    /**
     * Test using property qc.test.failed.upload.videos, default=true
     */
    @Test
    public void testScreencastUploadFailed() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        UITestUtils.takeScreenshot(driver, false);
        Assert.fail();
    }

//    /**
//     * Test using property qc.test.failed.upload.videos
//     */
//    @Test
//    public void testScreenshotUploadFallback() {
//        final WebDriver driver = WebDriverManager.getWebDriver();
//        UITestUtils.takeScreenshot(driver, false);
//        Assert.fail();
//    }

    /**
     * Test using property qc.test.failed.upload.videos=false
     */
    @Test
    public void testAttachmentUploadNone() {
        System.setProperty(QCProperties.UPLOAD_VIDEOS, "false");
        WebDriverManager.getWebDriver();
        Assert.fail();
    }
}
