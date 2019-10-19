 package utils;

import java.util.Properties;

import utils.util.YmlUtil;

/**
 * utils常量
 * @author goukunlin
 * @date 2019/10/19
 */
public interface Constant {
     
     Properties APPLICATION_YML = YmlUtil.getYml("application.yml");
    
}
