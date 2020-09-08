/*
 * Created on 15.07.2013
 *
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.qcrest.clients;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.qcrest.generated.Entity;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.AbstractEntity;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestPlanFolder;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestSetFolder;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA. User: pele Date: 09.07.13 Time: 11:29 To change this template use File | Settings | File
 * Templates.
 */
public class FolderFinder {

    /** Path of folder to find. */
    private final String path;
    /** Logger instance */
    private static final Logger LOGGER = LoggerFactory.getLogger(FolderFinder.class);
    /** parsed path as array */
    private final String[] folders;

    /**
     * Cache. Map is key: folder string, value: id
     */
    private static final Map<String, Integer> FOLDER_TO_ID_STORAGE = new ConcurrentHashMap<String, Integer>();
    /** Cache of TestFolderObjects mapped to their id. */
    private static final ConcurrentHashMap<Integer, TestSetFolder> LAB_FOLDER_CACHE = new ConcurrentHashMap<Integer, TestSetFolder>();
    /** Cache of TestPlanFolderObjects mapped to their id. */
    private static final ConcurrentHashMap<Integer, TestPlanFolder> PLAN_FOLDER_CACHE = new ConcurrentHashMap<Integer, TestPlanFolder>();

    /** RestConnector to access QC */
    private static final RestConnector CONNECTOR = RestConnector.getInstance();

    /** Query template to get a folder */
    private static final String QUERY_SUFFIX = "&fields=name,id,parent-id";
    /** Not found string */
    public static final String NOT_FOUND = "not_found";

    /**
     * Constructor. If the path has a trailing backslash, it is cut off.
     *
     * @param path Path of folder to find.
     */
    public FolderFinder(String path) {
        if (path.endsWith("\\")) {
            this.path = path.substring(0, path.length() - 1);
        } else {
            this.path = path;
        }
        folders = path.split("\\\\");
    }

    /**
     * @return baseUrl getting Lab Folders
     */
    private String getRestUrlLab() {
        return CONNECTOR.buildEntityCollectionUrl("test-set-folder");
    }

    /**
     * @return baseUrl getting plan folders
     */
    private String getRestUrlPlan() {
        return CONNECTOR.buildEntityCollectionUrl("test-folder");
    }

    /**
     * Gets a folder by its id.
     *
     * @param id Id of TestSetFolder
     * @return Testsetfolder object.
     */
    public static TestSetFolder getTestSetFolderById(int id) {
        if (LAB_FOLDER_CACHE.containsKey(id)) {
            TestSetFolder folder = LAB_FOLDER_CACHE.get(id);
            LOGGER.debug("Get folder from cache (id " + id + ") : " + folder);
            return folder;
        } else {
            try {
                TestSetFolder testSetFolderById = QcRestClient.getTestSetFolderById(id);
                if (testSetFolderById != null) {
                    LAB_FOLDER_CACHE.put(id, testSetFolderById); // store to cache
                }
                return testSetFolderById;
            } catch (Exception e) {
                throw new TesterraSystemException("Error getting testsetfolder", e);
            }
        }
    }

    /**
     * Gets a folder by its id.
     *
     * @param id Id of TestSetFolder
     * @return Testsetfolder object.
     */
    public static TestPlanFolder getTestPlanFolderById(int id) {
        if (LAB_FOLDER_CACHE.containsKey(id)) {
            TestPlanFolder folder = PLAN_FOLDER_CACHE.get(id);
            LOGGER.debug("Got folder from cache (id " + id + ") : " + folder);
            return folder;
        } else {
            try {
                TestPlanFolder testPlanFolderById = QcRestClient.getTestPlanFolderById(id);
                if (testPlanFolderById != null) {
                    PLAN_FOLDER_CACHE.put(id, testPlanFolderById); // store to cache
                }
                return testPlanFolderById;
            } catch (Exception e) {
                throw new TesterraSystemException("Error getting testplanfolder", e);
            }
        }
    }

