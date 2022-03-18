/*
 * Testerra
 *
 * (C) 2013, Stefan Prasse, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
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
