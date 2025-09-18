package com.zhuo.test.jd;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 日报标题及周报文件名称生成
 * @author: zhuo
 * @date: 2025-09-16 18:40
 */
public class Test {
    public static void printThisWeekDates() {
        // 日期格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 今天
        LocalDate today = LocalDate.now();

        // 找到本周周一（ISO 标准：周一是第一天）
        LocalDate monday = today.with(DayOfWeek.MONDAY);

        // 输出本周一到周日
        for (int i = 0; i < 14; i++) {
            LocalDate day = monday.plusDays(i);
            System.out.println("## " + day.format(formatter));
            System.out.println();
            System.out.println();
//            System.out.println();
//            System.out.println();
        }
    }

    public static void main(String[] args) {
        List<String> monthWeeks = getMonthWeeks();
        monthWeeks.forEach(System.out::println);

        printThisWeekDates();
    }

    public static List<String> getMonthWeeks() {
        List<String> weeks = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        // 当前日期
        LocalDate today = LocalDate.now();

        // 本月第一天
        LocalDate firstDay = today.withDayOfMonth(1);
        // 本月最后一天
        LocalDate lastDay = today.withDayOfMonth(today.lengthOfMonth());

        // 找到第一周的周一（如果第一天不是周一，就往前推）
        LocalDate start = firstDay.with(DayOfWeek.MONDAY);
        // 找到最后一周的周日（如果最后一天不是周日，就往后推）
        LocalDate end = lastDay.with(DayOfWeek.SUNDAY);

        // 遍历每一周
        LocalDate weekStart = start;
        while (!weekStart.isAfter(end)) {
            LocalDate weekEnd = weekStart.plusDays(6);
            String range = weekStart.format(formatter) + "-" + weekEnd.format(formatter);
            weeks.add(range);
            weekStart = weekStart.plusWeeks(1);
        }

        return weeks;
    }
}
