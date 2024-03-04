/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils.stat;

/**
 *
 * @author jrabc
 */
public final class IncrementalWStat {

    public enum StatType {
        SAMPLE, POPULATION, RELIABILITY_WEIGHTS, BASIC
    };

    private StatType type = StatType.POPULATION;

    private double wSum = 0d, mean = 0d, S = 0d;
    private double wSum2 = 0;

    public IncrementalWStat() {
    }

    public void setType(StatType type) {
        this.type = type;
    }

    /**
     *
     * @param w weight of inserted element
     * @param x inserted element
     */
    public void add(double w, double x) {
        if (w == 0) {
            return;
        }
        wSum += w;
        double meanOld = mean;
        double xMinusMeandOld = x - meanOld;
        mean = meanOld + (w / wSum) * xMinusMeandOld;
        if (type != StatType.BASIC) {
            wSum2 += w * w;
            S = S + (w * (xMinusMeandOld) * (x - mean));
        }
    }

    public void reset() {
        wSum = 0;
        mean = 0;
        S = 0;
        wSum2 = 0;
    }

    public double getVariance() {

        switch (type) {
            case POPULATION:
                if (wSum == 0) {
                    return 0;
                }
                return S / (wSum);
            case SAMPLE:
                double d = wSum - 1;
                if (d == 0) {
                    return 0;
                }
                return S / (d);
            case RELIABILITY_WEIGHTS:
                double a = wSum - (wSum2 / wSum);
                if (a == 0) {
                    return 0;
                }
                return S / (a);
        }
        return S / (wSum);              // # Population variance
    }

    public double getStdDev() {
        return Math.sqrt(getVariance());
    }

    public double getMean() {
        return mean;
    }

    public static void main(String[] args) {
        IncrementalWStat v = new IncrementalWStat();
        IncrementalStat is = new IncrementalStat();
        v.add(1, 1);
        v.add(1, 2);
        v.add(0.5, 2);
        v.add(0.5, 2);
        v.add(1d / 3, 2);
        v.add(1d / 3, 2);
        v.add(1d / 3, 2);
        is.add(1);
        is.add(2);
        is.add(2);
        is.add(2);
        System.out.println(v.getVariance());
        System.out.println(is.getVariance());
    }

}
