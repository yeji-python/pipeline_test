package com.sinochem.op.common.test.dto.external.charles;

import java.io.Serializable;

/**
 * Charles请求体
 */
public class CharlesBodyDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * text
     * 其实这是个JSONString，需要转换
     */
    private String text;

    /* getters and setters */

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
