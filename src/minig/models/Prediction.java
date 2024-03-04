/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.models;

import projectutils.ConsolePrintable;
import minig.data.core.dataset.NewInstance;

/**
 *
 * @author Jan Rabcan {jrabcanj@gmail.com}
 */
public interface Prediction extends ConsolePrintable, Model{

    public double predict(NewInstance instance);

}
