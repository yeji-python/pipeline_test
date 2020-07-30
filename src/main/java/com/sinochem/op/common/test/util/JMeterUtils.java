package com.sinochem.op.common.test.util;

import com.alibaba.fastjson.JSON;
import com.sinochem.op.common.share.constant.Mark;
import com.sinochem.op.common.share.enums.CommonResultEnum;
import com.sinochem.op.common.share.exception.ServiceException;
import com.sinochem.op.common.test.dto.external.charles.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.testelement.property.BooleanProperty;
import org.apache.jmeter.testelement.property.StringProperty;
import org.apache.jmeter.testelement.property.TestElementProperty;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.util.List;

public class JMeterUtils {

    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_POST = "POST";

    private static final String HTTP_GET_PARAM_SEPARATOR = "?";

    private static final String DEFAULT_TEST_PLAN_NAME = "MAP Performance Test Plan";
    private static final String DEFAULT_THREAD_GROUP = "Thread Group";
    private static final String DEFAULT_LOOP_CONTROLLER_NAME = "Loop Controller";
    private static final String DEFAULT_HTTP_HEADER_MANAGER_NAME = "Header Manager";
    private static final String DEFAULT_HTTP_SAMPLER_PROXY_NAME_PREDIX = "HTTP Sampler Proxy - ";

    private static final String DEFAULT_TEST_PLAN_GUI = "TestPlanGui";
    private static final boolean DEFAULT_TEST_PLAN_ENABLED = true;
    private static final String KEY_TEST_PLAN_COMMNETS = "TestPlan.comments";
    private static final String DEFAULT_TEST_PLAN_COMMENTS = StringUtils.EMPTY;
    private static final String KEY_TEST_PLAN_USER_DEFINE_CLASSPATH = "TestPlan.user_define_classpath";
    private static final String DEFAULT_TEST_PLAN_USER_DEFINE_CLASSPATH = StringUtils.EMPTY;
    private static final String KEY_TEST_PLAN_USER_DEFINE_VARIABLES = "TestPlan.user_defined_variables";
    private static final Arguments DEFAULT_TEST_PLAN_USER_DEFINE_VARIABLES = new Arguments();

    private static final String DEFAULT_THREAD_GROUP_GUI = "ThreadGroupGui";
    private static final boolean DEFAULT_THREAD_GROUP_ENABLED = true;
    private static final String KEY_THREAD_GROUP_MAIN_CONTROLLER = "ThreadGroup.main_controller";
    private static final int DEFAULT_THREAD_GROUP_THREAD_NUM = 1;
    private static final int DEFAULT_THREAD_GROUP_RAMP_UP = 1;

    private static final int DEFAULT_LOOP_CONTROLLER_LOOPS = 1;
    private static final String KEY_LOOP_CONTROLLER_CONTINUE_FOREVER = "LoopController.continue_forever";
    private static final boolean DEFAULT_LOOP_CONTROLLER_CONTINUE_FOREVER = false;
    private static final String DEFAULT_LOOP_CONTROLLER_GUI_CLASS = "org.apache.jmeter.control.gui.LoopControlPanel";
    private static final String DEFAULT_LOOP_CONTROLLER_TEST_CLASS = "org.apache.jmeter.control.LoopController";
    private static final boolean DEFAULT_LOOP_CONTROLLER_ENABLED = true;

    private static final String DEFAULT_HTTP_SAMPLER_PROXY_GUI = "org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui";
    private static final boolean DEFAULT_HTTP_SAMPLER_PROXY_ENABLED = true;

    private static final String DEFAULT_HEADER_MANAGER_GUI = "org.apache.jmeter.protocol.http.gui.HeaderPanel";
    private static final boolean DEFAULT_HEADER_MANAGER_ENABLED = true;

    private static final String STRING_PROP_HTTP_SAMPLER_ARGUMENT_VALUE = "Argument.value";

    private static final int START_COUNTER = 0;

    /* 公共方法 */

    /**
     * 写出HashTree
     */
    public static void exportHashTree(HashTree hashTree, OutputStream outputStream) {
        try {
            SaveService.saveTree(hashTree, outputStream);
        } catch (IOException e) {
            throw new ServiceException(CommonResultEnum.COMMON_IO_OPERATION_FAILED);
        }
    }

    /* 类内辅助方法 */

    /**
     * 将Charles结果转化为JMX的HashTree
     */
    private static HashTree convertToHashTree(List<CharlesResultDTO> charlesResultList) {
        return convertToHashTree(DEFAULT_TEST_PLAN_NAME, charlesResultList);
    }

