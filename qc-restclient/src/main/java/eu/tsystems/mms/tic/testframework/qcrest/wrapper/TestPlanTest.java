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

import eu.tsystems.mms.tic.testframework.qcrest.clients.FolderFinder;
import eu.tsystems.mms.tic.testframework.qcrest.clients.RestConnector;
import eu.tsystems.mms.tic.testframework.qcrest.generated.Entity;

/**
 * Java class for TestWr complex type.
 *
 * @author sepr
 */
public class TestPlanTest extends AbstractEntity {
    /** Cache parent */
    private TestPlanFolder parent;

    /**
     * Default constructor.
     *
     * @param entity Entity representing the object.
     */
    public TestPlanTest(final Entity entity) {
        super(entity);
    }

    /**
     * constructor to create new instance.
     */
    public TestPlanTest() {
        super("test");
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
     * Get's the folder that contains this test.
     *
     * @return TestPlanFolder object
     */
    public TestPlanFolder getParent() {
        if (parent == null) {
            final int parentId = Integer.parseInt(getFieldValueByName("parent-id"));
            if (parentId != 0) {
                // TODO replace with FolderFinder
                Entity oneEntity;
                try {
                    oneEntity = RestConnector.getInstance().getEntity(
                            RestConnector.getInstance().buildEntityCollectionUrl("test-folder") + "/" + parentId,
                            null);
                } catch (Exception e) {
                    log().error(e.getMessage());
                    return null;
                }
                if (oneEntity != null) {
                    parent = new TestPlanFolder(oneEntity);
                }
            } else {
                log().trace("Can't get instance of Subject folder");
                return null;
            }
        }
        return parent;
    }

    /**
     * Gets the TestPlanFolders path by recursive call of getName on parents. RuntimeException could occur if there is
     * an IOException while getting a parent folder.
     *
     * @return The TestPlanFolders path.
     */
    public String getPath() {
        // first lookup in cache
        String pathById = FolderFinder.getPathById(getId());
        if (!pathById.equals(FolderFinder.NOT_FOUND)) {
            return pathById;
        }

        // go to hell and recursive find the parents for path
        final StringBuilder path = new StringBuilder();
        path.append(getFieldValueByName("name"));
        TestPlanFolder parentFolder = getParent();
        while (parentFolder != null) {
            path.insert(0, parentFolder.getName() + "\\");
            parentFolder = parentFolder.getParent();
        }
        return path.toString();
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
     * Gets the value of the appropriate entity field.
     *
     * @return Fields value as String object.
     */
    public int getStepsCount() {
        String value = getFieldValueByName("steps");
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            log().warn("Could not get Steps Count of property value " + value);
            return 0;
        }
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
     * Sets the value of the parent-id property. Setting a value won't have any affect as long as you don't post this
     * object to the REST Service.
     *
     * @param value New value to set.
     *
     */
    public void setParentId(final int value) {
        setFieldValue("parent-id", value + "");
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

    @Override
    public String toString() {
        return "Test {name:" + getName() + ", status:" + getStatus() + ", folder:" + getPath() + "}";
    }
}
