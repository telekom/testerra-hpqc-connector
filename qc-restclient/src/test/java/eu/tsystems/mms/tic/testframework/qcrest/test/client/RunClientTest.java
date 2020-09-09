/*
 * Created on 26.11.2010
 *
 * Copyright(c) 2010 - 2099 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.qcrest.test.client;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.qcrest.clients.QcRestClient;
import eu.tsystems.mms.tic.testframework.qcrest.clients.RestConnector;
import eu.tsystems.mms.tic.testframework.qcrest.clients.UtilClient;
import eu.tsystems.mms.tic.testframework.qcrest.constants.QCProperties;
import eu.tsystems.mms.tic.testframework.qcrest.utils.FileByteConverter;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.Attachment;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestPlanTest;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestRun;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestSet;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestSetFolder;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestSetTest;
import eu.tsystems.mms.tic.testframework.testmanagement.annotation.QCTestset;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test class for testing QC Test lab methods.
 *
 * @author sepr, mibu
 */
@QCTestset("Root\\Xeta\\QC WebServiceClient\\RestClientTests")
public class RunClientTest extends AbstractTest {

    /**
     * Logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(RunClientTest.class);
    /**
     * Trash Folder.
     */
    private static final String TRASH = "TrashCan";

    /** Path to test in testlab. */
    // private static final String TEST_PATH = "Subject\\Xeta\\QCWebServiceClient";

