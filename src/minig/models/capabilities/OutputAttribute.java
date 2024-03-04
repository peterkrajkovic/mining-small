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
public class OutputAttribute extends DatasetCapability {

    public OutputAttribute(DataSet dataset) {
        super(dataset);
    }

    @Override
    public boolean check() {
        return getDataset().hasOutputAttr();
    }

}
