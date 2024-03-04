/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.intervals;

import java.util.List;
import minig.data.core.attribute.NumericAttr;

/**
 *
 * @author jrabc
 */
public interface IntervalFinder {

    public List<Double> getIntervals();
    
    public void setInputAttr(NumericAttr a);

}
