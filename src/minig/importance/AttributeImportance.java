/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.importance;

import minig.data.core.attribute.Attribute;
import projectutils.ProjectUtils;

/**
 *
 * @author rabcan
 */
public class AttributeImportance {

    private Attribute attribute;
    private double importance;

    public AttributeImportance(Attribute attribute) {
        this.attribute = attribute;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public double getImportance() {
        return importance;
    }

    public void setImportance(double importance) {
        this.importance = importance;
    }

    public void addToImportance(double weight, double importance) {
        this.importance += importance * weight;
    }

    @Override
    public String toString() {
        return attribute + " =" + ProjectUtils.formatDouble(importance);
    }

}