    /**
     * Creates a TestRunWr object.
     *
     * @param passed Create Run that is passed (true) or failed (false).
     *
     * @return A newly created TestRunWr.
     */
    private TestRun createTestRun(boolean passed) {
        final TestRun testRunWr = new TestRun();
        testRunWr.setName("QCRestClient TestRun");
        testRunWr.setStatus(passed ? "Passed" : "Failed");
        testRunWr.setExecutionTime(new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis())));
        final Random r = new Random();
        testRunWr.setDuration(r.nextInt(100));
        testRunWr.setHost("somepc");
        testRunWr.setOsName("Windows 7");
        return testRunWr;
    }

    /**
     * Prints a QC folder hierarchy (recursively) to the given StringBuilder.
     *
     * @param pStr    A StringBuilder.
     * @param pFolder A TestSetFolderWr. instance
     * @param pDeep   An integer indicating hierarchical deepness of pFolder.
     */
    private void printFolder(final StringBuilder pStr,
                             final TestSetFolder pFolder, final int pDeep) throws Exception {
        // max 3 folders
        if (pDeep > 3) {
            return;
        }
        for (int i = 0; i < pDeep * 3; i++) {
            pStr.append(' ');
        }

        pStr.append("- " + pFolder.getName());

        if (pFolder.getChildren() != null) {
            for (final TestSetFolder lFolder : pFolder.getChildren()) {
                pStr.append('\n');
                printFolder(pStr, lFolder, pDeep + 1);
            }
        }
    }

    /**
     * Test method: adds and removes attachments to/from a test set.
     *
     * @throws IOException Error loading or saving attachments to/from disk.
     */
    @Test
    public void testGetTestSetAttachments() throws Exception {
        LOG.info("Test add attachments to Testset");

        final TestSetFolder tsf = QcRestClient.getTestSetFolder(TESTSET_PATH);

        final TestSet ts = QcRestClient.getTestSet("RestClientTests", tsf);

        Attachment[] rAttachments = ts.getAttachments();
        boolean fileFound = false;
        for (final Attachment rAttachment : rAttachments) {
            LOG.debug("Found Attachment " + rAttachment.getName());

            if (rAttachment.getName().contains("test.txt")) {
                fileFound = true;
            }
        }
        Assert.assertTrue(fileFound, "File Attachment not found.");
    }

    /**
     * Test method: adds a new test run with attachments to a test set test.
     *
     * @throws IOException Error loading or saving attachments to/from disk.
     */
    @Test
    public void testAddTestRun() throws Exception {
        LOG.info("Test addTestRun");

        final TestSetTest tsTest = QcRestClient.getTestSetTest(TEST, TESTSET, TESTSET_PATH);
        final TestRun testRun = createTestRun(false);

        final List<Attachment> lAttachmentsList = new ArrayList<Attachment>();

        final Attachment attach1 = new Attachment();
        final File file = getResourceAsFile("test.txt");
        attach1.setContent(FileUtils.readFileToByteArray(file));
        attach1.setName("test.txt");
        attach1.setRefType(Attachment.Type.FILE.getValue());
        lAttachmentsList.add(attach1);

        final Attachment attach2 = new Attachment();
        attach2.setName("http://www.google.de");
        attach2.setRefType(Attachment.Type.URL.getValue());
        lAttachmentsList.add(attach2);

        testRun.addAttachments(lAttachmentsList);
        int runId = 0;
        runId = QcRestClient.addTestRun(tsTest, testRun);
        Assert.assertNotEquals(runId, 0);
        LOG.debug(testRun.getName() + " with "
                + lAttachmentsList.size() + " attachments added to TestSetTest "
                + tsTest.getTest().getName());
    }

    /**
     * Test method: gets all test runs from a test set test with their attachments.
     *
     * @throws IOException An I/O error occurred.
     */
    @Test(dependsOnMethods = "testAddTestRun")
    public void testGetTestRuns() throws Exception {
        LOG.info("Get testruns with attachments");

        final TestSetTest tsTest = QcRestClient.getTestSetTest(TEST, TESTSET, TESTSET_PATH);

        final List<TestRun> lTestRuns = QcRestClient.getXTestRuns(tsTest, 5);
        Assert.assertTrue(lTestRuns.size() > 0);
        for (final TestRun testRun : lTestRuns) {
            LOG.debug("TestRun name          : " + testRun.getName());
            LOG.debug("TestRun id            : " + testRun.getId());
            LOG.debug("TestRun execution time: " + testRun.getExecutionTime());
            LOG.debug("TestRun status        : " + testRun.getStatus());

            if (testRun.getAttachments() != null) {

                LOG.debug("Testrun attachments   : "
                        + testRun.getAttachments().size());

                final List<Attachment> lAttach = testRun.getAttachments();
                for (final Attachment attachment : lAttach) {
                    LOG.debug("\tAttachment name       : "
                            + attachment.getName());
                    LOG.debug("\tAttachment ref type   : "
                            + attachment.getRefType());
                    if (attachment.getRefType().equalsIgnoreCase(Attachment.Type.FILE.getValue())) {
                        LOG.debug("\tAttachment path       : "
                                + FileByteConverter.getFileFromBytes(
                                attachment.getName(),
                                attachment.getContent())
                                .getAbsolutePath());
                    } else {
                        LOG.debug("\tAttachment entry       : "
                                + attachment.getName());
                    }
                }
            }
        }
    }

    /**
     * Gets the allowed test run statuses.
     *
     * @throws IOException A remote error occurred.
     */
    @Test
    public void testGetTestRunStatuses() {
        LOG.info("Test getTestRunStatuses");

        final List<String> statuses = UtilClient.getUserFieldValues("Status", "test-instance");
        Assert.assertTrue(statuses.size() > 0);
        for (final String status : statuses) {
            LOG.debug(status);
        }
    }

    /**
     * Test method: gets a test set and prints its name and status.
     *
     * @throws IOException Exception during communication.
     */
    @Test
    public void testGetTestSet() throws Exception {
        LOG.info("Test getTestSet");
        final TestSetFolder lFolder = QcRestClient.getTestSetFolder(TESTSET_PATH);
        Assert.assertNotNull(lFolder);
        final TestSet testSetWr = QcRestClient.getTestSet(TESTSET, lFolder);
        Assert.assertNotNull(testSetWr);
        LOG.debug("Testset name  : " + testSetWr.getName());
        LOG.debug("Testset id    : " + testSetWr.getId());
        LOG.debug("Testset status: " + testSetWr.getStatus());
    }

    /**
     * Test method: gets a test set test and prints its name and status.
     *
     * @throws IOException Exception during communication.
     */
    @Test
    public void testGetTestSetTest() throws Exception {
        LOG.info("Test getTestSetTest");
        final TestSetTest testSetTestWr = QcRestClient.getTestSetTest(TEST, TESTSET, TESTSET_PATH);
        Assert.assertNotNull(testSetTestWr);
        LOG.debug("TestsetTest name  : " + testSetTestWr.getTest().getName());
        LOG.debug("TestsetTest id    : " + testSetTestWr.getId());
        LOG.debug("TestsetTest status: " + testSetTestWr.getStatus());
        Assert.assertEquals(testSetTestWr.getTest().getName(), TEST);
    }

    /**
     * Test method: gets a test set folder and prints the hierarchy downwards this folder.
     *
     * @throws IOException Exception during communication.
     */
    @Test
    public void testGetTestSetFolder() throws Exception {
        LOG.info("Test getTestSetFolder");

        final TestSetFolder lFolder = QcRestClient.getTestSetFolder(TESTSET_PATH);
        Assert.assertNotNull(lFolder);
        LOG.debug("FOLDER: " + lFolder.getPath());
        final StringBuilder lStr = new StringBuilder();

        printFolder(lStr, lFolder, 0);
        LOG.debug(lStr.toString());
    }

    /**
     * Test method: gets a test set folder and prints the hierarchy downwards this folder.
     *
     * @throws IOException Exception during communication.
     */
    @Test
    public void testGetTestSetFolderComplex() throws Exception {
        LOG.info("Test getTestSetFolder");

        String folderPath = "Root\\Xeta\\QC WebServiceClient\\ComplexFolder1\\"
                + "ComplexFolder1.2\\ComplexFolder1.2.2\\Xeta\\ComplexFolderHoneyPot";
        String folderPath2 = "Root\\Xeta\\QC WebServiceClient\\ComplexFolder1\\"
                + "ComplexFolder1.2\\ComplexFolder1.2.1\\Xeta\\ComplexFolderHoneyPot";
        String folderPath3 = "Root\\Xeta\\QC WebServiceClient\\ComplexFolder1\\"
                + "ComplexFolder1.3\\ComplexFolder1.2.1\\Xeta\\ComplexFolderHoneyPot";

        final TestSetFolder lFolder = QcRestClient.getTestSetFolder(folderPath);
        Assert.assertNotNull(lFolder);
        LOG.debug("FOLDER: " + lFolder.getPath());
        final StringBuilder lStr = new StringBuilder();

        printFolder(lStr, lFolder, 0);
        LOG.debug(lStr.toString());

        // get folderPath from cache
        QcRestClient.getTestSetFolder(folderPath2);
        // get folderPath from cache
        QcRestClient.getTestSetFolder(folderPath3);
    }

    /**
     * Test method: gets all test sets of a folder and print them.
     *
     * @throws IOException Exception during communication.
     */
    @Test
    public void testGetTestSets() throws Exception {
        LOG.info("Test getTestSets");

        final TestSetFolder lFolder = QcRestClient.getTestSetFolder(TESTSET_PATH);
        final List<TestSet> lTestSets = QcRestClient.getTestSets(lFolder);
        Assert.assertTrue(lTestSets.size() > 0, "No TestSets found.");
        LOG.debug("Found " + lTestSets.size()
                + " Testsets in TestSetFolder " + lFolder.getName() + ":");

        for (final TestSet testSetWr : lTestSets) {
            LOG.debug("Testset name  : " + testSetWr.getName());
            LOG.debug("Testset id    : " + testSetWr.getId());
            LOG.debug("Testset status: " + testSetWr.getStatus());
        }
    }

    /**
     * Test method: gets all test set test of a test set.
     *
     * @throws IOException Exception during communication.
     */
    @Test
    public void testGetTestSetTests() throws Exception {
        LOG.info("Test getTSTests");

        final List<TestSetTest> lTSTests = QcRestClient.getTestSetTests(TESTSET, TESTSET_PATH);
        Assert.assertTrue(lTSTests.size() > 0, "No TestSetTests found.");
        LOG.debug("Found " + lTSTests.size()
                + " TestSetTests in " + TESTSET + ":");

        for (final TestSetTest tsTest : lTSTests) {
            LOG.debug("TestSetTest name          : "
                    + tsTest.getTest().getName());
            LOG.debug("TestSetTest status        : "
                    + tsTest.getStatus());
            LOG.debug("TestSetTest execution time: "
                    + tsTest.getExecutionTime());
            LOG.debug("TestSetTest host name     : "
                    + tsTest.getHostName());
            LOG.debug("TestSetTest tester name   : "
                    + tsTest.getTesterName());
        }
    }

    /**
     * Test method: checks whether a test set exists.
     *
     * @throws IOException Exception during communication.
     */
    @Test
    public void testIsExistingTestSet() throws Exception {
        LOG.info("Test isExistingTestSet");
        boolean exists = QcRestClient.isExistingTestSet(TESTSET, TESTSET_PATH);
        LOG.debug("The testset " + TESTSET + " in folder "
                + TESTSET_PATH
                + (exists ? "" : " not") + " exists.");
        Assert.assertTrue(exists);
    }

    // /**
    // * Test method: checks whether a test set folder exists or not.
    // *
    // * @throws IOException Exception during communication.
    // *
    // * @see eu.tsystems.mms.tic.testframework.qcrest.clients.RunClient#isExistingTestSetFolder(String)
    // */
    // @Test
    // public void testIsExistingTestSetFolder() throws IOException {
    // LOG.info("Test isExistingTestSetFolder");
    // boolean exists = QcRestClient.isExistingTestSetFolder(TESTSET_PATH);
    //
    // LOG.debug("The folder " + TESTSET_PATH + (exists ? "" : " not") + " exists.");
    // Assert.assertTrue(exists);
    // }

    /**
     * Test method: checks whether a test set test exists or not.
     *
     * @throws IOException Exception during communication.
     */
    @Test
    public void testIsExistingTestSetTest() throws Exception {
        LOG.info("Test isExistingTSTest");

        final TestSetFolder lFolder = QcRestClient.getTestSetFolder(TESTSET_PATH);
        final TestSet lTestSet = QcRestClient.getTestSet(TESTSET, lFolder);
        final boolean exists = QcRestClient.isExistingTestSetTest("[1]testUnderTest", lTestSet);
        LOG.debug("The testsettest [1]testUnderTest in testset "
                + lTestSet.getName() + (exists ? "" : " not") + " exists.");
        Assert.assertTrue(exists);
    }

    /**
     * Save a resource to the filesystem.
     *
     * @param resourceName Name of resource to save.
     *
     * @return Filename.
     */
    protected static File getResourceAsFile(String resourceName) {
        InputStream resStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
        if (resStream != null) {
            try {
                File f = new File(System.getProperty("java.io.tmpdir"), resourceName);
                OutputStream out = new FileOutputStream(f);
                byte[] buf = new byte[1024];
                int len;
                while ((len = resStream.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                resStream.close();
                return f;
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Test creating a TestSetFolder
     *
     * @throws IOException REST communication error.
     */
    private void createTestSetFolder() throws Exception {
        if (QcRestClient.getTestSetFolder(TESTSET_PATH + "\\" + TRASH) == null) {
            final TestSetFolder creater = new TestSetFolder();
            final TestSetFolder parent = QcRestClient.getTestSetFolder(TESTSET_PATH);
            creater.setParentId(parent.getId());
            creater.setName(TRASH);
            int id = QcRestClient.createTestSetFolder(creater);
            Assert.assertNotEquals(id, 0, "id of generated entity should not be null.");
        }
    }

    /**
     * Creating a TestSet
     *
     * @throws IOException Rest connection error.
     */
    private void createTestSet() throws Exception {
        createTestSetFolder();
        final TestSet toCreate = new TestSet();
        final TestSetFolder parent = QcRestClient.getTestSetFolder(TESTSET_PATH + "\\" + TRASH);
        toCreate.setName(TRASH);
        toCreate.setTestSetFolder(parent);
        int id = QcRestClient.createTestSet(toCreate);
        Assert.assertNotEquals(id, 0);
    }

    /**
     * Test adding a Test from TestPlan to a TestSet
     *
     * @throws IOException Rest connection error.
     */
    @Test
    public void testAddTestToTestSet() throws Exception {
        TestSet parent = null;
        try {
            if (!QcRestClient.isExistingTestSet(TRASH, TESTSET_PATH + "\\" + TRASH)) {
                createTestSet();
            }
            parent = QcRestClient.getTestSet(TRASH, TESTSET_PATH + "\\" + TRASH);
            final TestPlanTest test = QcRestClient.getTestPlanTest("testUnderTest",
                    "Subject\\Xeta\\QCWebServiceClient");

            final TestSetTest testInstance = new TestSetTest();
            testInstance.setCycleId(parent.getId());
            testInstance.setTestId(test.getId());
            if ("11".equals(PropertyManager.getProperty(QCProperties.VERSION, "12"))) {
                testInstance.setOrder(0);
            }

            int testSetTestId = QcRestClient.addTestToTestSet(testInstance);
            Assert.assertNotEquals(testSetTestId, 0);
        } finally {
            // delete
            LOG.debug("Remove created Stuff");
            Map<String, String> requestHeaders = new HashMap<String, String>();
            requestHeaders.put("Accept", "application/xml");
            if (parent != null) {
                RestConnector.getInstance().httpDelete(
                        RestConnector.getInstance().buildEntityCollectionUrl("test-set") + "/" + parent.getId())
                        .toString();
                RestConnector
                        .getInstance()
                        .httpDelete(
                                RestConnector.getInstance().buildEntityCollectionUrl("test-set-folder") + "/"
                                        + parent.getParentId())
                        .toString();
            }
        }
    }
}
