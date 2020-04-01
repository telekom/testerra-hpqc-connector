/* 
 * Created on 21.02.2013
 * 
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.qcrest.clients;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.qcrest.constants.ErrorMessages;
import eu.tsystems.mms.tic.testframework.qcrest.generated.Entity;
import eu.tsystems.mms.tic.testframework.qcrest.utils.MarshallingUtils;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.Attachment;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.QcTest;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.RunStep;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestPlanFolder;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestPlanTest;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestRun;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestSet;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestSetFolder;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestSetTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * JavaClient for QC REST API.
 *
 * @author sepr
 */

/**
 * @author sepr
 */
public final class QcRestClient {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(QcRestClient.class);

    /**
     * Add Attachments to a Test Run.
     *
     * @param runId Id of TestRun.
     * @param attachments Attachments to add.
     * @throws IOException Exception during communication with REST Service.
     */
    public static void addAttachmentsToTestRun(final int runId, final List<Attachment> attachments) throws IOException {
        pAddAttachmentsToTestRun(runId, attachments);
    }

    /**
     * Add Attachments to a Test Run.
     *
     * @param runId Id of TestRun.
     * @param attachments Attachments to add.
     * @throws IOException Exception during communication with REST Service.
     */
    private static void pAddAttachmentsToTestRun(final int runId, final List<Attachment> attachments)
            throws IOException {
        LOGGER.trace("add " + attachments.size() + "Attachments To TestRun" + runId);
        final RestConnector connector = RestConnector.getInstance();
        for (Attachment attachment : attachments) {
            final StringBuilder restUrl = new StringBuilder();
            restUrl.append(connector.buildEntityCollectionUrl("run"));
            restUrl.append("/").append(runId).append("/attachments");
            // Existenz Prüfung ausschalten. So lang dauerts auch nicht wenn ein Bild mal doppelt hochgeladen wird.

            // final StringBuilder existingUrl = new StringBuilder(restUrl.toString());
            // existingUrl.append("/").append(attachment.getName().hashCode());
            // Entity existingAtt;
            // try {
            // existingAtt = connector.getEntity(existingUrl.toString(), null);
            // } catch (IOException e) {
            // // Attachment doesn't exist, thats what we wanted to check.
            // LOGGER.info("No attachment (ignore last warning) with this name found. Upload attachment now.");
            // existingAtt = null;
            // }
            // if (existingAtt == null) {
            // new (POST)
            final Map<String, String> headers = new HashMap<String, String>();
            if (attachment.getRefType().equals(Attachment.Type.URL.getValue())
                    && attachment.getName().startsWith("http:")) {
                headers.put("Slug", "UrlAttachmentUrl.url");
            } else {
                headers.put("Slug", attachment.getName());
            }
            headers.put("Content-Type", "application/octet-stream");
            byte[] content = attachment.getContent();
            if (content == null) {
                content = ("[InternetShortcut]" + System.getProperty("line.separator") + "URL="
                        + attachment.getName()).getBytes();
            }
            final Response response = connector.httpPost(restUrl.toString(), content, headers);
            if (response.getStatusCode() == 201) {
                LOGGER.info("Added attachment " + response.getResponseHeaders().get("Location"));
            } else {
                throw new IOException("Error adding attachment:", response.getFailure());
            }
            // }
        }
    }

    /**
     * Adds a Test Run to the given TestSetTest.
     *
     * @param testSetTest The TestSetTest of the new Run.
     * @param testRun Test Run to save.
     * @return Id of run or 0 if run could not be added.
     * @throws IOException Exception during communication with REST Service.
     */
    public static int addTestRun(final TestSetTest testSetTest, final TestRun testRun) throws IOException {
        return pAddTestRun(testSetTest, testRun);
    }

    /**
     * Adds a Test Run to the given TestSetTest.
     *
     * @param testSetTest The TestSetTest of the new Run.
     * @param testRun Test Run to save.
     * @return Id of run or 0 if run could not be added.
     * @throws IOException Exception during communication with REST Service.
     */
    private static int pAddTestRun(final TestSetTest testSetTest, final TestRun testRun) throws IOException {
        LOGGER.debug("addTestRun:");
        final RestConnector connector = RestConnector.getInstance();
        if (testSetTest.getId() == 0) {
            throw new TesterraRuntimeException(
                    "Id of given TestSetTest is 0. Getting it from RestService would avoid this.");
        }
        if (testRun.getParentId() == 0) {
            testRun.setParentId(testSetTest.getId());
        }
        if (testRun.getCycleId() == 0) {
            testRun.setCycleId(testSetTest.getCycleId());
        }
        if (testRun.getTestId() == 0) {
            testRun.setTestId(testSetTest.getTestId());
        }
        if (testRun.getExecutionDate() == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            testRun.setExecutionDate(sdf.format(new Date()));
        }
        if (testRun.getExecutionTime() == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            testRun.setExecutionTime(sdf.format(new Date()));
        }
        if (testRun.getOwner() == null) {
            testRun.setOwner(connector.getLoginData().getUser());
        }

        LOGGER.debug("Test run to add: " + testRun.toString());

        /*
         * Set QC Fields:
         */
        if (testRun.getFieldValueByName("subtype-id") == null) {
            testRun.setFieldValue("subtype-id", "hp.qc.run.MANUAL");
        }

        int idReturned = addEntity(testRun.getEntity());

        if (idReturned != 0) {
            LOGGER.debug("Set TestSetTest properties for run execution");
            updateTestSetTest(testSetTest, testRun);
            LOGGER.debug("Update RunSteps' Status'");
            updateRunStepStatuses(idReturned, testRun.getStatus());
            LOGGER.debug("Upload Attachments to TestRun " + testRun);
            if (testRun.isHasAttachmentWr()) {
                try {
                    addAttachmentsToTestRun(idReturned, testRun.getAttachments());
                } catch (IOException io) {
                    LOGGER.error("Error adding attachments to testrun {}", idReturned, io);
                }
            }
        }
        return idReturned;
    }

