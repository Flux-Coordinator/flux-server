package utils;

import java.util.Date;

public class DateHelper {
    public static boolean dateEquals(final Date d1, final Date d2) {
        if(d1 == null && d2 == null) {
            return true;
        } else if(d1 == null || d2 == null) {
            return false;
        }
        return d1.compareTo(d2) == 0;
    }
}
