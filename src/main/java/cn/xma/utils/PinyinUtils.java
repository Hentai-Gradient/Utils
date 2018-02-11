package cn.xma.utils;

import com.github.promeg.pinyinhelper.Pinyin;
import org.apache.commons.lang3.StringUtils;

public class PinyinUtils {

    public static String getPinyinFirst(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (Pinyin.isChinese(c)) {
                stringBuilder.append(Pinyin.toPinyin(c).substring(0, 1));
            } else {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString().toUpperCase();
    }

    public static String getPinyinFirst(String str, int maxLength) {
        String pinyinStr = getPinyinFirst(str);
        if (StringUtils.isEmpty(str)) {
            return null;
        } else {
            if (pinyinStr.length() > maxLength) {
                return pinyinStr.substring(0, maxLength);
            } else {
                return pinyinStr;
            }
        }
    }

    public static String getPinyin(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (Pinyin.isChinese(c)) {
                stringBuilder.append(Pinyin.toPinyin(c));
            } else {
                stringBuilder.append(c);
            }
        }

        return stringBuilder.toString().toUpperCase();
    }

    public static void main(String[] args) {
        String s = getPinyinFirst("abcdefghijabcdefghij1234567890");
        s = getPinyinFirst(s, 27);
        System.out.println(s);

        s = getPinyinFirst("1234567890");
        s = getPinyinFirst(s, 3);
        System.out.println(s);

        s = getPinyinFirst("");
        s = getPinyinFirst(s, 27);
        System.out.println(s);

        s = getPinyinFirst(null);
        s = getPinyinFirst(s, 27);
        System.out.println(s);


    }
}