    /**
     * Set Status of TestSteps to the one of the TestRun.
     *
     * @param runId Id of TestRun to get Steps from.
     * @param status Status to set.
     */
    private static void updateRunStepStatuses(int runId, String status) {
        final RestConnector connector = RestConnector.getInstance();
        final StringBuilder restUrl = new StringBuilder();
        restUrl.append(connector.buildEntityCollectionUrl("run"));
        restUrl.append("/").append(runId).append("/run-steps");
        List<Entity> steps;
        try {
            steps = connector.getEntities(restUrl.toString(), null);
        } catch (IOException e) {
            LOGGER.error("Error getting List of Run Steps for Run " + runId, e);
            return;
        }
        final RunStep change = new RunStep();
        if (status != null) {
            change.setStatus(status);
        } else {
            return;
        }
        for (Entity entity : steps) {
            final RunStep step = new RunStep(entity);
            if (step.getId() == 0) {
                continue;
            }
            final StringBuilder putUrl = new StringBuilder(restUrl.toString());
            putUrl.append("/").append(step.getId());
            try {
                final Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/xml");
                connector.httpPut(putUrl.toString(), MarshallingUtils.unmarshal(Entity.class, change.getEntity())
                        .getBytes(), headers);
            } catch (IOException e) {
                LOGGER.error("Error updating RunStep", e);
            } catch (JAXBException e) {
                LOGGER.error("Error parsing RunStep Object to xml.", e);
            }
        }
    }

    /**
     * Gets all TestSets.
     *
     * @return A List containing the TestSets.
     * @throws IOException Exception during communication with REST Service.
     */
    public static List<TestSet> getAllTestSets() throws IOException {
        return pGetAllTestSets();
    }

    /**
     * Gets all TestSets.
     *
     * @return A List containing the TestSets.
     * @throws IOException Exception during communication with REST Service.
     */
    private static List<TestSet> pGetAllTestSets() throws IOException {
        LOGGER.debug("getAllTestSets");
        final List<TestSet> testSets = new LinkedList<TestSet>();

        final RestConnector connector = RestConnector.getInstance();
        final String requestUrl = connector.buildEntityCollectionUrl("test-set");
        final List<Entity> entities = connector.getEntities(requestUrl, null);
        if (entities != null) {
            for (Entity folder : entities) {
                testSets.add(new TestSet(folder));
            }
        }
        return testSets;
    }

    /**
     * Gets a Test by its Id.
     *
     * @param id Id of Test.
     * @return Test object.
     * @throws IOException Exception during communication with REST Service.
     */
    public static QcTest getTestById(final int id) throws IOException {
        return pGetTestById(id);
    }

    /**
     * Gets a Test by its Id.
     *
     * @param id Id of Test.
     * @return Test object.
     * @throws IOException Exception during communication with REST Service.
     */
    private static QcTest pGetTestById(final int id) throws IOException {
        LOGGER.trace("getTestById " + id);
        final RestConnector connector = RestConnector.getInstance();
        final String restUrl = connector.buildEntityCollectionUrl("test") + "/" + id;
        final Entity entity = connector.getEntity(restUrl, null);
        return new QcTest(entity);
    }

    /**
     * Get all Runs of a TestSetTest.
     *
     * @param testSetTest The TestSetTest to get the Runs from.
     * @return A list containing the TestRuns.
     * @throws IOException Exception during communication with REST Service.
     */
    public static List<TestRun> getTestRuns(final TestSetTest testSetTest) throws IOException {
        return pGetTestRuns(testSetTest, -1);
    }

    /**
     * Get x latest Runs of a TestSetTest (faster than getting all).
     *
     * @param testSetTest The TestSetTest to get the Runs from.
     * @param count Max number of runs to get.
     * @return A list containing the TestRuns.
     * @throws IOException Exception during communication with REST Service.
     */
    public static List<TestRun> getXTestRuns(final TestSetTest testSetTest, final int count) throws IOException {
        return pGetTestRuns(testSetTest, count);
    }

