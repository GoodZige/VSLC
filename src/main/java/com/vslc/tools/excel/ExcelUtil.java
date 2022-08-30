package com.vslc.tools.excel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class ExcelUtil {

    private static SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat timeSdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public static String getValue(String value) {
        if (value.equals("")) return null;
        else return value;
    }

    public static Integer toInteger(String value) {
        if (value.equals("")) return null;
        else if (isInteger(value)) return Integer.valueOf(value);
        else return null;
    }

    public static Float toFloat(String value) {
        if (value.equals("")) return null;
        else return Float.valueOf(value);
    }

    public static Date parseDate(String date) {
        Date result = null;
        try {
            result = dateSdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Date parseTime(String time) {
        Date result = null;
        try {
            result = timeSdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
}
