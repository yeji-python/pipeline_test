package com.sinochem.op.common.test.dto;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * 模拟数据DTO
 */
public class MockDataDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模拟数据code
     */
    private String mockDataCode;

    /**
     * 模拟数据
     */
    private JSONObject dataObject;

    /**
     * 模拟数据
     */
    private JSONArray dataArray;

    /* getters and setters */

    public String getMockDataCode() {
        return mockDataCode;
    }

    public void setMockDataCode(String mockDataCode) {
        this.mockDataCode = mockDataCode;
    }

    public JSONObject getDataObject() {
        return dataObject;
    }

    public void setDataObject(JSONObject dataObject) {
        this.dataObject = dataObject;
    }

    public JSONArray getDataArray() {
        return dataArray;
    }

    public void setDataArray(JSONArray dataArray) {
        this.dataArray = dataArray;
    }
}
