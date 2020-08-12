/*
 * Created on 16.05.2013
 *
 * Copyright(c) 2011 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */

package eu.tsystems.mms.tic.testframework.qcconnector.constants;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.qcrest.constants.QCProperties;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds values for QC entity fields, that are read when the entity is synchronized. Only applies to TestRun at the
 * moment.
 *
 * @author sepr
 */
public final class QCFieldValues {

    /**
     * Logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(QCFieldValues.class);
    /**
     * Mapping of Field names to QCs internal user-field names.
     */
    private static Map<String, String> fieldMapping = getFieldMappingFromFile();
    /**
     * Field names and values to use for entity synchronization.
     */
    private static Map<String, String> fieldProperties = getPropertiesFromFile();

    /**
     * ThreadLocal Map of field names and values, that can be set per test.
     */
    private static ThreadLocal<Map<String, String>> threadProperties = new ThreadLocal<Map<String, String>>();


    /**
     * Hide constructor
     */
    private QCFieldValues() {
    }

    /**
     * gets the thread properties
     *
     * @return the threadProperties
     */
    public static Map<String, String> getThreadProperties() {
        return threadProperties.get();
    }

    /**
     * @return Returns the Map of entity fields used for synchronization. ThreadLocal properties will override the
     * static ones.
     */
    public static Map<String, String> getAllFieldsToAdd() {
        final Map<String, String> outMap = new HashMap<String, String>();
        outMap.putAll(fieldProperties);
        Map<String, String> map = threadProperties.get();
        if (map != null) {
            outMap.putAll(map);
        }
        for (String key : outMap.keySet()) {
            LOGGER.info("Setting QC field for test run: " + key + "=" + outMap.get(key));
        }
        return outMap;
    }

    /**
     * Get the mapping of QC Field labels to its internal names from qcconnection.properties.
     *
     * @return Map between label and name.
     */
    private static Map<String, String> getFieldMappingFromFile() {
        final Map<String, String> outMap = new HashMap<String, String>();

        PropertyManager.loadProperties("qcconnection.properties");
        final String mappingsString = PropertyManager.getProperty(QCProperties.QC_FIELD_MAPPING);
        if (mappingsString == null || mappingsString.isEmpty()) {
            return outMap;
        }
        final String[] mappings = mappingsString.split("\\|");
        for (final String mapping : mappings) {
            final String[] labelName = mapping.split(":");
            if (labelName.length == 2) {
                outMap.put(labelName[0].trim(), labelName[1].trim());
            } else {
                LOGGER.warn("Wrong format in Property " + QCProperties.QC_FIELD_MAPPING + ": " + mapping);
            }
        }
        return outMap;
    }

    /**
     * Gets the internal field name for the label based on the mapping in qcconnection.properties.
     *
     * @param label Label to get name from.
     * @return QC Name of the field according to mapping.
     */
    public static String getFieldNameForLabel(final String label) {
        return fieldMapping.get(label);
    }

    /**
     * Puts all values of fields defined in the mapping into the fieldProperties. Example:
     * qc.field.mapping.testrun=label:user01 --- Mapping field.label=value --- label,value are put in map.
     *
     * @return Map of field names (labels) and values.
     */
    private static Map<String, String> getPropertiesFromFile() {
        final Map<String, String> outMap = new HashMap<String, String>();
        for (final String label : fieldMapping.keySet()) {
            final String value = PropertyManager.getProperty("field." + label, null);
            if (value != null && !value.trim().isEmpty()) {
                outMap.put(label, value);
            }
        }
        return outMap;
    }

    /**
     * Reset the fields of the threadlocal variable.
     */
    public static void resetThreadLocalFields() {
        threadProperties.remove();
    }

    /**
     * Adds a map of label and value to the ThreadLocal list of fields. Those are added to the test run on
     * synchronization. Use setStaticField for global field values.
     *
     * @param label Label of field.
     * @param value Value of field.
     */
    public static void setField(final String label, final String value) {
        if (threadProperties.get() == null) {
            threadProperties.set(new HashMap<String, String>());
        }
        threadProperties.get().put(label, value);
    }

    /**
     * Adds a map of label and value to the static list of fields. Those are added to the test run on synchronization.
     * Use setField for ThreadLocal field values.
     *
     * @param label Label of field.
     * @param value Value of field.
     */
    public static void setStaticField(final String label, final String value) {
        fieldProperties.put(label, value);
    }
}
