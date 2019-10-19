 package utils.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;

import org.ho.yaml.Yaml;

/**
 * yml文件工具类
 * @author goukunlin
 * @date 2019/10/19
 */
public class YmlUtil {
     
     public static Properties getYml(String string) {
         @SuppressWarnings("unchecked")
         Map<String,Object> obj = (Map<String, Object>) Yaml.load(ClassLoader.getSystemResourceAsStream("application.yml"));
         Properties props = new Properties();
         loadProperties(props, obj, null);
         return props;
     }
     
     @SuppressWarnings("unchecked")
     private static void loadProperties(Properties props, Map<String, Object> obj, String[] keys) {
         if(keys == null) {
             keys = new String[] {};
         }
         for(Entry<String, Object> e : obj.entrySet()) {
             String key = e.getKey();
             Object value = e.getValue();
             if(Objects.isNull(value)) {
                 continue;
             }
             String[] tkeys = new String[keys.length+1];
             System.arraycopy(keys, 0, tkeys, 0, keys.length);
             tkeys[keys.length] = key;
             if(value instanceof Map) {
                 loadProperties(props, (Map<String, Object>) value, tkeys);
             }else{
                 props.setProperty(StringUtil.join(tkeys, "."), String.valueOf(value));
             }
         }
     }
}
