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
public class Mean {

    private double count;
    private double sum = 0d;

    public Mean() {
    }

    public void add(double x) {
        count++;
        sum += x;
    }

    public void add(double w, double x) {
        count += w;
        sum += w * x;
    }

    public void reset() {
        sum = 0;
        count = 0;
    }

    public void addAll(double... d) {
        for (int i = 0; i < d.length; i++) {
            add(d[i]);
        }
    }

    public double getCount() {
        return count;
    }

    public double getSum() {
        return sum;
    }

    public double get() {
        return sum / count;
    }
    
    public static void main(String[] args) {
        Mean v = new Mean();
        v.add(4);
        v.add(4);
        v.add(2,3);
        System.out.println(v.get());
    }
}
