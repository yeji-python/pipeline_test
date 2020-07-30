package com.sinochem.op.common.test.service;

import com.sinochem.op.common.http.service.HttpService;
import com.sinochem.op.common.test.base.BaseMockDataType;
import com.sinochem.op.common.test.base.MockDataSetter;
import com.sinochem.op.common.test.dto.MockDataDTO;
import com.sinochem.op.common.test.dto.TestDataDTO;

import java.util.List;

/**
 * 模拟数据Service
 */
public interface MockDataService {

    /**
     * 设置数据
     */
    void setMockData(MockDataDTO mockData);

    /**
     * 获取数据
     */
    <T> T getMockData(BaseMockDataType baseMockDataType);

    /**
     * 获取数据列表
     */
    <T> List<T> findMockDataList(BaseMockDataType baseMockDataType);

    /**
     * 自动化测试
     */
    List<String> runAutomatedTest(List<TestDataDTO> testDataList, MockDataSetter mockDataSetter);

    /**
     * 设置Http请求bean
     */
    void setHttpService(HttpService httpService);
}
