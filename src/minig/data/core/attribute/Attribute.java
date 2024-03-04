/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.core.attribute;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import minig.data.core.dataset.DataSet;
import projectutils.ConsolePrintable;
import projectutils.Sequencer;

/**
 *
 * @author jrabc
 * @param <T> type of the row of the attribute
 */
public abstract class Attribute<T> implements Serializable, ConsolePrintable {

    public final static int NUMERIC = 0;
    public final static int FUZZY = 1;
    public final static int LINGUISTIC = 2;

    private int attributeIndex = -1;
    private int subListIndex = -1;
    private String name;
    private transient DataSet dataset;
    private boolean isOutput = false;

    private int id = Sequencer.getSequencer().getNextValue();

    public Attribute(String name) {
        this.name = name;
    }

    public Attribute() {
    }

    public abstract Attribute getRawCopy();

    public abstract Attribute getEmptyCopy();

    public int getId() {
        return id;
    }

    public abstract int getType();

    public String getTypeString() {
        switch (getType()) {
            case 0:
                return "Numeric";
            case 1:
                return "Fuzzy";
            case 2:
                return "Linguistic";
        }
        return "Undefined";
    }

    public String getName() {
        return name;
    }

    public static String getTypeName(Attribute attr) {
        switch (attr.getType()) {
            case NUMERIC:
                return "Numeric";
            case LINGUISTIC:
                return "Lingvistic";
            case FUZZY:
                return "Fuzzy";
            default:
                return attr.getClass().getSimpleName();
        }
    }

    public int getSubListIndex() {
        return subListIndex;
    }

    public void setSubListIndex(int subListIndex) {
        this.subListIndex = subListIndex;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NumericAttr numeric() {
        return (NumericAttr) this;
    }

    public FuzzyAttr fuzzy() {
        return (FuzzyAttr) this;
    }

    public LinguisticAttr linguistic() {
        return (LinguisticAttr) this;
    }

    public CategoricalAttr categorical() {
        return (CategoricalAttr) this;
    }

    public boolean isLast() {
        if (getDataset() == null) {
            return false;
        }
        return getAttributeIndex() == getDataset().getAtributteCount() - 1;
    }

    public abstract void destroyData();

    public abstract void removeRow(int index);

    public abstract List getValues();

    public abstract List<String> getDomainNames();

    public abstract Collection getDomain();

    public int getDomainSize() {
        return getDomain().size();
    }

    public abstract int getRowLength();

    public abstract int getDataCount();

    public abstract void addValue(String valuesName);

    public abstract String getRowString(int i);

    public abstract String getUnformatedRowString(int i);

    public boolean isOutputAttr() {
        return isOutput;
    }

    public boolean isInputAttr() {
        return !isOutputAttr();
    }

    /**
     * Method set flag the attribute is the output attribute.
     *
     * @param isOutput if true attribute will be output attribute.
     */
    public void setOutputAttr(boolean isOutput) {
        this.isOutput = isOutput;
    }

    /**
     *
     * Each attribute represent one column in the dataset (fuzzy attributes has
     * column divided into sub columns). This method return value of the cell
     * specified by the attribute index in dataset and index specified by
     * parameter.
     *
     * @param index index of value. Attribute represent column. This index
     * specifies cell position in this column.
     * @return one row of the dataset at the specified index
     */
    public abstract T getRow(int index);

    /**
     * Method add one value of instance of the attribute. For example, for
     * numerical attribute AGE value 26 can be added as AGE.addRow(26); This
     * attribute represent one column in the dataset and 26 will be the last
     * number in this column.
     *
     * @param index
     */
    public abstract void addRow(T index);

    /**
     *
     * @param dataSet dataset which is set to the attribute.
     */
    public void setDataset(DataSet dataSet) {
        this.dataset = dataSet;
    }

    /**
     *
     * @return Method returns the dataset where the attribute belong.
     */
    public DataSet getDataset() {
        return dataset;
    }

    /**
     * Method returns index of attribute in the dataset.
     *
     * @return Method returns index of attribute in the dataset.
     */
    public int getAttributeIndex() {
        return attributeIndex;
    }

    public void setAttributeIndex(int attributeIndex) {
        this.attributeIndex = attributeIndex;
    }

    public static boolean isNumeric(Attribute a) {
        return a.getType() == NUMERIC;
    }

    public static boolean isLingvistic(Attribute a) {
        return a.getType() == LINGUISTIC;
    }

    public static boolean isFuzzy(Attribute a) {
        return a.getType() == FUZZY;
    }

    public static boolean isCategorical(Attribute a) {
        return a.getType() == FUZZY || a.getType() == LINGUISTIC;
    }

    /**
     * Test whatever the attribute is numerical.
     *
     * @return Method returns true if the attribute is numerical.
     */
    public boolean isNumeric() {
        return getType() == NUMERIC;
    }

    /**
     * Test whatever the attribute is categorical. Categorical attributes are:
     * Fuzzy and Linguistic.
     *
     * @return Method returns true if the attribute is categorical (Fuzzy and
     * Linguistic).
     */
    public boolean isCategorical() {
        return getType() == FUZZY || getType() == LINGUISTIC;
    }

    /**
     * Test whatever the attribute is fuzzy.
     *
     * @return Method returns true if the attribute is fuzzy.
     */
    public boolean isFuzzy() {
        return getType() == FUZZY;
    }

    /**
     * Test whatever the attribute is linguistic.
     *
     * @return Method returns true if the attribute is linguistic.
     */
    public boolean isLinguistic() {
        return getType() == LINGUISTIC;
    }

    @Override
    public String toString() {
        return "name=" + name + getDomain().size();
    }

}
