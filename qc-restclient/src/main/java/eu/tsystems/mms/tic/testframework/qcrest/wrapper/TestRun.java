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

import eu.tsystems.mms.tic.testframework.exceptions.SystemException;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.qcrest.clients.QcRestClient;
import eu.tsystems.mms.tic.testframework.qcrest.clients.RestConnector;
import eu.tsystems.mms.tic.testframework.qcrest.generated.Entity;
import java.util.LinkedList;
import java.util.List;

/**
 * Java class for TestRunWr complex type.
 */

public class TestRun extends AbstractEntity implements Loggable {
    /** Logger instance */
    /** List of attachments. */
    private final List<Attachment> attachments;
    /** Test instance the run belongs to */
    private TestSetTest testInstance;
    /** Test (from Testplan) the run belongs to. */
    private QcTest test;

    /**
     * Create an empty TestRun.
     */
    public TestRun() {
        super("run");
        attachments = new LinkedList<Attachment>();
    }

    /**
     * Create TestRun based on a xml entity.
     *
     * @param entity xml response from rest service.
     */
    public TestRun(final Entity entity) {
        super(entity);
        attachments = new LinkedList<Attachment>();
    }

    /**
     * Add an Attachment to the test run. Attachments will only be uploaded if this TestRun is posted through
     * QcRestClient.
     *
     * @param attachment Attachment to add.
     */
    public void addAttachments(final Attachment attachment) {
        attachments.add(attachment);
        if (attachment.getParentType() == null) {
            attachment.setEntityName("run");
        }
        setHasAttachment(true);
    }

    /**
     * Add list of Attachments to the TestRun.
     *
     * @param list Attachments to add.
     */
    public void addAttachments(final List<Attachment> list) {
        attachments.addAll(list);
        setHasAttachment(true);
    }

    /**
     * Gets all Attachments of this TestRun.
     *
     * @return List of Attachments.
     */
    public List<Attachment> getAttachments() throws Exception {
        if (attachments.isEmpty()) {
            final RestConnector connector = RestConnector.getInstance();
            final String restUrl = connector.buildEntityCollectionUrl("run") + "/" + getId() + "/attachments";
            List<Entity> entities;
            entities = connector.getEntities(restUrl, null);
            for (Entity entityAtt : entities) {
                attachments.add(new Attachment(entityAtt));
            }
        }
        return attachments;
    }

    /**
     * Get cycle-id of testrun (id of TestSet containing the TestRun).
     *
     * @return id of testset the run belongs to.
     */
    public int getCycleId() {
        final String field = getFieldValueByName("cycle-id");
        if (field != null) {
            return Integer.parseInt(field);
        } else {
            return 0;
        }
    }

