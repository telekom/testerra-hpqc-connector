/* 
 * Created on 20.02.2013
 * 
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.qcrest.wrapper;

import eu.tsystems.mms.tic.testframework.qcrest.clients.QcRestClient;
import eu.tsystems.mms.tic.testframework.qcrest.generated.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Java class for TestSetTestWr complex type.
 * 
 * @author sepr
 */
public class TestSetTest extends AbstractEntity {
    /** Logger instance */
    private static final Logger LOGGER = LoggerFactory.getLogger(TestSetTest.class);

    /** TestSet this test-instance belongs to. */
    private TestSet testSet;

    /** The referenced Test. */
    private QcTest test;

    /**
     * Empty constructor creating a new entity.
     */
    public TestSetTest() {
        super("test-instance");
    }

    /**
     * Default constructor.
     * 
     * @param entity Entity representing the object.
     */
    public TestSetTest(final Entity entity) {
        super(entity);
    }

    /**
     * gets the cycleID
     *
     * @return Id of TestSet this instance belongs to.
     */
    public int getCycleId() {
        final String field = getFieldValueByName("cycle-id");
        return Integer.parseInt(field);
    }

    /**
     * gets the test instance name including the instance count
     * e.g. testMethodName [1]
     *
     * @return name of test instance
     */
    public String getTestInstanceName() {

        return getFieldValueByName("name");
    }

    /**
     * Gets the value of the appropriate entity field.
     * 
     * @return Fields value as String object.
     */
    public String getExecutionDate() {
        return getFieldValueByName("exec-date");
    }

    /**
     * Gets the value of the appropriate entity field.
     * 
     * @return Fields value as String object.
     */
    public String getExecutionTime() {
        return getFieldValueByName("exec-time");
    }

    /**
     * Gets the value of the appropriate entity field.
     * 
     * @return Fields value as String object.
     */
    public String getHostName() {
        return getFieldValueByName("host-name");
    }

    /**
     * Gets the value of the order field.
     * 
     * @return value as int or 0 if not existing.
     * @deprecated can not be used with qc 12
     */
    @Deprecated
    public int getOrder() {
        final String field = getFieldValueByName("test-order");
        if (field != null) {
            return Integer.valueOf(field);
        }
        return 0;
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
     * Gets the Test that is referenced through the test-id field.
     * 
     * @return QcTest object or null (if test-id not set or IOException while getting).
     */
    public QcTest getTest() {
        if (test == null) {
            final String idField = getFieldValueByName("test-id");
            if (idField != null) {
                try {
                    test = QcRestClient.getTestById(Integer.parseInt(idField));
                } catch (IOException e) {
                    LOGGER.error("Rest-error while getting test from test-instance.", e);
                    return test;
                }
            } else {
                LOGGER.error("Could not get Test from Test-Instance cause test-id is not set.");
            }
        }
        return test;
    }

    /**
     * Gets the value of the appropriate entity field.
     * 
     * @return Fields value as String object.
     */
    public String getTesterName() {
        return getFieldValueByName("actual-tester");
    }

    /**
     * Get the id of test in test plan.
     * 
     * @return value of test-id field.
     */
    public int getTestId() {
        final String field = getFieldValueByName("test-id");
        if (field != null) {
            return Integer.parseInt(field);
        }
        return 0;
    }

    /**
     * Gets the TestSet that is referenced through the field cycle-id.
     * 
     * @return Found TestSet or null (if cycle-id not set or IOException);
     * 
     */
    public TestSet getTestSet() {
        if (testSet == null) {
            final String field = getFieldValueByName("cycle-id");
            if (field != null) {
                try {
                    testSet = QcRestClient.getTestSetById(Integer.parseInt(field));
                } catch (IOException e) {
                    LOGGER.error("Rest error while getting testSet from test-instance.", e);
                }
            } else {
                LOGGER.error("Could not get TestSet of test-instance, cause cycle-id is not set.");
            }
        }
        return testSet;
    }

    /**
     * gets the field value of the type
     *
     * @return Type value
     */
    public String getType() {
        return getFieldValueByName("subtype-id");
    }

    /**
     * Gets the value of a user defined field.
     * 
     * @param index of userfield (01-24).
     * @return Value as String object.
     * 
     */
    public String getUserField(final String index) {
        return getFieldValueByName("user-" + index);
    }

    /**
     * Gets the value of the attachement field.
     * 
     * @return true if TestSetTest has attachments.
     */
    public boolean isHasAttachement() {
        final String field = getFieldValueByName("attachment");
        return field != null && "Y".equals(field);
    }

    /**
     * sets the field value of the cycleID
     *
     * @param value Value to set.
     */
    public void setCycleId(final int value) {
        setFieldValue("cycle-id", value + "");
    }

    /**
     * Sets the value of the executionDate property. Setting a value won't have any affect as long as you don't post
     * this object to the REST Service.
     * 
     * @param value New value to set.
     * 
     */
    public void setExecutionDate(final String value) {
        setFieldValue("exec-date", value);
    }

    /**
     * Sets the value of the executionTime property. Setting a value won't have any affect as long as you don't post
     * this object to the REST Service.
     * 
     * @param value New value to set.
     * 
     */
    public void setExecutionTime(final String value) {
        setFieldValue("exec-time", value);
    }

    /**
     * Sets the value of the hasAttachement property. Setting a value won't have any affect as long as you don't post
     * this object to the REST Service.
     * 
     * @param value New value to set.
     */
    public void setHasAttachement(final boolean value) {
        setFieldValue("attachment", value ? "Y" : "N");
    }

    /**
     * Sets the value of the hostName property. Setting a value won't have any affect as long as you don't post this
     * object to the REST Service.
     * 
     * @param value New value to set.
     * 
     */
    public void setHostName(final String value) {
        setFieldValue("host-name", value);
    }

    /**
     * Sets the value of the order property. Setting a value won't have any affect as long as you don't post this object
     * to the REST Service.
     * 
     * @param value New value to set.
     * @deprecated can not be used with qc 12
     */
    @Deprecated
    public void setOrder(final int value) {
        setFieldValue("test-order", Integer.toString(value));
    }

    /**
     * Sets the value of the status property. Setting a value won't have any affect as long as you don't post this
     * object to the REST Service.
     * 
     * @param value New value to set.
     * 
     */
    public void setStatus(final String value) {
        setFieldValue("status", value);
    }

    /**
     * Sets the value of the testerName property. Setting a value won't have any affect as long as you don't post this
     * object to the REST Service.
     * 
     * @param value New value to set.
     * 
     */
    public void setTesterName(final String value) {
        setFieldValue("actual-tester", value);
    }

    /**
     * sets the value of the testID
     *
     * @param value New value to set.
     */
    public void setTestId(final int value) {
        setFieldValue("test-id", value + "");
    }

    /**
     * sets the value of the type
     *
     * @param value to set.
     */
    public void setType(final String value) {
        setFieldValue("subtype-id", value);
    }

    /**
     * Sets the value of the user defined field.
     * 
     * @param index Index of userfield (01-24).
     * @param value allowed object is {@link String }
     * 
     */
    public void setUserField(final String index, final String value) {
        setFieldValue("user-" + index, value);
    }

    @Override
    public String toString() {
        return getTestSet().getTestSetFolder().getPath() + "\\" + getTestSet().getName() + " - " + getTest().getName();
    }
}
