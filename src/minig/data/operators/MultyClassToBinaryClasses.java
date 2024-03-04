/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.operators;

import java.util.List;
import minig.data.core.attribute.AttrValue;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;

/**
 *
 * @author jrabc
 */
public class MultyClassToBinaryClasses {

    private FuzzyAttr attr;

    public MultyClassToBinaryClasses(FuzzyAttr attr) {
        this.attr = attr;
    }

    public MultyClassToBinaryClasses() {
    }

    public FuzzyAttr getAttr() {
        return attr;
    }

    public void setAttr(FuzzyAttr attr) {
        this.attr = attr;
    }

    public FuzzyAttr[] getAttributes() {
        FuzzyAttr[] attrs = new FuzzyAttr[attr.getDomainSize()];
        List<AttrValue> values = attr.getDomain();
        for (int i = 0; i < values.size(); i++) {
            AttrValue value = values.get(i);
            FuzzyAttr fattr = new FuzzyAttr(value.getName());
            fattr.addValue("is");
            fattr.addValue("not");
            for (int j = 0; j < value.getDataCount(); j++) {
                double d = value.get(j);
                fattr.addFuzzyRow(d, 1 - d);
            }
            attrs[i] = fattr;
        }
        return attrs;
    }
    
    public static void main(String[] args) {
        DataSet d = DatasetFactory.getDataset(0);
        d.lingvisticToFuzzy();
        FuzzyAttr fa = (d).getOutbputAttribute();
        MultyClassToBinaryClasses mc = new MultyClassToBinaryClasses(fa);
        new DataSet(mc.getAttributes()).print();
        
    }

}
