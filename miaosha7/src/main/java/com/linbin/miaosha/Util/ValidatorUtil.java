package com.linbin.miaosha.Util;

import org.codehaus.groovy.util.StringUtil;
import org.thymeleaf.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtil {
    private static final Pattern mobile_pattren = Pattern.compile("1\\d{10}");
    public static boolean isMobile(String src)
    {
        if(StringUtils.isEmpty(src))
            return false;
        Matcher  m = mobile_pattren.matcher(src);
        return ((Matcher) m).matches();
    }

    public  static void main(String[] args)
    {
        System.out.println(isMobile("15271826185"));
        System.out.println(isMobile("1527182618"));
    }
}
