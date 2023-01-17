package com.securithings.base;

import com.automation.remarks.testng.UniversalVideoListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.relevantcodes.extentreports.ExtentReports;
import com.securithings.common.CommonOps;
import com.securithings.common.Environments;
import com.securithings.common.Error;
import com.securithings.entities.device.Device;
import com.securithings.entities.Environment;
import com.securithings.entities.device.DeviceActionsEnum;
import com.securithings.entities.vms.VmsEntity;
import com.securithings.helpers.connector.ConnectorOps;
import com.securithings.helpers.connector.ConnectorRemoteUpgradeHelper;
import com.securithings.helpers.db.DbHelper;
import com.securithings.helpers.keycloak.KeycloakOps;
import com.securithings.helpers.pages.DeviceManagement.EnrollPage;
import com.securithings.helpers.pages.DeviceManagement.availableTasks.AvailableTasksHelper;
import com.securithings.helpers.pages.DeviceManagement.groups.GroupsPage;
import com.securithings.helpers.pages.DeviceManagement.deviceManagementBase.ManagePage;
import com.securithings.helpers.pages.DeviceManagement.RotatePasswordPane;
import com.securithings.helpers.pages.DeviceManagement.firmwareUpgrade.FirmwareUpgradePage;
import com.securithings.helpers.pages.DeviceManagement.manageDependencies.ManageDependenciesPane;
import com.securithings.helpers.pages.DeviceManagement.rotateCert.RotateCertPane;
import com.securithings.helpers.pages.LoginPage;
import com.securithings.helpers.pages.NavigationPane;
import com.securithings.helpers.pages.OverviewPage;
import com.securithings.helpers.pages.adminArea.*;
import com.securithings.helpers.pages.adminArea.deviceCredentials.DeviceCredentialsPage;
import com.securithings.helpers.pages.adminArea.deviceCredentials.DeviceCredentialsPageSNMP;
import com.securithings.helpers.pages.adminArea.horizonConnectors.CsvOps;
import com.securithings.helpers.pages.adminArea.horizonConnectors.HorizonConnectorsLSPPage;
import com.securithings.helpers.pages.adminArea.horizonConnectors.HorizonConnectorsPage;
import com.securithings.helpers.pages.adminArea.infraComponents.InfraComponentsPage;
import com.securithings.helpers.pages.adminArea.manageBinaries.ManageBinariesPage;
import com.securithings.helpers.pages.adminArea.permissions.ManagePermissionsPage;
import com.securithings.helpers.pages.adminArea.permissions.PermissionsHelper;
import com.securithings.helpers.pages.adminArea.permissions.PermissionsPage;
import com.securithings.helpers.pages.adminArea.siteManagement.SiteMgmtPage;
import com.securithings.helpers.pages.adminArea.upgradePaths.UpgradePathsPage;
import com.securithings.helpers.pages.automatedOps.CompletedOpsPage;
import com.securithings.helpers.pages.grafana.OperationalStatusOverviewPage;
import com.securithings.helpers.pages.grafana.deviceRealTimeMetrics.GrafanaDeviceRealTimeMetricsPage;
import com.securithings.helpers.pages.sitesView.SitesViewPage;
import com.securithings.helpers.pages.smartAlerts.SmartAlertsPage;
import com.securithings.webDriver.MyTimer;
import com.securithings.webDriver.WDTools;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;


@Listeners(UniversalVideoListener.class)
public class HorizonTestCaseBase {
    protected static ExtentReports extent;
    public static ExtentTest extentLogger;

    //for tests input and expected
    protected static String inputPath = "src/test/java/com/securithings/tests/input/";
    protected static String expectedPath = "src/test/java/com/securithings/tests/expected/";

    protected static Logger logger = LogManager.getLogger(HorizonTestCaseBase.class.getName());
    protected static Environment env;
    protected static List<Device> devices;
    protected static List<Device> csvDevices;
    protected static List<Device> switches;
    protected static List<VmsEntity> vmsList;

    public static Environments defaultEnv = Environments.stLab;
    public static String envConfigFile = "config/%ENV%/environment.ini";
    public static String devicesConfigFile = "config/%ENV%/devices.json";
    public static String csvDevicesConfigFile = "config/%ENV%/csvDevices.json";
    public static String switchesConfigFile = "config/%ENV%/switches.json";
    public static String vmsListConfigFile = "config/%ENV%/vmsList.json";
    public static String fwUpgradeConfigFile = "firmwares/fwUpgrade.json";
    public WDTools wdTools;
    public List<Error> errors;
    public List<String> requiredEvents;
    public String testName;
    public String testStatus;
    public Date testStarTime;
    public static String horizonEnvironment;
    public static String deviceVendor;
    public static String azureToken;

