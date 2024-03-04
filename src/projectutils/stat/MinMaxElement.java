/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils.stat;

/**
 *
 * @author Jan Rabcan {jrabcanj@gmail.com}
 * @param <V>
 */
public interface MinMaxElement<V> {

    public V getElement();

    public double get();

    public boolean add(double x, V element);

}
