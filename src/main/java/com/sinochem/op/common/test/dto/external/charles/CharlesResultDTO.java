package com.sinochem.op.common.test.dto.external.charles;

import java.io.Serializable;

/**
 * Charles结果
 */
public class CharlesResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * method
     */
    private String method;

    /**
     * host
     */
    private String host;

    /**
     * path
     */
    private String path;

    /**
     * query
     */
    private String query;

    /**
     * scheme
     */
    private String scheme;

    /**
     * actualPort
     */
    private String actualPort;

    /**
     * request
     */
    private CharlesRequestDTO request;

    /* getters and setters */

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public CharlesRequestDTO getRequest() {
        return request;
    }

    public void setRequest(CharlesRequestDTO request) {
        this.request = request;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getActualPort() {
        return actualPort;
    }

    public void setActualPort(String actualPort) {
        this.actualPort = actualPort;
    }
}
