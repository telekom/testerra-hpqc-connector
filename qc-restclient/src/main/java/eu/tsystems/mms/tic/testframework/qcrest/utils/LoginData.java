/*
 * Created on 26.02.2013
 *
 * Copyright(c) 2010 - 2013 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.qcrest.utils;

/**
 * Class holding all necessary data for a connection to QC.
 * 
 * @author sepr
 */
public class LoginData {

    /** QC Domain. */
    private String domain;
    /** QC Password. */
    private String password;
    /** QC project. */
    private String project;
    /** Url of qc server. */
    private String server;
    /** QC Username. */
    private String user;

    /**
     * Constructor filling all fields.
     * 
     * @param domain QC Domain.
     * @param password QC Password.
     * @param project QC project.
     * @param server Url of qc server.
     * @param user QC Username.
     */
    public LoginData(final String domain, final String password, final String project, final String server,
                     final String user) {
        this.domain = domain;
        this.password = password;
        this.project = project;
        this.server = server;
        this.user = user;
    }

    /**
     * Gets the value of the domain property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Sets the value of the domain property.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setDomain(final String value) {
        this.domain = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setPassword(final String value) {
        this.password = value;
    }

    /**
     * Gets the value of the project property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getProject() {
        return project;
    }

    /**
     * Sets the value of the project property.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setProject(final String value) {
        this.project = value;
    }

    /**
     * Gets the value of the server property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getServer() {
        return server;
    }

    /**
     * Sets the value of the server property.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setServer(final String value) {
        this.server = value;
    }

    /**
     * Gets the value of the user property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the value of the user property.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setUser(final String value) {
        this.user = value;
    }

}
