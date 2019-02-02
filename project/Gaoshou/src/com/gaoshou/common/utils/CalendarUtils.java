package com.gaoshou.common.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.gaoshou.common.constant.CommonConstant;

public class CalendarUtils {

    public static int getDistanceInMonthsWithToday(Date refDate) {
        int distanceInMonths = 0;

        if (null != refDate) {
            distanceInMonths = getDistanceInMonths(refDate, new Date());
        } // if (null != refDate)

        return distanceInMonths;
    }

    public static int getDistanceInMonths(Date refDate, Date targetDate) {
        int distanceInMonths = 0;
        if (null != refDate && null != targetDate) {
            Calendar target = Calendar.getInstance();
            target.setTime(targetDate);
            int targetYear = target.get(Calendar.YEAR);
            int targetMonth = target.get(Calendar.MONTH);
            int targetMonthCount = targetYear * 12 + (targetMonth + 1);

            Calendar ref = Calendar.getInstance();
            ref.setTime(refDate);
            int refYear = ref.get(Calendar.YEAR);
            int refMonth = ref.get(Calendar.MONTH);
            int refMonthCount = refYear * 12 + (refMonth + 1);

            distanceInMonths = refMonthCount - targetMonthCount;
        }
        return distanceInMonths;
    }

    public static int getDistanceInWeeksWithToday(Date refDate) {
        int distanceInWeeks = 0;

        if (null != refDate) {
            distanceInWeeks = getDistanceInWeeks(refDate, new Date());
        } // if (null != refDate)

        return distanceInWeeks;
    }

    public static int getDistanceInWeeks(Date refDate, Date targetDate) {
        int distanceInWeeks = 0;
        if (null != refDate && null != targetDate) {
            Calendar target = Calendar.getInstance();
            target.setTime(targetDate);
            int targetYear = target.get(Calendar.YEAR);
            int targetWeek = target.get(Calendar.WEEK_OF_YEAR);
            int targetWeekCount = targetYear * 52 + (targetWeek + 1);

            Calendar ref = Calendar.getInstance();
            ref.setTime(refDate);
            int refYear = ref.get(Calendar.YEAR);
            int refWeek = ref.get(Calendar.WEEK_OF_YEAR);
            int refWeekCount = refYear * 52 + (refWeek + 1);

            distanceInWeeks = refWeekCount - targetWeekCount;
        }
        return distanceInWeeks;
    }

    public static int getDistanceInDaysWithFirstDateOfWeekCalendar(Date refDate) {
        int distanceInDays = 0;
        if (null != refDate) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(refDate);
            distanceInDays = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        }
        return distanceInDays;
    }

    public static int getDistanceInDaysWithFirstDateOfMonthCalendar(Date refDate) {
        int distanceInDays = 0;
        if (null != refDate) {
            distanceInDays = getDistanceInDays(refDate, getFirstDateOfMonthCalendar(refDate));
        }
        return distanceInDays;
    }

    public static int getDistanceInDaysWithToday(Date refDate) {
        int distanceInDays = 0;

        if (null != refDate) {
            distanceInDays = getDistanceInDays(refDate, new Date());
        } // if (null != refDate)

        return distanceInDays;
    }

    public static int getDistanceInDays(Date refDate, Date targetDate) {
        int distanceInDays = 0;
        if (null != refDate && null != targetDate) {
            distanceInDays = (int) ((refDate.getTime() - refDate.getTimezoneOffset() * 60000) / 86400000 - (targetDate.getTime() - targetDate.getTimezoneOffset() * 60000) / 86400000);
        }
        return distanceInDays;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getPastTimeDistance(Date nowDate, Date targetDate) {
        String timeDistance = null;
        SimpleDateFormat day = new SimpleDateFormat("yyyyMMdd");

        if (null != nowDate && targetDate != null) {
            String nowDateStr = day.format(nowDate);
            String targetDateStr = day.format(targetDate);

            if (nowDateStr.equals(targetDateStr)) {
                long longTimeDistance = nowDate.getTime() - targetDate.getTime();
                if (longTimeDistance < 1000 * 60) {
                    if (longTimeDistance / 1000 < 10) {
                        timeDistance = "刚刚";
                    } else {
                        timeDistance = longTimeDistance / 1000 + "秒前";
                    }
                } else if (longTimeDistance < 1000 * 60 * 60) {
                    longTimeDistance = longTimeDistance / 1000 / 60;
                    timeDistance = longTimeDistance + "分钟前";
                } else if (longTimeDistance < 1000 * 60 * 60 * 24) {
                    longTimeDistance = longTimeDistance / 60 / 60 / 1000;
                    timeDistance = longTimeDistance + "小时前";
                }
            } else if ((Integer.parseInt(nowDateStr) - Integer.parseInt(targetDateStr)) < 365) {
                int nowDateInt = Integer.parseInt(nowDateStr);
                int targetDateInt = Integer.parseInt(targetDateStr);
                int dayDistance = nowDateInt - targetDateInt;
                if (1 == dayDistance) {
                    timeDistance = "昨天";
                } else if (2 == dayDistance) {
                    timeDistance = "前天";
                } else if (2 < dayDistance && dayDistance < 7) {
                    timeDistance = getDistanceInDays(nowDate, targetDate) + "天前";
                } else if (7 <= dayDistance && dayDistance < 31) {
                    timeDistance = getDistanceInWeeks(nowDate, targetDate) + "周前";
                } else if (dayDistance > 31) {
                    timeDistance = getDistanceInMonths(nowDate, targetDate) + "个月前";
                }
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.d");
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
                timeDistance = dateFormat.format(targetDate);
            }
        }

        return timeDistance;
    }

    public static Date getSameDateOfMonth(Date refDate, int startMonth, int offsetMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(refDate);
        calendar.add(Calendar.MONTH, offsetMonth - startMonth);

        return calendar.getTime();
    }

    public static Date getSameDateOfYear(Date refDate, int startDay, int offsetDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(refDate);
        calendar.add(Calendar.DAY_OF_YEAR, offsetDay - startDay);

        return calendar.getTime();
    }

    public static Date getSameDateOfWeek(Date refDate, int startWeek, int offsetWeek) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(refDate);
        calendar.add(Calendar.WEEK_OF_YEAR, offsetWeek - startWeek);

        return calendar.getTime();
    }

    public static Date getFirstDateOfMonth(Date refDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(refDate);

        int firstDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH) - 1;
        calendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth);

        return calendar.getTime();
    }

    public static Date getLastDateOfMonth(Date refDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(refDate);
        calendar.add(Calendar.MONTH, 1);

        int firstDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH) - 1;
        calendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth);
        calendar.add(Calendar.DAY_OF_YEAR, -1);

        return calendar.getTime();
    }

    public static Date getFirstDateOfMonthCalendar(Date refDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getFirstDateOfMonth(refDate));

        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.add(Calendar.DAY_OF_WEEK, -firstDayOfWeek);
        if (0 == firstDayOfWeek) {
            calendar.add(Calendar.DAY_OF_WEEK, -7);
        }

        return calendar.getTime();
    }

    public static Date getFirstDateOfWeek(Date refDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(refDate);

        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.add(Calendar.DAY_OF_WEEK, -firstDayOfWeek);

        return calendar.getTime();
    }

    public static Date strToDate(String str) {
        Date date = null;
        if (!TextUtils.isEmpty(str)) {
            try {
                date = CommonConstant.serverTimeFormat.parse(str);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return date;
    }
    
}
