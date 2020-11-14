package com.chozoi.product.domain.utils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class HandlerList {
    public static List<Integer> intersect(List<Integer> first_list, List<Integer> second_list) {
        List<Integer> response = new ArrayList<Integer>();
        for (Integer i : first_list) {
            if (second_list.contains(i)) {
                response.add(i);
            }
        }
        return response;
    }

    public static List<Long> intersectLong(List<Long> first_list, List<Long> second_list) {
        List<Long> response = new ArrayList<Long>();
        for (Long i : first_list) {
            if (second_list.contains(i)) {
                response.add(i);
            }
        }
        return response;
    }

    public static <T> List<T> different(List<T> first_list, List<T> second_list) {
        List<T> response = new ArrayList<T>();
        for (T i : first_list) {
            if (!second_list.contains(i)) {
                response.add(i);
            }
        }
        return response;
    }

    public static <T> List<T> merge(List<T> fistList, List<T> secondList) {
        Set<T> set = new LinkedHashSet<>(fistList);
        set.addAll(secondList);
        List<T> combinedList = new ArrayList<>(set);
        return combinedList;
    }
}
