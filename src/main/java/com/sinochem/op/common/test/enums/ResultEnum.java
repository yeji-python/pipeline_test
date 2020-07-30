package com.sinochem.op.common.test.enums;

import com.sinochem.op.common.share.base.BaseResult;

public enum ResultEnum implements BaseResult {

    UNSUPPORTED_REQUEST_METHOD("unsupported_request_method", "不支持该请求格式");

    /**
     * 代码
     */
    private String code;

    /**
     * 信息
     */
    private String message;

    /* constructors */

    ResultEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /* getters */

    @Override
    public String getCode() {
        return null;
    }

    @Override
    public String getMessage() {
        return null;
    }
}
