package com.lian.iprd.ratelimit.support;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lian.iprd.ratelimit.exception.RateLimitException;
import com.lian.iprd.ratelimit.model.RateClass;
import com.lian.iprd.ratelimit.model.RateMethod;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class JsonConfig {

    private final static String configFileName = "rateLimitConfig.json";

    private static Map<String, RateClass> rateClassMap = null;

    private static Logger logger = LoggerFactory.getLogger(JsonConfig.class);

//    static {
//        load();
//    }

    public static void load() {
        ClassPathResource resource = new ClassPathResource(configFileName);
        InputStream is = null;
        BufferedReader br = null;
        StringBuilder content = null;
        if (resource.exists()) {
            try {
                is = resource.getInputStream();
                br = new BufferedReader(new InputStreamReader(is, "utf-8"));
                String s = null;
                content = new StringBuilder();
                while ((s = br.readLine()) != null) {
                    content.append(s);
                }
            } catch (IOException e) {
                logger.error(configFileName + " 配置文件加载失败!", e);
            } finally {
                try {
                    br.close();
                    is.close();
                } catch (IOException e) {
                    logger.error(configFileName + " 流关闭异常!", e);
                }
            }
            jsonStrToMap(content.toString());
        } else {
            logger.error("Can not find " + configFileName);
        }
    }

    private static void jsonStrToMap(String jsonStr) {
        JSONObject obj = JSONObject.parseObject(jsonStr);
        JSONArray classArr = obj.getJSONArray("classList");
        if (null == classArr) {
            throw new RateLimitException(configFileName + " can not find classList");
        }
        Object[] classObjs = classArr.toArray();
        if(null == classObjs || 0 == classObjs.length)
            throw new RateLimitException(configFileName + " do not have classList");
        rateClassMap = new HashMap(classObjs.length);
        for (Object o : classObjs) {
            String className = ((JSONObject) o).getString("className");
            String classQPS = ((JSONObject) o).getString("classQPS");
            if (StringUtils.isBlank(className)) {
                throw new RateLimitException(configFileName + "中有className为空");
            }
            RateClass rc = rateClassMap.get(className);
            if (null == rc) {
                rc = new RateClass();
                if (StringUtils.isNotBlank(classQPS)) {
                    rc.setClassName(className);
                    rc.setQps(Double.parseDouble(classQPS));
                    System.out.println(className);
                }
                rateClassMap.put(className, rc);
            }
            JSONArray methodArr = ((JSONObject) o).getJSONArray("methodList");
            if (null != methodArr) {
                Map<String, RateMethod> methods = rc.getMethods();
                Object[] methodObjs = methodArr.toArray();
                for (Object method : methodObjs) {
                    String methodName = ((JSONObject) method).getString("methodName");
                    String methodQPS = ((JSONObject) method).getString("qps");
                    String timeout = ((JSONObject) method).getString("timeout");
                    String warmupPeriod = ((JSONObject) method).getString("warmupPeriod");
                    if (StringUtils.isBlank(methodName) || StringUtils.isBlank(methodQPS)) {
                        logger.error(configFileName + "配置文件中有methodName|qps为空");
                        continue;
                    }
                    RateMethod rm = new RateMethod();
                    rm.setMethodName(methodName);
                    rm.setQps(Double.parseDouble(methodQPS));
                    if (StringUtils.isNotBlank(timeout))
                        rm.setTimeout(Long.parseLong(timeout));
                    if(StringUtils.isNotBlank(warmupPeriod))
                        rm.setWarmupPeriod(Long.parseLong(warmupPeriod));
                    rc.getMethods().put(rm.getMethodName(), rm);
                }
            }
        }
    }


    public static Map<String, RateClass> loadRateClassMap() {
        load();
        return rateClassMap;
    }

    public static void main(String[] args) {
        System.out.println(rateClassMap.size());
    }

}