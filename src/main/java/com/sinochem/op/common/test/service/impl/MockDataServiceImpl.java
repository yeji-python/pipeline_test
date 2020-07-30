package com.sinochem.op.common.test.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sinochem.op.common.http.service.HttpService;
import com.sinochem.op.common.share.enums.CommonResultEnum;
import com.sinochem.op.common.share.exception.ServiceException;
import com.sinochem.op.common.test.base.BaseMockDataType;
import com.sinochem.op.common.test.base.MockDataSetter;
import com.sinochem.op.common.test.dto.MockDataDTO;
import com.sinochem.op.common.test.dto.TestDataDTO;
import com.sinochem.op.common.test.enums.ResultEnum;
import com.sinochem.op.common.test.service.MockDataService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class MockDataServiceImpl implements MockDataService {

    /**
     * 缓存key名称
     */
    private static final String CACHE_KEY_PREFIX = "akat-mock-data-";

    /**
     * 缓存有效时间（分钟）
     */
    private final int MOCK_DATA_LIFESPAN = 10;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private HttpService httpService;

    @Override
    public void setMockData(MockDataDTO mockData) {
        try {
            if (mockData == null) {
                throw new ServiceException(CommonResultEnum.COMMON_EMPTY_PARAM);
            }

            String mockDataCode = mockData.getMockDataCode();
            if (StringUtils.isEmpty(mockDataCode)) {
                throw new ServiceException(CommonResultEnum.COMMON_INVALID_PARAM);
            }

            ValueOperations<String, MockDataDTO> valueOperations = redisTemplate.opsForValue();
            valueOperations.set(this.genCacheKey(mockDataCode), mockData, MOCK_DATA_LIFESPAN, TimeUnit.MINUTES);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(CommonResultEnum.COMMON_SYS_ERROR);
        }
    }

    @Override
    public <T> T getMockData(BaseMockDataType baseMockDataType) {
        try {
            if (baseMockDataType == null) {
                throw new ServiceException(CommonResultEnum.COMMON_EMPTY_PARAM);
            }
            Class<T> clazz = baseMockDataType.getMockDataClass();
            String mockDataCode = baseMockDataType.getMockDataCode();
            if (clazz == null || StringUtils.isEmpty(mockDataCode)) {
                throw new ServiceException(CommonResultEnum.COMMON_EMPTY_PARAM);
            }

            ValueOperations<String, MockDataDTO> valueOperations = redisTemplate.opsForValue();
            MockDataDTO mockData = valueOperations.get(this.genCacheKey(mockDataCode));
            if (mockData == null) {
                throw new ServiceException(CommonResultEnum.COMMON_DATA_NOT_EXIST);
            }

            JSONObject dataObject = mockData.getDataObject();
            if (dataObject == null) {
                throw new ServiceException(CommonResultEnum.COMMON_INVALID_DATA);
            }

            return dataObject.toJavaObject(clazz);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(CommonResultEnum.COMMON_SYS_ERROR);
        }
    }

    @Override
    public <T> List<T> findMockDataList(BaseMockDataType baseMockDataType) {
        try {
            if (baseMockDataType == null) {
                throw new ServiceException(CommonResultEnum.COMMON_EMPTY_PARAM);
            }
            Class<T> clazz = baseMockDataType.getMockDataClass();
            String mockDataCode = baseMockDataType.getMockDataCode();
            if (clazz == null || StringUtils.isEmpty(mockDataCode)) {
                throw new ServiceException(CommonResultEnum.COMMON_EMPTY_PARAM);
            }

            ValueOperations<String, MockDataDTO> valueOperations = redisTemplate.opsForValue();
            MockDataDTO mockData = valueOperations.get(this.genCacheKey(mockDataCode));
            if (mockData == null) {
                throw new ServiceException(CommonResultEnum.COMMON_DATA_NOT_EXIST, mockDataCode);
            }

            JSONArray dataArray = mockData.getDataArray();
            if (dataArray == null) {
                throw new ServiceException(CommonResultEnum.COMMON_INVALID_DATA, mockDataCode);
            }

            return dataArray.toJavaList(clazz);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(CommonResultEnum.COMMON_SYS_ERROR);
        }
    }

    @Override
    public List<String> runAutomatedTest(List<TestDataDTO> testDataList, MockDataSetter mockDataSetter) {
        try {
            if (CollectionUtils.isEmpty(testDataList)) {
                throw new ServiceException(CommonResultEnum.COMMON_EMPTY_PARAM);
            }

            List<String> resultList = new ArrayList<>(testDataList.size());
            testDataList.forEach(testDataDTO -> {
                String result = null;

                try {
                    String requestUrl = testDataDTO.getRequestUrl();
                    if (StringUtils.isEmpty(requestUrl)) {
                        throw new ServiceException(CommonResultEnum.COMMON_INVALID_PARAM);
                    }

                    String requestMethod = testDataDTO.getRequestMethod();
                    if (StringUtils.isEmpty(requestMethod)) {
                        requestMethod = RequestMethod.POST.toString();
                    }

                    List<MockDataDTO> mockDataDTOList = testDataDTO.getMockDataList();
                    if (CollectionUtils.isNotEmpty(mockDataDTOList)) {
                        mockDataDTOList.forEach(mockDataDTO -> {
                            if (mockDataSetter != null) {
                                mockDataSetter.setMockData(mockDataDTO);
                            } else {
                                this.setMockData(mockDataDTO);
                            }
                        });
                    }

                    if (RequestMethod.GET.toString().equals(requestMethod)) {
                        result = httpService.get(requestUrl + testDataDTO.getParamString());
                    } else if (RequestMethod.POST.toString().equals(requestMethod)) {
                        result = httpService.postJson(requestUrl, testDataDTO.getParamString());
                    } else {
                        // 就是不支持除了GET和POST的任何请求方式，也不应该出现其它的方式
                        throw new ServiceException(ResultEnum.UNSUPPORTED_REQUEST_METHOD);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    result = e.getMessage();
                } finally {
                    resultList.add(result);
                }
            });

            return resultList;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(CommonResultEnum.COMMON_SYS_ERROR);
        }
    }

    @Override
    public void setHttpService(HttpService httpService) {
        if (httpService == null) {
            return;
        }

        this.httpService = httpService;
    }

    /* 类内辅助方法 */

    /**
     * 生成缓存key
     */
    private String genCacheKey(String code) {
        return CACHE_KEY_PREFIX + code;
    }
}
