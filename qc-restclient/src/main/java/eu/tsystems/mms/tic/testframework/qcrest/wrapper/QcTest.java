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
