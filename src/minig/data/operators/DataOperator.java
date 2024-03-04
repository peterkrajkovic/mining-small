/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.operators;

import minig.data.core.attribute.Attribute;
import minig.data.core.dataset.DataSet;

/**
 *
 * @author jrabc
 */
public abstract class DataOperator {
    
    private DataSet dataset;

    public DataOperator() {
    }
    
    public DataOperator(DataSet dataset) {
        this.dataset = dataset;
    }

    public DataSet getDataset() {
        return dataset;
    }

    public void setDataset(DataSet dataset) {
        this.dataset = dataset;
    }
    
}
