package com.sinochem.op.common.test.dto.external.charles;

import java.io.Serializable;

/**
 * Charles请求数据
 */
public class CharlesRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * mimeType
     */
    private String mimeType;

    /**
     * header
     */
    private CharlesHeaderListDTO header;

    /**
     * body
     */
    private CharlesBodyDTO body;

    /* getters and setters */

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public CharlesHeaderListDTO getHeader() {
        return header;
    }

    public void setHeader(CharlesHeaderListDTO header) {
        this.header = header;
    }

    public CharlesBodyDTO getBody() {
        return body;
    }

    public void setBody(CharlesBodyDTO body) {
        this.body = body;
    }
}
