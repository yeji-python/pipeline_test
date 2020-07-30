package com.sinochem.op.common.test.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 测试数据DTO
 */
public class TestDataDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 参数
     * GET请求时，直接拼接到requestUrl上
     * POST请求时，必须为JSON格式
     */
    private String paramString;

    /**
     * 请求方法
     * 默认POST
     */
    private String requestMethod;

    /**
     * 请求地址
     */
    private String requestUrl;

    /**
     * 模拟数据列表
     */
    private List<MockDataDTO> mockDataList;

    /* getters and setters */

    public String getParamString() {
        return paramString;
    }

    public void setParamString(String paramString) {
        this.paramString = paramString;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public List<MockDataDTO> getMockDataList() {
        return mockDataList;
    }

    public void setMockDataList(List<MockDataDTO> mockDataList) {
        this.mockDataList = mockDataList;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }
}
