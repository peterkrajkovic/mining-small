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
public class LinguisticOutputAttr extends OutputAttribute{

    public LinguisticOutputAttr(DataSet dataset) {
        super(dataset);
    }

    @Override
    public boolean check() {
        return super.check() && getDataset().getOutbputAttribute().isLinguistic();
    }
    
    
}
