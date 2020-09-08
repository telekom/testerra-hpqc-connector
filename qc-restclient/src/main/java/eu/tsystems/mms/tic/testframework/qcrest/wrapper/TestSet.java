/*
 * Created on 21.02.2013
 *
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.qcrest.wrapper;

import eu.tsystems.mms.tic.testframework.qcrest.clients.FolderFinder;
import eu.tsystems.mms.tic.testframework.qcrest.clients.RestConnector;
import eu.tsystems.mms.tic.testframework.qcrest.generated.Entity;
import java.util.ArrayList;
import java.util.List;

/**
 * Java class for TestSetWr complex type.
 *
 * @author sepr
 */
public class TestSet extends AbstractEntity {
    /** TestSetFolder containing this testSet. */
    private TestSetFolder testSetFolder;

    /**
     * Constructor.
     *
     * @param entity Entity representing the TestSet.
     */
    public TestSet(final Entity entity) {
        super(entity);
    }

    /**
     * Constructor to create new items.
     */
    public TestSet() {
        super("test-set");
    }

    /**
     * Gets the attachments of this entity.
     *
     * @return Array of attachments.
     */
    public Attachment[] getAttachments() throws Exception {
        final List<Attachment> attachments = new ArrayList<Attachment>();

        final RestConnector connector = RestConnector.getInstance();
        final String restUrl = connector.buildEntityCollectionUrl("test-set") + "/" + getId() + "/attachments";
        List<Entity> entities;
        entities = connector.getEntities(restUrl, null);
        for (Entity entityAtt : entities) {
            attachments.add(new Attachment(entityAtt));
        }
        return attachments.toArray(new Attachment[attachments.size()]);
    }

    /**
     * Gets the value of the appropriate entity field.
     *
     * @return Fields value as String object.
     */
    public String getCloseDate() {
        return getFieldValueByName("close-date");
    }

    /**
     * Gets the value of the appropriate entity field.
     *
     * @return Fields value as String object.
     */
    public String getModifiedDate() {
        return getFieldValueByName("last-modified");
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
    public String getOpenDate() {
        return getFieldValueByName("open-date");
    }

    /**
     * Gets the value of the appropriate entity field.
     *
     * @return Fields value as String object.
     */
    public int getParentId() {
        final String value = getFieldValueByName("parent-id");
        if (value != null) {
            return Integer.parseInt(value);
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
     * Get the TestSetFolder that contains the TestSet through the value of the parent-id field.
     *
     * @return TestSetFolder or null (if not found).
     */
    public TestSetFolder getTestSetFolder() {
        if (testSetFolder == null) {
            final String field = getFieldValueByName("parent-id");
            if (field == null) {
                log().error("Could not get TestSetFolder for TestSet " + getName() + " cause parent-id is not set.");
            } else {
                testSetFolder = FolderFinder.getTestSetFolderById(Integer.valueOf(field));
            }
        }
        return testSetFolder;
    }

    /**
     * Gets the value of the appropriate entity field.
     *
     * @return Fields value as String object.
     */
    public String getType() {
        return getFieldValueByName("subtype-id");
    }

    /**
     * Gets the value of the attachment field.
     *
     * @return true if TestSet has Attachments.
     */
    public boolean isHasAttachment() {
        final String field = getFieldValueByName("attachment");
        return field != null && "Y".equals(field);
    }

    /**
     * Sets the value of the closeDate property. Setting a value won't have any affect as long as you don't post this
     * object to the REST Service.
     *
     * @param value New value to set.
     *
     */
    public void setCloseDate(final String value) {
        setFieldValue("close-date", value);
    }

    /**
     * Sets the value of the hasAttachment property. Setting a value won't have any affect as long as you don't post
     * this object to the REST Service.
     *
     * @param value New value to set.
     */
    public void setHasAttachment(final boolean value) {
        setFieldValue("attachement", value ? "Y" : "N");
    }

    /**
     * Sets the value of the modifiedDate property. Setting a value won't have any affect as long as you don't post this
     * object to the REST Service.
     *
     * @param value New value to set.
     *
     */
    public void setModifiedDate(final String value) {
        setFieldValue("last-modified", value);
    }

    /**
     * Sets the value of the name property. Setting a value won't have any affect as long as you don't post this object
     * to the REST Service.
     *
     * @param value New value to set.
     *
     */
    public void setName(final String value) {
        setFieldValue("name", value);
    }

    /**
     * Sets the value of the openDate property. Setting a value won't have any affect as long as you don't post this
     * object to the REST Service.
     *
     * @param value New value to set.
     *
     */
    public void setOpenDate(final String value) {
        setFieldValue("open-date", value);
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
     * Sets the value of the testSetFolder property. Setting a value won't have any affect as long as you don't post
     * this object to the REST Service.
     *
     * @param value New value to set.
     *
     */
    public void setTestSetFolder(final TestSetFolder value) {
        testSetFolder = value;
        setFieldValue("parent-id", Integer.toString(value.getId()));
    }

    /**
     * Sets the value of the subtype-id property. Setting a value won't have any affect as long as you don't post this
     * object to the REST Service.
     *
     * @param value New value to set.
     *
     */
    public void setType(final String value) {
        setFieldValue("subtype-id", value);
    }

    @Override
    public String toString() {
        return getTestSetFolder().getPath() + "\\" + getName();
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof TestSet) && (getId() >= 0)
                ? getId() == (((TestSet) other).getId())
                : super.equals(other);
    }

    @Override
    public int hashCode() {
        return (getId() >= 0)
                ? getId()
                : super.hashCode();
    }
}
