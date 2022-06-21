package io.testerra.plugins.hpqcconn.cucumber.tests;

import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.Listeners;

@Listeners(TesterraListener.class)
@CucumberOptions(
        plugin = {"eu.tsystems.mms.tic.testerra.plugins.cucumber.TesterraReportPlugin"},
        features = "src/test/resources/features/",
        glue = {"io.testerra.plugins.hpqcconn.cucumber.steps"}
)
public class RunTesterraCucumberTest extends AbstractTestNGCucumberTests {
}
