package ua.com.threediller.eclair.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 19.05.2018.
 */

public class Tools {

    public static String concat(List<Long> items) {
        String result = "";
        for (int i = 0; i < items.size(); i++) {
            if (i != items.size() - 1)
                result += items.get(i) + ",";
            else
                result += items.get(i);
        }
        return result;
    }

    public static List<Long> stringArrayToLongList(String[] items) {
        List<Long> result = new ArrayList<>();
        for (String item: items) {
            result.add(Long.parseLong(item));
        }
        return result;
    }
}
