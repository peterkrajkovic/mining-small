/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.core.io.save;

import minig.data.core.attribute.Attribute;

/**
 *
 * @author rabcan
 */
public class ArffWriter extends DatasetWriter{
    
    @Override
    public void save(String path) {
       RabArffWriter arrf = new RabArffWriter(getDataset());
       arrf.setUseKeyWords(false);
       arrf.save(path);
    }
    
}
