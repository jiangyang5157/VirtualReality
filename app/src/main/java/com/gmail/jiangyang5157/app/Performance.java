package com.gmail.jiangyang5157.app;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author Yang
 * @since 7/16/2016
 */
public class Performance {
    public static final String TAG = "[Performance]";

    private ArrayList<Long> breakpoints;

    private static Performance instance;

    public static synchronized Performance getInstance() {
        if (instance == null) {
            instance = new Performance();
        }
        return instance;
    }

    private Performance() {
        breakpoints = new ArrayList<>();
    }

    public void addBreakpoint() {
        breakpoints.add(System.nanoTime());
    }

    private void printEvaluation(double nanoMultiplicator) {
        int size = breakpoints.size();
        for (int prev = 0, i = prev + 1; i < size; prev++, i++) {
            System.out.println(TAG + " dt(" + i + "-" + prev + ") = " + ((breakpoints.get(i) - breakpoints.get(prev)) * nanoMultiplicator));
        }
        breakpoints.clear();
    }

    public void printEvaluationInSeconds() {
        printEvaluation(TimeUtils.MULTIPLICATOR_NANO_2_SECOND);
    }

    public void printEvaluationInMilliseconds() {
        printEvaluation(TimeUtils.MULTIPLICATOR_NANO_2_MILLI);
    }

    public void printEvaluationInNanoSeconds() {
        printEvaluation(1.0);
    }

    public static void main(String[] args) {
        Random gen = new Random();
        for (int i = 0; i < 5; i++) {
            Performance.getInstance().addBreakpoint();
            try {
                Thread.sleep(gen.nextInt((int) TimeUtils.MILLI_IN_SECOND));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Performance.getInstance().printEvaluationInMilliseconds();
    }
}