    /**
     * Get all Runs of a TestSetTest.
     *
     * @param testSetTest The TestSetTest to get the Runs from.
     * @param count Maximum of runs to be returned
     * @return A list containing the TestRuns.
     * @throws IOException Exception during communication with REST Service.
     */
    private static List<TestRun> pGetTestRuns(final TestSetTest testSetTest, final int count) throws IOException {
        LOGGER.debug("getTestRuns of TestSetTest " + testSetTest);
        final String pageSizeQuery;
        if (count != -1) {
            pageSizeQuery = "&page-size=" + count;
        } else {
            pageSizeQuery = "";
        }
        if (testSetTest.getId() == 0) {
            throw new TesterraRuntimeException("Id of given TestSetTest is 0. Can't get TestRuns.");
        }
        final List<TestRun> testRuns = new LinkedList<TestRun>();
        final TestSet testSet = testSetTest.getTestSet();
        final QcTest test = testSetTest.getTest();

        final RestConnector connector = RestConnector.getInstance();
        final String restUrl = connector.buildEntityCollectionUrl("run");
        final String queryUrl = "query={cycle-id[" + testSet.getId() + "];test.name['" + test.getName() + "']}"
                + pageSizeQuery;
        final List<Entity> entities = connector.getEntities(restUrl, queryUrl);

        for (Entity entity : entities) {
            final TestRun run = new TestRun(entity);
            testRuns.add(run);
            LOGGER.debug("Got " + run);
        }
        return testRuns;
    }

    /**
     * Get a TestSet by its name and TestSetFolder path.
     *
     * @param testSetName The name of the TestSet.
     * @param tsfPath The path to the TestSetFolder containing the TestSet.
     * @return The TestSet object.
     * @throws IOException Exception during communication with REST Service.
     */
    public static TestSet getTestSet(final String testSetName, final String tsfPath) throws IOException {
        LOGGER.debug("getTestSet " + testSetName + " of " + tsfPath);
        final TestSetFolder folder = QcRestClient.getTestSetFolder(tsfPath);
        return getTestSet(testSetName, folder);
    }

    /**
     * Get a TestSet with the given Name of the given TestSetFolder.
     *
     * @param testSetName The name of the testset.
     * @param testSetFolder The folder containing the testset.
     * @return The TestSet object.
     * @throws IOException Exception during communication with REST Service.
     */
    public static TestSet getTestSet(final String testSetName, final TestSetFolder testSetFolder) throws IOException {
        return pGetTestSet(testSetName, testSetFolder);
    }

    /**
     * Get a TestSet with the given Name of the given TestSetFolder.
     *
     * @param testSetName The name of the testset.
     * @param testSetFolder The folder containing the testset.
     * @return The TestSet object.
     * @throws IOException Exception during communication with REST Service.
     */
    private static TestSet pGetTestSet(final String testSetName, final TestSetFolder testSetFolder) throws IOException {
        LOGGER.debug("getTestSet " + testSetName + " of " + testSetFolder);
        TestSet testSet = null;
        if (testSetFolder == null) {
            throw new TesterraRuntimeException(ErrorMessages.testSetNotFound(testSetName));
        }
        final RestConnector connector = RestConnector.getInstance();
        if (testSetFolder.getId() == 0) {
            throw new TesterraRuntimeException(
                    "Id of testSetFolder must be set. Get it from QC, don't create it yourself!");
        }
        final String queryUrl = "query={parent-id[" + testSetFolder.getId() + "];name['" + testSetName + "']}";
        final String requestUrl = connector.buildEntityCollectionUrl("test-set");
        final List<Entity> entities;
        entities = connector.getEntities(requestUrl, queryUrl);
        if (entities != null && !entities.isEmpty()) {
            testSet = new TestSet(entities.get(0));
        } else {
            LOGGER.error("TestSet " + testSetName + "could not be found.");
        }
        return testSet;
    }

    /**
     * Get a TestSet by its id.
     *
     * @param id Id of TestSet.
     * @return TestSet object.
     * @throws IOException Exception during communication with REST Service.
     */
    public static TestSet getTestSetById(final int id) throws IOException {
        return pGetTestSetById(id);
    }

    /**
     * Get a TestSet by its id.
     *
     * @param id Id of TestSet.
     * @return TestSet object.
     * @throws IOException Exception during communication with REST Service.
     */
    private static TestSet pGetTestSetById(final int id) throws IOException {
        LOGGER.trace("getTestSetById " + id);
        final RestConnector connector = RestConnector.getInstance();
        final String restUrl = connector.buildEntityCollectionUrl("test-set") + "/" + id;
        final Entity entity = connector.getEntity(restUrl, null);
        return new TestSet(entity);
    }

    /**
     * Get a TestSetFolder by its path.
     *
     * @param testSetFolderPath The testSetFolder path. Something like 'Root\ProjectsTest\v1.0'
     * @return The TestSetFolder.
     * @throws IOException Exception during communication with REST Service.
     */
    public static TestSetFolder getTestSetFolder(final String testSetFolderPath) throws IOException {
        return pGetTestSetFolder(testSetFolderPath);
    }

