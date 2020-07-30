package com.sinochem.op.common.test.dto.external.charles;

import java.io.Serializable;

/**
 * Charles请求头信息
 */
public class CharlesHeaderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * name
     */
    private String name;

    /**
     * value
     */
    private String value;

    /* getters and setters */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
