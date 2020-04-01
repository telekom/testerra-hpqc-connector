package eu.tsystems.mms.tic.testframework.qcrest.constants;

/**
 * User: rnhb
 * Date: 20.12.13
 */
public final class ErrorMessages {

    private ErrorMessages() {
    }

    /**
     * tests that test set is not found
     *
     * @param testSetName .
     * @return string message
     */
    public static String testSetNotFound(String testSetName) {
        return "The TestSet \""
                + testSetName
                + "\" was not found. It probably doesn't exist.";
    }

}
