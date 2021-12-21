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
package eu.tsystems.mms.tic.testframework.qcconnector.exceptions;

import eu.tsystems.mms.tic.testframework.report.model.Serial;

import java.io.IOException;

/**
 * Own Exception to bundle different errors thrown during test result synchronization (to qc/testlink).
 *
 * @author sepr
 */
public class TesterraQcResultSyncException extends IOException {

    /**
     * generated serialVersioUID
     */
    private static final long serialVersionUID = Serial.SERIAL;

    /**
     * default Constructor.
     *
     * @param cause Exception causing the sync error.
     */
    public TesterraQcResultSyncException(Throwable cause) {
        super(cause);
    }

    /**
     * default error message
     *
     * @param string error message
     */
    public TesterraQcResultSyncException(String string) {
        super(string);
    }
}
