/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.core.dataset;

import java.util.List;
import minig.data.core.attribute.NumericAttr;

/**
 *
 * @author rabcan
 */
public class NumericAttrRowIndexer {

    private DataSet dataset;
    private List<NumericAttr> attributes;
    int index = 0;

    public NumericAttrRowIndexer(DataSet dataset) {
        this.dataset = dataset;
        attributes = dataset.getNumericAttrs();
    }

    public DataSet getDataset() {
        return dataset;
    }

    public void setDataset(DataSet dt) {
        this.dataset = dt;
    }

    public int rowSize(){
        return attributes.size();
    }
    
    public boolean hasNext() {
        return dataset.getDataCount() > index;
    }

    public int getIndex() {
        return index;
    }
    
    public void next() {
        index++;
    }

    public double get(int attrIndex) {
        return attributes.get(attrIndex).get(index);
    }

}
