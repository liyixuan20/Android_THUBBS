package com.example.bbs_frontend.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 日期相关功能
 */
public class DateUtil3 {

    private static ThreadLocal<DateFormat> sdfThreadLocal = new ThreadLocal<DateFormat>() {
        @Override
        public SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }
    };

    public static String formatDate(Date date) throws ParseException {
        return sdfThreadLocal.get().format(date);
    }

    public static String formatDatePlus8(Date date) throws ParseException {
        return sdfThreadLocal.get().format(new Date(date.getTime() + 8 * 60 * 60));
    }

    public static Date parse(String strDate) throws ParseException {

        return sdfThreadLocal.get().parse(strDate);
    }
}
