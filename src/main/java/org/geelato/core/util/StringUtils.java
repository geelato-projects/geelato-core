package org.geelato.core.util;

/**
 * 继承org.springframework.util.StringUtils,
 * 增加join
 *
 * @author geemeta
 */
public class StringUtils extends org.springframework.util.StringUtils {

    /***
     * @param separator 连接字符串
     * @param array     需要连接的集合
     * @return
     */
    public static String join(String array[], String separator) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0, len = array.length; i < len; i++) {
            if (i == (len - 1)) {
                sb.append(array[i]);
            } else {
                sb.append(array[i]).append(separator);
            }
        }
        return sb.toString();
    }

    /**
     * @param repeatTimes    重复的次数
     * @param joinValue 重复拼接的内容、值
     * @param separator 拼接的连接符
     * @return
     */
    public static String join(int repeatTimes, String joinValue, String separator) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0, len = repeatTimes; i < len; i++) {
            if (i == (len - 1)) {
                sb.append(joinValue);
            } else {
                sb.append(joinValue).append(separator);
            }
        }
        return sb.toString();
    }

    /**
     * 校验空字符串和null，若为空字符串或null则返回true
     * @param text
     * @return
     */
    public static boolean isEmpty(String text) {
        return text == null || text.length() == 0;
    }
}
