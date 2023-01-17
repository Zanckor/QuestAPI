package com.zanckor.mod.util;

import org.apache.commons.lang3.Range;

public class Mathematic {
    public static boolean numberBetween(double number, double min, double max) {
        Range<Double> range = Range.between(min, max);

        if (range.contains(number)) return true;

        return false;
    }
}
