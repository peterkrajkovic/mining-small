/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.models;

import projectutils.ConsolePrintable;
import java.util.List;
import minig.data.core.dataset.Instance;

/**
 *
 * @author jrabc
 */
public interface Classifier extends ConsolePrintable, Model{

    public List<Double> classify(Instance instance);

}
