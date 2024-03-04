/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.classification.fdt.criterrions;

import java.util.ArrayList;
import java.util.List;
import minig.classification.fdt.FDTNode;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.dataset.DataSet;

/**
 *
 * @author jrabc
 */
public abstract class FuzzyCriterion {

    private boolean ordered = false;
    private DataSet dataset;

    public DataSet getDataset() {
        return dataset;
    }

    public void setDataset(DataSet dataset) {
        this.dataset = dataset;
    }
    
    public boolean isProductUnion(){
        return true;
    }

    public boolean isOrdered() {
        return ordered;
    }

    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }

    public abstract void findAssocAttr(FDTNode node, List<FuzzyAttr> freeAttrs);

    public abstract double getCriterionValue(FDTNode node, FuzzyAttr attr);

    public abstract double getCriterionValue(FuzzyAttr output, FuzzyAttr input);

}