    /**
     * Get a TestSetFolder by its path.
     *
     * @param testSetFolderPath The testSetFolder path. Something like 'Root\ProjectsTest\v1.0'
     * @return The TestSetFolder.
     * @throws IOException Exception during communication with REST Service.
     */
    private static TestSetFolder pGetTestSetFolder(final String testSetFolderPath) throws IOException {
        LOGGER.debug("Searching for testSetFolderPath: " + testSetFolderPath);

        FolderFinder folderFinder = new FolderFinder(testSetFolderPath);
        return folderFinder.find(TestSetFolder.class);
    }

    /**
     * Get a TestSetFolder by its id. CALL THIS METHOD FROM FOLDERFINDER ONLY!!!!
     *
     * @param id Id of TestSetFolder.
     * @return TestSetFolder object.
     * @throws IOException Exception during communication with REST Service.
     */
    public static TestSetFolder getTestSetFolderById(final int id) throws IOException {
        LOGGER.trace("getTestSetFolderById" + id);
        final RestConnector connector = RestConnector.getInstance();
        final String restUrl = connector.buildEntityCollectionUrl("test-set-folder") + "/" + id;
        final Entity entity = connector.getEntity(restUrl, null);
        return new TestSetFolder(entity);
    }

    /**
     * Get all TestSets of the given TestSetFolder.
     *
     * @param testSetFolder The folder containing the TestSets to get.
     * @return A list containing the TestSets.
     * @throws IOException Exception during communication with REST Service.
     */
    public static List<TestSet> getTestSets(final TestSetFolder testSetFolder) throws IOException {
        return pGetTestSets(testSetFolder);
    }

    /**
     * Get all TestSets of the given TestSetFolder.
     *
     * @param testSetFolder The folder containing the TestSets to get.
     * @return A list containing the TestSets.
     * @throws IOException Exception during communication with REST Service.
     */
    private static List<TestSet> pGetTestSets(final TestSetFolder testSetFolder) throws IOException {
        LOGGER.debug("getTestSets for " + testSetFolder.toString());
        final List<TestSet> testSets = new LinkedList<TestSet>();

        final RestConnector connector = RestConnector.getInstance();
        final String queryUrl = "query={parent-id[" + testSetFolder.getId() + "]}";
        final String requestUrl = connector.buildEntityCollectionUrl("test-set");
        final List<Entity> entities;
        entities = connector.getEntities(requestUrl, queryUrl);
        if (entities != null) {
            for (Entity folder : entities) {
                testSets.add(new TestSet(folder));
            }
        }
        return testSets;
    }

    /**
     * Get the TestSetTest by its name, the TestSet name and TestSetFolder path.
     *
     * @param name Name of Testinstance.
     * @param testSetName Name of testSet the instance should be in.
     * @param testSetPath Path of TestSetFolder the TestSet belongs to.
     * @return TestSetTestWr object.
     * @throws IOException Exception during communication with REST Service.
     */
    public static TestSetTest getTestSetTest(final String name, final String testSetName, final String testSetPath)
            throws IOException {
        return pGetTestSetTest(name, testSetName, testSetPath);
    }

    /**
     * Get the TestSetTest by its name, the TestSet name and TestSetFolder path.
     *
     * @param name Name of Testinstance.
     * @param testSetName Name of testSet the instance should be in.
     * @param testSetPath Path of TestSetFolder the TestSet belongs to.
     * @return TestSetTestWr object.
     * @throws IOException Exception during communication with REST Service.
     */
    private static TestSetTest pGetTestSetTest(final String name, final String testSetName, final String testSetPath)
            throws IOException {
        final TestSet testSet = getTestSet(testSetName, testSetPath);
        LOGGER.debug("getTestSetTest " + name + " from " + testSet);
        final RestConnector connector = RestConnector.getInstance();
        final String restUrl = connector.buildEntityCollectionUrl("test-instance");
        final String queryUrl = "query={cycle-id[" + testSet.getId() + "]}";
        List<Entity> entities;
        try {
            entities = connector.getEntities(restUrl, queryUrl);
        } catch (IOException e) {
            throw new IOException("Error communicating with REST service", e);
        }
        if (!entities.isEmpty()) {
            for (Entity entity : entities) {
                TestSetTest testInstance = new TestSetTest(entity);
                if (testInstance.getTest().getName().equals(name)) {
                    return testInstance;
                }
            }
        }
        throw new TesterraRuntimeException("TestSetTest " + name + " in testSet " + testSetPath + "\\" + testSetName
                + " could not be found.");
    }

    /**
     * Gets a TestSetTest by its id.
     *
     * @param id Id of TestSetTest.
     * @return TestSetTest object.
     * @throws IOException Exception during communication with REST Service.
     */
    public static TestSetTest getTestSetTestById(final int id) throws IOException {
        LOGGER.trace("getTestSetTestById " + id);
        final RestConnector connector = RestConnector.getInstance();
        final String restUrl = connector.buildEntityCollectionUrl("test-instance") + "/" + id;
        final Entity entity = connector.getEntity(restUrl, null);
        return new TestSetTest(entity);
    }

