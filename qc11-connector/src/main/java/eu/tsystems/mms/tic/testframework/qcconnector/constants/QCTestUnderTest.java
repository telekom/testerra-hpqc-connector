/*
 * Testerra
 *
 * (C) 2013, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.qcconnector.constants;

import java.util.Arrays;
import java.util.LinkedList;

public enum QCTestUnderTest {

    QCSYNC3_SUCCESSFULTEST("successfulTest"),
    QCSYNC3_FAILINGTEST("failingTest"),
    QCSYNC3_METHODCORRECTOVERRIDE("correctMethodAnnotationOverridesWrongClassAnnotation"),
    QCSYNC3_METHODWRONGOVERRIDE("wrongMethodAnnotationOverridesCorrectClassAnnotation"),
    QCSYNC3_CORRECTMETHOD("correctMethodAnnotation"),
    QCSYNC3_WRONGMETHOD("wrongMethodAnnotation"),
    QCSYNC3_CORRECTTESTNAME("correctTestNameAnnotation", "someNotKnownTestname"),
    QCSYNC3_CORRECTTESTNAME_WITHOUTINSTANCE("correctTestNameAnnotationWithoutInstanceCount", "correctTestNameAnnotationWithoutInstanceCount"),
    QCSYNC3_CORRECTTESTNAME_WITHINSTANCE("correctTestNameAnnotationWithInstanceCount", "correctTestNameAnnotationWithInstanceCount", 1),
    QCSYNC3_CORRECTTESTNAME_WITHINSTANCE_TWO("correctTestNameAnnotationWithInstanceCount", "correctTestNameAnnotationWithInstanceCountTwo", 2),
    QCSYNC3_WRONGTESTNAME("notExistingTestNameAnnotation", "someNotKnownTestnameWithWrongAnnotation"),
    QCSYNC3_CORRECTCLASS("correctClassAnnotation"),
    QCSYNCPROG_TEST1("testTest1"),
    QCSYNCPROG_TEST2("test3"),
    QCSYNC3_WRONGCLASS("wrongClassAnnotation");

    /**
     * The name of the TestSetTest in QC.
     */
    public final String testName;
    /**
     * The name of the method that is connected to the test.
     */
    public final String methodName;
    /**
     * The instance count.
     */
    public final int instanceCount;


    private QCTestUnderTest(String testName, String methodName, int instanceCount) {
        this.testName = testName;
        this.methodName = methodName;
        this.instanceCount = instanceCount;
    }

    private QCTestUnderTest(String testName, String methodName) {
        this(testName, methodName, 0);
    }

    private QCTestUnderTest(String testName) {
        this(testName, testName);
    }

    @Override
    public String toString() {
        return getTestName();
    }

    /**
     * gets the test names for the test sets
     *
     * @return test names
     */
    public static LinkedList<QCTestUnderTest> getTestSetTestNames() {
        return new LinkedList<>(Arrays.asList(QCTestUnderTest.values()));
    }

    /**
     * @return the testName
     */
    public String getTestName() {
        return testName;
    }

    /**
     * @return the methodName
     */
    public String getMethodName() {
        return methodName;
    }

    public int getInstanceCount() {
        return instanceCount;
    }
}
