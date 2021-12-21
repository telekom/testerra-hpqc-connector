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
package eu.tsystems.mms.tic.testframework.qcrest.wrapper;

import eu.tsystems.mms.tic.testframework.qcrest.generated.Entity;

/**
 * Java class for representing an TestStep of a TestRun
 */

public class RunStep extends AbstractEntity {

    /**
     * Create an empty TestRun.
     */
    public RunStep() {
        super("run-step");
    }

    /**
     * Create TestRun based on a xml entity.
     *
     * @param entity xml response from rest service.
     */
    public RunStep(final Entity entity) {
        super(entity);
    }

    /**
     * Gets the value of the appropriate entity field.
     *
     * @return Fields value as String object.
     */
    public String getExecutionDate() {
        return getFieldValueByName("execution-date");
    }

    /**
     * Gets the value of the appropriate entity field.
     *
     * @return Fields value as String object.
     */
    public String getExecutionTime() {
        return getFieldValueByName("execution-time");
    }

    /**
     * Gets the value of the appropriate entity field.
     *
     * @return Fields value as String object.
     */
    public String getName() {
        return getFieldValueByName("name");
    }

    /**
     * Gets the value of the testcycl-id field (id of TestSetTest this TestRun belongs to).
     *
     * @return possible object is {@link int }
     *
     */
    public int getParentId() {
        final String field = getFieldValueByName("parent-id");
        if (field != null) {
            return Integer.parseInt(field);
        } else {
            return 0;
        }
    }

    /**
     * Gets the value of the appropriate entity field.
     *
     * @return Fields value as String object.
     */
    public String getStatus() {
        return getFieldValueByName("status");
    }

    /**
     * Get test-id of testrun
     *
     * @return id of test (from test plan) the run belongs to (or 0 if not set).
     */
    public int getTestId() {
        final String field = getFieldValueByName("test-id");
        if (field != null) {
            return Integer.parseInt(field);
        } else {
            return 0;
        }
    }

    /**
     * Sets the value of the executionDate property.
     *
     * @param value New value to set.
     */
    public void setExecutionDate(final String value) {
        setFieldValue("execution-date", value);
    }

    /**
     * Sets the value of the executionTime property.
     *
     * @param value New value to set.
     */
    public void setExecutionTime(final String value) {
        setFieldValue("execution-time", value);
    }

    /**
     * Sets the value of the name property.
     *
     * @param value New value to set.
     */
    public void setName(final String value) {
        setFieldValue("name", value);
    }

    /**
     * Sets the value of the parent-id field representing the testrun this step belongs to.
     *
     * @param value New value to set.
     *
     */
    public void setParentId(final int value) {
        setFieldValue("parent-id", Integer.toString(value));
    }

    /**
     * Sets the value of the status property.
     *
     * @param value New value to set.
     *
     */
    public void setStatus(final String value) {
        setFieldValue("status", value);
    }

    /**
     * Sets the value of the test-id field, that represents the test (from test plan) the testrun belongs to.
     *
     * @param value id of referenced test.
     *
     */
    public void setTestId(final int value) {
        setFieldValue("test-id", Integer.toString(value));
    }
}
