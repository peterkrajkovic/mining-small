/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils;

import java.util.List;
import java.util.Objects;
import projectutils.structures.DoubleVector;

/**
 *
 * @author rabcan
 */
public class StopWatch {

    public enum TimeUnit {
        NANO, MIKRO, MILI, SECOND;
    }
    private long start;
    private final DoubleVector laps = new DoubleVector();
    private TimeUnit unit = TimeUnit.SECOND;

    private boolean isStarted = false;

    public StopWatch() {
        start();
    }

    public StopWatch(TimeUnit timeUnit) {
        Objects.requireNonNull(timeUnit);
        setUnit(unit);
        start();
    }

    public final void setUnit(TimeUnit unit) {
        if (isStarted) {
            this.unit = unit;
        } else {
            throwStartedError();
        }
    }

    public final void start() {
        if (!isStarted) {
            isStarted = true;
            laps.clear();
            start = System.nanoTime();
        } else {
            throwStartedError();
        }
    }

    private void throwStartedError() {
        throw new Error("The StopWatch has been started. To perform this action the stop method has to be called.");
    }

    private void throwNotStartedError() {
        throw new Error("The StopWatch has not been started. To perform this action the start method has to be called.");
    }

    public List<Double> stop() {
        if (isStarted) {
            lap();
        }
        isStarted = false;
        return laps;
    }

    public boolean isIsStarted() {
        return isStarted;
    }

    public final void lap() {
        if (isStarted) {
            laps.add(getCurrentTime());
            start = System.nanoTime();
        } else {
            throwNotStartedError();
        }
    }

    public void reset() {
        isStarted = false;
        laps.clear();
        start = System.nanoTime();
    }

    public void resetLapTime() {
        start = System.nanoTime();
    }
    
    public List<Double> getLaps(){
        return laps;
    }

    private long getUnitDivisor() {
        switch (unit) {
            case NANO:
                return 1;
            case MIKRO:
                return 1000;
            case MILI:
                return 1000000;
            case SECOND:
                return 1000000000;
            default:
                return 1000000000;
        }
    }

    public double getCurrentTime() {
        return ((double) (System.nanoTime() - start)) / getUnitDivisor();
    }

    public void printTime() {
        System.out.println(getCurrentTime());
    }

    public void printTimeAndReset() {
        System.out.println(getCurrentTime());
        reset();
        start();
    }

    public void printTimeAndReset(String message) {
        final double currentTime = getCurrentTime();
        System.out.println(message + ": " + currentTime);
        reset();
    }

    public static void main(String[] args) {
        StopWatch sw = new StopWatch(null);
    }

}
