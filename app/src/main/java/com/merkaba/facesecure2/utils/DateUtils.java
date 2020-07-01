package com.merkaba.facesecure2.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    private String mTimeFormat = "dd-MM-yyyy HH:mm:ss.SSSSSS";
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;
    public static final String SEPARATOR = " ";

    public DateUtils(String separator) {
        this.calendar = Calendar.getInstance();
        mTimeFormat = "dd" + separator + "MM" + separator + "yyyy HH:mm:ss.SSSSSS";
        this.simpleDateFormat = new SimpleDateFormat(mTimeFormat);
    }

    public String getCurrentDate() {
        String formattedDate = simpleDateFormat.format(calendar.getTime());
        String[] separated = formattedDate.split(SEPARATOR);
        String createdAt = separated[0].trim(); // this will contain "Fruit"
        return createdAt;
    }

//    public String getCurrentDate(String separator) {
//        String time_format = "dd" + separator + "mm" + separator + "yyyy";
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(time_format);
//        String formattedDate = simpleDateFormat.format(calendar.getTime());
//        return formattedDate;
//    }

    public String getCurrentTime() {
        String formattedDate = simpleDateFormat.format(calendar.getTime());
        String[] separated = formattedDate.split(SEPARATOR);
        String createdOn = separated[1].trim();
        return createdOn;
    }

    public String epochToDateString(long epoch) {
        Date date = new Date(epoch);
        return simpleDateFormat.format(date);
    }

    public Date stringToDate(String dateString) {
        try {
            return simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public long stringToEpoch(String dateString) {
        return stringToDate(dateString).getTime();
    }

}
