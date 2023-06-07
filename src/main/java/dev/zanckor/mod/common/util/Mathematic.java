package dev.zanckor.mod.common.util;

import com.mojang.math.Matrix4f;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.Range;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class Mathematic {
    public static boolean numberBetween(double number, double min, double max) {
        Range<Double> range = Range.between(min, max);

        return range.contains(number);
    }

    public static boolean vec3NumberBetween(Vec3 vec3, Vec3 vec32, double min, double max) {
        Range<Double> rangeX = Range.between(vec32.x + min, vec32.x + max);
        Range<Double> rangeY = Range.between(vec32.y + min, vec32.y + max);
        Range<Double> rangeZ = Range.between(vec32.z + min, vec32.z + max);

        return rangeX.contains(vec3.x) && rangeY.contains(vec3.y) && rangeZ.contains(vec3.z);
    }

    public static int numberRandomizerBetween(int min, int max) {
        int randomizer = (int) Mth.randomBetween(RandomSource.createNewThreadLocalInstance(), min, max);
        return randomizer;
    }
}