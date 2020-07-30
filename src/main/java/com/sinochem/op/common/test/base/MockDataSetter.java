package com.sinochem.op.common.test.base;

import com.sinochem.op.common.test.dto.MockDataDTO;

@FunctionalInterface
public interface MockDataSetter {

    /**
     * 设置模拟数据
     */
    void setMockData(MockDataDTO mockData);
}
