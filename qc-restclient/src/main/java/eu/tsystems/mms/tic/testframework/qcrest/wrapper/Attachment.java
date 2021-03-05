/*
 * Created on 20.02.2013
 *
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.qcrest.wrapper;

import eu.tsystems.mms.tic.testframework.qcrest.clients.Response;
import eu.tsystems.mms.tic.testframework.qcrest.clients.RestConnector;
import eu.tsystems.mms.tic.testframework.qcrest.generated.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Java class for AttachmentWr complex type.
 */
public class Attachment extends AbstractEntity {

    /**
     * The type of the attachment.
     *
     * @author sepr
     */
    public enum Type {

        /** FILE, URL */
        FILE("File"), URL("Internet Web Address");

        /** The Attachment type. On of FILE or URL */
        private final String type;

        /**
         * Private constructor
         *
         * @param pType The attachment type.
         */
        private Type(final String pType) {
            type = pType;
        }

        /**
         * Returns the value.
         *
         * @return The value.
         */
        public String getValue() {
            return type;
        }
    }

    /** File content. */
    private byte[] content;

    /**
     * Create a new Attachment. An Underlying entity object will be created.
     */
    public Attachment() {
        super("attachment");
    }

    /**
     * Build an attachment from an existing Entity object.
     *
     * @param entity Underlying xml representation.
     */
    public Attachment(final Entity entity) {
        super(entity);
    }

    /**
     * Gets the value of the content property.
     *
     * @return possible object is byte[]
     */
    public byte[] getContent() throws Exception {
        if (content == null) {
            final Logger logger = LoggerFactory.getLogger(Attachment.class);
            if (!getRefType().equalsIgnoreCase(Attachment.Type.FILE.getValue())) {
                logger.error("Can't get byte[] content for an URL Attachment");
                return null;
            }
            final RestConnector connector = RestConnector.getInstance();
            final String restUrl = connector.buildEntityCollectionUrl(getParentType()) + "/" + getParentId()
                    + "/attachments/"
                    + getName();
            final Response response;
            response = connector.httpGet(restUrl, null);
            content = response.getResponseData();
        }
        return content.clone();
    }

    /**
     * Gets the value of the appropriate entity field.
     *
     * @return Fields value as String object.
     */
    public String getDescription() {
        return getFieldValueByName("description");
    }

    /**
     * Gets the value of the appropriate entity field.
     *
     * @return Fields value as String object.
     */
    public String getParentType() {
        return getFieldValueByName("parent-type");
    }

    /**
     * Gets the value of the appropriate entity field.
     *
     * @return Fields value as String object.
     */
    public String getLastModified() {
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
     * @return Fields value as int or 0.
     */
    public int getParentId() {
        final String field = getFieldValueByName("parent-id");
        return Integer.parseInt(field);
    }

    /**
     * Gets the value of the appropriate entity field.
     *
     * @return Fields value as String object.
     */
    public String getRefType() {
        return getFieldValueByName("ref-type");
    }

    /**
     * Gets the value of the appropriate entity field.
     *
     * @return Fields value as long or 0.
     */
    public long getSize() {
        final String field = getFieldValueByName("file-size");
        if (field == null) {
            return 0;
        }
        return Long.parseLong(field);
    }

    /**
     * Sets the value of the content property.
     *
     * @param value allowed object is byte[]
     */
    public void setContent(final byte[] value) {
        this.content = value.clone();
        setFieldValue("file-size", Integer.toString(value.length));
    }

    /**
     * Sets the value of the description property.
     *
     * @param value New value to set.
     *
     */
    public void setDescription(final String value) {
        setFieldValue("description", value);
    }

    /**
     * Set the type of the entity this attachment belongs to.
     *
     * @param value New value to set.
     */
    public void setEntityName(final String value) {
        setFieldValue("parent-type", value);
    }

    /**
     * Sets the value of the lastModified property.
     *
     * @param value New value to set.
     *
     */
    public void setLastModified(final String value) {
        setFieldValue("last-modified", value);
    }

    /**
     * Sets the value of the name property. If the Attachment is of type Url this field should contain the weblink.
     *
     * @param value New value to set.
     *
     */
    public void setName(final String value) {
        setFieldValue("name", value);
    }

    /**
     * Sets the value of the refId property.
     *
     * @param value New value to set.
     *
     */
    public void setParentId(final int value) {
        setFieldValue("parent-id", Integer.toString(value));
    }

    /**
     * Sets the value of the refType property.
     *
     * @param value New value to set.
     *
     */
    public void setRefType(final String value) {
        setFieldValue("ref-type", value);
    }

    @Override
    public String toString() {
        return getName() + " for entity " + getEntity() + " with id " + getParentId();
    }

}
