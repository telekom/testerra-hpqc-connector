package io.testerra.plugins.hpqcconn.cucumber.pages;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class GoogleSearchResultPage extends GoogleSearchPage {

    GuiElement resultElements = new GuiElement(getWebDriver(), By.cssSelector("#rso .g"));

    public GoogleSearchResultPage(WebDriver driver) {
        super(driver);
    }

    public boolean containsResult(String resultText) {
        boolean resultFound = false;
        for (GuiElement guiElement : resultElements.getList()) {
            resultFound = resultFound || guiElement.getSubElement(By.cssSelector("a h3")).getText().toLowerCase().contains(resultText.toLowerCase());
        }
        return resultFound;
    }
}
