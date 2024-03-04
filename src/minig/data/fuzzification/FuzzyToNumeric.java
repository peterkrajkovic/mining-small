/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification;

import java.util.ArrayList;
import java.util.List;
import minig.data.core.attribute.AttrValue;
import minig.data.core.attribute.Attribute;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.attribute.LinguisticAttr;
import minig.data.core.attribute.NumericAttr;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;

/**
 *
 * @author rabcan
 */
public class FuzzyToNumeric {

    public List<NumericAttr> toNumeric(FuzzyAttr attr) {
        List<NumericAttr> attrs = new ArrayList<>();
        for (AttrValue value : attr.getValues()) {
            NumericAttr numAttr = new NumericAttr(value.getName(), value.getType());
            for (Double x : value.getValues()) {
                numAttr.addValue(x);
            }
            attrs.add(numAttr);
        }
        return attrs;
    }

    public static DataSet fuzzyToNumeric(DataSet dt) {
        FuzzyToNumeric ltf = new FuzzyToNumeric();
        final List<Attribute> inputAttrs = dt.getInputAttrs();
        
        for (Attribute attribute : inputAttrs) {
            if (Attribute.isFuzzy(attribute)) {
                List<NumericAttr> attrs = ltf.toNumeric(attribute.fuzzy());
                dt.replaceAttribute(attrs, attribute);
            }
        }
        return dt;
    }
    
    public static void main(String[] args) {
        DataSet dt = DatasetFactory.getDataset(0);
        dt.setOutputAttr();
        dt = dt.toFuzzyDataset(2);
        dt = fuzzyToNumeric(dt);
        dt.print();
        
    }

}
