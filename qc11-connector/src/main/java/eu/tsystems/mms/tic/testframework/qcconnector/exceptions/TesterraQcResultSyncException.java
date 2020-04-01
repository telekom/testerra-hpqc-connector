/*
 * Created on 31.01.2014
 *
 * Copyright(c) 2011 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.qcconnector.exceptions;

import eu.tsystems.mms.tic.testframework.report.model.Serial;

import java.io.IOException;

/**
 * Own Exception to bundle different errors thrown during test result synchronization (to qc/testlink).
 *
 * @author sepr
 */
public class TesterraQcResultSyncException extends IOException {

    /**
     * generated serialVersioUID
     */
    private static final long serialVersionUID = Serial.SERIAL;

    /**
     * default Constructor.
     *
     * @param cause Exception causing the sync error.
     */
    public TesterraQcResultSyncException(Throwable cause) {
        super(cause);
    }

    /**
     * default error message
     *
     * @param string error message
     */
    public TesterraQcResultSyncException(String string) {
        super(string);
    }
}
