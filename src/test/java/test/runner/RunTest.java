package test.runner;

import org.junit.runner.RunWith;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;


@RunWith(Cucumber.class)
@CucumberOptions(
        format = { "pretty", "html:target/cucumber","json:target/cucumber/cucumber.json"},
        glue = "solutions.heavywater.test.steps/",
        features = "classpath:cucumber/",
        monochrome = true
)
public class RunTest {

}
