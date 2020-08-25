package com.sy.tool;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class AlipayProperties {

    private static final String APP_ID = "appId";
    private static final String PRIVATE_KEY = "privateKey";
    private static final String PUBLIC_KEY = "publicKey";
    private static final String NOTIFY_URL = "notifyUrl";
    private static final String RETURN_URL = "returnUrl";
    private static final String SIGN_TYPE = "signType";
    private static final String CHARSET = "charset";
    private static final String GATEWAY_URL = "gatewayUrl";
    private static final String LOG_PATH = "logPath";

    /**
     * 保存加载配置参数
     */
    private static Map<String, String> propertiesMap = new HashMap<>();

    /**
     * 是将配置文件转换成map集合
     *
     * 加载属性
     */
    @PostConstruct
    public void loadProperties() throws Exception {
        // 获得PathMatchingResourcePatternResolver对象
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            // 加载resource文件(也可以加载resources)
            Resource resources = resolver.getResource("classpath:zfbinfo.properties");
            PropertiesFactoryBean config = new PropertiesFactoryBean();
            config.setLocation(resources);
            config.afterPropertiesSet();
            Properties prop = config.getObject();
            // 循环遍历所有得键值对并且存入集合
            for (String key : prop.stringPropertyNames()) {
                propertiesMap.put(key, (String) prop.get(key));
            }
        } catch (Exception e) {
            throw new Exception("配置文件加载失败");
        }
    }


    public String getAppId() {
        return propertiesMap.get(APP_ID);
    }

    public String getPrivateKey() {
        return propertiesMap.get(PRIVATE_KEY);
    }

    public String getPublicKey() {
        return propertiesMap.get(PUBLIC_KEY);
    }

    public String getNotifyUrl() {
        return propertiesMap.get(NOTIFY_URL);
    }

    public String getReturnUrl() {
        return propertiesMap.get(RETURN_URL);
    }

    public String getSignType() {
        return propertiesMap.get(SIGN_TYPE);
    }

    public String getCharset() {
        return propertiesMap.get(CHARSET);
    }

    public String getGatewayUrl() {
        return propertiesMap.get(GATEWAY_URL);
    }

    public String getLogPath() {
        return propertiesMap.get(LOG_PATH);
    }


}
