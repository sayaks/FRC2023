package frc.robot.lib;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class NumberUtilTest {
    @Test
    void ticksToDegsZero() {
        double ticks = 0;
        assertEquals(0, NumberUtil.ticksToDegs(ticks), 0.001);
    }

    @Test
    void ticksToDegsTest() {
        assertEquals(180, NumberUtil.ticksToDegs(512.0 / 1_000_000.0), 0.001);
    }
}
