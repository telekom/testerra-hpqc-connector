package io.testerra.plugins.hpqcconn.cucumber.pages;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.PageFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class GoogleSearchPage extends Page {

    protected UiElement inputSearch = find(By.xpath("//input[@title='Suche']"));

    public GoogleSearchPage(WebDriver driver) {
        super(driver);
    }

    public GoogleSearchResultPage searchTerm(String term) {
        inputSearch.sendKeys(term);
        inputSearch.submit();
        return createPage(GoogleSearchResultPage.class);
    }
}