    //pages to init
    public LoginPage loginPage;
    public NavigationPane navPage;
    public OverviewPage overviewPage;
    public EnrollPage enrollPage;
    public GroupsPage groupsPage;
    public ManagePage managePage;
    public CompletedOpsPage completedOpsPage;
    public AuditLogPage auditLogPage;
    public DeviceCredentialsPage deviceCredentialsPage;
    public DeviceCredentialsPageSNMP deviceCredentialsPageSNMP;
    public HorizonConnectorsPage horizonConnectorsPage;
    public HorizonConnectorsLSPPage horizonConnectorsLSPPage;
    public InfraComponentsPage infraComponentsPage;
    public ManageBinariesPage manageBinariesPage;
    public SiteMgmtPage siteMgmtPage;
    public UpgradePathsPage upgradePathsPage;
    public RotatePasswordPane rotatePasswordPane;
    public FirmwareUpgradePage firmwareUpgradePage;
    public ManageDependenciesPane manageDependenciesPane;
    public RotateCertPane rotateCertPane;
    public OperationalStatusOverviewPage operationalStatusOverviewPage;
    public SitesViewPage sitesViewPage;
    public SmartAlertsPage smartAlertsPage;
    public GrafanaDeviceRealTimeMetricsPage grafanaDeviceRealTimeMetricsPage;
    public AvailableTasksHelper availableTasksHelper;
    public CsvOps csvOps;

    public KeycloakOps keycloakOps;
    public PermissionsPage permissionsPage;
    public ManagePermissionsPage managePermissionsPage;
    public PermissionsHelper permissionsHelper;
    public ConnectorRemoteUpgradeHelper connectorRemoteUpgradeHelper;



    //password
    private static boolean deleteCredentials = true;
    public static String FW_STORAGE_URL = "https://securithingspub.blob.core.windows.net/firmware-files/Device%20FW's/";

    public static boolean runBrowser;
    public static boolean doLogin;
    public static boolean doLogout;

    //DB
    private static String openSshToDBCommand;
    private static long openSshToDBCommandProcId = -1;
    private static long openSshToKeycloakCommandProcId = -1;
    public static String connectorVersion = "";
    //============================================================================
    //@BeforeSuite (alwaysRun = true)
    public static void startDbTunnel () throws Exception{
        logger.info("In before GROUPS - startDbTunnel");
        openSshToDBCommand = "ssh " + env.getSshTunnelMysql();
        //run separate process with ssh command - open tunnel to DB
        openSshToDBCommandProcId = CommonOps.launchProcess(openSshToDBCommand);
        Thread.sleep(1000);
        //verify process is up and running
        if (!CommonOps.isProcessRunning(openSshToDBCommandProcId)){
            logger.error("DB Tunnel failed to open !!!!");
        }
    }
    //============================================================================

    //============================================================================
    //@AfterSuite (alwaysRun = true)
    public static void stopDbTunnel () throws Exception{
        logger.info("In after GROUPS - stopDbTunnel");
        CommonOps.killProcessByPid(openSshToDBCommandProcId);
        Thread.sleep(1000);
        //verify process is down
        if (CommonOps.isProcessRunning(openSshToDBCommandProcId)){
            logger.error("DB Tunnel failed to close !!!!");
        }
    }
    //============================================================================
    public static void startKeycloakTunnel () throws Exception{
        logger.info("In before GROUPS - startKeycloakTunnel");
        //run separate process with ssh command - open tunnel to Keycloak
        openSshToKeycloakCommandProcId = CommonOps.launchProcess("ssh " + env.getSshTunnelKeycloak());
        Thread.sleep(1000);
        //verify process is up and running
        if (!CommonOps.isProcessRunning(openSshToKeycloakCommandProcId)){
            logger.error("Keycloak Tunnel failed to open !!!!");
        }
    }

    public static void stopKeycloakTunnel () throws Exception{
        logger.info("In after GROUPS - stopKeycloakTunnel");
        CommonOps.killProcessByPid(openSshToKeycloakCommandProcId);
        Thread.sleep(1000);
        //verify process is down
        if (CommonOps.isProcessRunning(openSshToKeycloakCommandProcId)){
            logger.error("DB Tunnel failed to close !!!!");
        }
    }
    //============================================================================
    @BeforeSuite(alwaysRun = true)
    public void beforeSuite () {
        initHtmlReports();
    }

