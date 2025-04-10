package com.bos.util;

import io.micrometer.common.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

public class DateUtil {

    public static Date strToDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException ex) {
            return null;
        }
    }

    public static boolean isDateAfterOrEqual(String from, String to) {
        if (StringUtils.isEmpty(from)) {
            return false;
        }

        if (StringUtils.isEmpty(to)) {
            return false;
        }

        return Objects.requireNonNull(strToDate(to)).compareTo(strToDate(from)) >= 0;
    }
}
