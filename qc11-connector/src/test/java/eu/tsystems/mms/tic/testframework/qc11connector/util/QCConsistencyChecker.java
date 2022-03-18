/*
 * Testerra
 *
 * (C) 2014, Stefan Prasse, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.qc11connector.util;

import eu.tsystems.mms.tic.testframework.qc11connector.constants.AssertionMessages;
import eu.tsystems.mms.tic.testframework.qcconnector.constants.QCTestUnderTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

public final class QCConsistencyChecker {

    private QCConsistencyChecker() {
    }

    private static Logger logger = LoggerFactory.getLogger(QCConsistencyChecker.class.getName());

    private static void checkForUnneededTestSetTests(Iterable<String> testsInQC, Iterable<QCTestUnderTest>
            testsUnderTest, String testSetPath) {
        // check if there are TestSetTests present that are not represented in QCConstants
        for (String testSetTestName : testsInQC) {
            boolean found = false;
            for (QCTestUnderTest test : testsUnderTest) {
                if (test.getTestName() == null || test.getTestName().equals(testSetTestName)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                logger.warn(AssertionMessages.testSetTestPresentWithoutQCConstant(testSetTestName, testSetPath));
            }
        }
    }

    private static void checkForTestsUnderTestWithoutTestSetTest(Iterable<String> testsInQC,
                                                                 Iterable<QCTestUnderTest> testsUnderTest, String testSetPath) {
        for (QCTestUnderTest test : testsUnderTest) {
            if (test.getTestName() == null) {
                continue;
            }
            boolean found = false;
            for (String testSetTestName : testsInQC) {
                if (testSetTestName.equals(test.getTestName())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                logger.warn(AssertionMessages.testFromConstantNotFoundInTestSet(test.getTestName(), testSetPath));
            }
        }
    }

    /**
     * checks the consistency with the qc
     *
     * @param testSetpath      .
     * @param testSetTestNames .
     */
    public static void checkConsistencyWithQC(String testSetpath, LinkedList<String> testSetTestNames) {
        LinkedList<QCTestUnderTest> tests = QCTestUnderTest.getTestSetTestNames();
        checkForTestsUnderTestWithoutTestSetTest(testSetTestNames, tests, testSetpath);
        checkForUnneededTestSetTests(testSetTestNames, tests, testSetpath);
    }
}
