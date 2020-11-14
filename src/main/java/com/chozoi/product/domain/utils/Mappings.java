package com.chozoi.product.domain.utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Mappings {
    public static LocalDateTime toLocalDateTime(String str) {
        String str_1 = str.replace(" ", "");
        String str_2 = str_1.replace("[", "");
        String str_3 = str_2.replace("]", "");
        String[] output = str_3.split(",");
        List<Integer> list = new ArrayList<>();
        // Iterate through the array
        for (String t : output) {
            // Add each element into the list
            try {
                list.add(Integer.parseInt(t));
            } catch (Exception e) {

            }
        }
        if (list.size() > 6) {
            return LocalDateTime.of(list.get(0), list.get(1), list.get(2), list.get(3), list.get(4), list.get(5));
        }
        return null;
    }

}
