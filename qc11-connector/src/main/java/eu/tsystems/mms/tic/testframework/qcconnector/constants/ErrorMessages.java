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

public final class ErrorMessages {

    private ErrorMessages() {
    }

    /**
     * Error message when ta_scriptname in QC was not set
     *
     * @param testSetPath .
     * @param annotatedClass .
     * @return formatted error message
     */
    public static String wrongQCTestSetAnnotation(String testSetPath, String annotatedClass) {
        return "Cannot find "
                + testSetPath
                + " in QC. Please correct the path in QCTestSet Annotation in Class "
                + annotatedClass
                + "!";
    }

    /**
     * Error message when no method with matching name was found in testset.
     *
     * @param methodname .
     * @param testSetPath .
     * @return formatted error message
     */
    public static String noTestMethodFoundInQC(String methodname, String testSetPath) {
        return "No method "
                + methodname
                + " found in Testset "
                + testSetPath
                + ". Could not synchronize with QC!";
    }

}
