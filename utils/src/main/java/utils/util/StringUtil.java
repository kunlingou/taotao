package utils.util;

import java.util.Objects;

/**
 * string字符串工具类
 * @author goukunlin
 * @date 2019/10/19
 */
public class StringUtil {
	
	public static final String EMPTY = "";
	
	public static String join(Object[] array, String separator) {
		return join(array, separator, 0, array.length);
	}
	
	public static String join(final Object[] array, String separator, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = EMPTY;
        }

        final int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return EMPTY;
        }

        final StringBuilder buf = new StringBuilder(noOfItems * 16);

        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }
	
	public static boolean isEmpty(String obj) {
		return obj == null || "".equals(obj);
	}
	
	public static String bytesToHexString(byte[] src) {
	    int len = 0;
	    if(Objects.isNull(src) || (len = src.length)==0) {
	        return StringUtil.EMPTY;
	    }
	    StringBuilder sb = new StringBuilder();
	    for(int i=0;i<len;i++) {
	        int v = src[i] & 0xFF;
	        String hv = Integer.toHexString(v).toUpperCase();
	        if(hv.length()<2) {
	            sb.append("0");
	        }
	        sb.append(hv);
	    }
	    return sb.toString();
	}
}
