package com.sinochem.op.common.test.dto.external.charles;

import java.io.Serializable;
import java.util.List;

/**
 * Charles请求头列表
 */
public class CharlesHeaderListDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * headers
     */
    private List<CharlesHeaderDTO> headers;

    /* getters and setters */

    public List<CharlesHeaderDTO> getHeaders() {
        return headers;
    }

    public void setHeaders(List<CharlesHeaderDTO> headers) {
        this.headers = headers;
    }
}
