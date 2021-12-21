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
package eu.tsystems.mms.tic.testframework.qc11connector.testsundertest;

import eu.tsystems.mms.tic.testframework.qc11connector.constants.QCConstants;
import eu.tsystems.mms.tic.testframework.testmanagement.annotation.QCTestname;
import eu.tsystems.mms.tic.testframework.testmanagement.annotation.QCTestset;
import org.testng.Assert;

/**
 * Sample test with QCTestSet class-annotation for integration-tests of QCSync Type 3.
 *
 */
@QCTestset(QCConstants.QC_TESTSUNDERTEST_FOLDER + QCConstants.SYNC_TESTSET_NAME)
public class CorrectClassAnnotationTest {


    /**
     * Test under test for unit-test correctClassAnnotation. The integration-test will also
     * control the methodname-mapping (e.g. cutting of 'test_').
     */
    @org.testng.annotations.Test
    public void correctClassAnnotation() {
        Assert.assertTrue(true);
    }

    /**
     * Test under test for test wrongMethodAnnotationOverridesCorrectClassAnnotation
     */
    @QCTestset(QCConstants.NOT_EXISTING_FOLDER + QCConstants.NOT_EXISTING_TESTSET)
    @org.testng.annotations.Test
    public void wrongMethodAnnotationOverridesCorrectClassAnnotation() {
        Assert.assertTrue(true);
    }

    /**
     *
     */
    @org.testng.annotations.Test
    public void failingTest() {
        Assert.assertTrue(false);
    }

    /**
     *
     */
    @org.testng.annotations.Test
    public void successfulTest() {
        Assert.assertTrue(true);
    }

    @org.testng.annotations.Test
    @QCTestname("notExistingTestNameAnnotation")
    public void someNotKnownTestnameWithWrongAnnotation() {
        Assert.assertTrue(true);
    }
}
