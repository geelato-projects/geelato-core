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
    public static String join(String[] array, String separator) {
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
     * @param repeatTimes 重复的次数
     * @param joinValue   重复拼接的内容、值
     * @param separator   拼接的连接符
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
     *
     * @param text
     * @return
     */
    public static boolean isEmpty(String text) {
        return text == null || text.length() == 0;
    }

    /**
     * 字符串占位替换 "-{0}-{1}-{2}-"
     *
     * @param template 字符串模板
     * @param args     替换值
     * @return
     */
    public static String format(String template, Object... args) {
        if (isEmpty(template)) {
            return "";
        } else if (null != args && args.length != 0) {
            char[] templateChars = template.toCharArray();
            int templateLength = templateChars.length;
            int length = 0;
            int tokenCount = args.length;

            for (int i = 0; i < tokenCount; ++i) {
                Object sourceString = args[i];
                if (sourceString != null) {
                    length += sourceString.toString().length();
                }
            }

            StringBuilder buffer = new StringBuilder(length + templateLength);
            int lastStart = 0;

            for (int i = 0; i < templateLength; ++i) {
                char ch = templateChars[i];
                if (ch == '{' && i + 2 < templateLength && templateChars[i + 2] == '}') {
                    int tokenIndex = templateChars[i + 1] - 48;
                    if (tokenIndex >= 0 && tokenIndex < tokenCount) {
                        buffer.append(templateChars, lastStart, i - lastStart);
                        Object sourceString = args[tokenIndex];
                        if (sourceString != null) {
                            buffer.append(sourceString.toString());
                        }

                        i += 2;
                        lastStart = i + 1;
                    }
                }
            }

            buffer.append(templateChars, lastStart, templateLength - lastStart);
            return new String(buffer);
        } else {
            return template;
        }
    }
}
