package com.ihrdata.demo.common.utils;

import org.apache.commons.lang3.StringUtils;

public class TreeUtil {
    public static final String ROOT = "0";
    public static final String SEPARATOR = ".";

    public static String calcLevel(Long id, String level) {
        return StringUtils.isBlank(level) ? ROOT : StringUtils.join(level, SEPARATOR, id);
    }
}
