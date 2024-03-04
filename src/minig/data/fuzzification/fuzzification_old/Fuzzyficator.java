/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.fuzzification_old;

import java.util.List;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.attribute.NumericAttr;

/**
 *
 * @author jrabc
 */
public interface Fuzzyficator {

    public List<Double> getFuzzyTerm(double number);

    public int getTermsCount();

    default FuzzyAttr getFuzzyAttr(NumericAttr attr) {
        FuzzyAttr fattr = getEmptyFuzzyAttr(attr);
        List<Double> values = attr.getValues();
        for (int i = 0; i < values.size(); i++) {
            Double val = values.get(i);
            fattr.addFuzzyRow(getFuzzyTerm(val));
        }
        return fattr;
    }

    default FuzzyAttr getEmptyFuzzyAttr(NumericAttr attr) {
        FuzzyAttr fattr = new FuzzyAttr(attr.getName());
        for (int i = 0; i < getTermsCount(); i++) {
            fattr.addValue(fattr.getName() + "_" + i);
        }
        return fattr;
    }

}
