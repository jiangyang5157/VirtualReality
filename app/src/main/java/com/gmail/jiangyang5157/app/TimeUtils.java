package com.gmail.jiangyang5157.app;

/**
 * @author Yang
 * @since 7/16/2016
 */
public class TimeUtils {

    public static final long MILLI_IN_SECOND = 1000L;
    public static final long MICRO_IN_SECOND = MILLI_IN_SECOND * 1000L;
    public static final long NANO_IN_SECOND = MICRO_IN_SECOND * 1000L;

    public static final double MULTIPLICATOR_NANO_2_MICRO = 0.001;
    public static final double MULTIPLICATOR_NANO_2_MILLI = 0.000001;
    public static final double MULTIPLICATOR_NANO_2_SECOND = 0.000000001;

    public static final long MILLI_IN_MINUTE = 60 * MILLI_IN_SECOND;
    public static final long MILLI_IN_HOUR = 60 * MILLI_IN_MINUTE;
    public static final long MILLI_IN_DAY = 24 * MILLI_IN_HOUR;

    public static double nano2milli(long time) {
        return time * MULTIPLICATOR_NANO_2_MILLI;
    }
}