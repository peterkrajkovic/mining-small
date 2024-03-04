/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils.stat;

import java.util.function.Predicate;

/**
 *
 * @author Jan Rabcan {jrabcanj@gmail.com}
 */
public class MaxElement<V> implements MinMaxElement<V> {

    private double max = Double.NEGATIVE_INFINITY;
    private V element;

    @Override
    public boolean add(double x, V element) {
        if (x >= max) {
            max = x;
            this.element = element;
            return true;
        }
        return false;
    }

    public boolean addIf(double x, V element, Test test) {
        if (x > max && test.test()) {
            max = x;
            this.element = element;
            return true;
        }
        return false;
    }

    public void setElement(V element) {
        this.element = element;
    }

    public double get() {
        return max;
    }

    public void reset() {
        max = Double.NEGATIVE_INFINITY;
        element = null;
    }

    public V getElement() {
        return element;
    }

    @Override
    public String toString() {
        return "max=" + max + ", element=" + element;
    }

    @FunctionalInterface
    public interface Test {

        public boolean test();
    }

}