    public static void initHtmlReports() {
        logger.info("BEFORE SUITE - create html reports");
        try {
            if (!Files.isDirectory(Paths.get(System.getProperty("user.dir") + "/htmlreports")))
                Files.createDirectories(Paths.get(System.getProperty("user.dir") + "/htmlreports"));
        } catch (IOException e) {
            System.err.println("Cannot create directories - " + e);
        }
        if (extent == null) {
            extent = new ExtentReports(System.getProperty("user.dir") + "/htmlreports/AutomationReport.html", true);
            extent.loadConfig(new File(System.getProperty("user.dir") + "/extentConfig.xml"));
        }
    }
    //============================================================================

    //============================================================================
    @BeforeClass(alwaysRun=true)
    public static void beforeClass() throws Exception{
        logger.info("In before Class !!");
        if (extent == null)
            initHtmlReports();
        runBrowser = true;
        doLogin = true;
        doLogout = true;
        //for DEBUG - set to true --------------------------- or set -DENV=test5 -Ddebug=true for example
        boolean debug = false;
        if (debug) {
            HashMap<String, String> envMap = new HashMap<>();
            //envMap.put("ENV", "facebook");
            //envMap.put("ENV", "stLab");
            //envMap.put("ENV", "staging");
            //envMap.put("ENV", "test5");
            envMap.put("ENV", "test5");
            //envMap.put("ENV", "production");
            CommonOps.setEnv(envMap);
            deleteCredentials = false;
        }
        //----------------------------------------
        //set azure secret token
        azureToken = System.getenv("azureToken");
        if (azureToken == null) {//todo - delete !!!
            azureToken = "sp=r&st=2022-07-25T12:01:48Z&se=2023-07-25T20:01:48Z&spr=https&sv=2021-06-08&sr=c&sig=30%2BvF93hfpojaN6ZexLy1dn1VlKbCH%2FrPGHST38eNnU%3D";
            logger.error("MUST SUPPLY AZURE TOKEN FOR FW UPGRADE TESTS TO WORK !");
            //System.exit(1);
        }
        logger.info("AZURE TOKEN === " + azureToken);
        //set vendor
        deviceVendor = System.getenv("device_vendor");
        //deviceVendor = "Hanwha"; //for debug
        logger.info("device Vendor from environment:: " + deviceVendor);
        if (deviceVendor == null || deviceVendor.equalsIgnoreCase("All_Automation_Devices")) {
            deviceVendor = "";
            logger.info("Will select all devices in List !!");
        }
        logger.info("------------------------------");
        logger.info("VENDOR TESTED:: " + deviceVendor);
        logger.info("------------------------------");

        setConfigFolder();//get environment to work on from external parameter ENV. If it does not exist, exit program
        envConfig();
        //get devices config from json
        devicesConfig();
        try {
            csvDevicesConfig();
            switchesConfig();
        }
        catch (Exception e) {
            logger.warn("NO CSV DEVICES FILE OR SWITCHES FILE FOR ENV: " + env.getHorizonUrl() + "\n" + e.getMessage());
        }
        //get vms config from json
        vmsConfig();
        //get passwords from jenkins provided file, from its credentials (file deleted after data collected)
        setPasswords();
        //unblock all devices just to be sure
        for (Device device: devices) {
            try {
                ConnectorOps.unBlockIp(device.getIpAddress(), env);
            }
            catch (Exception e) {
                logger.info("failed to unblock ip: " + device.getIpAddress() + ", probably because not blocked: " + e.getMessage());
            }
        }
    }
    //============================================================================

    //============================================================================
    private static void setPasswords () throws Exception{
        logger.info("");
        //read all passwords from file saved on base folder - its name is the env name
        HashMap<String, String> passwords = CommonOps.HashMapFromTextFile(horizonEnvironment + ".txt", "=");
//        logger.info("map :::");
//        logger.info(Arrays.asList(passwords));
//        logger.info("map :::");
//        logger.info(Collections.singletonList(passwords));
        //set environment passwords
        String templatePwd = env.getHorizonPassword();
        String horizonPwd = passwords.get(templatePwd);
        env.setHorizonPassword(horizonPwd);

        //set all devices passwords
        for (int i = 0; i < devices.size(); i++) {
            Device device = devices.get(i);
            templatePwd = device.getDevicePassword();
            String devicePwd = passwords.get(templatePwd);
            device.setDevicePassword(devicePwd);
        }
        //get all vms passwords
        for (int i = 0; i < vmsList.size(); i++) {
            VmsEntity vms = vmsList.get(i);
            templatePwd = vms.getPassword();
            String vmsPwd = passwords.get(templatePwd);
            vms.setPassword(vmsPwd);
        }
        //set db password
        templatePwd = env.getDbPassword();
        String dbPwd = passwords.get(templatePwd);
        env.setDbPassword(dbPwd);
        //set email password
        templatePwd = env.getEmailPwd();
        String emailPwd = passwords.get(templatePwd);
        env.setEmailPwd(emailPwd);

        //Remove passwords file
        if (deleteCredentials) {
            try {
                //FileUtils.forceDelete(new File(horizonEnvironment + ".txt"));
            }
            catch (Exception e) {}
        }

        //extent.addSystemInfo("Tested Connector Version",  DbHelper.getConnectorVersion(env));
    }
    //============================================================================

