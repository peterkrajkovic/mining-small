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
public class Means {

    private double[] sums;
    private double[] count;

    public void add(int meanIndex, double number) {
        sums[meanIndex] += number;
        count[meanIndex]++;
    }

    public void add(int meanIndex, double weight, double number) {
        sums[meanIndex] += number;
        count[meanIndex] += weight;
    }

    public double getMean(int meanIndex) {
        return sums[meanIndex] / count[meanIndex];
    }
}
