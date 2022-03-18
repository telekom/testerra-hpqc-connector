/*
 * Testerra
 *
 * (C) 2013, Stefan Prasse, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package eu.tsystems.mms.tic.testframework.qcrest.test.client;

import eu.tsystems.mms.tic.testframework.qcrest.clients.FolderFinder;
import eu.tsystems.mms.tic.testframework.qcrest.clients.QcRestClient;
import eu.tsystems.mms.tic.testframework.qcrest.clients.UtilClient;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestSetFolder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Tests for new Rest API of QcRestClient.
 *
 * @author sepr
 */
//@QCTestset("Root\\Testerra\\QCRestClient\\RestClientTests")
public class RestClientTest extends AbstractTest {

    /**
     * Test QcRestClient.getTestSetFolderById
     */
    @Test
    public void testGetTestSetFolderById() throws Exception {
        log().info("Executing testGetTestSetFolderById");
        final TestSetFolder lFolder = QcRestClient.getTestSetFolder("Root\\Testerra");
        TestSetFolder result = FolderFinder.getTestSetFolderById(lFolder.getId());
        log().info("Got TestSetFolder with name " + result.getName());
        Assert.assertEquals(result.getName(), "Testerra");
    }

    /**
     * Test if rest client gets the correct user field name for ta_scriptname User Field.
     */
    @Test
    public void testGetTAScriptnameField() {
        log().info("Executing testGetTAScriptnameField");
        String index = UtilClient.getIndexOfUserLabel("ta_scriptname", "test");
        Assert.assertEquals(index, "01", "Index of user-field ta_scriptname should be '01' for TAIntern.");
    }

    /**
     * Test if rest client gets the correct user field name for ta_scriptname User Field.
     */
    @Test
    public void testGetTestStatuses() {
        log().info("Executing testGetTestStatuses");
        List<String> statuses = UtilClient
                .getUserFieldValues("Status", "test");
        Assert.assertTrue(statuses.size() > 0, "Test statuses could not be get");
        log().debug(statuses.toString());
    }
}
