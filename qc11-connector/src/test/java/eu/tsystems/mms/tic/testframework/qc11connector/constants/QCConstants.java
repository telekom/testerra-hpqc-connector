package eu.tsystems.mms.tic.testframework.qc11connector.constants;

/**
 * User: rnhb Date: 13.12.13
 */
public final class QCConstants {

    private QCConstants() {
    }

    /**
     * QC Path where all TestSets are placed.
     */
    public static final String QC_TESTSUNDERTEST_FOLDER = "Root\\Xeta\\TestSetsUnderTest\\";

    /**
     * Maximum of time that has been gone between QC Synchronization of tests and control by integration tests. Tests
     * take about 15 minutes now, so we chose 20 minutes as max. interval.
     */
    public static final long SYSTEMTEST_SYNCINTERVAL = 3000000;

    public static final long QCREST_SYNCINTERVAL = 15000;
    public static final long QCWEBSERVICE_SYNCINTERVAL = 60000;

    /**
     * Path that does not exist in QC
     */
    public static final String NOT_EXISTING_FOLDER = "Root\\Xeta\\Not\\Existing\\";
    public static final String NOT_EXISTING_TESTSET = "NotExistingTestSet";
    public static final String NOT_EXISTING_PATH = NOT_EXISTING_FOLDER + NOT_EXISTING_TESTSET;

    /**
     * Name of the TestSets for QC Sync Tests, for both System- and UnitTests
     */
    public static final String QCSYNC3_TESTSET_NAME = "QCSync3";
}
