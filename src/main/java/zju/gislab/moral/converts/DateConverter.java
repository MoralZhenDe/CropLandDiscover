package zju.gislab.moral.converts;

import java.util.Calendar;
import java.util.Date;

public class DateConverter {
    /***
     * 根据nass week推断具体日期
     */
    public static Date convertNassWeek2Date(int year, int week)
    {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setWeekDate(year,week, Calendar.SUNDAY);
        return cal.getTime();
    }

    public static int convertDate2NassWeek(int year, int month,int date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.YEAR,year);
        //why ????
        cal.set(Calendar.MONTH,month-1);
        cal.set(Calendar.DAY_OF_MONTH,date);
        return cal.get(Calendar.WEEK_OF_YEAR);
    }
}
