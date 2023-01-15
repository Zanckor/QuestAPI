package com.zanckor.questapi.utils;

import org.apache.commons.lang3.Range;

public class Maths {
    public static boolean numberBetween(double number, double min, double max) {
        Range<Double> range = Range.between(min, max);

        if (range.contains(number)) return true;

        return false;
    }
}
