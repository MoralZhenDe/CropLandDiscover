package zju.gislab.moral.converts;

import java.util.Calendar;
import java.util.Date;

public class DateConverter {
    /***
     * 根据nass week推断具体日期
     * @param year
     * @param week
     * @return
     */
    public static Date convertNassWeek2Date(int year, int week)
    {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setWeekDate(year,week, Calendar.SUNDAY);
        return cal.getTime();
    }
}
