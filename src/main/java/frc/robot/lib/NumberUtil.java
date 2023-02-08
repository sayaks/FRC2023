package frc.robot.lib;

public class NumberUtil {
    public static int unsign(final byte num) {
        if (num < 0) {
            return (1 << Byte.SIZE) + num;
        } else {
            return num;
        }
    }

    public static int unsign(final short num) {
        if (num < 0) {
            return (1 << Short.SIZE) + num;
        } else {
            return num;
        }
    }

    public static long unsign(final int num) {
        if (num < 0) {
            return (1 << Integer.SIZE) + num;
        } else {
            return num;
        }
    }

    public static double ticksToDegs(double ticks) {
        return ticks * (360.0 / 1024.0) * 1000000;
    }
}