    /**
     * Get a list of TestSetTests from a given TestSet.
     *
     * @param testSetName The name of the TestSet.
     * @param testSetFolderPath The path to the TestSetFolder containing the TestSet.
     * @return A list of TestSetTests.
     * @throws IOException Exception during communication with REST Service.
     */
    public static List<TestSetTest> getTestSetTests(final String testSetName, final String testSetFolderPath)
            throws IOException {
        LOGGER.debug("getTestSetTests for " + testSetFolderPath + "\\" + testSetName);
        final TestSet testSet = getTestSet(testSetName, QcRestClient.getTestSetFolder(testSetFolderPath));
        return getTestSetTests(testSet);
    }

    /**
     * Get a list of TestSetTests for a given TestSet.
     *
     * @param testSet The testset containing the TestSetTests.
     * @return A list containing the TestSetTests.
     * @throws IOException Exception during communication with REST Service.
     */
    public static List<TestSetTest> getTestSetTests(final TestSet testSet) throws IOException {
        return pGetTestSetTests(testSet);
    }

    /**
     * Get a list of TestSetTests for a given TestSet.
     *
     * @param testSet The testset containing the TestSetTests.
     * @return A list containing the TestSetTests.
     * @throws IOException Exception during communication with REST Service.
     */
    private static List<TestSetTest> pGetTestSetTests(final TestSet testSet) throws IOException {
        LOGGER.debug("getTestSetTests for " + testSet);
        final List<TestSetTest> testSetTests = new LinkedList<TestSetTest>();
        final RestConnector connector = RestConnector.getInstance();
        final String restUrl = connector.buildEntityCollectionUrl("test-instance");
        final String queryUrl = "query={cycle-id[" + testSet.getId() + "]}";
        final List<Entity> entities = connector.getEntities(restUrl, queryUrl);
        for (Entity entity : entities) {
            testSetTests.add(new TestSetTest(entity));
        }
        return testSetTests;
    }

    /**
     * Check if the given TestSet exists in the given TestSetFolder.
     *
     * @param testSetName The name of the TestSet.
     * @param testSetPath The path of the TestSetFolder, that should contain the TestSet.
     * @return True if it exists.
     * @throws IOException Exception during communication with REST Service.
     */
    public static boolean isExistingTestSet(final String testSetName, final String testSetPath) throws IOException {
        return pIsExistingTestSet(testSetName, testSetPath);
    }

    /**
     * Check if the given TestSet exists in the given TestSetFolder.
     *
     * @param testSetName The name of the TestSet.
     * @param testSetPath The path of the TestSetFolder, that should contain the TestSet.
     * @return True if it exists.
     * @throws IOException Exception during communication with REST Service.
     */
    private static boolean pIsExistingTestSet(final String testSetName, final String testSetPath) throws IOException {
        LOGGER.debug("isExistingTestSet: " + testSetPath + "\\" + testSetName);

        final TestSetFolder folder = QcRestClient.getTestSetFolder(testSetPath);

        if (folder == null) {
            LOGGER.warn("Testset folder does not exist: " + testSetPath);
            return false;
        }

        final RestConnector connector = RestConnector.getInstance();
        final String queryUrl = "query={parent-id[" + folder.getId() + "];name['" + testSetName + "']}";
        final String requestUrl = connector.buildEntityCollectionUrl("test-set");
        final List<Entity> entities;
        try {
            entities = connector.getEntities(requestUrl, queryUrl);
        } catch (IOException e) {
            throw new IOException("Error communicating with REST service", e);
        }

        if (entities == null) {
            LOGGER.error("isExistingTestSet entities is null");
            return false;
        } else if (entities.isEmpty()) {
            LOGGER.error("isExistingTestSet entities is empty");
            return false;
        }

        return true;
    }

    /**
     * !!! Aus Performancegruenden sollten hier nicht alle TestFolder-Informationen geholt werden, nur um dann zu sagen,
     * dass sie existieren, ums sie anschließend nochmal zu holen!! Deprecated!
     * <p/>
     * Check if a TestSetFolder exists. *
     *
     * @param pFolderPath The TestSetFolder path. Something like 'Root\ProjectsTest\v1.0'
     * @return true if the TestSetFolder exists, false otherwise.
     * @throws IOException Exception during communication with REST Service.
     * @deprecated !!! Aus Performancegruenden sollten hier nicht alle TestFolder-Informationen geholt werden, nur um
     *             dann zu sagen, dass sie existieren, ums sie anschließend nochmal zu holen!!
     */
    @Deprecated
    public static boolean isExistingTestSetFolder(final String pFolderPath) throws IOException {
        LOGGER.debug("isExistingTestSetFolder: " + pFolderPath);
        return getTestSetFolder(pFolderPath) != null;
    }

    /**
     * Checks if a TestSetTest with the given name exists in a given TestSet.
     *
     * @param testName The name of the TestSetTest.
     * @param testSet The TestSet that should contain a TestSetTest with the name.
     * @return True if the TestSetTest exists.
     * @throws IOException Exception during communication with REST Service.
     */
    public static boolean isExistingTestSetTest(final String testName, final TestSet testSet) throws IOException {
        return pIsExistingTestSetTest(testName, testSet);
    }

