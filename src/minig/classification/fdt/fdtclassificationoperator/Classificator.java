/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.classification.fdt.fdtclassificationoperator;

import minig.data.core.dataset.Instance;

/**
 *
 * @author jrabc
 */
public interface Classificator {
    
     public Object classify(Instance instance);
    
}
