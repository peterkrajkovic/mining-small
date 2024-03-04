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
public class RootMeanSquareDeviation {

    private int count;
    private double sumOfSquares = 0;

    public void add(double a1, double a2) {
        count++;
        sumOfSquares += (a1 - a2) * (a1 - a2);
    }

    public double get() {
        return Math.sqrt(sumOfSquares / count);
    }
}