    /**
     * Gets the value of the duration field.
     *
     * @return duration as int or 0 if field not set.
     *
     */
    public int getDuration() {
        final String field = getFieldValueByName("duration");
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
    public String getHost() {
        return getFieldValueByName("host");
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
     * Gets the value of the appropriate entity field.
     *
     * @return Fields value as String object.
     */
    public String getOsBuild() {
        return getFieldValueByName("os-build");
    }

    /**
     * Gets the value of the appropriate entity field.
     *
     * @return Fields value as String object.
     */
    public String getOsName() {
        return getFieldValueByName("os-name");
    }

    /**
     * Gets the value of the appropriate entity field.
     *
     * @return Fields value as String object.
     */
    public String getOsSP() {
        return getFieldValueByName("os-sp");
    }

    /**
     * Gets the value of the appropriate entity field.
     *
     * @return Fields value as String object.
     */
    public String getOwner() {
        return getFieldValueByName("owner");
    }

    /**
     * Gets the value of the testcycl-id field (id of TestSetTest this TestRun belongs to).
     *
     * @return possible object is {@link int }
     *
     */
    public int getParentId() {
        final String field = getFieldValueByName("testcycl-id");
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
     * gets the test instance
     *
     * @return the testInstance
     */
    public TestSetTest getTestInstance() {
        if (testInstance == null) {
            final int field = getParentId();
            if (field == 0) {
                log().error("Could not get Test for Run " + getName() + ", because test-id is not set.");
            } else {
                try {
                    testInstance = QcRestClient.getTestSetTestById(field);
                } catch (Exception e) {
                    log().error("Rest error while getting the test for testRun " + getName(), e);
                }
            }
        }

        if (testInstance == null) {
            throw new SystemException("Error getting TestInstance, see ERROR above");
        }

        return testInstance;
    }

    /**
     * gets the test
     *
     * @return the test
     */
    public QcTest getTest() {
        if (test == null) {
            final int field = getTestId();
            if (field == 0) {
                log().error("Could not get Test for Run " + getName() + " cause test-id is not set.");
            } else {
                try {
                    test = QcRestClient.getTestById(field);
                } catch (Exception e) {
                    log().error("Rest-error while getting the test for testRun " + getName(), e);
                }
            }
        }
        return test;
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
     * Gets the value of the testVersion property.
     *
     * @return Version number of test.
     * @deprecated can not be used with qc 12
     */
    @Deprecated
    public int getTestVersion() {
        final String field = getFieldValueByName("run-ver-stamp");
        if (field == null) {
            return 0;
        }
        return Integer.parseInt(field);
    }

    /**
     * Gets the value of a user defined field of the entity.
     *
     * @param index Number of UserField to get (01-24).
     * @return possible object is {@link String }
     */
    public String getUserField(final String index) {
        return getFieldValueByName("user-" + index);
    }

    /**
     * Gets the value of the attachment field.
     *
     * @return true, if test run has attachments.
     */
    public boolean isHasAttachmentWr() {
        final String field = getFieldValueByName("attachment");
        return field != null && "Y".equals(field);
    }

    /**
     * Sets the value of the cycle-id field, that represents the test set the testrun is in.
     *
     * @param value New value to set.
     */
    public void setCycleId(final int value) {
        setFieldValue("cycle-id", Integer.toString(value));
    }

    /**
     * Sets the value of the duration property.
     *
     * @param value New value to set.
     */
    public void setDuration(final int value) {
        setFieldValue("duration", Integer.toString(value));
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
     * Sets the value of the hasAttachmentWr property.
     *
     * @param value Value to set.
     */
    public void setHasAttachment(final boolean value) {
        setFieldValue("attachment", value ? "Y" : "N");
    }

    /**
     * Sets the value of the host property.
     *
     * @param value New value to set.
     */
    public void setHost(final String value) {
        setFieldValue("host", value);
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
     * Sets the value of the osBuild property.
     *
     * @param value New value to set.
     *
     */
    public void setOsBuild(final String value) {
        setFieldValue("os-build", value);
    }

    /**
     * Sets the value of the osName property.
     *
     * @param value New value to set.
     *
     */
    public void setOsName(final String value) {
        setFieldValue("os-name", value);
    }

    /**
     * Sets the value of the osSP property.
     *
     * @param value New value to set.
     *
     */
    public void setOsSP(final String value) {
        setFieldValue("os-sp", value);
    }

    /**
     * sets the value of the owner
     *
     * @param value Owner of run.
     */
    public void setOwner(final String value) {
        setFieldValue("owner", value);
    }

    /**
     * Sets the value of the testcycl-id field representing the testinstance this test belongs to.
     *
     * @param value New value to set.
     *
     */
    public void setParentId(final int value) {
        setFieldValue("testcycl-id", Integer.toString(value));
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

    /**
     * Sets the value of the testVersion property.
     *
     * @param value Value to set.
     * @deprecated can not be used with qc 12
     */
    @Deprecated
    public void setTestVersion(final int value) {
        setFieldValue("run-ver-stamp", Integer.toString(value));
    }

    /**
     * Sets the value of the user field.
     *
     * @param value allowed object is {@link String }
     * @param index Number of user field to set (01 - 24).
     */
    public void setUserField(final String index, final String value) {
        setFieldValue("user-" + index, value);
    }

    @Override
    public String toString() {
        final StringBuilder out = new StringBuilder();
        out.append("Run: ").append(getName());
        out.append(" (").append(getExecutionDate());
        out.append(" ").append(getExecutionTime()).append(") ");
        if (getParentId() != 0) {
            out.append("for TestSetTest:").append(getTestInstance().toString());
        }
        return out.toString();
    }
}
