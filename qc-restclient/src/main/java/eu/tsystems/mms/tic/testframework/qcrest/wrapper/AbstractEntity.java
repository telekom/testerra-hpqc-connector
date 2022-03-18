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

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.qcrest.generated.Entity;
import eu.tsystems.mms.tic.testframework.qcrest.generated.Entity.Fields;
import eu.tsystems.mms.tic.testframework.qcrest.generated.Entity.Fields.Field;
import eu.tsystems.mms.tic.testframework.qcrest.generated.Entity.Fields.Field.Value;
import eu.tsystems.mms.tic.testframework.qcrest.generated.ObjectFactory;
import java.util.LinkedList;
import java.util.List;

/**
 * Abstract parent class for all Entities used by a client.
 *
 * @author sepr
 */
public abstract class AbstractEntity implements Loggable {

    /** Underlying xml-based entity. */
    private final Entity entity;

    /** Id of entity. */
    private int idField;

    /**
     * Creates a new AbstractEntity from its XML representation and sets its id.
     *
     * @param entity Entity parsed from Rest Response.
     */
    protected AbstractEntity(final Entity entity) {
        this.entity = entity;
        final String idValue = getFieldValueByName("id");
        if (idValue != null) {
            // If entity is created by User.
            this.idField = Integer.parseInt(idValue);
        }
    }

    /**
     * Creates a new entity of the given type.
     *
     * @param entityType Type of entity (e.g. test-instance, run, ...)
     */
    protected AbstractEntity(final String entityType) {
        final ObjectFactory objectFactory = new ObjectFactory();
        entity = objectFactory.createEntity();
        entity.setType(entityType);
        final Fields fields = objectFactory.createEntityFields();
        entity.setFields(fields);
        // LOGGER.trace("Required fields for " + entityType + ": " + UtilClient.getRequiredFields(entityType));
    }

    /**
     * gets the entity
     *
     * @return the entity
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Get the value String of the field with the given name of this entity. Use only for fields with a single value!
     *
     * @param name Name of the field to get the value from.
     * @return Value as string or null if field or value does not exist.
     */
    public final String getFieldValueByName(final String name) {
        final List<Field> fields = entity.getFields().getField();
        for (Field field : fields) {
            if (field.getName().equals(name)) {
                List<Value> value = field.getValue();
                if (value.size() == 0) {
                    return null;
                }
                else {
                    return value.get(0).getValue();
                }
            }
        }
        log().debug("No field with name " + name + " found for entity.");
        return null;
    }

    /**
     * gets the id
     *
     * @return the id
     */
    public int getId() {
        return idField;
    }

    /**
     * Sets the value of a field for this entity. Not existing fields will be created. Use only for fields with a single
     * value!
     *
     * @param fieldName Name of field.
     * @param fieldValue Value of field.
     */
    public final void setFieldValue(final String fieldName, final String fieldValue) {
        final List<Field> fields = entity.getFields().getField();
        Field fieldToSet = null;
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                fieldToSet = field;
                break;
            }
        }
        if (fieldToSet == null) {
            log().trace("Field with name " + fieldName + " created.");
            final ObjectFactory oFactory = new ObjectFactory();
            fieldToSet = oFactory.createEntityFieldsField();
            fieldToSet.setName(fieldName);
            fields.add(fieldToSet);
        }
        List<Value> fieldValues = fieldToSet.getValue();
        if (fieldValues == null) {
            fieldValues = new LinkedList<Value>();
        }
        final Value valueToSet;
        if (fieldValues.isEmpty()) {
            final ObjectFactory oFactory = new ObjectFactory();
            valueToSet = oFactory.createEntityFieldsFieldValue();
            fieldValues.add(valueToSet);
        } else {
            valueToSet = fieldValues.get(0);
        }
        valueToSet.setValue(fieldValue);
        log().trace("Value of field " + fieldName + "set.");
    }
}
