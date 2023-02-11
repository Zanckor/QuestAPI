package dev.zanckor.mod.util;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.apache.commons.lang3.Range;

public class Mathematic {
    public static boolean numberBetween(double number, double min, double max) {
        Range<Double> range = Range.between(min, max);

        if (range.contains(number)) return true;

        return false;
    }

    public static int numberRandomizerBetween(int min, int max) {
        int randomizer = (int) Mth.randomBetween(RandomSource.createNewThreadLocalInstance(), min, max);
        return randomizer;
    }
}