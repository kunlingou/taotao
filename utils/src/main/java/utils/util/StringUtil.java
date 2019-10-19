package utils.util;

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
}
