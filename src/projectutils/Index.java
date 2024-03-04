/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils;

/**
 *
 * @author jrabc
 */
public class Index {

    private int index;

    public Index(int index) {
        this.index = index;
    }

    public Index() {
    }

    public int getIndex() {
        return index;
    }
    
    public void increment() {
        index = index + 1;
    }

    public void decrement() {
        index = index - 1;
    }

    public void increment(int num) {
        index = index + num;
    }

    public void decrement(int num) {
        index = index - num;
    }
}
