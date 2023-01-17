package com.securithings.webDriver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class WDTools {
    protected Logger logger = LogManager.getLogger(LogManager.getLogger(WDTools.class.getName()));
    public WebDriver driver;
    public int waitElementTimeout = 20;
    public int pageLoadimeout = 90;
    private By lastElem = null;
    private String lastBorder = null;
    //for highlight elements
    public static WebElement lastElement = null;
    public static String backgroundColour = "YELLOW";
    public static String lastElementOrigColour;

    public static JSONObject dataChromeDevTools;
    public static JavascriptExecutor javascriptExecutor;

    
    public static List<String> SystemSpecs() {
        List<String> systemList = new ArrayList<>();
        systemList.add(System.getProperty("os.name"));
        systemList.add(System.getProperty("os.arch"));
        return systemList;
    }
   

    //more setting needed , timeout and so on...
    //============================================================================
    public WDTools () {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("ignore-certificate-errors");
        options.addArguments("--start-maximized");
        String headless = System.getenv("headless");
        logger.info("HEADLESS = " + headless);
        options.addArguments("--window-size=2560,1440"); // Big resolution...
        if ((headless != null && headless.equalsIgnoreCase("true")) || (System.getProperty("headless") != null && System.getProperty("headless").equalsIgnoreCase("true")))
        {
            HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
            chromePrefs.put("profile.default_content_settings.popups", 0);
            if (SystemSpecs().get(0).contains("Windows")){
                chromePrefs.put("download.default_directory", System.getenv("USERPROFILE") + "\\Downloads\\");
                System.setProperty("webdriver.chrome.driver", "chromedriver.exe");}
            if (SystemSpecs().get(0).contains("Linux")){
                System.setProperty("webdriver.chrome.driver", "chromedriver_linux_64");
                chromePrefs.put("download.default_directory", System.getenv("HOME") + "/Downloads/");}
            if (SystemSpecs().get(0).contains("Mac")){
                if (SystemSpecs().get(1).contains("aarch")){
                    System.setProperty("webdriver.chrome.driver", "chromedriver_mac_arm64");
                }else{
                    System.setProperty("webdriver.chrome.driver", "chromedriver_mac_intl64");
                }
                chromePrefs.put("download.default_directory", System.getenv("HOME") + "/Downloads/");
            }
            options.setExperimentalOption("prefs", chromePrefs);
            options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--headless");
            logger.info("RUNNING HEADLESS");
        }        

        System.setProperty("webdriver.chrome.silentOutput","true");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(pageLoadimeout, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(waitElementTimeout, TimeUnit.SECONDS);

        javascriptExecutor = (JavascriptExecutor) driver;
    }
    //============================================================================

    //============================================================================
    public void click(By by) throws Exception {
        try
        {
            highlightElement(driver.findElement(by));
            driver.findElement(by).click();
        }
        catch(Exception e)
        {
            try {
                waitForElement(by, true, waitElementTimeout, true);
                highlightElement(driver.findElement(by));
                driver.findElement(by).click();
            }
            catch (Exception e1) {
                Thread.sleep(2000);
                highlightElement(driver.findElement(by));
                driver.findElement(by).click();
            }
        }
    }
    //============================================================================

    //============================================================================
    public void click(By by, int timeout) throws Exception {
        try
        {
            driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
            highlightElement(driver.findElement(by));
            driver.findElement(by).click();
        }
        catch(Exception e)
        {
            waitForElement(by, true, timeout, true);
            highlightElement(driver.findElement(by));
            driver.findElement(by).click();

        }
        finally {
            driver.manage().timeouts().implicitlyWait(waitElementTimeout, TimeUnit.SECONDS);
        }
    }
    //============================================================================

    //============================================================================
    public String read(By by) throws Exception {
        String text = "FAILED READ";
        try
        {
            highlightElement(driver.findElement(by));
            text = driver.findElement(by).getText();
        }
        catch (Exception e)
        {
            waitForElement(by, false, waitElementTimeout, true);
            highlightElement(driver.findElement(by));
            text = driver.findElement(by).getText();
        }
        return text;
    }
    //============================================================================

    //============================================================================
    public String tryRead(By by, int timeout) throws Exception {
        String text = "";
        try
        {
            driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);
            text = driver.findElement(by).getText();
        }
        catch (Exception e)
        {
            logger.info("expected failure to read, returning empty string");
        }
        return text;
    }
    //============================================================================

    //============================================================================
    public String tryRead(By by) throws Exception {
        String text = "";
        try
        {
            text = read(by);
        }
        catch (Exception e)
        {
            logger.info("expected failure to read, returning empty string");
        }
        return text;
    }
    //============================================================================

    //============================================================================
    public String read(By by, int timeout) throws Exception {
        String text = "FAILED READ";
        try
        {
            highlightElement(driver.findElement(by));
            text = driver.findElement(by).getText();
        }
        catch (Exception e)
        {
            waitForElement(by, false, timeout, true);
            highlightElement(driver.findElement(by));
            text = driver.findElement(by).getText();
        }
        return text;
    }
    //============================================================================

    //============================================================================
    public void write(By by, String text) throws Exception {
        try {
            highlightElement(driver.findElement(by));
            driver.findElement(by).clear();
            driver.findElement(by).sendKeys(text);
        }
        catch(Exception e) {
            waitForElement(by, true, waitElementTimeout, true);
            highlightElement(driver.findElement(by));
            driver.findElement(by).clear();
            driver.findElement(by).sendKeys(text);
        }
    }
    //============================================================================

    //============================================================================
    public void hitEnter (By by) throws Exception{
        try {
            highlightElement(driver.findElement(by));
            driver.findElement(by).sendKeys(Keys.RETURN);
        }
        catch(Exception e) {
            waitForElement(by, true, waitElementTimeout, true);
            highlightElement(driver.findElement(by));
            driver.findElement(by).sendKeys(Keys.RETURN);
        }
    }
    //============================================================================

    //============================================================================
    public String getAttribute (By by, String attribute) throws Exception{
        try {
            highlightElement(driver.findElement(by));
            return driver.findElement(by).getAttribute(attribute);
        }
        catch(Exception e) {
            waitForElement(by, true, waitElementTimeout, true);
            highlightElement(driver.findElement(by));
            return driver.findElement(by).getAttribute(attribute);
        }
    }
    //============================================================================

    //============================================================================
    public String getAttribute (By by, String attribute, int timeout) throws Exception{
        try {
            highlightElement(driver.findElement(by));
            return driver.findElement(by).getAttribute(attribute);
        }
        catch(Exception e) {
            waitForElement(by, true, timeout, true);
            highlightElement(driver.findElement(by));
            return driver.findElement(by).getAttribute(attribute);
        }
    }
    //============================================================================

    //============================================================================
    public String tryGetAttribute(By by, String attribute, int waitElementTimeout) throws Exception {
        String text = "";
        try
        {
            driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
            text = getAttribute(by, attribute, waitElementTimeout);
        }
        catch (Exception e)
        {
            logger.info("expected failure to read, returning empty string");
        }
        finally {
            driver.manage().timeouts().implicitlyWait(this.waitElementTimeout, TimeUnit.SECONDS);
        }
        return text;
    }
    //============================================================================

    //============================================================================
    public void select(By by, String value) throws Exception{
        highlightElement(driver.findElement(by));
        waitForElement(by, false,2, true);
        Select select = new Select(driver.findElement(by));
        select.selectByValue(value);
    }
    //============================================================================

    //============================================================================
    public void select(By by, int index) throws Exception{
        highlightElement(driver.findElement(by));
        waitForElement(by, false,2, true);
        Select select = new Select(driver.findElement(by));
        select.selectByIndex(index);
    }
    //============================================================================

    //============================================================================
    public void selectByClick (By selectOpen, int selectIndex) throws Exception{
        highlightElement(driver.findElement(selectOpen));
        waitForElement(selectOpen, false,2, true);
        click(selectOpen);
        Thread.sleep(500);
        //click();
        //select.selectByIndex(index);
    }
    //============================================================================

    //============================================================================
    public boolean waitForElement(By by, boolean checkEnabled, int timeout, boolean throwException) throws Exception{
        logger.debug("By = " + by.toString() + ", Timeout: " + timeout);
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        MyTimer timer = new MyTimer(timeout);
        boolean ok = false;
        if (checkEnabled)
            ok = isElementPresent(by) && isElementDisplayed(by) && isElementEnabled(by);
        else
            ok = isElementPresent(by) && isElementDisplayed(by);
        while (!ok &&timer.timeLeft(throwException, by.toString())) {
            logger.debug("Waiting for :: " + by.toString());
            timer.tick(500);
            if (checkEnabled)
                ok = isElementPresent(by) && isElementDisplayed(by) && isElementEnabled(by);
            else
                ok = isElementPresent(by) && isElementDisplayed(by);
        }
        driver.manage().timeouts().implicitlyWait(waitElementTimeout, TimeUnit.SECONDS);
        return ok;
        //TODO add exact reason of failure
    }
    //============================================================================

    //============================================================================
    public boolean waitForHiddenElement(By by, int timeout, boolean throwException) throws Exception{
        logger.info("");
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        MyTimer timer = new MyTimer(timeout);
        boolean ok = false;

        ok = isElementPresent(by);
        while (!ok &&timer.timeLeft(throwException, by.toString())) {
            logger.debug("Waiting for :: " + by.toString());
            timer.tick(500);
            ok = isElementPresent(by);
        }
        driver.manage().timeouts().implicitlyWait(waitElementTimeout, TimeUnit.SECONDS);
        return ok;
    }
    //============================================================================

    //============================================================================
    public boolean waitForElementGone(By by, int timeout, boolean throwException) throws Exception{
        logger.info("");
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        MyTimer timer = new MyTimer(timeout);
        boolean ok = false;
        ok = isElementPresent(by) && isElementDisplayed(by);
        while (ok &&timer.timeLeft(throwException, by.toString())) {
            logger.debug("Waiting for element to disappear:: " + by.toString());
            timer.tick(500);
            ok = isElementPresent(by) && isElementDisplayed(by);
        }
        driver.manage().timeouts().implicitlyWait(waitElementTimeout, TimeUnit.SECONDS);
        return ok;
    }
    //============================================================================

    //============================================================================
    public List<WebElement> findElements(By by, int timeout) {
        driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);
        List<WebElement> elements = new ArrayList<>();
        try {
            elements = driver.findElements(by);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        driver.manage().timeouts().implicitlyWait(waitElementTimeout, TimeUnit.SECONDS);
        return elements;
    }
    //============================================================================

    //============================================================================
    public boolean isElementPresent(By by)
    {
        try {
            driver.findElement(by);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
    //============================================================================

    //============================================================================
    public boolean isElementDisplayed(By by)
    {
        try
        {
            boolean b = driver.findElement(by).isDisplayed();
            if (b)
                return true;
            else
                return false;
        }
        catch (Exception e)
        {
            return false;
        }
    }
    //============================================================================

    //============================================================================
    public boolean isElementEnabled(By by)
    {
        try
        {
            boolean b = driver.findElement(by).isEnabled();
            if (b)
                return true;
            else
                return false;
        }
        catch (Exception e)
        {
            return false;
        }
    }
    //============================================================================

    //============================================================================
    public void hover(By by) throws Exception{
        highlightElement(driver.findElement(by));
        Actions action = new Actions(driver);
        waitForElement(by, false, waitElementTimeout, true);
        action.moveToElement(driver.findElement(by)).perform();
    }
    //============================================================================

    //============================================================================
    public void moveToCenter() throws Exception{
        Actions action = new Actions(driver);
        action.moveByOffset(300, 20).perform();
        Thread.sleep(500);
    }
    //============================================================================

    //============================================================================
    public void hover(WebElement element) throws Exception{
        highlightElement(element);
        Actions action = new Actions(driver);
        action.moveToElement(element).perform();
    }
    //============================================================================


    //============================================================================
    public WebElement highlightElement(WebElement newElement){
        // turn off last element
        if(lastElement!=null){
            setBackgroundColourOfElement(lastElement, driver, this.lastElementOrigColour);
        }
        lastElement = newElement;
        if(newElement!=null){
            this.lastElementOrigColour = newElement.getCssValue("backgroundColor");
            setBackgroundColourOfElement(newElement, driver, backgroundColour);
        }
        return newElement;
    }
    //============================================================================

    //============================================================================
    private void setBackgroundColourOfElement(final WebElement element,WebDriver driver, final String desiredColour) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].style.backgroundColor=arguments[1]", element, desiredColour);

        }catch(Exception e){
            if(e instanceof StaleElementReferenceException){
                // ignore
            }else {
                logger.error("Error setting background colour of element");
                e.printStackTrace();
            }
        }
    }
    //============================================================================

    //============================================================================
    public void waitForSecondWindow(int timeout ) throws Exception {
        MyTimer timer = new MyTimer(1000 * timeout);
        Set handles = driver.getWindowHandles();
        while (handles.size() < 2 && timer.timeLeft("another window"))
        {
            timer.tick(1000);
            handles = driver.getWindowHandles();
        }
    }
    //============================================================================

    //============================================================================
    public String getSecondWindowHandle(String handle) {
        Set handles = driver.getWindowHandles();
        for (Object o: handles)
        //foreach (String s in handles)
        {
            if (!(o.toString().equalsIgnoreCase(handle)))
            {
                return o.toString();
            }
        }
        return null;
    }
    //============================================================================

    //============================================================================
    public void switchToSecondWindow(int timeout) throws Exception{
        String firstWinHandle = driver.getWindowHandle();
        waitForSecondWindow(timeout);
        String secondWindowHandle = getSecondWindowHandle(firstWinHandle);
        if (secondWindowHandle == null)
        {
            throw new Exception("Could not find second window");
        }
        driver.switchTo().window(secondWindowHandle);

    }
    //============================================================================

    //============================================================================
    /**
     * closes all tabs but original and returns control to original tab
     */
    public void closeAllTabsButOriginal() {
        //if only 1 tab - close it
        if (driver.getWindowHandles().size() == 1) {
            driver.close();
        }
        else {
            String originalHandle = driver.getWindowHandles().toArray()[0].toString();
            //Do something to open new tabs
            for (String handle : driver.getWindowHandles())
            {
                if (!handle.equals(originalHandle)) {
                    driver.switchTo().window(handle);
                    driver.close();
                }
            }
            driver.switchTo().window(originalHandle);
        }
    }
    //============================================================================

    //============================================================================
    public void scrollToElement (By by) throws Exception{
        WebElement element = driver.findElement(by);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        Thread.sleep(500);
    }
    //============================================================================

    //============================================================================
    public void checkBoxes(By checkBoxBy, boolean shouldBeChecked) throws Exception{
        if (shouldBeChecked && !driver.findElement(checkBoxBy).isSelected()) {
            click(checkBoxBy);
        }
        else if (!shouldBeChecked && driver.findElement(checkBoxBy).isSelected()) {
            click(checkBoxBy);
        }
    }
    //============================================================================

    public void refreshPage(int afterSecs) throws Exception {
        Thread.sleep(afterSecs * 1000);
        driver.navigate().refresh();
    }





}
