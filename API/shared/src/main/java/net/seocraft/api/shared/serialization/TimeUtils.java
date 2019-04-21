package net.seocraft.api.shared.serialization;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    public static Date addMinutes(Date date, Integer minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    public static String formatAgoTime(Date date, String locale) {
        return StringUtils.capitalizeString(new PrettyTime(new Locale(locale)).format(date));
    }

    public static String formatAgoTimeInt(Integer stamp, String locale) {
        return StringUtils.capitalizeString(new PrettyTime(new Locale(locale)).format(new Date(stamp * 1000L)));
    }

    public static Integer getUnixStamp(Date date) {
        return (int) (date.getTime()/1000);
    }

    public static Date parseUnixStamp(Integer stamp) {
        return new Date(stamp * 1000L);
    }

}
