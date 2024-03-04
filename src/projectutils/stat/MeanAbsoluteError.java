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
public class MeanAbsoluteError {

    private int count;
    private double sumOfAbsolutes = 0;

    public void add(double y, double x) {
        count++;
        sumOfAbsolutes += Math.abs(y-x);
    }

    public double get() {
        return sumOfAbsolutes / count;
    }

}
