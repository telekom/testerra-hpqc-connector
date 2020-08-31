/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
/*
 * Created on 26.03.2014
 *
 * Copyright(c) 2011 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */

package eu.tsystems.mms.tic.testframework.qcrest.constants;

/**
 * property names of qcconnection.properties
 *
 * @author sepr
 */
public final class QCProperties {

    /**
     * Enable Synchronization
     */
    public static final String SYNC_ACTIVE = "qc.sync.active";


    /**
     * Property to set qc server to synchronize
     */
    public static final String SERVER = "qc.connection.server";

    /**
     * Property to set qc domain to synchronize
     */
    public static final String DOMAIN = "qc.connection.domain";

    /**
     * Property to set qc project to synchronize
     */
    public static final String PROJECT = "qc.connection.project";

    /**
     * Property to set qc user to synchronize
     */
    public static final String USER = "qc.connection.user";

    /**
     * Property to set qc password to synchronize
     */
    public static final String PASSWORD = "qc.connection.password";

    /**
     * Property to set used qc server version.
     */
    public static final String VERSION = "qc.version";

    /**
     * Mapping of field labels and internal names in qcconnection.properties
     */
    public static final String QC_FIELD_MAPPING = "qc.field.mapping.testrun";

    /**
     * QC upload screenshots global off.
     */
    public static final String UPLOAD_SCREENSHOTS_OFF = "qc.upload.screenshots.off";

    /**
     * Upload all screenshots on test failure
     */
    public static final String UPLOAD_SCREENSHOTS_FAILED = "qc.test.failed.upload.screenshots";

    /**
     * Upload all screenshots on test success
     */
    public static final String UPLOAD_SCREENSHOTS_PASSED = "qc.test.passed.upload.screenshots";

    /**
     * Property indicating to upload screencast video to qc
     */
    public static final String UPLOAD_VIDEOS = "qc.upload.videos";

    /**
     * Property indicating to upload screencast video to qc on failed Tests
     */
    public static final String UPLOAD_VIDEOS_FAILED = "qc.test.failed.upload.videos";

    /**
     * Property indicating to upload screencast video to qc on passed Tests
     */
    public static final String UPLOAD_VIDEOS_SUCCESS = "qc.test.passed.upload.videos";

    /**
     * Property to filter tests exectued by QCSurefireProvider
     */
    public static final String EXECUTION_FILTER = "qc.test.execution.filter";

    /**
     * Hide constructor
     */
    private QCProperties() {
    }

}