    /**
     * 将Charles结果转化为JMX的HashTree
     */
    private static HashTree convertToHashTree(String testPlanName, List<CharlesResultDTO> charlesResultList) {
        if (CollectionUtils.isEmpty(charlesResultList)) {
            throw new ServiceException(CommonResultEnum.COMMON_EMPTY_PARAM);
        }

        HashTree httpSamplerProxyListHashTree = new ListedHashTree();

        int size = charlesResultList.size();
        int counter = START_COUNTER;
        for (int i = Mark.FIRST_INDEX; i < size; i++) {
            CharlesResultDTO charlesResultDTO = charlesResultList.get(i);
            if (charlesResultDTO == null) {
                throw new ServiceException(CommonResultEnum.COMMON_INVALID_PARAM);
            }

            CharlesRequestDTO charlesRequestDTO = charlesResultDTO.getRequest();
            if (charlesRequestDTO == null) {
                throw new ServiceException(CommonResultEnum.COMMON_INVALID_PARAM);
            }

            /* HTTP Sampler Proxy */
            HTTPSamplerProxy httpSamplerProxy = new HTTPSamplerProxy();
            httpSamplerProxy.setName(DEFAULT_HTTP_SAMPLER_PROXY_NAME_PREDIX + counter);
            httpSamplerProxy.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
            httpSamplerProxy.setProperty(TestElement.GUI_CLASS, DEFAULT_HTTP_SAMPLER_PROXY_GUI);
            httpSamplerProxy.setProperty(new BooleanProperty(TestElement.ENABLED, DEFAULT_HTTP_SAMPLER_PROXY_ENABLED));

            String path = charlesResultDTO.getPath();
            if (StringUtils.isEmpty(path)) {
                continue;
            }

            String method = charlesResultDTO.getMethod();
            if (HTTP_METHOD_GET.equalsIgnoreCase(method)) {
                String query = charlesResultDTO.getQuery();

                if (StringUtils.isNotEmpty(query)) {
                    path = path + HTTP_GET_PARAM_SEPARATOR + query;
                }
            } else if (HTTP_METHOD_POST.equalsIgnoreCase(method)) {
                CharlesBodyDTO charlesBodyDTO = charlesRequestDTO.getBody();

                if (charlesBodyDTO != null) {
                    String text = charlesBodyDTO.getText();

                    if (StringUtils.isNotEmpty(text)) {
                        httpSamplerProxy.setPostBodyRaw(true);
                        httpSamplerProxy.addArgument(
                                STRING_PROP_HTTP_SAMPLER_ARGUMENT_VALUE, text
                        );
                    }
                }
            } else {
                // 是的，就是不支持GET和POST之外的请求，就不应该有除了GET和POST以外的请求
                // 纯REST风格适用且仅适用于最简单的CRUD系统
                continue;
            }

            httpSamplerProxy.setPort(Integer.parseInt(charlesResultDTO.getActualPort()));
            httpSamplerProxy.setProtocol(charlesResultDTO.getScheme());
            httpSamplerProxy.setPath(path);
            httpSamplerProxy.setMethod(method);
            httpSamplerProxy.setDomain(charlesResultDTO.getHost());

            /* Header Manager */
            CharlesHeaderListDTO charlesHeaderListDTO = charlesRequestDTO.getHeader();
            if (charlesHeaderListDTO == null) {
                throw new ServiceException(CommonResultEnum.COMMON_INVALID_PARAM);
            }


            List<CharlesHeaderDTO> charlesHeaderDTOList = charlesHeaderListDTO.getHeaders();
            if (CollectionUtils.isEmpty(charlesHeaderDTOList)) {
                throw new ServiceException(CommonResultEnum.COMMON_INVALID_PARAM);
            }

            HeaderManager headerManager = new HeaderManager();
            headerManager.setName(DEFAULT_HTTP_HEADER_MANAGER_NAME);
            headerManager.setProperty(TestElement.TEST_CLASS, HeaderManager.class.getName());
            headerManager.setProperty(TestElement.GUI_CLASS, DEFAULT_HEADER_MANAGER_GUI);
            headerManager.setProperty(new BooleanProperty(TestElement.ENABLED, DEFAULT_HEADER_MANAGER_ENABLED));

            final String INVALID_HEADER_STARTER = ":";
            charlesHeaderDTOList.forEach(charlesHeaderDTO -> {
                // Header里面有格式错误你敢信?
                String headerName = charlesHeaderDTO.getName();
                if (headerName.startsWith(INVALID_HEADER_STARTER)) {
                    headerName = headerName.replaceFirst(INVALID_HEADER_STARTER, StringUtils.EMPTY);
                }

                headerManager.add(new Header(headerName, charlesHeaderDTO.getValue()));
            });

            HashTree headerManagerHashTree = new HashTree();
            headerManagerHashTree.add(headerManager);

            /* 添加线程组 */
            HashTree httpSamplerProxyHashTree = new ListedHashTree();
            httpSamplerProxyHashTree.add(httpSamplerProxy, headerManagerHashTree);

            httpSamplerProxyListHashTree.add(httpSamplerProxyHashTree);

            /* 计数器 */
            counter++;
        }

        /* Loop Controller */
        LoopController loopController = new LoopController();
        loopController.setName(DEFAULT_LOOP_CONTROLLER_NAME);
        loopController.setLoops(DEFAULT_LOOP_CONTROLLER_LOOPS);
        loopController.setProperty(new BooleanProperty(KEY_LOOP_CONTROLLER_CONTINUE_FOREVER, DEFAULT_LOOP_CONTROLLER_CONTINUE_FOREVER));
        loopController.setProperty(new StringProperty(TestElement.GUI_CLASS, DEFAULT_LOOP_CONTROLLER_GUI_CLASS));
        loopController.setProperty(new StringProperty(TestElement.TEST_CLASS, DEFAULT_LOOP_CONTROLLER_TEST_CLASS));
        loopController.setProperty(new BooleanProperty(TestElement.ENABLED, DEFAULT_LOOP_CONTROLLER_ENABLED));

        /* Thread Group */
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName(DEFAULT_THREAD_GROUP);
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS, DEFAULT_THREAD_GROUP_GUI);
        threadGroup.setProperty(new BooleanProperty(TestElement.ENABLED, DEFAULT_THREAD_GROUP_ENABLED));
        threadGroup.setProperty(new TestElementProperty(KEY_THREAD_GROUP_MAIN_CONTROLLER, loopController));
        threadGroup.setNumThreads(DEFAULT_THREAD_GROUP_THREAD_NUM);
        threadGroup.setRampUp(DEFAULT_THREAD_GROUP_RAMP_UP);

