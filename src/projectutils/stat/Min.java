/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils.stat;

import java.util.List;

/**
 *
 * @author Jan Rabcan {jrabcanj@gmail.com}
 */
public class Min {

    private double max = Double.POSITIVE_INFINITY;

    public void add(double x) {
        if (x < max) {
            max = x;
        }
    }

    /**
     *
     * @param x
     * @param newMinIndex
     * @param oldMinIndex
     * @return
     */
    public int add(double x, int newMinIndex, int oldMinIndex) {
        if (x < max) {
            max = x;
            return newMinIndex;
        }
        return oldMinIndex;
    }

    public void add(double weight, double x) {
        double c = weight * x;
        if (c < max) {
            max = c;
        }
    }

    public double get() {
        return max;
    }

    public static double from(double[] arr) {
        Min m = new Min();
        for (int i = 0; i < arr.length; i++) {
            m.add(arr[i]);
        }
        return m.max;
    }

    public static double from(List<Double> list) {
        Min m = new Min();
        for (Double x : list) {
            m.add(x);
        }
        return m.max;
    }

    public static void main(String[] args) {
        byte b = -127;
        System.out.println(b + b);
    }

}
