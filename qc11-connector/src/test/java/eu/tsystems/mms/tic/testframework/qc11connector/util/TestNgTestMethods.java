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
package eu.tsystems.mms.tic.testframework.qc11connector.util;

import eu.tsystems.mms.tic.testframework.utils.UITestUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

/**
 * Basic TestNg TestMethods called by different Tests.
 *
 * @author sepr
 */
public final class TestNgTestMethods {

//    /**
//     * Title of Testpage 1.
//     */
//    private static final String HEADER1 = "XetaTest";
//    /**
//     * Title of Testpage 2.
//     */
//    private static final String HEADER2 = "XetaTest Seite2";
//    /**
//     * http prefix for URLs.
//     */
//    private static final String HTTP = "http://";
//
//    /**
//     * Hide constructor.
//     */
//    private TestNgTestMethods() {
//    }
//
//    /**
//     * A sample successful test.
//     */
//    public static void successfulWebDriverTest() {
//        final WebDriver webDriver = WebDriverManager.getWebDriver();
//        webDriver.get(HTTP + Constants.TESTSERVERIP + "/httptest.html");
//        Assert.assertEquals(HEADER1, webDriver.getTitle());
//        final WebElement element = webDriver.findElement(By.name("link"));
//        element.click();
//        Assert.assertEquals(webDriver.getTitle(), HEADER2);
//    }
//
//    /**
//     * failingWebDriverTest.
//     */
//    public static void failingWebDriverTest() {
//        final WebDriver webDriver = WebDriverManager.getWebDriver();
//        webDriver.get(HTTP + Constants.TESTSERVERIP + "/httptest.html");
//        UITestUtils.takeScreenshot(webDriver, false);
//        Assert.assertEquals(HEADER1, webDriver.getTitle());
//        final WebElement element = webDriver.findElement(By.name("link"));
//        element.click();
//        Assert.assertEquals(webDriver.getTitle(), "was anderes");
//    }
//
//    /**
//     * brokenWebDriverTest.
//     */
//    public static void brokenWebDriverTest() {
//        final WebDriver webDriver = WebDriverManager.getWebDriver();
//        brokenWebDriverTest(webDriver);
//    }
//
//    public static void brokenWebDriverTest(WebDriver webDriver) {
//        webDriver.get(HTTP + Constants.TESTSERVERIP + "/httptest.html");
//        Assert.assertEquals(HEADER1, webDriver.getTitle());
//        final WebElement element = webDriver.findElement(By.name("gibts nicht"));
//        element.click();
//        Assert.assertEquals(webDriver.getTitle(), HEADER2);
//    }
//
//    /**
//     * brokenWebDriverTest.
//     */
//    public static void successfulWebDriverTestDifferentBaseURL() {
//        final WebDriver webDriver = WebDriverManager.getWebDriver();
//        webDriver.get(HTTP + Constants.TESTSERVERIP + "/sub/test.html");
//        Assert.assertEquals(webDriver.getTitle(), HEADER2);
//        final WebElement element = webDriver.findElement(By.name("link"));
//        element.click();
//        Assert.assertEquals(webDriver.getTitle(), HEADER1);
//    }

}
