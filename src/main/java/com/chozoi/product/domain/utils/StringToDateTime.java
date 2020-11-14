package com.chozoi.product.domain.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StringToDateTime {

    public static LocalDateTime main(String args) {
        LocalDateTime localDateTime = LocalDateTime.parse(args, DateTimeFormatter.ISO_DATE_TIME);
        return localDateTime;
    }
}
