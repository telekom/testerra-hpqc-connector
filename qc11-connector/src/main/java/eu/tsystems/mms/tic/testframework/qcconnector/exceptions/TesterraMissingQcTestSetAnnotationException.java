/*
 * Created on 31.01.2014
 *
 * Copyright(c) 2011 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.qcconnector.exceptions;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;

/**
 * Own Exception to bundle mark missing qc test set annotation when sync is active (to qc/testlink).
 *
 * @author sepr
 */
public class TesterraMissingQcTestSetAnnotationException extends TesterraSystemException {

    /**
     * default Constructor.
     *
     * @param cause Exception causing the sync error.
     */
    public TesterraMissingQcTestSetAnnotationException(Throwable cause) {
        super(cause);
    }

    /**
     * default error message
     *
     * @param string error message
     */
    public TesterraMissingQcTestSetAnnotationException(String string) {
        super(string);
    }
}