    /**
     * Checks if a TestSetTest with the given name exists in a given TestSet.
     *
     * @param testName The name of the TestSetTest.
     * @param testSet The TestSet that should contain a TestSetTest with the name.
     * @return True if the TestSetTest exists.
     * @throws IOException Exception during communication with REST Service.
     */
    private static boolean pIsExistingTestSetTest(final String testName, final TestSet testSet) throws IOException {
        LOGGER.debug("isExistingTestSetTest: " + testSet + ":" + testName);
        final RestConnector connector = RestConnector.getInstance();
        final String restUrl = connector.buildEntityCollectionUrl("test-instance");
        final String queryUrl = "query={cycle-id[" + testSet.getId() + "];test.name['" + testName.substring(3) + "']}";
        final List<Entity> entities;
        try {
            entities = connector.getEntities(restUrl, queryUrl);
        } catch (IOException e) {
            throw new IOException("Error communicating with REST service", e);
        }
        return entities != null && !entities.isEmpty();
    }

    /**
     * Update a TestSetTest with information from a TestRun (execution date + time, status).
     *
     * @param tsTest TestSetTest to update.
     * @param run TestRun providing the information.
     * @throws IOException Exception during communication with REST Service.
     */
    private static void updateTestSetTest(final TestSetTest tsTest, final TestRun run) throws IOException {
        if (tsTest.getId() == 0) {
            return;
        }
        final TestSetTest change = new TestSetTest();
        if (run.getStatus() != null) {
            change.setStatus(run.getStatus());
        }
        if (run.getExecutionDate() != null) {
            change.setExecutionDate(run.getExecutionDate());
        }
        if (run.getExecutionTime() != null) {
            change.setExecutionTime(run.getExecutionTime());
        }
        final Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/xml");
        final RestConnector connector = RestConnector.getInstance();
        final String restUrl = connector.buildEntityCollectionUrl("test-instance") + "/" + tsTest.getId();
        try {
            Response putResponse = connector.httpPut(restUrl,
                    MarshallingUtils.unmarshal(Entity.class, change.getEntity()).getBytes(),
                    headers);
            Entity entity = MarshallingUtils.marshal(Entity.class, putResponse.toString());
            List<TestRun> lastRuns = getXTestRuns(new TestSetTest(entity), 1);
            if (lastRuns.size() == 1) {
                TestRun lastRun = lastRuns.get(0);
                if (lastRun.getName().startsWith("Fast_Run")) {
                    if (!removeTestRun(lastRun)) {
                        LOGGER.error("Setting status of test-instance created Fast_Run");
                    }
                }
            }
        } catch (JAXBException e) {
            LOGGER.error("Error parsing TestSetTestObject to xml.", e);
        }
    }

    /**
     * Removes the TestRun with the given Id from QC.
     * 
     * @param runId Id of run to remove
     * @return true if remove was successfull
     */
    public static boolean removeTestRun(final int runId) {
        return pRemoveTestRun(runId);
    }

    /**
     * Removes the given TestRun from QC.
     * 
     * @param run TestRun to remove
     * @return true if remove was successfull
     */
    public static boolean removeTestRun(final TestRun run) {
        return pRemoveTestRun(run.getId());
    }

    /**
     * @param runId Id of run to remove
     * @return true if remove was successfull
     */
    private static boolean pRemoveTestRun(final int runId) {
        final RestConnector connector = RestConnector.getInstance();
        final String restUrl = connector.buildEntityCollectionUrl("run") + "/" + runId;
        try {
            LOGGER.debug("Removing TestRun with id " + runId);
            Response deleteResponse = connector.httpDelete(restUrl);
            return (deleteResponse.getStatusCode() == 200);
        } catch (IOException e) {
            LOGGER.error("Error removing FastRun created by setting TestSetTest status.", e);
            return false;
        }
    }

    /**
     * Get a test from testPlan by its id.
     *
     * @param id Id of test to get
     * @return Test object.
     * @throws IOException Rest communication error.
     */
    public static TestPlanTest getTestPlanTestById(int id) throws IOException {
        return pGetTestPlanTestById(id);
    }

    /**
     * Get a test from testPlan by its id.
     *
     * @param testName Name of test to get
     * @param testFolder Path of folder containing test.
     * @return Test object.
     * @throws IOException Rest communication error.
     */
    public static TestPlanTest getTestPlanTest(final String testName, final String testFolder) throws IOException {
        return pGetTestPlanTest(testName, testFolder);
    }

    /**
     * Get a test from testPlan by its name and folder path.
     *
     * @param testName Name of test to get
     * @param testFolder Path of folder containing test.
     * @return Test object.
     * @throws IOException Rest communication error.
     */
    private static TestPlanTest pGetTestPlanTest(final String testName, final String testFolder) throws IOException {
        final TestPlanFolder folder = pGetTestPlanFolder(testFolder);
        return pGetTestPlanTest(testName, folder);
    }

