package cn.xma.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xma
 */
public class StringUtil {
    private static final char[] HEX_LOOKUP_STRING = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String bytesToHexStr(byte[] bcd) {
        StringBuilder s = new StringBuilder(bcd.length * 2);
        for (byte aBcd : bcd) {
            s.append(HEX_LOOKUP_STRING[(aBcd >>> 4) & 0x0f]);
            s.append(HEX_LOOKUP_STRING[aBcd & 0x0f]);
        }

        return s.toString();
    }

    public static byte[] hexStrToBytes(String s) {
        byte[] bytes = new byte[s.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2), 16);
        }

        return bytes;
    }

    public static Boolean isBarcode(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        // 验证条形码
        p = Pattern.compile("[A-Za-z0-9]+");
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    public static String validateStringAllowBlank(String str, boolean allowBlank) {
        if (str != null) {
            if (!allowBlank) {
                Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                Matcher m = p.matcher(str);
                str = m.replaceAll("");
            }
            String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
            Pattern p1 = Pattern.compile(regEx);
            Matcher m1 = p1.matcher(str);
            str = m1.replaceAll("").trim();
        }
        return str;
    }

    public static String validateString(String str) {
        return validateString(str, true);
    }

    public static String validateString(String str, Boolean all) {
        if (StringUtils.isNotEmpty(str)) {
            Pattern p = Pattern.compile("\\s|\t|\r|\n");
            Matcher m = p.matcher(str);
            str = m.replaceAll("");

            if (all) {
                String regEx = "[`~!@#$%^&*()+=|{}':;,\\[\\].<>/?~！@#￥%……&*（）——+|{}\\\\【】‘；：”“’。，、？\'\"]";
                Pattern p1 = Pattern.compile(regEx);
                Matcher m1 = p1.matcher(str);
                return m1.replaceAll("").trim();
            } else {
                return str.trim();
            }
        }

        return null;
    }

    public static String noMoreThan(String str, Integer maxLength) {
        if (str == null) {
            return null;
        }

        if (str.length() > maxLength) {
            return str.substring(0, maxLength);
        }

        return str;
    }

    /**
     * 是否该字段里只有中英文和数字
     *
     * @param str 需要校验字段
     * @return 返回true/false
     */
    public static boolean isLetterDigitChinese(String str) {
        for (char c : str.toCharArray()) {
            // 如果字符上中文返回true
            if (!isLetterDigitChinese(c)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isLetterDigitChinese(char c) {
        // 中文
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }

        // 字母
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
            return true;
        }

        // 数字
        return Character.isDigit(c);
    }

    /**
     * 使用给定的 charset 将此 String 编码到 byte 序列，并将结果存储到新的 byte 数组。
     *
     * @param content 字符串对象
     * @param charset 编码方式
     * @return 所得 byte 数组
     */
    public static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }

        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalArgumentException("Not support:" + charset, ex);
        }
    }

    public static boolean fuzzyContains(String s, Set<String> set) {
        for (String element : set) {
            if (element.contains(s)) {
                return true;
            }
        }
        return false;
    }
}
