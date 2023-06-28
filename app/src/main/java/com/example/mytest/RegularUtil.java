package com.example.mytest;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式工具类
 */
public class RegularUtil {

    /**
     * 判断输入字符串和模式是否匹配
     *
     * @param param
     * 		待检验字符串
     * @param pattern
     * 		正则模式
     *
     * @return 是否匹配
     */
    public static boolean isMatch(String param, String pattern) {
        return param != null && pattern != null && Pattern.compile(pattern).matcher(param).matches();
    }

    /**
     * 判断输入字符串是否是纯数字
     *
     * @param param
     * 		待检验字符串
     *
     * @return 是否是纯数字
     */
    public static boolean isNumber(String param) {

        return param != null && Pattern.compile("[0-9]+").matcher(param).matches();
    }

    public static boolean isPhone(String param) {

        return param != null && Pattern.compile("^[1][0-9]{10}$").matcher(param).matches();
    }

    /**
     * 分离以大写字母开头的单词
     *
     * @param param
     * 		待分离的字符串
     *
     * @return 分离出的单词组
     */
    public static List<String> splitWithUpcase(String param) {
        List<String> array = new ArrayList<>();
        if (param == null || param.isEmpty()) {
            return array;
        }
        Pattern pattern = Pattern.compile("[A-Z]{1}[a-z0-9]*");
        Matcher matcher = pattern.matcher(param);
        while (matcher.find()) {
            array.add(matcher.group());
        }
        return array;
    }

    public static boolean isContainEmoji(String account) {
        int len = account.length();
        boolean isEmoji = false;
        for (int i = 0; i < len; i++) {
            char hs = account.charAt(i);
            if (0xd800 <= hs && hs <= 0xdbff) {
                if (account.length() > 1) {
                    char ls = account.charAt(i+1);
                    int uc = ((hs - 0xd800) * 0x400) + (ls - 0xdc00) + 0x10000;
                    if (0x1d000 <= uc && uc <= 0x1f77f) {
                        return true;
                    }
                }
            } else {
                // non surrogate
                if (0x2100 <= hs && hs <= 0x27ff && hs != 0x263b) {
                    return true;
                } else if (0x2B05 <= hs && hs <= 0x2b07) {
                    return true;
                } else if (0x2934 <= hs && hs <= 0x2935) {
                    return true;
                } else if (0x3297 <= hs && hs <= 0x3299) {
                    return true;
                } else if (hs == 0xa9 || hs == 0xae || hs == 0x303d || hs == 0x3030 || hs == 0x2b55 || hs == 0x2b1c || hs == 0x2b1b || hs == 0x2b50|| hs == 0x231a ) {
                    return true;
                }
                if (!isEmoji && account.length() > 1 && i < account.length() -1) {
                    char ls = account.charAt(i+1);
                    if (ls == 0x20e3) {
                        return true;
                    }
                }
            }
        }
        return  isEmoji;
    }

    public static boolean isContainSpecialChar(String account) {
        String regExMail = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        String regExCustom = "[\\w]*";
        Pattern p1 = Pattern.compile(regExMail);
        Pattern p2 = Pattern.compile(regExCustom);
        Matcher m1 = p1.matcher(account);
        Matcher m2 = p2.matcher(account);
        return !(m1.matches() || m2.matches());
    }

    /**
     * 检查字符串中的每个字符是否为空格，如果字符串中至少有一个非空格字符，则返回false。如果字符串为空或只包含空格，则返回true
     */
    public static boolean isOnlyWhitespace(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
