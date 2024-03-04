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
public class HarmonicMean {

    private double wSum;
    private double mSum;

    public HarmonicMean() {
    }

    public void add(double x) {
        wSum++;
        mSum += 1 / x;
    }

    public void add(double w, double x) {
        wSum += w;
        mSum += w / x;
    }

    public void addAll(double... d) {
        for (int i = 0; i < d.length; i++) {
            add(d[i]);
        }
    }

    public double getMean() {
        return wSum / mSum;
    }

    public void reset() {
        wSum = 0;
        mSum = 0;
    }

    public static void main(String[] args) {
        HarmonicMean hm = new HarmonicMean();
        hm.add(2, 1);
        hm.add(2);
        hm.add(3);
        hm.add(11);
        System.out.println(hm.getMean());
    }

}
