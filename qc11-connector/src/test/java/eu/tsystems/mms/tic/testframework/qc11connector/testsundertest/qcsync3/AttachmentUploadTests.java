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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;

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
        System.clearProperty(QCProperties.QCUPLOADAUTOSCREENSHOT);
        System.clearProperty(QCProperties.QCUPLOADAUTOSCREENHOTOLD);
        System.clearProperty(QCProperties.QCUPLOADSCREENCAST);
        System.clearProperty(QCProperties.QCUPLOADSCREENCASTPASSED);
        System.clearProperty(QCProperties.QCUPLOADSCREENSHOTSPASSED);
        System.clearProperty(QCProperties.QCUPLOADSCREENSHOTS);
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
        System.clearProperty(QCProperties.QCUPLOADAUTOSCREENSHOT);
        System.clearProperty(QCProperties.QCUPLOADAUTOSCREENHOTOLD);
        System.clearProperty(QCProperties.QCUPLOADSCREENCAST);
        System.clearProperty(QCProperties.QCUPLOADSCREENCASTPASSED);
        System.clearProperty(QCProperties.QCUPLOADSCREENSHOTSPASSED);
        System.clearProperty(QCProperties.QCUPLOADSCREENSHOTS);
    }

    /**
     * Test using property qc.test.passed.upload.screenshots
     */
    @org.testng.annotations.Test
    public void testScreenshotUploadAllPassed() {
        System.setProperty(QCProperties.QCUPLOADSCREENSHOTSPASSED, "true");
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
        System.setProperty(QCProperties.QCUPLOADSCREENSHOTS, "true");
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
        System.setProperty(QCProperties.QCUPLOADAUTOSCREENSHOT, "false");
        TestNgTestMethods.failingWebDriverTest();
        Assert.fail();
    }

    /**
     * Test using property qc.test.failed.upload.screencast
     */
    @org.testng.annotations.Test
    public void testScreenshotUploadFallback() {
        System.setProperty(QCProperties.QCUPLOADAUTOSCREENSHOT, "false");
        System.setProperty(QCProperties.QCUPLOADAUTOSCREENHOTOLD, "true");
        TestNgTestMethods.failingWebDriverTest();
    }

    /**
     * Test using property qc.test.failed.upload.screencast
     */
    @org.testng.annotations.Test
    public void testAttachmentUploadNone() {
        System.setProperty(QCProperties.QCUPLOADAUTOSCREENSHOT, "false");
        System.setProperty(QCProperties.QCUPLOADSCREENCAST, "false");
        TestNgTestMethods.failingWebDriverTest();
    }
}
