/* 
 * Created on 20.02.2013
 * 
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.qcrest.wrapper;

import eu.tsystems.mms.tic.testframework.qcrest.generated.Entity;

/**
 * Java class for TestWr complex type.
 * 
 * @author sepr
 */
public class QcTest extends AbstractEntity {

    /**
     * Default constructor.
     * 
     * @param entity Entity representing the object.
     */
    public QcTest(final Entity entity) {
        super(entity);
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
    public String getName() {
        return getFieldValueByName("name");
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
     * Gets the value of a user defined field.
     * 
     * @param index of userfield (01-24).
     * @return possible object is {@link String }
     * 
     */
    public String getUserField(final String index) {
        return getFieldValueByName("user-" + index);
    }

    /**
     * Sets the value of the description property. Setting a value won't have any affect as long as you don't post this
     * object to the REST Service.
     * 
     * @param value New value to set.
     * 
     */
    public void setDescription(final String value) {
        setFieldValue("description", value);
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
     * Sets the value of the type property. Setting a value won't have any affect as long as you don't post this object
     * to the REST Service.
     * 
     * @param value New value to set.
     * 
     */
    public void setType(final String value) {
        setFieldValue("subtype-id", value);
    }

    /**
     * Sets the value of the user defined field. Setting a value won't have any affect as long as you don't post this
     * object to the REST Service.
     * 
     * @param index Index of userfield (01-24) to set.
     * @param value allowed object is {@link String }
     * 
     */
    public void setUserField(final String index, final String value) {
        setFieldValue("user-" + index, value);
    }
}
