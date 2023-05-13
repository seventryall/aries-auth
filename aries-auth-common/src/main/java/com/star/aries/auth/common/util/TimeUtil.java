package com.star.aries.auth.common.util;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class TimeUtil {

    /**
     * 获取当前时间字符串
     *
     * @return
     */
    public static String getNowTimeWithString() {
        return getStringDate(Clock.systemUTC().millis());
    }


    /**
     * Long 转LocalDatetime
     *
     * @param time
     * @return
     */
    public static String getStringDate(Long time) {
        Date date = new Date(time);
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime.toString();
    }

    /**
     * LocalDatetime 转 Long
     *
     * @param stringTime
     * @return
     */
    public static long getLongDate(String stringTime) {
        return LocalDateTime.parse(stringTime).toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }

    /**
     * @param plusNum 增减年份
     * @return
     */
    public static LocalDateTime plusYear(Long plusNum) {
        return LocalDateTime.now().plusYears(plusNum);
    }

    /**
     * @param plusNum 增减月份
     * @return
     */
    public static LocalDateTime plusMonth(Long plusNum) {
        return LocalDateTime.now().plusMonths(plusNum);
    }

    /**
     * @param plusNum 增减天数
     * @return
     */
    public static LocalDateTime plusDay(Long plusNum) {
        return LocalDateTime.now().plusDays(plusNum);
    }

    /**
     * 获取某年某月某天0时0分0秒的时间戳
     */
    public static Long getTime(int year, int month, int day) {
        return LocalDateTime.now().withYear(year).withMonth(month).withDayOfMonth(day).withHour(0).withMinute(0).withSecond(0).withNano(0).toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }

    /**
     * 获取某年某月某天某时0分0秒的时间戳
     */
    public static Long getTime(int year, int month, int day, int hour) {
        return LocalDateTime.now().withYear(year).withMonth(month).withDayOfMonth(day).withHour(hour).withMinute(0).withSecond(0).withNano(0).toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }

    /**
     * 格式化时间
     *
     * @param time 时间戳
     * @return
     */
    public static String format(Long time) {
        Date date = new Date(time);
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public static String format2String(Long time, DateTimeFormatter dateTimeFormatter) {
        Date date = new Date(time);
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime().format(dateTimeFormatter);
    }

    public static String formatDate2String(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    public static Date formatString2Date(String time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取日期小时数
     *
     * @param date
     * @return
     */
    public static int getCurrHour(Date date) {
        if (date == null) {
            date = new Date();
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.getHourOfDay();
    }

    public static int getYear(Date date) {
        if (date == null) {
            date = new Date();
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.getYear();
    }

    public static int getMonth(Date date) {
        if (date == null) {
            date = new Date();
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.getMonthOfYear();
    }

    public static Date getStartOfDay(Date date) {
        if (date == null) {
            date = new Date();
        }
        DateTime dateTime = new DateTime(date).secondOfDay().withMinimumValue().millisOfDay().withMinimumValue();
        return dateTime.toDate();
    }

    public static Date getEndOfDay(Date date) {
        if (date == null) {
            date = new Date();
        }
        DateTime dateTime = new DateTime(date).secondOfDay().withMaximumValue().millisOfDay().withMaximumValue();
        return dateTime.toDate();
    }

    /**
     * 该日所在月第一天（日为null，取当月）
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfMonth(Date date) {
        if (date == null) {
            date = new Date();
        }
        DateTime dateTime = new DateTime(date).dayOfMonth().withMinimumValue().secondOfDay().withMinimumValue().millisOfDay().withMinimumValue();
        return dateTime.toDate();
    }

    /**
     * 该日所在月最后一天（日为null，取当月）
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfMonth(Date date) {
        if (date == null) {
            date = new Date();
        }
        DateTime dateTime = new DateTime(date).dayOfMonth().withMaximumValue().secondOfDay().withMaximumValue();
        return dateTime.toDate();
    }

    public static Date getFirstDayOfLastMonth(Date date) {
        if (date == null) {
            date = new Date();
        }
        DateTime dateTime = new DateTime(date).monthOfYear().withMaximumValue().dayOfMonth().withMinimumValue()
                .secondOfDay().withMinimumValue().millisOfDay().withMinimumValue();
        return dateTime.toDate();
    }


    /**
     * 获取该日所在年的第一天
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfYear(Date date) {
        if (date == null) {
            date = new Date();
        }
        DateTime dateTime = new DateTime(date).dayOfYear().withMinimumValue().secondOfDay().withMinimumValue().millisOfDay().withMinimumValue();
        return dateTime.toDate();
    }

    /**
     * 获取该日所在年的最后一天
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfYear(Date date) {
        if (date == null) {
            date = new Date();
        }
        DateTime dateTime = new DateTime(date).dayOfYear().withMaximumValue().secondOfDay().withMaximumValue();
        return dateTime.toDate();
    }
}
