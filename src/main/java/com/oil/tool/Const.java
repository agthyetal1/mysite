package com.oil.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class Const {

    private static Logger log = LoggerFactory.getLogger(Const.class);

    private static Properties props = new Properties();

    // 配置文件路径
    private static String CONFIG_FILE = "conf.properties";

    /**
     * 静态执行一段代码，将配置文件读入到内存中
     */
    static{
        InputStreamReader is = null;
        try {
            is = new InputStreamReader(Const.class.getClassLoader().getResourceAsStream(CONFIG_FILE), "utf-8");
            props.load(is);
        } catch (IOException e) {
            log.error(e.getMessage(), e.fillInStackTrace());
        }  finally {
            if(is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e.fillInStackTrace());
                }
            }
        }
    }

    public final static Integer NO = 0;

    public final static Integer YES = 1;

    public final static Integer TOKEN_DEBUG = Integer.parseInt(props.getProperty("TOKEN_DEBUG"));

}
