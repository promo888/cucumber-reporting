package net.masterthought.cucumber;

import net.masterthought.cucumber.json.Element;
import net.masterthought.cucumber.json.Feature;
import net.masterthought.cucumber.json.Step;
import net.masterthought.cucumber.util.Status;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static net.masterthought.cucumber.FileReaderUtil.getAbsolutePathFromResource;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ElementTest {

    ReportParser reportParser;
    Element passingElement;
    Element failingElement;
    Element undefinedElement;
    Element skippedElement;
    Element taggedElement;


    @Before
    public void setUpJsonReports() throws IOException {
        List<String> jsonReports = new ArrayList<String>();
        jsonReports.add(getAbsolutePathFromResource("net/masterthought/cucumber/project3.json"));
        reportParser = new ReportParser(jsonReports);
        
        Feature passingFeature = reportParser.getFeatures().entrySet().iterator().next().getValue().get(0);
        Feature failingFeature = reportParser.getFeatures().entrySet().iterator().next().getValue().get(1);
        Feature undefinedFeature = reportParser.getFeatures().entrySet().iterator().next().getValue().get(2);
        Feature skippedFeature = reportParser.getFeatures().entrySet().iterator().next().getValue().get(3);

        passingFeature.processSteps();
        failingFeature.processSteps();
        undefinedFeature.processSteps();
        skippedFeature.processSteps();
        
        passingElement = passingFeature.getElements().get(0);
        failingElement = failingFeature.getElements().get(0);
        undefinedElement = undefinedFeature.getElements().get(0);
        skippedElement = skippedFeature.getElements().get(0);
        
        taggedElement = passingFeature.getElements().get(1);
    }

    @Test
    public void shouldReturnSteps() {
        assertThat(passingElement.getSteps().get(0), is(Step.class));
    }

    @Test
    public void shouldReturnStatus() {
        assertThat(passingElement.getStatus(), is(Status.PASSED));
        assertThat(failingElement.getStatus(), is(Status.FAILED));
        assertThat(undefinedElement.getStatus(), is(Status.PASSED));
        assertThat(skippedElement.getStatus(), is(Status.PASSED));
    }

    @Test
    public void shouldReturnNameWhenConfigSkippedTurnedOn() {
        ConfigurationOptions configuration = ConfigurationOptions.instance();
        configuration.setSkippedFailsBuild(true);
    	try {
            assertThat(passingElement.getStatus(), is(Status.PASSED));
            assertThat(failingElement.getStatus(), is(Status.FAILED));
            assertThat(undefinedElement.getStatus(), is(Status.PASSED));
            assertThat(skippedElement.getStatus(), is(Status.FAILED));
    	} finally {
    		// restore the initial state for next tests
            configuration.setSkippedFailsBuild(false);
    	}
    }
    
    @Test
    public void shouldReturnNameWhenConfiUndefinedTurnedOn() {
        ConfigurationOptions configuration = ConfigurationOptions.instance();
        configuration.setUndefinedFailsBuild(true);
    	try {
            assertThat(passingElement.getStatus(), is(Status.PASSED));
            assertThat(failingElement.getStatus(), is(Status.FAILED));
            assertThat(undefinedElement.getStatus(), is(Status.FAILED));
            assertThat(skippedElement.getStatus(), is(Status.PASSED));
    	} finally {
    		// restore the initial state for next tests
            configuration.setUndefinedFailsBuild(false);
    	}
    }
    
    
    @Test
    public void shouldReturnName() {
        assertThat(passingElement.getName(), is("<div class=\"passed\"><span class=\"scenario-keyword\">Background: </span> <span class=\"scenario-name\">Activate Credit Card</span></div>"
        ));
    }

    @Test
    public void shouldReturnKeyword() {
        assertThat(passingElement.getKeyword(), is("Background"));
    }

    @Test
    public void shouldReturnTagList(){
        List<String> expectedList = new ArrayList<String>();
        expectedList.add("@fast");
        expectedList.add("@super");
        expectedList.add("@checkout");
        assertThat(taggedElement.getTagList().toList(), is(expectedList));
    }

    @Test
    public void shouldKnowIfHasTags(){
        assertThat(taggedElement.hasTags(), is(true));
    }

    @Test
    public void shouldReturnTagsAsHtml(){
        assertThat(taggedElement.getTagsList(), is("<div class=\"feature-tags\"><a href=\"fast.html\">@fast</a>,<a href=\"super.html\">@super</a>,<a href=\"checkout.html\">@checkout</a></div>"));
    }
    
}
