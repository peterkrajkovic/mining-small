/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils.stat;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author jrabc
 */
public final class IncrementalStat implements Serializable {

    private int count;
    private double sum = 0d;
    private double sumSquare = 0d;

    private double max = Double.MIN_VALUE;
    private double min = Double.MAX_VALUE;

    public IncrementalStat() {
    }

    public IncrementalStat(double sum, int count) {
        this.count = count;
        this.sum = sum;
    }

    public IncrementalStat(Collection<Double> dataSet) {
        for (Double x : dataSet) {
            this.add(x);
        }
    }

    public IncrementalStat(Double[] dataSet) {
        for (Double data : dataSet) {
            this.add(data);
        }
    }

    public void addAll(List<Double> l) {
        l.forEach(a -> add(a));
    }

    private void minMax(double x) {
        if (x > max) {
            max = x;
        }
        if (x < min) {
            min = x;
        }
    }

    public void add(double cislo) {
        sumSquare += cislo * cislo;
        count++;
        minMax(cislo);
        sum += cislo;
    }

    public void reset() {
        sum = 0;
        count = 0;
        sumSquare = 0;
        max = Double.MIN_VALUE;
        min = Double.MAX_VALUE;
    }

    public double getVariance() {
        if (count == 0) {
            return Double.NaN;
        } else if (count == 1) {
            return 0;
        }
        return (sumSquare - ((sum * sum) / (count))) / (count - 1);
    }

    public double getStdDev() {
        if (count < 1) {
            return Double.NaN;
        }
        return Math.sqrt(getVariance());
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public double getRange() {
        return max - min;
    }

    public int getCount() {
        return count;
    }

    public double getSum() {
        return sum;
    }

    public double getMean() {
        return sum / count;
    }

    public void getCopy() {
        IncrementalStat s = new IncrementalStat();
        s.count = count;
        s.max = max;
        s.min = min;
        s.sumSquare = sumSquare;
        s.sum = sum;
    }

    public void printStat() {
        System.out.println("avg    : " + getMean());
        System.out.println("sum    : " + getSum());
        System.out.println("min    : " + getMin());
        System.out.println("max    : " + getMax());
        System.out.println("var    : " + getVariance());
        System.out.println("st dev : " + getStdDev());
    }

    public static void main(String[] args) {
        IncrementalStat v = new IncrementalStat();

        System.out.println(v.getMean());
        v.reset();
        v.add(4);
        v.add(4);
        v.add(3);
        v.add(4);
        System.out.println(v.getMean());
    }

}
