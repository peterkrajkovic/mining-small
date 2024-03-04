/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.core.io.save;

import minig.data.core.dataset.DataSet;

/**
 *
 * @author jrabc
 */
public abstract class DatasetWriter {

    private DataSet dataset;

    public DatasetWriter() {
    }

    public DatasetWriter(DataSet dataset) {
        this.dataset = dataset;
    }

    public abstract void save(String path);

    public DataSet getDataset() {
        return dataset;
    }

    public void setDataset(DataSet dataset) {
        this.dataset = dataset;
    }
    
    
}
