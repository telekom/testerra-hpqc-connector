/*
 * Created on 17.10.2013
 *
 * Copyright(c) 2011 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.qc11connector.testsundertest.qcsync3;

import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.qc11connector.util.TestNgTestMethods;
import eu.tsystems.mms.tic.testframework.qcrest.constants.QCProperties;
import eu.tsystems.mms.tic.testframework.utils.UITestUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import java.lang.reflect.Method;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;

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
        if (!method.getName().contains("Screenshot")) {
            final FirefoxProfile profile = new FirefoxProfile();
            WebDriverManager.setGlobalExtraCapability(FirefoxDriver.PROFILE, profile);
            WebDriver driver = WebDriverManager.getWebDriver();
        }
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
    @org.testng.annotations.Test
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
    @org.testng.annotations.Test
    public void testScreenshotUploadAllFailed() {
        System.setProperty(QCProperties.UPLOAD_SCREENSHOTS_FAILED, "true");
        final WebDriver driver = WebDriverManager.getWebDriver();
        UITestUtils.takeScreenshot(driver, false);
        Assert.fail();
    }

    /**
     * Test using property qc.test.failed.upload.automatic.screenshot
     */
    @org.testng.annotations.Test
    public void testScreenshotUploadAutoFailed() {
        final WebDriver driver = WebDriverManager.getWebDriver();
        UITestUtils.takeScreenshot(driver, false);
        Assert.fail();
    }

    /**
     * Test using property qc.test.failed.upload.screencast
     */
    @org.testng.annotations.Test
    public void testScreencastUploadFailed() {
        TestNgTestMethods.failingWebDriverTest();
        Assert.fail();
    }

    /**
     * Test using property qc.test.failed.upload.screencast
     */
    @org.testng.annotations.Test
    public void testScreenshotUploadFallback() {
        TestNgTestMethods.failingWebDriverTest();
    }

    /**
     * Test using property qc.test.failed.upload.screencast
     */
    @org.testng.annotations.Test
    public void testAttachmentUploadNone() {
        System.setProperty(QCProperties.QCUPLOADSCREENCAST, "false");
        TestNgTestMethods.failingWebDriverTest();
    }
}