    /**
     * @param name Name of test.
     * @param folder Folder containing the test.
     * @return {@link TestPlanTest} object.
     * @throws IOException Rest Exception.
     */
    private static TestPlanTest pGetTestPlanTest(final String name, final TestPlanFolder folder) throws IOException {
        LOGGER.debug("getTestPlanTest " + name + " of " + folder);
        TestPlanTest test = null;
        if (folder == null) {
            throw new TesterraRuntimeException("Test probably doesn't exist. Could not find given TestPlanFolder.");
        }
        final RestConnector connector = RestConnector.getInstance();
        if (folder.getId() == 0) {
            throw new TesterraRuntimeException(
                    "Id of testSetFolder must be set. Get it from QC, don't create it yourself!");
        }
        final String queryUrl = "query={parent-id[" + folder.getId() + "];name['" + name + "']}";
        final String requestUrl = connector.buildEntityCollectionUrl("test");
        final List<Entity> entities;
        entities = connector.getEntities(requestUrl, queryUrl);
        if (entities != null && !entities.isEmpty()) {
            test = new TestPlanTest(entities.get(0));
        } else {
            LOGGER.error("TestPlanTest " + name + " could not be found in folder " + folder.getName());
        }
        return test;
    }

    /**
     * gets the testPlanFolder
     *
     * @param folderPath Path to folder.
     * @return {@link TestPlanFolder} object.
     * @throws IOException Rest communication error.
     */
    public static TestPlanFolder getTestPlanFolder(final String folderPath) throws IOException {
        return pGetTestPlanFolder(folderPath);
    }

    /**
     * @param testFolder Path to folder.
     * @return {@link TestPlanFolder} object.
     * @throws IOException Rest communication error.
     */
    private static TestPlanFolder pGetTestPlanFolder(final String testFolder) throws IOException {
        LOGGER.debug("Searching for testPlanFolderPath: " + testFolder);

        FolderFinder folderFinder = new FolderFinder(testFolder);
        return folderFinder.find(TestPlanFolder.class);
    }

    /**
     * gets the testPlanFolder by ID
     *
     * @param id Id of element to get.
     * @return {@link TestPlanFolder} object
     * @throws IOException Communication error with Rest service.
     */
    public static TestPlanFolder getTestPlanFolderById(int id) throws IOException {
        LOGGER.trace("getTestPlanFolderById" + id);
        final RestConnector connector = RestConnector.getInstance();
        final String restUrl = connector.buildEntityCollectionUrl("test-folder") + "/" + id;
        final Entity entity = connector.getEntity(restUrl, null);
        return new TestPlanFolder(entity);
    }

    /**
     * Get a test from testPlan by its id.
     *
     * @param id Id of test to get
     * @return Test object.
     * @throws IOException Rest communication error.
     */
    private static TestPlanTest pGetTestPlanTestById(int id) throws IOException {
        LOGGER.trace("getTestPlanById " + id);
        final RestConnector connector = RestConnector.getInstance();
        final String restUrl = connector.buildEntityCollectionUrl("test") + "/" + id;
        final Entity entity = connector.getEntity(restUrl, null);
        return new TestPlanTest(entity);
    }

    /**
     * Hide constructor of utility class.
     */
    private QcRestClient() {
    }

    /**
     * Add a test in QC (test without id) or upload changes you've made (test has id)
     *
     * @param test Test to add or change.
     * @return id of added or changed test.
     * @throws IOException Rest communication error.
     */
    public static int addTest(final TestPlanTest test) throws IOException {
        return pAddTest(test, 0);
    }

    /**
     * Upload changes to the test with the given id.
     *
     * @param test Test to change.
     * @param id Id of test to change
     * @return id changed test.
     * @throws IOException Rest communication error.
     */
    public static int editTest(final TestPlanTest test, final int id) throws IOException {
        return pAddTest(test, id);
    }

    /**
     * Creates a new folder in a given one.
     *
     * @param folder Folder to create (should contain name and parent-id.)
     * @return id of created folder.
     * @throws IOException Rest communication error.
     */
    public static int createTestFolder(final TestPlanFolder folder) throws IOException {
        return pCreateTestFolder(folder);
    }

    /**
     * Creates a new folder in a given one.
     *
     * @param folder Folder to create (should contain name and parent-id.)
     * @return id of created folder.
     * @throws IOException Rest communication error.
     */
    private static int pCreateTestFolder(final TestPlanFolder folder) throws IOException {
        LOGGER.info("Create TestPlanFolder");
        if (folder.getName() == null || folder.getParentId() == 0) {
            LOGGER.error("Name and parent-id of folder to create must not be null!");
            return 0;
        }
        return addEntity(folder.getEntity());
    }

