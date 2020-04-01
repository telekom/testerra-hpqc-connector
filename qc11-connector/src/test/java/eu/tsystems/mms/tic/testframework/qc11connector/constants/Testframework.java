package eu.tsystems.mms.tic.testframework.qc11connector.constants;

/**
 * User: rnhb
 * Date: 19.12.13
 */
public enum Testframework {
    TESTNG("TestNG");

    private String framework;

    Testframework(String framework) {
        this.framework = framework;
    }

    /**
     * .
     *
     * @return framework
     */
    public String toString() {
        return framework;
    }
}
