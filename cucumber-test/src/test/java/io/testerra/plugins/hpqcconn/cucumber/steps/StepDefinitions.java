package io.testerra.plugins.hpqcconn.cucumber.steps;

import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class StepDefinitions implements Loggable {

    @When("the user searches for {string}")
    public void iSearchFor(String searchInput) {
        log().info("Search something " + searchInput);
    }

    @Then("an entry for {string} is shown")
    public void anEntryForIsShown(String resultEntryText) {
        log().info("Assert search result to contain " + resultEntryText);
    }

    @Then("it fails")
    public void itFails() {
        TimerUtils.sleep(2_000, "Waiter to get test duration.");
        Assert.fail("This step is supposed to fail");
    }

    @When("the user does a step")
    public void theUserDoesAStep() {

    }

    @Fails(description = "This is supposed to fail", ticketString = "TESTID-12345")
    @Then("it fails expectedly")
    public void itFailsExpectedly() {
        Assert.fail("This step is supposed to fail with an expected fail");
    }


    @Fails(description = "This is supposed to work", ticketString = "TESTID-12345")
    @Then("it doesn't fails unexpectedly")
    public void itDoesnTFailsUnexpectedly() {
        Assert.assertTrue(true);
    }
}
