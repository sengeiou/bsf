package com.weking.core;

import com.wekingframework.core.LibSysUtils;
import jdk.nashorn.internal.parser.Token;
import org.springframework.util.DigestUtils;
import wk.rtc.comm.WkUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtils {
    public static SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");

    public static final SimpleDateFormat datetimeFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat yyyymmddhhmmss = new SimpleDateFormat(
            "yyyyMMddHHmmss");
    //获取当天的开始时间
    public static Date getDayBegin() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    //获取当天的结束时间
    public static Date getDayEnd() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    //获取昨天的开始时间
    public static Date getBeginDayOfYesterday() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getDayBegin());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }

    //获取昨天的结束时间
    public static Date getEndDayOfYesterDay() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getDayEnd());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }

    //获取明天的开始时间
    public static Date getBeginDayOfTomorrow() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getDayBegin());
        cal.add(Calendar.DAY_OF_MONTH, 1);

        return cal.getTime();
    }

    //获取明天的结束时间
    public static Date getEndDayOfTomorrow() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getDayEnd());
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    //获取本周的开始时间
    public static Date getBeginDayOfWeek() {
        Date date = new Date();
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayofweek == 1) {
            dayofweek += 7;
        }
        cal.add(Calendar.DATE, 2 - dayofweek);
        return getDayStartTime(cal.getTime());
    }

    //获取本周的结束时间
    public static Date getEndDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getBeginDayOfWeek());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        Date weekEndSta = cal.getTime();
        return getDayEndTime(weekEndSta);
    }

    //获取本月的开始时间
    public static Date getBeginDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 1, 1);
        return getDayStartTime(calendar.getTime());
    }

    //获取本月的结束时间
    public static Date getEndDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 1, 1);
        int day = calendar.getActualMaximum(5);
        calendar.set(getNowYear(), getNowMonth() - 1, day);
        return getDayEndTime(calendar.getTime());
    }

    //获取本年的开始时间
    public static Date getBeginDayOfYear() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, getNowYear());
        // cal.set
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DATE, 1);

        return getDayStartTime(cal.getTime());
    }

    //获取本年的结束时间
    public static Date getEndDayOfYear() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, getNowYear());
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DATE, 31);
        return getDayEndTime(cal.getTime());
    }

    //获取某个日期的开始时间
    public static Timestamp getDayStartTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d) calendar.setTime(d);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }

    //获取某个日期的结束时间
    public static Timestamp getDayEndTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d) calendar.setTime(d);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return new Timestamp(calendar.getTimeInMillis());
    }

    //获取今年是哪一年
    public static Integer getNowYear() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return Integer.valueOf(gc.get(1));
    }

    //获取本月是哪一月
    public static int getNowMonth() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return gc.get(2) + 1;
    }

    //两个日期相减得到的天数
    public static int getDiffDays(long beginDay, long endDay) {
        DateFormat format1 = new SimpleDateFormat("yyyyMMdd");
        try {
            Date beginDate = format1.parse(LibSysUtils.toString(beginDay));
            Date endDate = format1.parse(LibSysUtils.toString(endDay));
            return getDiffDays(beginDate,endDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }

    }

    //两个日期相减得到的天数
    public static int getDiffDays(Date beginDate, Date endDate) {

        if (beginDate == null || endDate == null) {
            throw new IllegalArgumentException("getDiffDays param is null!");
        }

        long diff = (endDate.getTime() - beginDate.getTime())
                / (1000 * 60 * 60 * 24);

        int days = new Long(diff).intValue();

        return days;
    }

    //两个日期相减得到的毫秒数
    public static long dateDiff(Date beginDate, Date endDate) {
        long date1ms = beginDate.getTime();
        long date2ms = endDate.getTime();
        return date2ms - date1ms;
    }

    //获取两个日期中的最大日期
    public static Date max(Date beginDate, Date endDate) {
        if (beginDate == null) {
            return endDate;
        }
        if (endDate == null) {
            return beginDate;
        }
        if (beginDate.after(endDate)) {
            return beginDate;
        }
        return endDate;
    }

    //获取两个日期中的最小日期
    public static Date min(Date beginDate, Date endDate) {
        if (beginDate == null) {
            return endDate;
        }
        if (endDate == null) {
            return beginDate;
        }
        if (beginDate.after(endDate)) {
            return endDate;
        }
        return beginDate;
    }

    //返回某月该季度的第一个月
    public static Date getFirstSeasonDate(Date date) {
        final int[] SEASON = {1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int sean = SEASON[cal.get(Calendar.MONTH)];
        cal.set(Calendar.MONTH, sean * 3 - 3);
        return cal.getTime();
    }

    //返回某个日期下几天的日期
    public static Date getNextDay(Date date, int i) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) + i);
        return cal.getTime();
    }

    //返回某个日期前几天的日期
    public static Date getFrontDay(Date date, int i) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) - i);
        return cal.getTime();
    }

    //返回某个日期前几天的日期
    public static long getFrontDay(long date, int i) {
        DateFormat format1 = new SimpleDateFormat("yyyyMMdd");
        Date beginDate = null;
        try {
            beginDate = format1.parse(LibSysUtils.toString(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = new GregorianCalendar();
        cal.setTime(beginDate);
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) - i);
        long result = LibSysUtils.toLong(format1.format(cal.getTime()));
        return result;
    }

    //获取某年某月到某年某月按天的切片日期集合（间隔天数的日期集合）
    public static List getTimeList(int beginYear, int beginMonth, int endYear,
                                   int endMonth, int k) {
        List list = new ArrayList();
        if (beginYear == endYear) {
            for (int j = beginMonth; j <= endMonth; j++) {
                list.add(getTimeList(beginYear, j, k));

            }
        } else {
            {
                for (int j = beginMonth; j < 12; j++) {
                    list.add(getTimeList(beginYear, j, k));
                }

                for (int i = beginYear + 1; i < endYear; i++) {
                    for (int j = 0; j < 12; j++) {
                        list.add(getTimeList(i, j, k));
                    }
                }
                for (int j = 0; j <= endMonth; j++) {
                    list.add(getTimeList(endYear, j, k));
                }
            }
        }
        return list;
    }

    //获取某年某月按天切片日期集合（某个月间隔多少天的日期集合）
    public static List getTimeList(int beginYear, int beginMonth, int k) {
        List list = new ArrayList();
        Calendar begincal = new GregorianCalendar(beginYear, beginMonth, 1);
        int max = begincal.getActualMaximum(Calendar.DATE);
        for (int i = 1; i < max; i = i + k) {
            list.add(begincal.getTime());
            begincal.add(Calendar.DATE, k);
        }
        begincal = new GregorianCalendar(beginYear, beginMonth, max);
        list.add(begincal.getTime());
        return list;
    }

    //获取某年某月的第一天日期
    public static Date getStartMonthDate(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        return calendar.getTime();
    }

    //获取某年某月的最后一天日期
    public static Date getEndMonthDate(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        int day = calendar.getActualMaximum(5);
        calendar.set(year, month - 1, day);
        return calendar.getTime();
    }

    //获取昨天
    public static long getYesterday() {
        Date date = new Date();//取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(GregorianCalendar.DATE, -1);//把日期往前减少一天，若想把日期向后推一天则将负数改为正数
        date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd000000");
        String dateStr = formatter.format(date);
        return LibSysUtils.toLong(dateStr);
    }

    //获得当前时间点离当前天结束剩余的秒数
    public static Integer getRemainSecondsOneDay(Date currentDate) {
        Calendar midnight=Calendar.getInstance();
        midnight.setTime(currentDate);
        midnight.add(midnight.DAY_OF_MONTH,1);
        midnight.set(midnight.HOUR_OF_DAY,0);
        midnight.set(midnight.MINUTE,0);
        midnight.set(midnight.SECOND,0);
        midnight.set(midnight.MILLISECOND,0);
        Integer seconds=(int)((midnight.getTime().getTime()-currentDate.getTime())/1000);
        return seconds;
    }

    //获取当前时间
    public static long getLongNowtime(){

        Date date=new Date();
        DateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
        String time=format.format(date);
        long  result  = Long.parseLong(time);
        return result;
    }

    // 获取本周一时间
    public static long getMondayOfThisWeek(String format) {
        Calendar c = Calendar.getInstance();
        int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0)
            day_of_week = 7;
        c.add(Calendar.DATE, -day_of_week + 1);
        Date m = c.getTime();
        DateFormat dateFormat = new SimpleDateFormat(format);
        long time = Long.parseLong(dateFormat.format(m));
        return time;
    }

    /**
     * 当前日期的前N天
     *
     * @param day N天
     * @return
     */
    public static String BeforeNowByDay(int day) {
        String result = "";
        try {
            Calendar ca = Calendar.getInstance();
            ca.add(Calendar.DAY_OF_MONTH, -day);
            result = yyyyMMdd.format(ca.getTime());
        } catch (Exception e) {
        }
        return result;
    }

    public static String longTimeToString(Long time)  {
        String date = String.valueOf(time);
        Date date1 = null;
        try {
            date1 = yyyymmddhhmmss.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String re = datetimeFormat.format(date1);
        return re;
    }

    public static void main(String[] args) {

/*
        String s = WkUtils.decryptString("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");
*/

       String overSign="1234551234555833901811001appsmegame";
        String s = DigestUtils.md5DigestAsHex(overSign.getBytes());
        System.out.println(s);

        String token="583390181123455appsmegame";
        token= DigestUtils.md5DigestAsHex(token.getBytes());
        System.out.println(token);



    }


}
