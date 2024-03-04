/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils.stat;

/**
 *
 * @author Jan Rabcan {jrabcanj@gmail.com}
 */
public class GeometricMean {

    private double wSum;
    private double lnXsum;

    public GeometricMean() {
    }

    public void add(double weight, double x) {
        wSum += weight;
        lnXsum += weight * Math.log(x);
    }

    public void add(double x) {
        wSum += 1;
        lnXsum += Math.log(x);
    }

    public void addAll(double... d) {
        for (int i = 0; i < d.length; i++) {
            add(d[i]);
        }
    }

    public void reset() {
        wSum = 0;
        lnXsum = 0;
    }

    public double getMean() {
        return Math.exp(lnXsum / wSum);
    }

    public static void main(String[] args) {
        GeometricMean wm = new GeometricMean();
        wm.add(1, 2);
        wm.add(1, 1);
        wm.add(1, 4);
        wm.add(1, 8);
        wm.add(1, 3);
        System.out.println(wm.getMean());
    }

}
