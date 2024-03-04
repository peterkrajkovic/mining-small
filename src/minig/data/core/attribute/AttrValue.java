/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.core.attribute;

import java.util.Collection;
import java.util.List;
import projectutils.stat.IncrementalStat;
import projectutils.structures.BinaryVector;
import projectutils.structures.DoubleVector;
import projectutils.structures.FloatVector;
import projectutils.structures.Vector;

/**
 *
 * @author jrabc
 */
public class AttrValue {

    public enum Type {
        BINARY, DOUBLE, FLOAT;
    }

    private String name;
    private transient Vector values;
    private Attribute attribute;
    private int indexOfValue;
    private IncrementalStat stat = new IncrementalStat();
    private Type type = Type.DOUBLE;

    private int missingValues = 0;

    public AttrValue(AttrValue val, Attribute a) {
        this.name = val.name;
        attribute = a;
        values = val.getValues();
    }

    public AttrValue() {
        values = new DoubleVector(2);
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public final void setType(Type type) {
        this.type = type;
    }

    public AttrValue(String name, Attribute attribute) {
        this.name = name;
        this.attribute = attribute;
        values = getNewVector();
    }

    public AttrValue(String name, Attribute attribute, Type type) {
        this.name = name;
        this.attribute = attribute;
        setType(type);
        values = getNewVector();
    }

    private Vector getNewVector() {
        switch (type) {
            case DOUBLE:
                return new DoubleVector();
            case BINARY:
                return new BinaryVector();
            case FLOAT:
                return new FloatVector();
        }
        return new DoubleVector();
    }

    public void setValueAt(int index, double value) {
        getValues().set(index, value);
    }

    public final Vector getValues() {
        return values;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public AttrValue(AttrValue val) {
        this.name = val.name;
        this.attribute = val.attribute;
        this.type = val.type;
        this.values = val.values;
        this.indexOfValue = val.getIndexOfValue();
        this.stat = val.stat;
    }

    public String getFingerPrint() {
        return getAttribute().getName() + getName();
    }

    //TODO check
    public AttrValue getEmptyCompy(AttrValue val) {
        AttrValue newValue = new AttrValue(val);
        newValue.values = getNewVector();
        newValue.stat = stat;
        indexOfValue = val.indexOfValue;
        return newValue;
    }

    public AttrValue getReferencedCopy() {
        AttrValue newValue = new AttrValue(getName(), null, type);
        newValue.stat = stat;
        newValue.values = getValues();
        newValue.indexOfValue = indexOfValue;
        return newValue;
    }

    public AttrValue getHardCopy() {
        AttrValue newValue = new AttrValue(getName(), null, type);
        newValue.indexOfValue = indexOfValue;
        for (Double value : getValues()) {
            newValue.addVaule(value);
        }
        return newValue;
    }

    public int size() {
        return values.size();
    }

    public double get(int index) {
        return values.getNum(index);
    }

    public Number getInDedaultType(int index) {
        return values.getNumDefault(index);
    }

    public double getSum() {
        return getStat().getSum();
    }

    public int getIndexOfValue() {
        return indexOfValue;
    }

    void setIndexOfValue(int indexOfValue) {
        this.indexOfValue = indexOfValue;
    }

    public void addVaule(double vaule) {
        values.addNum(vaule);
        if (!Double.isNaN(vaule)) {
            stat.add(vaule);
        } else {
            missingValues++;
        }
    }

    public int getMissingValues() {
        return missingValues;
    }

    public boolean hasMissingValues() {
        return missingValues > 0;
    }

    public void addVaule(double... vaule) {
        for (double w : vaule) {
            addVaule(w);
        }
    }

    public void addVaule(Collection<Double> vaule) {
        for (double w : vaule) {
            addVaule(w);
        }
    }

    public IncrementalStat getStat() {
        return stat;
    }

    public List<Double> getDeepCopyOfVaules() {
        return values.getDeepCopy();
    }

    /**
     *
     * @return mnoztvo zaznamaov - pocet datovych instnaci
     */
    public int getDataCount() {
        return values.size();
    }

    public String getName() {
        return name;
    }

    public <T extends Attribute> T getAttribute() {
        return (T) attribute;
    }

    @Override
    public String toString() {
        return name;
    }

    @Deprecated
    public void addScalar(double scalar) {
        stat.reset();
        for (int i = 0; i < values.size(); i++) {
            double val = values.get(i);
            double newVal = val + scalar;
            values.set(i, newVal);
            stat.add(val);
        }
    }

    void cutValuesToSize(int size) {
        if (values.size() > size) {
            values.trimToSize(size);
        }
    }

}
