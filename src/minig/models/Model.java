/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.models;

import minig.data.core.dataset.DataSet;

/**
 *
 * @author Jan Rabcan {jrabcanj@gmail.com}
 */
public interface Model {

    public <T extends DataSet> T getDataset();

    public void setDataset(DataSet dt);

    public void buildModel();
    
}
