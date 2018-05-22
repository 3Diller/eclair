package ua.com.threediller.eclair.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import ua.com.threediller.eclair.R;

/**
 * Created by David on 08.05.2018.
 */

public class DateUtils {
    public static String getRelativeData(Date date, boolean time) {
        String[] months = SharedData.getActivity().getResources().getStringArray(R.array.months);
        SimpleDateFormat format = new SimpleDateFormat("yyyy MM dd HH mm ss");
        String[] dateStrArr = format.format(date).split(" ");

        int year = Integer.parseInt(dateStrArr[0]);
        int month = Integer.parseInt(dateStrArr[1]);
        int day = Integer.parseInt(dateStrArr[2]);
        int hour = Integer.parseInt(dateStrArr[3]);
        String minute = dateStrArr[4];

        long taskAllDays = date.getTime() / 86400000;
        long allDays = new Date().getTime() / 86400000;
        long allYears = allDays / 365;

        String resultDate;

        if (taskAllDays == allDays) {
            resultDate = SharedData.getActivity().getResources().getString(R.string.today);
        } else if (taskAllDays + 1 == allDays) {
            resultDate = SharedData.getActivity().getResources().getString(R.string.yesterday);
        } else if (allYears == year - 1970){
            resultDate = day + " " + months[month-1];
        } else {
            resultDate = day + " " + months[month-1] + " " + year;
        }

        if (time) {
            resultDate += ", " + hour + ":" + minute;
        }

        return resultDate;
    }
}
