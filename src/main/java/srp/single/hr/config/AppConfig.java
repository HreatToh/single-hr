package srp.single.hr.config;


import cn.hutool.core.util.StrUtil;
import jdk.nashorn.internal.runtime.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class AppConfig {

    /**
     * 日志对象
     */
    private final static Logger logger = LogManager.getLogger(AppConfig.class);

    private static Properties properties = new Properties() ;

    private static final String propFileName = "application.properties";

    private static final String propInCludeFileName = "application-{}.properties";


    static {
        Properties props = new Properties();
        InputStream is = AppConfig.class.getClassLoader().getResourceAsStream(propFileName);
        try {
            props.load(new InputStreamReader(is, "UTF-8"));
            properties.putAll(props);

            /**  引入的配置文件*/
            String profile_active = props.getProperty("spring.profiles.active");
            String profile_include = props.getProperty("spring.profiles.include");

            if (StrUtil.isNotBlank(profile_active)){
                is = AppConfig.class.getClassLoader().getResourceAsStream(StrUtil.format(propInCludeFileName,profile_active));
                props.load(new InputStreamReader(is, "UTF-8"));
                properties.putAll(props);
            }

            if (StrUtil.isNotBlank(profile_include)){
                String[] split = profile_include.split(",");
                for (int i = 0; i < split.length ; i++) {
                    is = AppConfig.class.getClassLoader().getResourceAsStream(StrUtil.format(propInCludeFileName,split[i]));
                    props.load(new InputStreamReader(is, "UTF-8"));
                    properties.putAll(props);
                }
            }
        } catch (IOException e) {
            logger.error("读取日志文件["+propFileName+"]异常",e);
        }
    }

    /**
     * 获取配置文件值
     * @param key
     * @return
     */
    public static String get(String key){
        return properties.getProperty(key);
    }


    /**
     * 获取配置文件值  默认值
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getByDefault(String key,String defaultValue){
        String value = get(key);
        return StrUtil.isNotBlank(value)?value:defaultValue;
    }
}
