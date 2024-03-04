/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification;

import minig.data.core.attribute.NumericAttr;
import minig.data.fuzzification.toFuzzzy.ToFuzzy;

/**
 *
 * @author jrabc
 */
public interface NumericToFuzzy extends ToFuzzy {

    public void setFuzzifiedAttr(NumericAttr attr);
}
