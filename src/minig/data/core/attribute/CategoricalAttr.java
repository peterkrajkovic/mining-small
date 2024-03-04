/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.core.attribute;

import minig.data.core.dataset.DatasetInstance;

/**
 *
 * @author Jan Rabcan {jrabcanj@gmail.com}
 * @param <T> type of the row of the attribute
 */
public abstract class CategoricalAttr<T> extends Attribute<T> {

    public CategoricalAttr(String name) {
        super(name);
    }

    public CategoricalAttr() {
        super();
    }

    public abstract int getDomainSize();

    public abstract int getClassIndex(int row);

    public abstract int getClassIndex(DatasetInstance ins);

    public abstract String getAttrValueName(int index);

    public abstract double[] getMemberships(int index);
    
    public abstract void setClassName(int index, String name);
}
