package net.seocraft.api.core.utils;

import org.ocpsoft.prettytime.PrettyTime;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class TimeUtils {

    private static final Map<String, ChronoUnit> units;

    static {
        units = new HashMap<>();
        units.put("s", ChronoUnit.SECONDS);
        units.put("S", ChronoUnit.SECONDS);
        units.put("m", ChronoUnit.MINUTES);
        units.put("h", ChronoUnit.HOURS);
        units.put("H", ChronoUnit.HOURS);
        units.put("d", ChronoUnit.DAYS);
        units.put("D", ChronoUnit.DAYS);
        units.put("w", ChronoUnit.WEEKS);
        units.put("W", ChronoUnit.WEEKS);
        units.put("M", ChronoUnit.MONTHS);
        units.put("y", ChronoUnit.YEARS);
        units.put("Y", ChronoUnit.YEARS);
    }

    public static long parseDuration(String stringDuration){
        long sum = 0;

        StringBuilder number = new StringBuilder();

        for (final char c : stringDuration.toCharArray()) {
            if(Character.isDigit(c)){
                number.append(c);
            } else {
                if(units.containsKey(c + "") && (number.length() > 0)) {
                    long parsedLong = Long.parseLong(number.toString());
                    ChronoUnit unit = units.get(c + "");
                    sum += unit.getDuration().multipliedBy(parsedLong).toMillis();
                    number = new StringBuilder();
                }
            }
        }

        return sum;
    }

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

    public static Integer getUnixStamp(ZonedDateTime date) {
        return getUnixStamp(Date.from(date.toInstant()));
    }

    public static Integer getUnixStamp(Date date) {
        return (int) (date.getTime()/1000);
    }

    public static Date parseUnixStamp(Integer stamp) {
        return new Date(stamp * 1000L);
    }

}