        HashTree threadGroupHashTree = new ListedHashTree();
        threadGroupHashTree.add(threadGroup, httpSamplerProxyListHashTree);

        TestPlan testPlan = new TestPlan(testPlanName);
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, DEFAULT_TEST_PLAN_GUI);
        testPlan.setProperty(new BooleanProperty(TestElement.ENABLED, DEFAULT_TEST_PLAN_ENABLED));
        testPlan.setProperty(new StringProperty(KEY_TEST_PLAN_COMMNETS, DEFAULT_TEST_PLAN_COMMENTS));
        testPlan.setProperty(new StringProperty(KEY_TEST_PLAN_USER_DEFINE_CLASSPATH, DEFAULT_TEST_PLAN_USER_DEFINE_CLASSPATH));
        testPlan.setProperty(new TestElementProperty(KEY_TEST_PLAN_USER_DEFINE_VARIABLES, DEFAULT_TEST_PLAN_USER_DEFINE_VARIABLES));

        HashTree testPlanHashTree = new ListedHashTree();
        testPlanHashTree.add(testPlan, threadGroupHashTree);

        return testPlanHashTree;
    }

    /**
     * 测试主程序
     */
    public static void main(String[] args) {
        final String KEY_SAVE_SERVICE_PROPERTIES = "saveservice_properties";
        final String PATH_SAVE_SERVICE_PROPERTIES = "D:\\dev\\apache-jmeter-5.1.1\\bin\\saveservice.properties";

        final String PATH_JMETER_PROPERTIES = "D:\\dev\\apache-jmeter-5.1.1\\bin\\jmeter.properties";

        final String TEST_PLAN_NAME = "创建返回";
        final String CHARLES_RESULT_FILE = "D:\\dev\\jmeter-conversion\\" + TEST_PLAN_NAME + ".chlsj";
        final String OUTPUT_JMX_FILE = "D:\\dev\\jmeter-conversion\\" + TEST_PLAN_NAME + ".jmx";

        org.apache.jmeter.util.JMeterUtils.loadJMeterProperties(PATH_JMETER_PROPERTIES);
        org.apache.jmeter.util.JMeterUtils.setProperty(KEY_SAVE_SERVICE_PROPERTIES, PATH_SAVE_SERVICE_PROPERTIES);

        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;

        try {
            fileInputStream = new FileInputStream(new File(CHARLES_RESULT_FILE));
            byte[] content = new byte[fileInputStream.available()];
            fileInputStream.read(content);

            String charlesResultString = new String(content);
            List<CharlesResultDTO> charlesResultList = JSON.parseArray(charlesResultString, CharlesResultDTO.class);

            File outputJmxFile = new File(OUTPUT_JMX_FILE);
            outputJmxFile.createNewFile();

            fileOutputStream = new FileOutputStream(outputJmxFile);
            fileInputStream = new FileInputStream(outputJmxFile);

            exportHashTree(convertToHashTree(TEST_PLAN_NAME, charlesResultList), fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
