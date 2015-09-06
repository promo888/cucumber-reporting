package net.masterthought.cucumber;

import net.masterthought.cucumber.json.Feature;
import net.masterthought.cucumber.json.Row;
import net.masterthought.cucumber.json.Step;

import org.junit.Before;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static net.masterthought.cucumber.FileReaderUtil.getAbsolutePathFromResource;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: Igor
 * Date: 9/6/15
 * Time: 1:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyTest {

    Step passingStep;
    Step failingStep;
    Step skippedStep;
    Step withOutput;

    @Before
    public void setUpJsonReports() throws IOException {
        List<String> jsonReports = new ArrayList<String>();
        jsonReports.add(getAbsolutePathFromResource("net/masterthought/cucumber/project1.json"));
        ReportParser reportParser = new ReportParser(jsonReports);
        Feature passingFeature = reportParser.getFeatures().entrySet().iterator().next().getValue().get(0);
        Feature failingFeature = reportParser.getFeatures().entrySet().iterator().next().getValue().get(1);
        passingFeature.processSteps();
        failingFeature.processSteps();
        passingStep = passingFeature.getElements().first().getSteps().first();
        failingStep = failingFeature.getElements().first().getSteps().get(5);
        skippedStep = failingFeature.getElements().first().getSteps().get(6);
        withOutput = passingFeature.getElements().get(1).getSteps().first();
    }

    @Test()
    public void testNgTest() {
        List<String> jsonReports = new ArrayList<String>();
        jsonReports.add(getAbsolutePathFromResource("net/masterthought/cucumber/cells.json"));
        ReportParser reportParser = null;
        try {
            reportParser = new ReportParser(jsonReports);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        Feature feature = reportParser.getFeatures().entrySet().iterator().next().getValue().get(0);
        Step step = feature.getElements().get(0).getSteps().get(0);
        feature.processSteps();
        assertThat(step.getRows()[0], is(Row.class));
    }
}
