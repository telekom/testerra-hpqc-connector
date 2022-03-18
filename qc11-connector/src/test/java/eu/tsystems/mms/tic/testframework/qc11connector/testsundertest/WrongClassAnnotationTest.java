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
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.testmanagement.annotation.QCTestset;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Sample test with wrong QCTestSet class annotation for integration-test of QCSync Type 3.
 */
@QCTestset(QCConstants.NOT_EXISTING_FOLDER + QCConstants.NOT_EXISTING_TESTSET)
public class WrongClassAnnotationTest  {

    /**
     * Test under test for test wrongClassAnnotation
     */
    @Test
    public void wrongClassAnnotation() {
        Assert.assertTrue(true);
    }

    /**
     * Test under test for test methodOverridesClassAnnotation Path in annotation is correct and must override the wrong
     * path in class annotation
     */
    @QCTestset(QCConstants.QC_TESTSUNDERTEST_FOLDER + QCConstants.SYNC_TESTSET_NAME)
    @Test
    public void correctMethodAnnotationOverridesWrongClassAnnotation() {
        Assert.assertTrue(true);
    }

}
