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
 * Sample test with QCTestSet method-annotations for integration-tests of QCSync Type 3.
 *
 */
public class NoClassAnnotationTest {

    /**
     *
     */
    @QCTestset(QCConstants.QC_TESTSUNDERTEST_FOLDER + QCConstants.QCSYNC3_TESTSET_NAME)
    @org.testng.annotations.Test
    public void correctMethodAnnotation() {
        Assert.assertTrue(true);
    }

    /**
     *
     */
    @QCTestset(QCConstants.NOT_EXISTING_FOLDER + QCConstants.NOT_EXISTING_TESTSET)
    @org.testng.annotations.Test
    public void wrongMethodAnnotation() {
        Assert.assertTrue(true);
    }

    @org.testng.annotations.Test
    public void noAnnotation() {
        Assert.assertTrue(true);
    }

    @org.testng.annotations.Test
    @QCTestset(QCConstants.QC_TESTSUNDERTEST_FOLDER + QCConstants.QCSYNC3_TESTSET_NAME)
    @QCTestname("correctTestNameAnnotation")
    public void someNotKnownTestname() {
        Assert.assertTrue(true);
    }

    @QCTestset(QCConstants.QC_TESTSUNDERTEST_FOLDER + QCConstants.QCSYNC3_TESTSET_NAME)
    @org.testng.annotations.Test
    @QCTestname(value = "correctTestNameAnnotationWithoutInstanceCount")
    public void correctTestNameAnnotationWithoutInstanceCount() {
        Assert.assertTrue(true);
    }

    @QCTestset(QCConstants.QC_TESTSUNDERTEST_FOLDER + QCConstants.QCSYNC3_TESTSET_NAME)
    @org.testng.annotations.Test
    @QCTestname(value = "correctTestNameAnnotationWithInstanceCount", instanceCount = 1)
    public void correctTestNameAnnotationWithInstanceCount() {
        Assert.assertTrue(true);
    }

    @QCTestset(QCConstants.QC_TESTSUNDERTEST_FOLDER + QCConstants.QCSYNC3_TESTSET_NAME)
    @org.testng.annotations.Test
    @QCTestname(value = "correctTestNameAnnotationWithInstanceCount", instanceCount = 2)
    public void correctTestNameAnnotationWithInstanceCountTwo() {
        Assert.assertTrue(false, "Error.");
    }
}
