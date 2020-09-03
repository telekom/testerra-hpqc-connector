/*
 * Created on 30.01.2012
 *
 * Copyright(c) 2011 - 2011 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */

package eu.tsystems.mms.tic.testframework.qcconnector.provider;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.connectors.util.AbstractCommonProvider;
import eu.tsystems.mms.tic.testframework.connectors.util.SyncType;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.qcrest.constants.QCProperties;
import org.apache.maven.surefire.providerapi.ProviderParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to provide the test to run to Maven-Surefire-Plugin. It reads the tests to run from configuration
 * file 'TestSetsToRun.txt' and loads the collects the needed classes and methods to run by reading the necessary data
 * from HP Quality Center.
 * <p>
 * The calculation of the test methods:
 * <ul>
 * <li>Read the test sets to run from configuration files</li>
 * <li>Read all test set tests from the test sets</li>
 * <li>Use one of the synctypes do examine the class and method names</li>
 * <li>Load the class and validate the correctness of the method</li>
 * <li>Return the collected methods</li>
 * <li>see http://teamweb.mms-at-work.de/display/butic/HP+Quality+Center+Synchronisierung</li>
 * </ul>
 *
 * @author sepr, mibu, mrgi
 */
public class QualityCenterSurefireProvider extends AbstractCommonProvider implements Loggable {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(QualityCenterSurefireProvider.class);

    /**
     * Public constructor. Creates a new <code>QualityCenterSurefireProvider</code> object.
     *
     * @param parameters The parameters given by surefire.
     */
    public QualityCenterSurefireProvider(final ProviderParameters parameters) {
        super(parameters);
        init();
    }

    /**
     * Constructor to create provider instance in test classes.
     */
    public QualityCenterSurefireProvider() {
        testClassLoader = Thread.currentThread().getContextClassLoader();
        init();
    }

    /**
     * Initialize members
     */
    private void init() {

        PropertyManager.loadProperties(getPropertiesFile());
        syncType = SyncType.ANNOTATION;
        syncToProvider = PropertyManager.getBooleanProperty(QCProperties.SYNC_ACTIVE, false);
        log().info("Using qc.connection.project: " + PropertyManager.getProperty(QCProperties.PROJECT, PropertyManager.getProperty("project")));
        log().info("Using user name: " + PropertyManager.getProperty(QCProperties.USER, PropertyManager.getProperty("user")));
    }

    @Override
    public final String getPropertiesFile() {
        return "qcconnection.properties";
    }

}