    //============================================================================
    public static void setConfigFolder() {
        logger.info("");
        //check for environment variable - env
        horizonEnvironment = System.getenv("ENV") !=null ? System.getenv("ENV") : System.getProperty("ENV");
        if (horizonEnvironment == null) {
            logger.error("No environment supplied, Exiting automation");
            System.exit(1);
        }
        else {
            logger.info("Running on Horizon environment: " + horizonEnvironment);
        }
        envConfigFile = envConfigFile.replace("%ENV%", horizonEnvironment);
        devicesConfigFile = devicesConfigFile.replace("%ENV%", horizonEnvironment);
        csvDevicesConfigFile = csvDevicesConfigFile.replace("%ENV%", horizonEnvironment);
        switchesConfigFile = switchesConfigFile.replace("%ENV%", horizonEnvironment);
        vmsListConfigFile = vmsListConfigFile.replace("%ENV%", horizonEnvironment);
        fwUpgradeConfigFile = fwUpgradeConfigFile.replace("%ENV%", horizonEnvironment);
    }
    //============================================================================

    //============================================================================
    @BeforeMethod(alwaysRun = true)
    public void beforeTestMethod(Method method) throws Exception  {
        logger.info("");
        if (extentLogger == null)
            initHtmlReports();
        addConnectorVersionToHtmlReport();
        extentLogger = extent.startTest(method.getName());
        CommonOps.killChromeDriver();
        testStarTime = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(testStarTime);
        cal.add(Calendar.SECOND, -40);
        testStarTime = cal.getTime();
        logger.info("testStarTime (-30 seconds) == " + testStarTime.toString());
        logger.info("========================================");
        logger.info("============== TEST SETUP START =============");
        logger.info("========================================\n");

        //init errors
        errors = new ArrayList<>();
        requiredEvents = new ArrayList<>();

        //login
        try {
            if (runBrowser) {
                //init driver
                wdTools = new WDTools();
                //init pages
                initPages();
            } else {
                doLogin = false;
                doLogout = false;
            }
            if (doLogin)
                loginPage.login(env.getHorizonUrl(), env.getHorizonUser(), env.getHorizonPassword());
        }
        catch (Exception e) {
            errors.add(new Error("Login failed", "Blank page again"));
        }
        testName = method.getName();
        logger.info("========================================");
        logger.info("===== TEST START: " + testName + " ====");
        logger.info("========================================\n");
    }
    //============================================================================

    //============================================================================
    public void verifyErrors () {
        logger.info("");
        StringBuilder allErrors = new StringBuilder();
        int i = 1;
        if (errors.size() > 0) {
            for (Error error: errors) {
                allErrors.append("\n========== Error: " + i + " ==========\n");
                i++;
                allErrors.append(error.subject + ": " + error.description + "\n");
                if (error.expected != null && error.actual != null) {
                    allErrors.append("Expected: " + error.expected + "\n");
                    allErrors.append("Actual  : " + error.actual + "\n");
                }
                if(error.ex != null) {
                    allErrors.append(error.ex + "\n");
                }
                if (error.expectedObj != null && error.actualObj != null) {
                    allErrors.append("EXPECTED:\n");
                    allErrors.append(error.expectedObj.toString() + "\n");
                    allErrors.append("ACTUAL:\n");
                    allErrors.append(error.actualObj.toString() + "\n");
                }
            }
            //logger.error(allErrors.toString());
            testStatus = "FAILED";
            if (errors.size() > 0) {
                Assert.fail(allErrors.toString());
            }
        }
    }
    //============================================================================

