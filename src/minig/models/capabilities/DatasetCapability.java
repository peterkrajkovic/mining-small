/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.models.capabilities;

import minig.data.core.dataset.DataSet;

/**
 *
 * @author jrabc
 */
public abstract class DatasetCapability extends Capability{

    private DataSet dataset;

    public DatasetCapability(DataSet dataset) {
        this.dataset = dataset;
    }

    public DataSet getDataset() {
        return dataset;
    }

}
