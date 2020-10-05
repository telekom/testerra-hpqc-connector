/*
 * Created on 20.02.2013
 *
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.qcrest.clients;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.qcrest.constants.QCProperties;
import eu.tsystems.mms.tic.testframework.qcrest.generated.Entities;
import eu.tsystems.mms.tic.testframework.qcrest.generated.Entity;
import eu.tsystems.mms.tic.testframework.qcrest.generated.QCRestException;
import eu.tsystems.mms.tic.testframework.qcrest.utils.LoginData;
import eu.tsystems.mms.tic.testframework.qcrest.utils.MarshallingUtils;
import eu.tsystems.mms.tic.testframework.utils.CertUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.net.ssl.SSLException;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The RestConnector can be used to build Requests for the QC REST API and send them via http. Any project and user
 * specific data is read from the qcconnection.properties resource file. This class is a threadlocal singleton so any
 * thread will use a shared login and shared cookies.
 *
 * @author sepr
 */
public final class RestConnector {

    static {
        CertUtils.trustAllCerts();
    }

    private static final int PAGE_SIZE = PropertyManager.getIntProperty("qc.rest.pagesize", 5000);

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RestConnector.class);
    /**
     * Singleton instance.
     */
    private static RestConnector instance;
    /**
     * lock object for synchronized instance access
     */
    private static Object instanceLock = new Object();

    /**
     * Returns the Quality Center connection properties. Initializes the connection properties using the file
     * <code>qcconnection.properties</code> in the resources folder if not initialized already.
     * <p>
     * The file <code>qcconnection.properties</code> must include the worths for:
     * <ul>
     * <li>server</li>
     * <li>user</li>
     * <li>password</li>
     * <li>domain</li>
     * <li>project</li>
     * </ul>
     *
     * @return The connection data as <code>LoginData</code> object.
     */
    public static LoginData getConnectionProperties() {
        return pGetConnectionProperties();
    }

    /**
     * Returns the Quality Center connection properties. Initializes the connection properties using the file
     * <code>qcconnection.properties</code> in the resources folder if not initialized already.
     * <p>
     * The file <code>qcconnection.properties</code> must include the worths for:
     * <ul>
     * <li>server</li>
     * <li>user</li>
     * <li>password</li>
     * <li>domain</li>
     * <li>project</li>
     * </ul>
     *
     * @return The connection data as <code>LoginData</code> object.
     */
    private static LoginData pGetConnectionProperties() {
        LOGGER.trace("Reading properties from qcconnection.properties File");
        PropertyManager.loadProperties("qcconnection.properties");
        final LoginData loginData = new LoginData(
                PropertyManager.getProperty(QCProperties.DOMAIN, PropertyManager.getProperty("domain")),
                PropertyManager.getProperty(QCProperties.PASSWORD, PropertyManager.getProperty("password")),
                PropertyManager.getProperty(QCProperties.PROJECT, PropertyManager.getProperty("project")),
                PropertyManager.getProperty(QCProperties.SERVER, PropertyManager.getProperty("server")),
                PropertyManager.getProperty(QCProperties.USER, PropertyManager.getProperty("user")));
        LOGGER.trace("Server: "
                + PropertyManager.getProperty(QCProperties.SERVER, PropertyManager.getProperty("server")));
        LOGGER.trace("Domain: "
                + PropertyManager.getProperty(QCProperties.DOMAIN, PropertyManager.getProperty("domain")));
        LOGGER.trace("Project: " + PropertyManager.getProperty(QCProperties.PROJECT),
                PropertyManager.getProperty("project"));
        LOGGER.trace("User: " + PropertyManager.getProperty(QCProperties.USER), PropertyManager.getProperty("user"));
        LOGGER.trace("Password: "
                + PropertyManager.getProperty(QCProperties.PASSWORD, PropertyManager.getProperty("password")));
        return loginData;
    }

    /**
     * Get instance of RestConnector. logout() should be called when you are done.
     *
     * @return {@link RestConnector}.
     */
    public static RestConnector getInstance() {
        synchronized (instanceLock) {
            if (instance == null) {
                instance = new RestConnector();
            }
            return instance;
        }
    }

    /**
     * Map holding cookies.
     */
    private Map<String, String> cookies;
    /**
     * Default Headers of Requests.
     */
    private final Map<String, String> requestHeaders;

    /**
     * LoginData to use.
     */
    private LoginData loginData;

    /**
     * QC Server url
     */
    private String server;

    /**
     * Count tries to login
     */
    private int loginTries;

    /**
     * Constructor.
     */
    private RestConnector() {
        cookies = new HashMap<String, String>();
        loginData = getConnectionProperties();
        requestHeaders = new HashMap<String, String>();
        requestHeaders.put("Accept", "application/xml");
        server = loginData.getServer();
        loginTries = 0;
        if (server.endsWith("qcbin")) {
            server = server.substring(0, server.length() - "/qcbin".length());
        }
        if (server.endsWith("/")) {
            server = server.substring(0, server.length() - 1);
        }
    }

    /**
     * Build an Url to get collections of entities.
     *
     * @param entityType Type of entity (e.g. run, test-set).
     *
     * @return Url as String.
     */
    public String buildEntityCollectionUrl(final String entityType) {
        return buildUrl("qcbin/rest/domains/"
                + loginData.getDomain()
                + "/projects/"
                + loginData.getProject()
                + "/"
                + entityType
                + "s");
    }

    /**
     * Appends the QC Server (from qcconnection.properties) at the front of the given path.
     *
     * @param path on the server to use
     *
     * @return a url on the server for the path parameter
     */
    public String buildUrl(String path) {
        return String.format("%1$s/%2$s", server, path);
    }

    /**
     * Executes the http request and returns the answer wrapped in a {@link Response} object.
     *
     * @param type        of the http operation: get post put delete
     * @param url         to work on
     * @param queryString query argument to append to request.
     * @param data        to write, if a writable operation
     * @param headers     Headers to add.
     *
     * @return http response
     *
     * @throws IOException Exception while request transport.
     */
    private Response doHttp(final String type, String url, String queryString, final byte[] data,
                            Map<String, String> headers)
            throws Exception {
        synchronized (instanceLock) {
            url = url.replaceAll(" ", "%20");
            URI escapedUri = null;
            if ((queryString != null) && !queryString.isEmpty()) {
                try {
                    escapedUri = new URI(null, null, url, queryString, null);
                } catch (URISyntaxException e) {
                    LOGGER.error("Error building uri for Request.", e);
                }
            } else {
                try {
                    escapedUri = new URI(url);
                } catch (URISyntaxException e) {
                    LOGGER.error("Error building uri for Request.", e);
                }
            }
            if (headers != null) {
                headers.putAll(requestHeaders);
            } else {
                headers = requestHeaders;
            }
            LOGGER.debug("Sending request to url: " + escapedUri.toString());

            /*
            Open the connection
             */
            HttpURLConnection con = (HttpURLConnection) escapedUri.toURL().openConnection();
            con.setRequestMethod(type);
            final String cookieString = getCookieString();
            prepareHttpRequest(con, headers, data, cookieString);
            try {
                con.connect();
            } catch (SSLException e) {
                LOGGER.warn("Exception while (https) - connecting to QC Server. Reconnect...");
                con = (HttpURLConnection) escapedUri.toURL().openConnection();
                con.setRequestMethod(type);
                prepareHttpRequest(con, headers, data, cookieString);
                try {
                    con.connect();
                } catch (SSLException ssle) {
                    LOGGER.error("SSLException occured a second time.", ssle);
                    throw ssle;
                }
            } catch (SocketException se) {
                if (se.getMessage().contains("Connection reset")) {
                    LOGGER.warn("Got exception on connection reset. Wait a second and retry.");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        LOGGER.debug("Sleep interrupted.");
                    }
                    con = (HttpURLConnection) escapedUri.toURL().openConnection();
                    con.setRequestMethod(type);
                    prepareHttpRequest(con, headers, data, cookieString);
                    con.connect();
                }
            }
            Response ret = retrieveHtmlResponse(con);
            if (ret.getStatusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                if (loginTries < 5) {
                    loginTries++;
                    LOGGER.debug("Unauthorized Exception. Send login Request and retry request");
                    String version = PropertyManager.getProperty(QCProperties.VERSION, "12");
                    if ("11".equals(version)) {
                        login(server + "/qcbin/authentication-point/authenticate", loginData.getUser(),
                                loginData.getPassword());
                    } else {
                        login(server + "/qcbin/api/authentication/sign-in", loginData.getUser(),
                                loginData.getPassword());
                    }
                    final HttpURLConnection conRetry = (HttpURLConnection) escapedUri.toURL().openConnection();
                    conRetry.setRequestMethod(type);
                    final String cookieString2 = getCookieString();
                    prepareHttpRequest(conRetry, headers, data, cookieString2);
                    conRetry.connect();
                    ret = retrieveHtmlResponse(conRetry);
                } else {
                    throw new Exception("QC Login not working. Tried 5 times in a row.");
                }
            }
            LOGGER.trace("Got Response: " + ret.toString());
            updateCookies(ret);
            return ret;
        }
    }

    /**
     * gets the cookies
     *
     * @return the cookies
     */
    public Map<String, String> getCookies() {
        return cookies;
    }

    /**
     * Get String representation of all cookies.
     *
     * @return String with names and value fo cookies. (name=value;name2=value2;...)
     */
    public String getCookieString() {
        final StringBuilder builder = new StringBuilder();
        if (cookies != null && !cookies.isEmpty()) {
            final Set<Entry<String, String>> cookieEntries = cookies.entrySet();
            for (Entry<String, String> entry : cookieEntries) {
                builder.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
            }
        }
        return builder.toString();
    }

    /**
     * Get entities returned by the query or null at any exception.
     *
     * @param restUrl  Target for request.
     * @param queryUrl Query on result.
     *
     * @return List of Entity objects.
     *
     * @throws IOException Exception during communication with rest service.
     */
    public List<Entity> getEntities(final String restUrl, String queryUrl) throws Exception {
        // Set page size to maximum
        if (queryUrl == null || queryUrl.isEmpty()) {
            queryUrl = "page-size=" + PAGE_SIZE;
        } else {
            queryUrl = queryUrl + "&page-size=" + PAGE_SIZE;
        }

        final Response response = httpGet(restUrl, queryUrl);
        final Entities entities;
        entities = MarshallingUtils.marshal(Entities.class, response.toString());
        return entities.getEntity();
    }

    /**
     * Get entities returned by the query or null at any exception.
     *
     * @param restUrl  Target for request.
     * @param queryUrl Query on result.
     *
     * @return List of Entity objects.
     *
     * @throws IOException Exception during communication with rest service.
     */
    public Entity getEntity(final String restUrl, final String queryUrl) throws Exception {
        final Entity entity;
        final Response response = httpGet(restUrl, queryUrl);
        try {
            entity = MarshallingUtils.marshal(Entity.class, response.toString());
        } catch (Exception e) {
            String msg = "Exception getting entity for" + "\n" + response.toString();
            throw new IOException(msg, e);
        }
        return entity;
    }

    /**
     * gets the loginData
     *
     * @return the loginData
     */
    public LoginData getLoginData() {
        return loginData;
    }

    /**
     * Do DELETE-Request at rest service.
     *
     * @param url Target of request.
     *
     * @return Requests response.
     *
     * @throws IOException Exception during Request.
     */
    public Response httpDelete(final String url) throws Exception {
        LOGGER.trace("Do DELETE on REST Service");
        return doHttp("DELETE", url, null, null, null);
    }

    /**
     * Do GET-Request at rest service.
     *
     * @param url         Target of request.
     * @param queryString Possible query to constrain output.
     *
     * @return Requests response.
     *
     * @throws IOException Exception during Request.
     */
    public Response httpGet(final String url, final String queryString)
            throws Exception {
        LOGGER.trace("Do GET on REST Service:\n" +
                "url: " + url + "\n" +
                "query: " + queryString);
        return doHttp("GET", url, queryString, null, null);
    }

    /**
     * Do POST-Request at rest service.
     *
     * @param url     Target of request.
     * @param data    Data to send.
     * @param headers Special headers for POST.
     *
     * @return Requests response.
     *
     * @throws IOException Exception during Request.
     */
    public Response httpPost(final String url, final byte[] data, final Map<String, String> headers)
            throws Exception {
        String hString = "";
        if (headers != null) {
            for (String key : headers.keySet()) {
                hString += "\n" + key + " = " + headers.get(key);
            }
        }

        LOGGER.trace("Do POST on REST Service:\n" +
                "url: " + url + "\n" +
                "data: " + new String(data) + "\n" +
                "headers: " + hString);
        return doHttp("POST", url, null, data, headers);
    }

    /**
     * Do PUT-Request at rest service.
     *
     * @param url     Target of request.
     * @param data    Data to send.
     * @param headers Special headers for POST.
     *
     * @return Requests response.
     *
     * @throws IOException Exception during Request.
     */
    public Response httpPut(final String url, final byte[] data, final Map<String, String> headers)
            throws Exception {
        String hString = "";
        if (headers != null) {
            for (String key : headers.keySet()) {
                hString += "\n" + key + " = " + headers.get(key);
            }
        }
        LOGGER.trace("Do PUT on REST Service:\n" +
                "url: " + url + "\n" +
                "data: " + new String(data) + "\n" +
                "headers: " + hString);
        return doHttp("PUT", url, null, data, headers);
    }

    /**
     * Logging in to QC is standard http login (basic authentication), where one must store the returned cookies for
     * further use.
     *
     * @param loginUrl to authenticate at
     * @param username username used for authentication.
     * @param password password used for authentication.
     *
     * @return true on operation success, false otherwise
     *
     * @throws IOException http exception.
     */
    private boolean login(final String loginUrl, final String username, final String password) throws Exception {
        // Create a string that looks like "Basic ((username:password)<as bytes>)<64encoded>"
        final byte[] credBytes = (username + ":" + password).getBytes();
        final String credEncodedString = "Basic " + Base64.getEncoder().encodeToString(credBytes);
        requestHeaders.put("Authorization", credEncodedString);
        final Response response = doHttp("GET", loginUrl, null, null, null);
        final boolean ret = response.getStatusCode() == HttpURLConnection.HTTP_OK;
        requestHeaders.remove("Authorization");
        LOGGER.debug("Login of User " + username + (ret ? " successful." : " not successful."));
        if (ret) {
            loginTries = 0;
        }
        return ret;
    }

    /**
     * Logout from QC.
     *
     * @return true if logout successful
     */
    public boolean logout() {
        synchronized (instanceLock) {
            try {
                Response response = doHttp("GET", buildUrl("qcbin/authentication-point/logout"), null, null, null);
                LOGGER.debug("Logged out User from QC Rest Service");

                boolean loggedOut = response.getStatusCode() == HttpURLConnection.HTTP_OK;
                if (loggedOut) {
                    instance = null;
                }
                return loggedOut;
            } catch (Exception e) {
                LOGGER.debug("Logout failed", e);
            }
        }
        return false;
    }

    /**
     * Prepare the request.
     *
     * @param con          to set the headers and bytes in
     * @param headers      to use in the request, such as content-type
     * @param bytes        the actual data to post in the connection.
     * @param cookieString the cookies data from clientside, such as lwsso, qcsession, jsession etc..
     *
     * @throws IOException Exception during request.
     */
    private void prepareHttpRequest(
            final HttpURLConnection con,
            final Map<String, String> headers,
            final byte[] bytes,
            final String cookieString) throws IOException {
        String contentType = null;
        // Attach cookie information if it exists.
        if ((cookieString != null) && !cookieString.isEmpty()) {
            con.setRequestProperty("Cookie", cookieString);
        }
        // Send data from headers.
        if (headers != null) {
            // Skip the content-type header. The content-type header should only be sent if you are sending content. See
            // below.
            contentType = headers.remove("Content-Type");
            final Iterator<Entry<String, String>> headersIterator = headers.entrySet().iterator();
            while (headersIterator.hasNext()) {
                final Entry<String, String> header = headersIterator.next();
                con.setRequestProperty(header.getKey(), header.getValue());
            }
        }
        // If there is data to attach to the request, it's handled here. Note that if data exists, we take into account
        // previously removed content-type.
        if ((bytes != null) && (bytes.length > 0)) {
            con.setDoOutput(true);
            // Warning: If you add a content-type header then it is an error not to send information.
            if (contentType != null) {
                con.setRequestProperty("Content-Type", contentType);
            }
            final OutputStream out = con.getOutputStream();
            out.write(bytes);
            out.flush();
            out.close();
        }
    }

    /**
     * Remove the header entry with the given key.
     *
     * @param key Key of entry to remove.
     */
    public void removeRequestHeader(final String key) {
        requestHeaders.remove(key);
    }

    /**
     * @param con that already connected to it's url with an http request, and that should contain a response for us to
     *            retrieve
     *
     * @return a response from the server to the previously submitted http request
     *
     * @throws IOException Exception during http request.
     */
    private Response retrieveHtmlResponse(final HttpURLConnection con) throws IOException {
        final Response ret = new Response();
        ret.setStatusCode(con.getResponseCode());
        ret.setResponseHeaders(con.getHeaderFields());
        InputStream inputStream;
        // Select the source of the input bytes, first try "regular" input
        try {
            inputStream = con.getInputStream();
        }
        /*
         * If the connection to the server failed, for example 404 or 500, con.getInputStream() throws an exception,
         * which is saved. The body of the exception page is stored in the response data.
         */ catch (Exception e) {
            inputStream = con.getErrorStream();
            ret.setFailure(e);
        }
        // This takes the data from the previously decided stream (error or input) and stores it in a byte[] inside the
        // response.
        final ByteArrayOutputStream container = new ByteArrayOutputStream();
        final byte[] buf = new byte[1024];
        int read;
        while ((read = inputStream.read(buf, 0, 1024)) > 0) {
            container.write(buf, 0, read);
        }
        ret.setResponseData(container.toByteArray());

        if (ret.getStatusCode() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
            try {
                ret.setServerException(MarshallingUtils.marshal(QCRestException.class, ret.toString()));
            } catch (JAXBException e) {
                LOGGER.warn("Error in response", e);
            }
        }
        return ret;
    }

    /**
     * sets the cookies
     *
     * @param cookies the cookies to set
     */
    public void setCookies(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    /**
     * sets the loginData
     *
     * @param loginData the loginData to set
     */
    public void setLoginData(final LoginData loginData) {
        this.loginData = loginData;
    }

    /**
     * Get cookies of response and copy to Map.
     *
     * @param response Http Response.
     */
    private void updateCookies(final Response response) {
        final Iterable<String> newCookies = response.getResponseHeaders().get("Set-Cookie");
        if (newCookies != null) {
            for (String cookie : newCookies) {
                final int equalIndex = cookie.indexOf('=');
                final int semicolonIndex = cookie.indexOf(';');
                final String cookieKey = cookie.substring(0, equalIndex);
                final String cookieValue = cookie.substring(equalIndex + 1, semicolonIndex);
                cookies.put(cookieKey, cookieValue);
            }
        }
    }
}
