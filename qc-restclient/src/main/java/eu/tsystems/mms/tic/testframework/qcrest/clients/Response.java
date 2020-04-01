/* 
 * Created on 20.02.2013
 * 
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.qcrest.clients;

import eu.tsystems.mms.tic.testframework.qcrest.generated.QCRestException;

import java.util.Map;

/**
 * This is a naive implementation of an HTTP response. We use it to simplify matters in the examples. It is a container
 * of the response headers and the response body.
 */
public class Response {

    /**
     * Map for headers of http requests.
     */
    private Map<String, ? extends Iterable<String>> responseHeaders = null;
    /**
     * Response of rest requests.
     */
    private byte[] responseData = null;
    /**
     * Failure of request.
     */
    private Exception failure = null;
    /**
     * Internal exception returned by server.
     */
    private QCRestException serverException = null;
    /**
     * Response status.
     */
    private int statusCode = 0;

    /**
     * Constructor of Response.
     * 
     * @param responseHeaders Headers.
     * @param responseData Data of response.
     * @param failure Failure of response.
     * @param statusCode HTML statusCode.
     */
    public Response(
            final Map<String, Iterable<String>> responseHeaders,
            final byte[] responseData,
            final Exception failure,
            final int statusCode) {
        super();
        this.responseHeaders = responseHeaders;
        this.responseData = responseData.clone();
        this.failure = failure;
        this.statusCode = statusCode;
    }

    /**
     * Default constructor.
     */
    public Response() {
        // set the fields manually.
    }

    /**
     * gets the response headers
     *
     * @return the response Headers
     */
    public Map<String, ? extends Iterable<String>> getResponseHeaders() {
        return responseHeaders;
    }

    /**
     * sets the responseHeaders
     *
     * @param responseHeaders the responseHeaders to set
     */
    public void setResponseHeaders(final Map<String, ? extends Iterable<String>> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    /**
     * gets the response data
     *
     * @return the response Data
     */
    public byte[] getResponseData() {
        return responseData.clone();
    }

    /**
     * sets the response data
     *
     * @param responseData the responseData to set
     */
    public void setResponseData(final byte[] responseData) {
        this.responseData = responseData.clone();
    }

    /**
     * gets the failure
     *
     * @return the failure if the access to the requested URL failed, such as a 404 or 500 If no failure occurred, this
     *         method returns null.
     */
    public Exception getFailure() {
        return failure;
    }

    /**
     * sets the failure
     *
     * @param failure the failure to set
     */
    public void setFailure(final Exception failure) {
        this.failure = failure;
    }

    /**
     * gets the server exception
     *
     * @return the serverException
     */
    public QCRestException getServerException() {
        return serverException;
    }

    /**
     * sets the server exception
     *
     * @param serverException the serverException to set
     */
    public void setServerException(final QCRestException serverException) {
        this.serverException = serverException;
    }

    /**
     * gets the status code
     *
     * @return the statusCode
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * sets the status code
     *
     * @param statusCode the statusCode to set
     */
    public void setStatusCode(final int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     *
     *
     * @see java.lang.Object#toString() return the contents of the byte[] data as a string.
     * @return responseData as String.
     */
    @Override
    public String toString() {
        return new String(this.responseData);
    }
}