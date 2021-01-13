package com.sixtofly.chineseaddressanalyzer.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * 正则表达式校验
 * @author xie yuan bing
 * @date 2021-01-12 14:27
 */
public class RegExUtil {

    /**
     * 电话号码-正则表达式
     */
    private static final String PHONE_REGEX = "^0\\d{2}-\\d{8}|^0\\d{3}-\\d{7,8}|^1\\d{10}|^0\\d{10,11}|\\d{7,8}|^[48]00\\d{7}";

    /**
     * 身份证-正则表达式
     */
    private static final String ID_CARD_REGEX = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)";

    /**
     * 车牌-正则表达式
     */
    private static final String CAR_PLATE_REGEX = "^([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[a-zA-Z](([DF]((?![IO])[a-zA-Z0-9](?![IO]))[0-9]{4})|([0-9]{5}[DF]))|[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1})$";

    /**
     * 错误密码-正则表达式
     */
    private static final String CIPHER_REGEX = "(^([0-9])+$|^[a-zA-Z]+$|^[,\\.#%'\\+\\*\\-:;^_`]+$)";

    /**
     * 是否是电话号码
     */
    public static boolean isPhone(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }

        final Pattern phonePattern = Pattern.compile(PHONE_REGEX);
        return phonePattern.matcher(str).matches();
    }

    /**
     * 是否为身份证
     */
    public static boolean isIdCard(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }

        final Pattern idCardPattern = Pattern.compile(ID_CARD_REGEX);
        return idCardPattern.matcher(str).matches();
    }

    /**
     * 是否为车牌
     */
    public static boolean isCarPlate(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }

        final Pattern carPlatePattern = Pattern.compile(CAR_PLATE_REGEX);
        return carPlatePattern.matcher(str).matches();
    }

    /**
     * 是否为错误密码
     */
    public static boolean isNotCipher(String str) {
        if (StringUtils.isBlank(str)) {
            return true;
        }

        final Pattern idCardPattern = Pattern.compile(CIPHER_REGEX);
        return idCardPattern.matcher(str).find();
    }

    /**
     * 是否为整数
     */
    public static boolean isNumble(double obj) {
        // 精度范围
        double eps = 1e-10;
        return obj - Math.floor(obj) < eps;
    }
}
