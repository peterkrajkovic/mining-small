/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils;

/**
 *
 * @author jrabc
 * @param <V>
 */
public class Value<V> {

    private V object;

    public Value(V defaultValue) {
        this.object = defaultValue;
    }

    public void set(V object) {
        this.object = object;
    }

    public V get() {
        return object;
    }
}
