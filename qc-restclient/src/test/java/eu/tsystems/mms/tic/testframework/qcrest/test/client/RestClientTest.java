/*
 * Created on 21.02.2013
 *
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.qcrest.test.client;

import eu.tsystems.mms.tic.testframework.qcrest.clients.FolderFinder;
import eu.tsystems.mms.tic.testframework.qcrest.clients.UtilClient;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestSetFolder;
import eu.tsystems.mms.tic.testframework.testmanagement.annotation.QCTestset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Tests for new Rest API of QcRestClient.
 *
 * @author sepr
 */
@QCTestset("Root\\Xeta\\QC WebServiceClient\\RestClientTests")
public class RestClientTest extends AbstractTest {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RestClientTest.class);

    /**
     * Test QcRestClient.getTestSetFolderById
     *
     * @throws Exception Exception during request.
     */
    @Test
    public void testGetTestSetFolderById() throws Exception {
        LOGGER.info("Executing testGetTestSetFolderById");
        TestSetFolder result = FolderFinder.getTestSetFolderById(148);
        LOGGER.info("Got TestSetFolder with name " + result.getName());
        Assert.assertEquals(result.getName(), "Xeta");
    }

    /**
     * Test if rest client gets the correct user field name for ta_scriptname User Field.
     */
    @Test
    public void testGetTAScriptnameField() {
        LOGGER.info("Executing testGetTAScriptnameField");
        String index = UtilClient.getIndexOfUserLabel("ta_scriptname", "test");
        Assert.assertEquals(index, "01", "Index of user-field ta_scriptname should be '01' for TAIntern.");
    }

    /**
     * Test if rest client gets the correct user field name for ta_scriptname User Field.
     */
    @Test
    public void testGetTestStatuses() {
        LOGGER.info("Executing testGetTestStatuses");
        List<String> statuses = UtilClient
                .getUserFieldValues("Status", "test");
        Assert.assertTrue(statuses.size() > 0, "Test statuses could not be get");
        LOGGER.debug(statuses.toString());
    }
}
