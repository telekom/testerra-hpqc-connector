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
import eu.tsystems.mms.tic.testframework.qcrest.clients.FolderFinder;
import eu.tsystems.mms.tic.testframework.qcrest.clients.RestConnector;
import eu.tsystems.mms.tic.testframework.qcrest.generated.Entity;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.LoggerFactory;

/**
 * @author sepr
 *
 */
public class TestPlanFolder extends AbstractEntity implements Loggable {

    /** Parent of the TestSetFolder. */
    private TestPlanFolder parent;

    /**
     * Default constructor.
     *
     * @param entity Entity representing the object.
     */
    public TestPlanFolder(final Entity entity) {
        super(entity);
    }

    /**
     * constructor to create new entity.
     */
    public TestPlanFolder() {
        super("test-folder");
    }

    /**
     * Gets a list of TestSetFolders that are the children of this folder.
     *
     * @return List of children.
     */
    public List<TestPlanFolder> getChildren() throws Exception {
        final List<TestPlanFolder> children = new LinkedList<TestPlanFolder>();
        // DO NOT CHECK the number of sons, it doesnt work!
        // final int noOfChildren = Integer.parseInt(getFieldValueByName("no-of-sons"));
        // if (noOfChildren == 0) {
        // return children;
        // }
        final String queryUrl = "query={parent-id[=" + getId() + "]}";
        final RestConnector connector = RestConnector.getInstance();
        List<Entity> entities;
        entities = connector.getEntities(connector.buildEntityCollectionUrl("test-set-folder"), queryUrl);
        if (entities != null) {
            for (Entity oneEntity : entities) {
                children.add(new TestPlanFolder(oneEntity));
            }
        }
        return children;
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
     * Get the TestSetFolder that contains this TestSetFolder.
     *
     * @return Parent TestSetFolder or null (if parent is Root).
     */
    public TestPlanFolder getParent() {
        if (parent == null) {
            final int parentId = Integer.parseInt(getFieldValueByName("parent-id"));
            if (parentId != 0) {
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
                LoggerFactory.getLogger(TestPlanFolder.class).trace("Can't get instance of Root folder");
                return null;
            }
        }
        return parent;
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is {@link String }
     *
     */
    public int getParentId() {
        final String field = getFieldValueByName("parent-id");
        if (field != null) {
            return Integer.parseInt(field);
        }
        return 0;
    }

    /**
     * Gets the TestSetFolders path by recursive call of getName on parents. RuntimeException could occur if there is an
     * IOException while getting a parent folder.
     *
     * @return The TestSetFolders path.
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
     * Sets the value of the name property. Setting a value won't have any affect as long as you don't post this object
     * to the REST Service.
     *
     * @param value New value to set.
     */
    public void setName(final String value) {
        setFieldValue("name", value);
    }

    /**
     * Sets the value of the name property. Setting a value won't have any affect as long as you don't post this object
     * to the REST Service.
     *
     * @param value New value to set.
     */
    public void setParentId(final int value) {
        setFieldValue("parent-id", Integer.toString(value));
        parent = null;
    }

    @Override
    public String toString() {
        return getPath();
    }
}
