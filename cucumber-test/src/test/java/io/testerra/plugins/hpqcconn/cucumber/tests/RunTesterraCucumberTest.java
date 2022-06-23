package io.testerra.plugins.hpqcconn.cucumber.tests;

import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import eu.tsystems.mms.tic.testframework.testmanagement.annotation.QCTestset;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;

@Listeners(TesterraListener.class)
//@QCTestset("Root\\Testerra\\QCSyncResultTests\\QcSyncResultTests")
@CucumberOptions(
        plugin = {"eu.tsystems.mms.tic.testerra.plugins.cucumber.TesterraReportPlugin"},
        features = "src/test/resources/features/",
        glue = {"io.testerra.plugins.hpqcconn.cucumber.steps"
                ,"eu.tsystems.mms.tic.testerra.plugins.cucumber"
        }
)
public class RunTesterraCucumberTest extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }

}
