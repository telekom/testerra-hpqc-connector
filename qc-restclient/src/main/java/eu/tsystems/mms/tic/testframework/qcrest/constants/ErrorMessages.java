/*
 * Testerra
 *
 * (C) 2013, René Habermann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.qcrest.constants;

/**
 * User: rnhb
 * Date: 20.12.13
 */
public final class ErrorMessages {

    private ErrorMessages() {
    }

    /**
     * tests that test set is not found
     *
     * @param testSetName .
     * @return string message
     */
    public static String testSetNotFound(String testSetName) {
        return "The TestSet \""
                + testSetName
                + "\" was not found. It probably doesn't exist.";
    }

}
