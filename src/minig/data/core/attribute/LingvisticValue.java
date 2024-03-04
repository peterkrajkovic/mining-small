/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.core.attribute;

import java.util.Objects;

/**
 *
 * @author Jan Rabcan {jrabcanj@gmail.com}
 */
public class LingvisticValue {

    private LinguisticAttr attribute;
    private String name;
    private int valIndex;

    public LingvisticValue(LinguisticAttr attribute, String name) {
        this.attribute = attribute;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public LinguisticAttr getAttribute() {
        return attribute;
    }

    public int getValIndex() {
        return valIndex;
    }

    public int get(int index) {
        if (attribute.getValues().get(index) == valIndex) {
            return valIndex;
        }
        return 0;
    }

    /**
     * This method returns true, if attribute value of any instance is
     * represented by this class. For example, this class can represent value
     * slow for attribute speed. If the instance has value of attribute equals
     * to slow, then this method returns true. Otherwise false is returned.
     *
     * @param index the index of row/instance of dataset.
     * @return This method returns true, if attribute value of any instance is
     * represented by this class. Otherwise false is returned.
     */
    public boolean is(int index) {
        if (attribute.getValues().get(index) == valIndex) {
            return true;
        }
        return false;
    }

    void setValIndex(int valIndex) {
        this.valIndex = valIndex;
    }

    public void setAttribute(LinguisticAttr attribute) {
        this.attribute = attribute;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.attribute);
        hash = 83 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LingvisticValue other = (LingvisticValue) obj;
        if (name.equals(other.name) && attribute.equals(attribute)) {
            return true;
        }
        return false;
    }

}
