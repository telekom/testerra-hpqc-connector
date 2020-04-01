/* 
 * Created on 20.02.2013
 * 
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.qcrest.wrapper;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.qcrest.clients.FolderFinder;
import eu.tsystems.mms.tic.testframework.qcrest.clients.RestConnector;
import eu.tsystems.mms.tic.testframework.qcrest.generated.Entity;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author sepr
 * 
 */
public class TestPlanFolder extends AbstractEntity {

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
    public List<TestPlanFolder> getChildren() {
        final List<TestPlanFolder> children = new LinkedList<TestPlanFolder>();
        // DO NOT CHECK the number of sons, it doesnt work!
        // final int noOfChildren = Integer.parseInt(getFieldValueByName("no-of-sons"));
        // if (noOfChildren == 0) {
        // return children;
        // }
        final String queryUrl = "query={parent-id[=" + getId() + "]}";
        final RestConnector connector = RestConnector.getInstance();
        List<Entity> entities;
        try {
            entities = connector.getEntities(connector.buildEntityCollectionUrl("test-set-folder"), queryUrl);
        } catch (IOException e) {
            throw new TesterraRuntimeException("Error getting children for testsetfolder.", e);
        }
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
                } catch (IOException e) {
                    LoggerFactory.getLogger(this.getClass()).error("Error getting parent of testsetfolder.", e);
                    throw new TesterraRuntimeException("Error getting Entity with id " + parentId, e);
                }
                if (oneEntity == null) {
                    throw new TesterraRuntimeException("Error getting Entity with id " + parentId);
                } else {
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
