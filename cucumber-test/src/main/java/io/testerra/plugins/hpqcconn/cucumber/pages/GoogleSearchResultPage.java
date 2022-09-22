package io.testerra.plugins.hpqcconn.cucumber.pages;

import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class GoogleSearchResultPage extends GoogleSearchPage {

    private UiElement resultElements = find(By.cssSelector("#rso .g"));

    public GoogleSearchResultPage(WebDriver driver) {
        super(driver);
    }

    public boolean containsResult(String resultText) {
        boolean resultFound = false;
        for (UiElement guiElement : resultElements.list()) {
            resultFound = resultFound
                    || guiElement
                    .find(By.cssSelector("a h3"))
                    .waitFor().text().map(String::toLowerCase).isContaining(resultText.toLowerCase());
        }
        return resultFound;
    }
}