    //============================================================================
    @AfterMethod(alwaysRun = true)
    public void afterTestMethod(Method method, ITestResult result) throws Exception{
        logger.info("");
        result.getName();
        if (result.isSuccess()) {
            testStatus = "PASSED";
            extentLogger.log(LogStatus.PASS, "Test Case Passed: " + result.getName());
            extent.flush();
            extent.endTest(extentLogger);
        } else if (result.getStatus() == ITestResult.SKIP) {
            extentLogger.log(LogStatus.SKIP, "Test Case Skipped: " + result.getName());
            extent.flush();
            extent.endTest(extentLogger);
        }
        else {
            testStatus = "FAILED";
            try {
                String screenshotPath = screenCapture();
                extentLogger.log(LogStatus.FAIL, result.getThrowable().toString());
                extentLogger.log(LogStatus.FAIL, extentLogger.addScreenCapture(screenshotPath));
            } catch (Exception e) {
                //ignore
            }
            extent.flush();
            extent.endTest(extentLogger);
        }

        try {
            if (doLogout)
                loginPage.logout(testName);
        } catch (Exception e) {
            if (doLogout)
                extentLogger.log(LogStatus.FAIL, "Logout failed");
        }
        logger.info("============================================================");
        logger.info("===== TEST END  : " + testName + " " + testStatus + " =====");
        logger.info("============================================================\n");
        try
        {
            if (runBrowser) {
                wdTools.driver.close();
                CommonOps.killChromeDriver();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
    //============================================================================
    @AfterSuite(alwaysRun = true)
    private void endTestReport() throws Exception {
        try {
            extent.close();
        } catch (Exception e) {
            //ignore
            logger.info(e.getMessage()!=null && !e.getMessage().isEmpty() ? e.getMessage() : "HtmlReport Closed");
        }
    }
    //============================================================================
    private void initPages() throws Exception{
        logger.info("");
        //init all pages
        loginPage = new LoginPage(wdTools, errors);
        overviewPage = new OverviewPage(wdTools, errors);
        //device management
        navPage = new NavigationPane(wdTools, errors);
        enrollPage = new EnrollPage(wdTools, errors, requiredEvents);
        managePage = new ManagePage(wdTools, errors, requiredEvents);
        groupsPage = new GroupsPage(wdTools, errors, requiredEvents);
        completedOpsPage = new CompletedOpsPage(wdTools, errors);
        rotatePasswordPane = new RotatePasswordPane(wdTools, errors, requiredEvents);
        rotateCertPane = new RotateCertPane(wdTools, errors, requiredEvents);
        firmwareUpgradePage = new FirmwareUpgradePage(wdTools, errors, requiredEvents);
//        //admin
//        auditLogPage = new AuditLogPage(wdTools, errors);
        deviceCredentialsPage = new DeviceCredentialsPage(wdTools, errors);
        deviceCredentialsPageSNMP = new DeviceCredentialsPageSNMP(wdTools, errors);
        horizonConnectorsPage = new HorizonConnectorsPage(wdTools, errors);
        horizonConnectorsLSPPage = new HorizonConnectorsLSPPage(wdTools, errors);
        infraComponentsPage = new InfraComponentsPage(wdTools, errors);
//        manageBinariesPage = new ManageBinariesPage(wdTools, errors);
        siteMgmtPage = new SiteMgmtPage(wdTools, errors);
        upgradePathsPage = new UpgradePathsPage(wdTools, errors);
        manageDependenciesPane = new ManageDependenciesPane(wdTools, errors, requiredEvents);
        manageBinariesPage = new ManageBinariesPage(wdTools, errors);
        operationalStatusOverviewPage = new OperationalStatusOverviewPage(wdTools, errors);
        sitesViewPage = new SitesViewPage(wdTools, errors);
        smartAlertsPage = new SmartAlertsPage(wdTools, errors);
        grafanaDeviceRealTimeMetricsPage = new GrafanaDeviceRealTimeMetricsPage(wdTools, errors);
        availableTasksHelper = new AvailableTasksHelper(wdTools, errors, requiredEvents);
        csvOps = new CsvOps(wdTools, errors);
        keycloakOps = new KeycloakOps(env);
        permissionsPage = new PermissionsPage(wdTools);
        managePermissionsPage = new ManagePermissionsPage(wdTools);
        permissionsHelper = new PermissionsHelper(this);
        connectorRemoteUpgradeHelper = new ConnectorRemoteUpgradeHelper(env, errors);

    }
    //============================================================================
    public static void setRunBrowser(boolean open) throws Exception {
        runBrowser = open;
    }
    public static void setDoLogin(boolean todo) throws Exception {
        doLogin = todo;
    }

    public static void setDoLogout(boolean todo) throws Exception {
        doLogout = todo;
    }

    public static void info(String msg) throws Exception {
        logger.info(msg);
        extentLogger.log(LogStatus.INFO, msg);
    }

    public static void fail(String msg) throws Exception {
        logger.error(msg);
        extentLogger.log(LogStatus.FAIL, msg);
        throw new Exception(msg);
    }
    //============================================================================
    public static void csvDevicesConfig() throws Exception{
        logger.info("");
        csvDevices = new ArrayList<>();
        //read from csv json - name and password
        JSONObject json = new JSONObject(CommonOps.readWholeFile(csvDevicesConfigFile));
        JSONArray deviceArr = json.getJSONArray("devices");
        for (Object dev: deviceArr) {
            Device d = new Device(((JSONObject)dev).getString("vendor"),((JSONObject)dev).getString("macAddress"),
                    ((JSONObject)dev).getString("deviceUser"),
                    ((JSONObject)dev).getString("devicePassword"),
                    ((JSONObject)dev).getString("credentialName"),
                    ((JSONObject)dev).getString("model"),
                    ((JSONObject)dev).getString("deviceSource"));

            d.setSupportedActions(((JSONObject)dev).getJSONObject("actionsSupported").getBoolean("rotatePassword"),
                    ((JSONObject)dev).getJSONObject("actionsSupported").getBoolean("upgradeFirmware"),
                    ((JSONObject)dev).getJSONObject("actionsSupported").getBoolean("restartDevice"),
                    ((JSONObject)dev).getJSONObject("actionsSupported").getBoolean("rotateCertificate"));
            d.setHost(((JSONObject) dev).getString("host"));
            d.setPort(((JSONObject) dev).getString("port"));
            d.setDeviceName(((JSONObject) dev).getString("deviceName"));
            csvDevices.add(d);
        }
        printDevicesInList(csvDevices, "CSV DEVICE");
    }
    //============================================================================

    public static void switchesConfig() throws Exception{
        logger.info("");
        switches = new ArrayList<>();
        //read from csv json - name and password
        JSONObject json = new JSONObject(CommonOps.readWholeFile(switchesConfigFile));
        JSONArray deviceArr = json.getJSONArray("devices");
        for (Object dev: deviceArr) {
            Device d = new Device();
            d.setHost(((JSONObject) dev).getString("host"));
            d.setPort(((JSONObject) dev).getString("port"));
            d.setDeviceName(((JSONObject) dev).getString("deviceName"));
            d.setVendor(((JSONObject) dev).getString("vendor"));
            d.setCredTemplate(((JSONObject) dev).getString("credentialName"));
            d.setMacAddress(((JSONObject)dev).getString("macAddress"));
            d.setHorizonDeviceId(((JSONObject)dev).getString("macAddress"));
            d.setModel(((JSONObject)dev).getString("model"));
            switches.add(d);
        }
        printDevicesInList(switches, "switch");
    }
    //============================================================================
    private static void devicesConfig() throws Exception{
        logger.info("");
        devices = new ArrayList<>();
        //read from json - name and password
        JSONObject json = new JSONObject(CommonOps.readWholeFile(devicesConfigFile));
        JSONArray deviceArr = json.getJSONArray("devices");
        for (Object dev: deviceArr) {
            Device d = new Device(((JSONObject)dev).getString("vendor"),((JSONObject)dev).getString("macAddress"),
                    ((JSONObject)dev).getString("deviceUser"),
                    ((JSONObject)dev).getString("devicePassword"),
                    ((JSONObject)dev).getString("credentialName"),
                    ((JSONObject)dev).getString("model"),
                    ((JSONObject)dev).getString("deviceSource"));

            d.setSupportedActions(((JSONObject)dev).getJSONObject("actionsSupported").getBoolean("rotatePassword"),
                    ((JSONObject)dev).getJSONObject("actionsSupported").getBoolean("upgradeFirmware"),
                    ((JSONObject)dev).getJSONObject("actionsSupported").getBoolean("restartDevice"),
                    ((JSONObject)dev).getJSONObject("actionsSupported").getBoolean("rotateCertificate"));
            devices.add(d);
        }
        printDevicesInList(devices, "device");
    }
    //============================================================================

    //============================================================================
    private static void printDevicesInList(List<Device> devices, String type) {
        logger.info(type + "s IN LIST: " + devices.size());
        for (Device dev: devices) {
            logger.info(type + " IN LIST: " + dev.getMacAddress());
        }
    }

    private static void envConfig () throws Exception{
        logger.info("");
        //read from ini
        HashMap<String,String> envMap = CommonOps.readSectionFromIni(envConfigFile,"env_details");
        env = new Environment(envMap.get("horizonUrl"), envMap.get("horizonUserName"), envMap.get("password"), envMap.get("connectorVm"), envMap.get("connectorUser"));
        // extended report
        extent.addSystemInfo("Tested Url", envMap.get("horizonUrl"));
        extent.addSystemInfo("Tested User", envMap.get("horizonUserName"));
        extent.addSystemInfo("Tested Connector Vm", envMap.get("connectorVm"));
        extent.addSystemInfo("Tested Connector User", envMap.get("connectorUser"));
        //
        try {
            env.setDbHost(envMap.get("dbHost"));
            env.setDbPort(envMap.get("dbPort"));
            env.setDbName(envMap.get("dbName"));
            env.setDbUser(envMap.get("dbUser"));
            env.setDbPassword(envMap.get("dbPassword"));
            env.setEmailAddress(envMap.get("emailAddress"));
            env.setEmailPwd(envMap.get("emailPwd"));
            env.setSshTunnelMysql("sshTunnelMysql");
            env.setSshTunnelKeycloak(envMap.get("sshTunnelKeycloak"));
            env.setSshMigrationHost(envMap.get("hostMigration"));
            env.setFileApiLocalPort(envMap.get("fileApiPort")); //8888
            env.setFileApiUploadAmount(System.getProperty("fileApiUploadAmount")!=null ? System.getProperty("fileApiUploadAmount") : envMap.get("fileApiUploadAmount"));
            env.setFileApiMigrationTimeoutMins(System.getProperty("fileApiMigrationTimeoutMins")!=null ? System.getProperty("fileApiMigrationTimeoutMins") : envMap.get("fileApiMigrationTimeoutMins"));
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("Failed to set DB environment details");
        }
    }
    //============================================================================
    public static Environment getEnvConfig () throws Exception{
        return env;
    }
    //============================================================================
    private static void vmsConfig() throws Exception{
        logger.info("");
        vmsList = new ArrayList<>();
        String json = CommonOps.readWholeFile(vmsListConfigFile);
        JSONArray vmss = new JSONObject(json).getJSONArray("vms list");
        for (Object jsonObject:vmss) {
            ObjectMapper mapper = new ObjectMapper();
            VmsEntity vmsEntity = mapper.readValue(jsonObject.toString(), VmsEntity.class);
            //vmsEntity.setPassword(vmsPassword);
            vmsList.add(vmsEntity);
        }
    }
    //============================================================================

    //============================================================================
    public Device getDeviceByPartialName (String partialDeviceName) throws Exception{
        logger.info("");
        for (Device device: devices) {
            if (device.getDeviceName().toLowerCase().contains(partialDeviceName.toLowerCase()))
                return device;
        }
        throw new Exception("Device name pattern does not exist in devices: "  + partialDeviceName);
    }
    //============================================================================

    //============================================================================
    public Device getRandomDevice () throws Exception{
        logger.info("");
        int max = devices.size()-1;
        int min = 0;
        Random random = new Random();
        int value = random.nextInt(max + min) + min;
        return devices.get(value);
    }
    //============================================================================

    //============================================================================
    public List<Device> getFewRandomDevices (int numberOfDevices) throws Exception{
        logger.info("");
        Random random = new Random();
        int num = random.nextInt(devices.size());
        List<Integer> ints = new ArrayList<>();
        ints.add(num);
        MyTimer timer = new MyTimer(5);
        while(ints.size() < numberOfDevices && timer.timeLeft(true, "getting devices")) {
            num = random.nextInt(devices.size());
            if (!ints.contains(num)) {
                ints.add(num);
            }
            timer.tick(100);
        }
        List<Device> devicesLocal = new ArrayList<>();
        for (int i: ints) {
            devicesLocal.add(devices.get(i));
        }
        return devicesLocal;
    }
    //============================================================================

    //============================================================================
    public static Device getRandomDevice (List<Device> devices) throws Exception{
        logger.info("");
        if (devices.size() == 1) {
            return devices.get(0);
        }
        int max = devices.size()-1;
        int min = 0;
        Random random = new Random();
        int value = random.nextInt(max + min) + min;
        return devices.get(value);
    }
    //============================================================================

    //============================================================================
    public List<Device> getRandomDeviceInList () throws Exception{
        logger.info("");
        int max = devices.size()-1;
        int min = 0;
        Random random = new Random();
        int value = random.nextInt(max + min) + min;
        List<Device> devicesLocal = new ArrayList<>();
        devicesLocal.add(devices.get(value));
        return devicesLocal;
    }
    //============================================================================

    //============================================================================
    public List<Device> getDevicesByPartialName (String partialDeviceName) throws Exception{
        logger.info("");
        List<Device> requiredDevices = new ArrayList<>();
        if (devices.size() == 0) {
            throw new Exception("No devices in device list");
        }
        for (Device device: devices) {
            if (device.getDeviceName().toLowerCase().contains(partialDeviceName.toLowerCase()))
                requiredDevices.add(device);
        }
        if (requiredDevices.size() == 0) {
            throw new Exception("Device name pattern does not exist in devices: "  + partialDeviceName);
        }
        return requiredDevices;
    }
    //============================================================================

    //============================================================================
    public List<Device> getDevicesByAction(DeviceActionsEnum action) throws Exception{
        logger.info("");
        List<Device> requiredDevices = new ArrayList<>();
        for (Device device: devices) {
            if (action == DeviceActionsEnum.ROTATE_PASSWORD) {
                if (device.isRotatePasswordAction()) {
                    requiredDevices.add(device);
                }
            }
            else if (action == DeviceActionsEnum.ROTATE_CERT) {
                if (device.isRotateCertificateAction()) {
                    requiredDevices.add(device);
                }
            }
            else if (action == DeviceActionsEnum.RESTART_DEVICE) {
                if (device.isRestartDeviceAction()) {
                    requiredDevices.add(device);
                }
            }
            else if (action == DeviceActionsEnum.UPGRADE_FW) {
                if (device.isUpgradeFirmwareAction()) {
                    requiredDevices.add(device);
                }
            }
            else if (action == DeviceActionsEnum.ASSIGN_DEVICE_TYPE) {
                requiredDevices.add(device);
            }
        }
        if (requiredDevices.size() == 0) {
            throw new Exception("No devices selected for this test. Required action was: "  + action.toString());
        }
        else {
            logger.info("===== Devices selected for test (" + requiredDevices.size() + ")::");
            for (Device dev: requiredDevices) {
                logger.info(dev.getMacAddress());
            }
            logger.info("=====================================================");
        }
        return requiredDevices;
    }
    //============================================================================

    //============================================================================
    public static VmsEntity getVmsByPartialName (String partialVmsName) throws Exception{
        logger.info("");
        for (VmsEntity vmsEntity: vmsList) {
            if (vmsEntity.getName().toLowerCase().contains(partialVmsName.toLowerCase()))
                return vmsEntity;
        }
        throw new Exception("Device name pattern does not exist in devices: "  + partialVmsName);
    }
    //============================================================================

    //============================================================================
    public static void startRdp () throws Exception{
        logger.info("");
        stopRdp();
        try {
            CommonOps.runProcess("jenkins/startRdp.bat");
            logger.info("Opened RDP session to: ");
        }
        catch (Exception e) {
            logger.error("FAILED to open RDP session to: ");
            e.printStackTrace();
        }
    }
    //============================================================================

    //============================================================================
    public static void stopRdp () throws Exception{
        logger.info("");
        try {
            CommonOps.runProcess("jenkins/stopRdp.bat");
            logger.info("closed all RDP sessions");
        }
        catch (Exception e) {
            logger.error("FAILED to close all RDP sessions");
            e.printStackTrace();
        }
    }
    //============================================================================


    //============================================================================
    public String screenCapture() throws IOException {
        if (wdTools.driver==null)
            return "no open browser";
        System.setProperty("org.uncommons.reportng.escape-output", "false");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formater = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
        File scrFile = ((TakesScreenshot) wdTools.driver).getScreenshotAs(OutputType.FILE);
        String screenshotsFolder = System.getProperty("user.dir") + "/htmlreports";
        String fileName = formater.format(calendar.getTime()) + ".png";
        String screenshotPath = screenshotsFolder  + "/" + fileName;
        String screenshotRelativePath = "htmlreports/" + fileName;
        File screenshotName = new File(screenshotPath);
        //add screenshot to results by copying the file
        FileUtils.copyFile(scrFile, screenshotName);

        return fileName;
    }

    //============================================================================

    public static Environment getEnv() throws Exception {
        return env;
    }

    public static List<Device> getDevices() throws Exception {
        return devices;
    }

    //////////////////// getConnectorVersion from Db //////////////////////////////////////
    public class HtmlReport extends ExtentReports {

        public HtmlReport() {
            super(System.getProperty("user.dir") + "/extentConfig.xml");
        }

        public Map<String, String> getSysInfo() throws Exception {
            return super.getSystemInfoMap();
        }
    }

    public void addConnectorVersionToHtmlReport() throws Exception {
        if (new HtmlReport().getSysInfo().get("Tested Connector Version")==null
                && !env.getHorizonUrl().toLowerCase().contains("stlab.horizon.securithings.com")
        ) {
            try {
                startDbTunnel();
                connectorVersion = DbHelper.getConnectorVersion(env);
                extent.addSystemInfo("Tested Connector Version", connectorVersion);
            } catch (Exception e) {
                //ignore
            } finally {
                try {
                    stopDbTunnel();
                } catch (Exception e) {
                    //ignore
                }
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////


}
