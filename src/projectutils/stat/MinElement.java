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
public class MinElement<V> implements MinMaxElement<V> {

    private double min = Double.POSITIVE_INFINITY;
    private V element;

    public boolean add(double x, V element) {
        if (x < min) {
            min = x;
            this.element = element;
            return true;
        }
        return false;
    }

    public double get() {
        return min;
    }

    public V getElement() {
        return element;
    }

    public void reset() {
        min = Double.POSITIVE_INFINITY;
        V element = null;
    }

    @Override
    public String toString() {
        return "MinElement{" + "min=" + min + ", element=" + element + '}';
    }

}
