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
public class Sequencer {
    
    private int value = 0;
    private static Sequencer sequencer;
    
    public int getNextValue(){
        return value++;
    }

    public int getValue() {
        return value;
    }
    
    public static Sequencer getSequencer(){
        if (sequencer == null) {
            sequencer = new Sequencer();
        }
        return sequencer;
    }
}
