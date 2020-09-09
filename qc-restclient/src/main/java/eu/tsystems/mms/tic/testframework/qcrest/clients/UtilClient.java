/*
 * Created on 06.12.2010
 *
 * Copyright(c) 2010 - 2010 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.qcrest.clients;

import eu.tsystems.mms.tic.testframework.qcrest.generated.Field;
import eu.tsystems.mms.tic.testframework.qcrest.generated.Fields;
import eu.tsystems.mms.tic.testframework.qcrest.generated.Item;
import eu.tsystems.mms.tic.testframework.qcrest.generated.Lists;
import eu.tsystems.mms.tic.testframework.qcrest.utils.LoginData;
import eu.tsystems.mms.tic.testframework.qcrest.utils.MarshallingUtils;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestSetFolder;
import java.io.IOException;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client containing utility functions for QC Rest Service.
 *
 * @author sepr
 */
public final class UtilClient {

    /**
     * Hide Constructor.
     */
    private UtilClient() {

    }

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilClient.class);

    /**
     * Gets the possible values for a user definded field.
     *
     * @param userLabel Label of the user defined field.
     * @param entity Entity which holds the UserField (e.g. TEST, BUG, ...)
     * @return A list of allowed values as String or null if any value is allowed or field is not found. Field may has
     *         another type than String!
     */
    public static List<String> getUserFieldValues(final String userLabel, final String entity) {
        final List<String> out = new LinkedList<String>();
        final RestConnector connector = RestConnector.getInstance();
        final String restUrl = connector.buildEntityCollectionUrl("customization/entitie") + "/" + entity + "/fields";
        BigInteger listId = null;
        Fields fields;
        try {
            final Response response = connector.httpGet(restUrl, null);
            fields = MarshallingUtils.marshal(Fields.class, response.toString());
        } catch (JAXBException e) {
            LOGGER.error("Error parsing REST Response to object.", e);
            return out;
        } catch (Exception e) {
            LOGGER.error("Error getting information from REST Service.", e);
            return out;
        }
        for (Field field : fields.getField()) {
            if (field.getLabel() != null && field.getLabel().equals(userLabel)) {
                listId = field.getListId();
                break;
            }
        }
        if (listId != null) {
            try {
                final String listUrl = connector.buildEntityCollectionUrl("customization/list") + "/?id=" + listId;
                final Response listResponse = connector.httpGet(listUrl, null);
                final Lists lists = MarshallingUtils.marshal(Lists.class, listResponse.toString());
                if (lists.getList().size() == 0) {
                    return out;
                }
                final eu.tsystems.mms.tic.testframework.qcrest.generated.Lists.List list = lists.getList().get(0);
                for (Item item : list.getItems().getItem()) {
                    out.add(item.getValue());
                }
            } catch (JAXBException e) {
                LOGGER.error("Error parsing REST Response to object.", e);
                return out;
            } catch (Exception e) {
                LOGGER.error("Error getting information from REST Service.", e);
                return out;
            }
        }
        return out;
    }

    /**
     * Get the index of the internal name for a UserField (e.g. 01 for TS_USER_01).
     *
     * @param userLabel Name of the UserField to get the index from.
     * @param entity Entity which holds the UserField (e.g. TEST, BUG, ...)
     * @return Index of the internal name as string or null if UserField not found.
     */
    public static String getIndexOfUserLabel(final String userLabel, final String entity) {
        return pGetIndexOfUserLabel(userLabel, entity);
    }

    /**
     * Get the index of the internal name for a UserField (e.g. 01 for TS_USER_01).
     *
     * @param userLabel Name of the UserField to get the index from.
     * @param entity Entity which holds the UserField (e.g. TEST, BUG, ...)
     * @return Index of the internal name as string or null if UserField not found.
     */
    private static String pGetIndexOfUserLabel(final String userLabel, final String entity) {
        final RestConnector connector = RestConnector.getInstance();
        final String restUrl = connector.buildEntityCollectionUrl("customization/entitie") + "/" + entity + "/fields";
        Fields fields;
        try {
            final Response response = connector.httpGet(restUrl, null);
            fields = MarshallingUtils.marshal(Fields.class, response.toString());
        } catch (JAXBException e) {
            LOGGER.error("Error parsing REST Response to object.", e);
            return null;
        } catch (Exception e) {
            LOGGER.error("Error getting information from REST Service.", e);
            return null;
        }
        for (Field field : fields.getField()) {
            if (field.getLabel() != null && field.getLabel().equals(userLabel)) {
                if (field.getName().startsWith("user-")) {
                    return field.getName().substring("user-".length());
                } else {
                    return field.getName();
                }
            }
        }
        return null;
    }

    /**
     * !! Get the Path by the folder name and check that to your given path!!!
     *
     * Test if a folder with the given id has a given name and calls this funtion recursively with the parent.
     *
     * @param folderId Id of TestSetFolder to test.
     * @param names LinkedList of names, whose last should be the name of the testsetfolder with the specified id.
     * @return true if the folder with the specified id and its parents have the given names, false otherwise.
     * @throws IOException Some REST exception.
     * @deprecated !! Get the Path by the folder name and check that to your given path!!!
     */
    @Deprecated
    protected static boolean isTestSetFolderRecursive(final int folderId, final LinkedList<String> names)
            throws IOException {
        if (folderId == 0) {
            return false;
        }
        final String name = names.removeLast();
        final TestSetFolder folder = FolderFinder.getTestSetFolderById(folderId);
        if (!folder.getName().equals(name)) {
            return false;
        }
        final int parentId = folder.getParentId();
        if (names.size() == 1 && parentId == 0) {
            return true;
        }
        // copy list and call function recursively.
        final LinkedList<String> leftNames = new LinkedList<String>();
        leftNames.addAll(names);
        return isTestSetFolderRecursive(parentId, names);
    }

    /**
     * Get a list of required fields for an entity.
     *
     * @param entityType Type of the entity to get fields from (run, test, test-set, etc.)
     * @return List of names of required fields.
     */
    public static List<String> getRequiredFields(final String entityType) {
        return pGetRequiredFields(entityType);
    }

    /**
     * Get a list of required fields for an entity.
     *
     * @param entityType Type of the entity to get fields from (run, test, test-set, etc.)
     * @return List of names of required fields.
     */
    private static List<String> pGetRequiredFields(final String entityType) {
        final RestConnector connector = RestConnector.getInstance();
        final LoginData loginData = connector.getLoginData();
        final String restUrl = connector.buildUrl("qcbin/rest/domains/") + loginData.getDomain() + "/projects/"
                + loginData.getProject()
                + "/customization/entities/" + entityType + "/fields?required=true";
        final List<String> out = new LinkedList<String>();
        try {
            final Response response = connector.httpGet(restUrl, null);
            final Fields fields = MarshallingUtils.marshal(Fields.class, response.toString());
            for (Field field : fields.getField()) {
                out.add(field.getName());
            }
        } catch (Exception e) {
            LoggerFactory.getLogger(UtilClient.class).error("Error getting required fields for " + entityType, e);
        }
        return out;
    }
}