    /**
     * Add a test in QC (id==null) or upload changes you've made (id given)
     *
     * @param test Test to add or change.
     * @param id id of test to change.
     * @return id of added or changed test.
     * @throws IOException Rest communication error.
     */
    private static int pAddTest(final TestPlanTest test, final int id) throws IOException {
        final RestConnector connector = RestConnector.getInstance();
        if (id != 0) {
            LOGGER.info("Edit TestPlanTest");
            // Changed Test to upload.
            final String putUrl = connector.buildEntityCollectionUrl("test") + "/" + id;
            final Map<String, String> headers = new HashMap<String, String>();
            headers.put("Content-Type", "application/xml");
            try {
                Response response = connector
                        .httpPut(putUrl.toString(),
                                MarshallingUtils.unmarshal(Entity.class, test.getEntity())
                                        .getBytes(), headers);
                LOGGER.info("Response: " + response.toString());
                TestPlanTest returnde = new TestPlanTest(MarshallingUtils.marshal(Entity.class, response.toString()));
                return returnde.getId();
            } catch (JAXBException e) {
                LOGGER.error("Error changing test. Test Object could not be serialized. ", e);
                return 0;
            }

        } else {
            // new test to add.
            LOGGER.info("Add TestPlanTest");
            if (test.getName() == null || test.getFieldValueByName("parent-id") == null) {
                LOGGER.error("This test needs at least a name and a parent-id!");
                return 0;
            }
            if (test.getType() == null) {
                LOGGER.warn("Type should be set by you. Setting it to MANUAL for this test.");
                test.setType("MANUAL");
            }
            return addEntity(test.getEntity());
        }
    }

    /**
     * creates testSetFolder
     *
     * @param creater TestSetFolder to create.
     * @return id of created folder
     * @throws IOException Rest communication error.
     */
    public static int createTestSetFolder(final TestSetFolder creater) throws IOException {
        return pCreateTestSetFolder(creater);
    }

    /**
     * @param folder TestSetFolder to create.
     * @return id of created folder
     * @throws IOException IOException Rest communication error.
     */
    private static int pCreateTestSetFolder(final TestSetFolder folder) throws IOException {
        LOGGER.info("Create TestSetFolder");
        if (folder.getName() == null || folder.getParentId() == 0) {
            LOGGER.error("Name and parent-id of folder to create must not be null!");
            return 0;
        }
        return addEntity(folder.getEntity());
    }

    /**
     * creates testSet
     *
     * @param toCreate TestSet to create
     * @return id of created testSet.
     * @throws IOException Rest communication error.
     */
    public static int createTestSet(final TestSet toCreate) throws IOException {
        return pCreateTestSet(toCreate);
    }

    /**
     * @param toCreate TestSet to create
     * @return id of created testSet.
     * @throws IOException Rest communication error.
     */
    private static int pCreateTestSet(final TestSet toCreate) throws IOException {
        LOGGER.info("Create TestSet");
        if (toCreate.getName() == null || toCreate.getParentId() == 0) {
            LOGGER.error("Name and parent-id of testSet to create must not be null!");
            return 0;
        }
        if (toCreate.getType() == null) {
            LOGGER.warn("Type property of TestSet not set. Setting to Default (hp.qc.test-set.default).");
            toCreate.setType("hp.qc.test-set.default");
        }
        return addEntity(toCreate.getEntity());
    }

    /**
     * @param toAdd Entity to add.
     * @return id of created entity (0 on error)
     * @throws IOException REST connection error.
     */
    private static int addEntity(final Entity toAdd) throws IOException {
        final RestConnector connector = RestConnector.getInstance();
        String idReturned = null;
        final Response response;
        try {
            final Map<String, String> headers = new HashMap<String, String>();
            headers.put("Content-Type", "application/xml");

            response = connector.httpPost(connector.buildEntityCollectionUrl(toAdd.getType()),
                    MarshallingUtils.unmarshal(Entity.class, toAdd)
                            .getBytes(), headers);
        } catch (JAXBException e) {
            LOGGER.error("Error parsing Entity to xml.", e);
            return 0;
        }
        if (response.getStatusCode() == 201) {
            final String location = response.getResponseHeaders().get("Location").iterator().next();
            LOGGER.info("Added " + toAdd.getType() + " " + location);
            idReturned = location.substring(location.lastIndexOf('/') + 1);
            try {
                return Integer.parseInt(idReturned);
            } catch (NumberFormatException nfe) {
                LOGGER.error("Could not parse id from location:" + location);
                return 0;
            }
        } else {
            LOGGER.error("Error creating " + toAdd.getType() + ". See Exception for cause.", response.getFailure());
            return 0;
        }
    }

    /**
     * adds test to testSet
     *
     * @param testInstance TestSetTest to create.
     * @return Id of generated TestSetTest
     * @throws IOException REST connection error.
     */
    public static int addTestToTestSet(final TestSetTest testInstance) throws IOException {
        return pAddTestToTestSet(testInstance);
    }

    /**
     * @param testInstance TestSetTest to create.
     * @return Id of generated TestSetTest
     * @throws IOException REST connection error.
     */
    private static int pAddTestToTestSet(final TestSetTest testInstance) throws IOException {
        LOGGER.info("Create TestSet");
        if (testInstance.getTestId() == 0 || testInstance.getCycleId() == 0) {
            LOGGER.error("test-id and cycle-id (testSet) of testSetTest to create must not be null!");
            return 0;
        }
        if (testInstance.getType() == null) {
            LOGGER.warn("Type property of TestSet not set. Setting to MANUAL.");
            testInstance.setType("hp.qc.test-instance.MANUAL");
        }
        return addEntity(testInstance.getEntity());
    }
}
