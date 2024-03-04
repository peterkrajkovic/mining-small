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
public class Max {

    private double max = Double.NEGATIVE_INFINITY;

    public boolean add(double x) {
        if (x > max) {
            max = x;
            return true;
        }
        return false;
    }
    
    public void reset(){
        max = Double.NEGATIVE_INFINITY;
    }

    public void add(double weight, double x) {
        double c = weight * x;
        if (c > max) {
            max = c;
        }
    }

    public double get() {
        return max;
    }

    public static double from(double[] arr) {
        Max m = new Max();
        for (int i = 0; i < arr.length; i++) {
            m.add(arr[i]);
        }
        return m.max;
    }

    public static double from(List<Double> list) {
        Max m = new Max();
        for (Double x : list) {
            m.add(x);
        }
        return m.max;
    }

    public static int valueIndex(List<Double> val) {
        int i = -1;
        double max = Double.NEGATIVE_INFINITY;
        for (int k = 0; k < val.size(); k++) {
            double d = val.get(k);
            if (d > max) {
                i = k;
                max = d;
            }
        }
        return i;
    }

}