    /**
     * Find TestSetFolder by path field.
     *
     * @param folderType TestSetFolder or TestPlanFolder
     * @param <T> generic return type (TestSetFolder or TestPlanFolder).
     * @return TestSetFolder or TestPlanFolder object
     * @throws IOException Folder could not be found.
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractEntity> T find(final Class<T> folderType) throws Exception {
        if (folders.length == 0) {
            return null;
        }
        if (folders.length == 1) {
            if ("Root".equals(folders[0]) || "Subject".equals(folders[0])) {
                throw new TesterraRuntimeException(folders[0] + " can not be returned by REST Service.");
            } else {
                LOGGER.error("TestFolder " + path
                        + " not found. Must begin with Root\\... (TestSet) or Subject\\... (TestPlan).");
                return null;
            }
        }

        /*
         * cache lookup:
         */
        if (FOLDER_TO_ID_STORAGE.containsKey(path)) {
            Integer id = FOLDER_TO_ID_STORAGE.get(path);
            if (folderType == TestPlanFolder.class) {
                return (T) getTestPlanFolderById(id);
            } else if (folderType == TestSetFolder.class) {
                return (T) getTestSetFolderById(id);
            } else {
                LOGGER.error("FolderFinder can only be used for Entities TestSetFolder and TestPlanFolder.");
                return null;
            }
        }

        /*
         * Find the starting folder under root
         */
        Entity firstEntity = null;
        Integer id = null; // the found entity id
        // request cache first
        id = lookupIdCache(1);

        // if not in cache, then query qc
        if (id == null) {
            final List<Entity> entities;
            if (folderType == TestPlanFolder.class) {
                entities = CONNECTOR
                        .getEntities(getRestUrlPlan(), "query={name['" + folders[1] + "']}" + QUERY_SUFFIX);
            } else if (folderType == TestSetFolder.class) {
                entities = CONNECTOR.getEntities(getRestUrlLab(), "query={name['" + folders[1] + "']}" + QUERY_SUFFIX);
            } else {
                LOGGER.error("FolderFinder can only be used for Entities TestSetFolder and TestPlanFolder.");
                return null;
            }
            // TODO Kann parent-id check nicht mit in query einfließen?
            for (Entity entity : entities) {
                EntityValues entityValues = getEntityValues(entity);
                if (entityValues.getParentId() == 0 || entityValues.getParentId() == 2) {
                    // wenn parent ist root
                    id = entityValues.getId();
                    firstEntity = entity;
                    break;
                }
            }

            // Exception
            if (id == null) {
                throw new TesterraSystemException("Could not find Folder " + path);
            }

            // store to cache
            storePath(1, id);
        } else {
            // if id was cached
            if (folderType == TestPlanFolder.class) {
                TestPlanFolder testPlanFolder = getTestPlanFolderById(id);
                if (testPlanFolder.getPath().equals(path)) {
                    // folder id and testsetfolder object was cached
                    return (T) testPlanFolder;
                }
                // get entity
                firstEntity = testPlanFolder.getEntity();
            } else if (folderType == TestSetFolder.class) {
                TestSetFolder testSetFolder = getTestSetFolderById(id);
                if (testSetFolder.getPath().equals(path)) {
                    // folder id and testsetfolder object was cached
                    return (T) testSetFolder;
                }
                // get entity
                firstEntity = testSetFolder.getEntity();
            } else {
                LOGGER.error("FolderFinder can only be used for Entities TestSetFolder and TestPlanFolder.");
                return null;
            }

        }

        /*
         * Find the testfolder
         */
        T folderRecursive = findFolderRecursive(firstEntity, 1, folderType);

        if (folderRecursive != null) {
            if (folderType == TestPlanFolder.class) {
                PLAN_FOLDER_CACHE.put(folderRecursive.getId(), (TestPlanFolder) folderRecursive);
            } else if (folderType == TestSetFolder.class) {
                LAB_FOLDER_CACHE.put(folderRecursive.getId(), (TestSetFolder) folderRecursive);
            } else {
                LOGGER.error("FolderFinder can only be used for Entities TestSetFolder and TestPlanFolder.");
                return null;
            }
        }

