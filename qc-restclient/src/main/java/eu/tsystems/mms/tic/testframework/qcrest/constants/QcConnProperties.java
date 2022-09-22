package eu.tsystems.mms.tic.testframework.qcrest.constants;

import eu.tsystems.mms.tic.testframework.common.IProperties;

/**
 * Created on 2022-09-22
 *
 * @author mgn
 */
public enum QcConnProperties implements IProperties {

    /**
     * Removed properties:
     * - Upload all screenshots on test failure
     *   public static final String UPLOAD_SCREENSHOTS_FAILED = "qc.test.failed.upload.screenshots";
     *
     * - Upload all screenshots on test success
     *   public static final String UPLOAD_SCREENSHOTS_PASSED = "qc.test.passed.upload.screenshots";
     *
     * - Property indicating to upload screencast video to qc on failed Tests
     *   public static final String UPLOAD_VIDEOS_FAILED = "qc.test.failed.upload.videos";
     *
     * - Property indicating to upload screencast video to qc on passed Tests
     *   public static final String UPLOAD_VIDEOS_SUCCESS = "qc.test.passed.upload.videos";
     *
     * - QC upload screenshots global off.
     *   public static final String UPLOAD_SCREENSHOTS_OFF = "qc.upload.screenshots.off";
     */

    SYNC_ACTIVE("qc.sync.active", true),
    SERVER("qc.connection.server", ""),
    DOMAIN("qc.connection.domain", ""),
    PROJECT("qc.connection.project", ""),
    USER("qc.connection.user", ""),
    PASSWORD("qc.connection.password", ""),
    VERSION("qc.version", "12"),
    QC_FIELD_MAPPING("qc.field.mapping.testrun", ""),
    UPLOAD_SCREENSHOTS ("qc.upload.screenshots", false),
    UPLOAD_VIDEOS("qc.upload.videos", false),
    EXECUTION_FILTER("qc.test.execution.filter", "")
    ;

    private final String property;

    private final Object defaultValue;

    QcConnProperties(String property, Object defaultValue) {
        this.property = property;
        this.defaultValue = defaultValue;
    }

    @Override
    public Object getDefault() {
        return null;
    }

    @Override
    public String toString() {
        return this.property;
    }
}
