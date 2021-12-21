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
package eu.tsystems.mms.tic.testframework.qc11connector.constants;

public final class QCConstants {

    private QCConstants() {
    }

    /**
     * QC Path where all TestSets are placed.
     */
    public static final String QC_TESTSUNDERTEST_FOLDER = "Root\\Testerra\\TestSetsUnderTest\\";

    /**
     * Maximum of time that has been gone between QC Synchronization of tests and control by integration tests. Tests
     * take about 15 minutes now, so we chose 20 minutes as max. interval.
     */
    public static final long SYSTEMTEST_SYNCINTERVAL = 3000000;

    public static final long QCREST_SYNCINTERVAL = 15000;
    public static final long QCWEBSERVICE_SYNCINTERVAL = 60000;

    /**
     * Path that does not exist in QC
     */
    public static final String NOT_EXISTING_FOLDER = "Root\\Testerra\\Not\\Existing\\";
    public static final String NOT_EXISTING_TESTSET = "NotExistingTestSet";
    public static final String NOT_EXISTING_PATH = NOT_EXISTING_FOLDER + NOT_EXISTING_TESTSET;

    /**
     * Name of the TestSets for QC Sync Tests, for both System- and UnitTests
     */
    public static final String SYNC_TESTSET_NAME = "SyncTests";
}