        return folderRecursive; // start with position 1 and given id
    }

    /**
     * @param pathPosition .
     * @return .
     */
    private Integer lookupIdCache(int pathPosition) {
        String pathToPosition = getPathToPosition(pathPosition);
        return lookupIdCache(pathToPosition);
    }

    /**
     * Look for an id for the given path in the cache
     *
     * @param thePath Path to look up id for.
     * @return id matching path or null.
     */
    private Integer lookupIdCache(String thePath) {
        if (FOLDER_TO_ID_STORAGE.containsKey(thePath)) {
            return FOLDER_TO_ID_STORAGE.get(thePath);
        } else {
            return null;
        }
    }

    /**
     * Gets path for the given position in the path array
     *
     * @param position .
     * @return .
     */
    private String getPathToPosition(int position) {
        String thePath = "";
        for (int i = 0; i <= position; i++) {
            thePath += folders[i] + "\\";
        }
        thePath = thePath.substring(0, thePath.length() - 1);
        return thePath;
    }

    /**
     * Stores path with given id
     *
     * @param pathPosition Position in path
     * @param id Id of entity
     */
    private void storePath(int pathPosition, int id) {
        String pathToPosition = getPathToPosition(pathPosition);
        FOLDER_TO_ID_STORAGE.put(pathToPosition, id);
    }

    /**
     * Array: 0: parent-id 1: id
     *
     * @param entity .
     * @return .
     */
    private EntityValues getEntityValues(Entity entity) {
        Integer id = null;
        Integer parentId = null;
        String name = null;
        List<Entity.Fields.Field> fields = entity.getFields().getField();
        for (Entity.Fields.Field field : fields) {
            // get the id
            if ("id".equals(field.getName())) {
                if ((field.getValue() == null) || (field.getValue().size() == 0)) {
                    id = null;
                } else {
                    String value = field.getValue().get(0).getValue();
                    id = Integer.valueOf(value);
                }
            }
            // get the parent id
            else if ("parent-id".equals(field.getName())) {
                if ((field.getValue() == null) || (field.getValue().size() == 0)) {
                    parentId = null;
                } else {
                    String value = field.getValue().get(0).getValue();
                    parentId = Integer.valueOf(value);
                }
            }
            // get the name
            else if ("name".equals(field.getName())) {
                if ((field.getValue() == null) || (field.getValue().size() == 0)) {
                    name = null;
                } else {
                    String value = field.getValue().get(0).getValue();
                    name = value;
                }
            }
        }
        EntityValues entityValues = new EntityValues(name, parentId, id);
        return entityValues;
    }

    /**
     * Get path to folder for given id.
     *
     * @param id Id to look up.
     * @return folder path or null if id is not in FOLDER_TO_ID_STORAGE
     */
    public static String getPathById(int id) {
        if (FOLDER_TO_ID_STORAGE.containsValue(id)) {
            for (String key : FOLDER_TO_ID_STORAGE.keySet()) {
                if (FOLDER_TO_ID_STORAGE.get(key) == id) {
                    LOGGER.debug("Get path from cache (id " + id + ") : " + key);
                    return key;
                }
            }
        } else if (id == 0) {
            return "Root";
        }
        return NOT_FOUND;
    }

    /**
     * Check if the given Entity is the one to be looked up.
     *
     * @param entityValues Values to identify entity
     * @return True if this is the folder to be found.
     */
    private boolean isThisMyEntity(EntityValues entityValues) {
        String pathToCheck = getPathById(entityValues.getParentId()) + "\\" + entityValues.getName();
        if (pathToCheck.equals(path)) {
            return true;
        }
        return false;
    }

    /**
     * Get children for a folder with the given id.
     *
     * @param id Id of folder to get children from.
     * @param targetType Folder type (lab/plan)
     * @return List of childrens folder.
     */
    private List<Entity> getChildren(int id, Class<? extends AbstractEntity> targetType) {
        List<Entity> entities = null;
        final String query = "query={parent-id[=" + id + "]}" + QUERY_SUFFIX;
        try {
            if (targetType == TestSetFolder.class) {
                entities = CONNECTOR.getEntities(getRestUrlLab(), query);
            } else {
                entities = CONNECTOR.getEntities(getRestUrlPlan(), query);
            }
        } catch (Exception e) {
            throw new TesterraSystemException("Error getting children", e);
        }
        return entities;
    }

    /**
     * Recursive method to find Testfolders.
     *
     * @param entity Entity to check for matching the expected folder.
     * @param pathPosition Position in path array
     * @param folderType return type
     * @param <T> Generic to make it usable with TestSetFolder and TestPlanFolder
     * @return T
     * @throws IOException Communication error with rest service.
     */
    @SuppressWarnings("unchecked")
    private <T extends AbstractEntity> T findFolderRecursive(Entity entity, int pathPosition, Class<T> folderType)
            throws IOException {
        // get the testfolder of this entity
        EntityValues entityValues = getEntityValues(entity);
        Integer id = entityValues.getId();
        String name = entityValues.getName();
        LOGGER.debug("Checking " + name + " (" + id + ")");

        if (!name.equals(folders[pathPosition])) {
            // the current path does not match the requested path
            LOGGER.debug("Wrong path: " + name + " (" + id + ")");
            return null;
        }

        // Store current path to chache
        storePath(pathPosition, id);

        // is this the requested folder?
        if (isThisMyEntity(entityValues)) {
            LOGGER.debug("Yeah, we found the folder. It is named " + name + " with id " + id);
            storePath(pathPosition, id);
            if (folderType == TestPlanFolder.class) {
                return (T) new TestPlanFolder(entity);
            } else if (folderType == TestSetFolder.class) {
                return (T) new TestSetFolder(entity);
            } else {
                LOGGER.error("FolderFinder can only be used for Entities TestSetFolder and TestPlanFolder.");
                return null;
            }
        }

        pathPosition++; // increase path position for children
        // check partial path cached
        Integer cachedId = null;
        cachedId = lookupIdCache(pathPosition);
        if (cachedId != null && cachedId.intValue() > 0) {
            Entity e;
            if (folderType == TestPlanFolder.class) {
                e = getTestPlanFolderById(cachedId.intValue()).getEntity();
            } else if (folderType == TestSetFolder.class) {
                e = getTestSetFolderById(cachedId.intValue()).getEntity();
            } else {
                LOGGER.error("FolderFinder can only be used for Entities TestSetFolder and TestPlanFolder.");
                return null;
            }
            return findFolderRecursive(e, pathPosition, folderType);
        }

        // iterate through children
        List<Entity> children = getChildren(id, folderType);
        if (children == null || children.size() == 0) {
            LOGGER.debug(name + " (" + id + ") has no children");
            return null;
        }
        for (Entity child : children) {
            T folderRecursive = findFolderRecursive(child, pathPosition, folderType);
            if (folderRecursive != null) {
                return folderRecursive;
            }
        }
        // nothing found
        return null;
    }

    /**
     * Internal class holding only important entity properties.
     *
     * @author pele
     */
    class EntityValues {
        /** folderName */
        private String name;
        /** parentId */
        private Integer parentId;
        /** id */
        private Integer id;

        String getName() {
            return name;
        }

        void setName(String name) {
            this.name = name;
        }

        Integer getParentId() {
            return parentId;
        }

        void setParentId(Integer parentId) {
            this.parentId = parentId;
        }

        Integer getId() {
            return id;
        }

        void setId(Integer id) {
            this.id = id;
        }

        /**
         * Default constructor
         *
         * @param name folderName
         * @param parentId parent ID
         * @param id ID
         */
        protected EntityValues(String name, Integer parentId, Integer id) {
            this.name = name;
            this.parentId = parentId;
            this.id = id;
        }
    }
}
