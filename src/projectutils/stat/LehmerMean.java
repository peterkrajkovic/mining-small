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
public class LehmerMean {

    private double wSum;
    private double wmsum;
    private double p;

    public LehmerMean() {
        p = 2;
    }

    public LehmerMean(double p) {
        this.p = p;
    }

    public void add(double weight, double x) {
        wSum += weight * Math.pow(x, p);
        wmsum += weight * Math.pow(x, p - 1);
    }

    public void add(double x) {
        wSum += Math.pow(x, p);
        wmsum += Math.pow(x, p - 1);
    }

    public void addAll(double... d) {
        for (int i = 0; i < d.length; i++) {
            add(d[i]);
        }
    }

    public void setP(double p) {
        this.p = p;
    }

    public double getMean() {
        return Math.exp(wmsum / wSum);
    }

    public static void main(String[] args) {
        LehmerMean wm = new LehmerMean();
        wm.add(1, 2);
        wm.add(1, 1);
        wm.add(1, 4);
        wm.add(1, 8);
        wm.add(1, 3);
        System.out.println(wm.getMean());
    }
}
