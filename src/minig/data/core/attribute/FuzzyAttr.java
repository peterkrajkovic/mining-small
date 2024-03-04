/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.core.attribute;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import minig.data.core.dataset.DatasetInstance;
import projectutils.ProjectUtils;
import projectutils.structures.DoubleVector;

/**
 * linguistic - fuzzyAttribute
 *
 * @author jrabc
 */
public class FuzzyAttr extends CategoricalAttr<List<Double>> implements Serializable {

    private ArrayList<AttrValue> values = new ArrayList<>();

    public FuzzyAttr(String name, String... classes) {
        super(name);
        values = new ArrayList<>(classes.length);
        for (int i = 0; i < classes.length; i++) {
            String valuesName = classes[i];
            addValue(valuesName);
        }
    }
    
    public void destroyData() {
        for (AttrValue value : values) {
            value.getValues().destroyData();
        }
    }

    public FuzzyAttr() {
        super("");
    }

    public FuzzyAttr(int domainSize) {
        values = new ArrayList<>(domainSize);
        for (int i = 0; i < domainSize; i++) {
            addValue(i + ".");
        }
    }

    public FuzzyAttr(String name, Collection<String> classes) {
        super(name);
        values = new ArrayList<>(classes.size());
        for (String valuesName : classes) {
            addValue(valuesName);
        }
    }

    public FuzzyAttr(String name, Collection<String> classes, AttrValue.Type type) {
        super(name);
        values = new ArrayList<>(classes.size());
        for (String valuesName : classes) {
            addValue(valuesName, type);
        }
    }

    public FuzzyAttr(String name, String[] classes, AttrValue.Type type) {
        super(name);
        values = new ArrayList<>(classes.length);
        for (String valuesName : classes) {
            addValue(valuesName, type);
        }
    }

    public void swapValues(int index, int index2) {
        values.get(index).setIndexOfValue(index2);
        values.get(index2).setIndexOfValue(index);
        Collections.swap(values, index, index2);

    }

    public void forEachAttrValue(Consumer<AttrValue> c) {
        values.forEach(c);
    }

    public FuzzyAttr(String name) {
        super(name);
    }

    public void setAttributeIndex(int index) {
        super.setAttributeIndex(index);
        for (AttrValue attrValue : values) {
            attrValue.setAttribute(this);
        }
    }

    public FuzzyAttr(FuzzyAttr a) {
        super(a.getName());
        setDataset(a.getDataset());
        a.values = values;
    }

    public int getClassIndex(int row) {
        double max = Double.NEGATIVE_INFINITY;
        int index = 0;
        AttrValue value;
        for (int i = 0; i < values.size(); i++) {
            value = values.get(i);
            if (value.get(row) > max) {
                max = value.get(row);
                index = i;
            }
        }
        return index;
    }
    
    public String getInfo(){
        String info = "Name: " + getName() + System.lineSeparator(); 
        info += "Type: " + "Fuzzy" + System.lineSeparator();
        info += "Values count: " + getDomainSize() + System.lineSeparator();
        info += "------VALUES------" + System.lineSeparator();
        int maxLength = getDomainNames().stream().mapToInt(t -> t.length()).max().getAsInt();
        for (AttrValue value : values) {
            info += String.format("%"+maxLength+"s", value.getName() )+ " [" + value.getIndexOfValue() + "]" + System.lineSeparator();
        }
        return info;
    }

    @Override
    public int getClassIndex(DatasetInstance ins) {
        return getClassIndex(ins.getIndex());
    }

    @Override
    public List<String> getDomainNames() {
        List<String> names = new ArrayList(values.size());
        values.forEach(v -> names.add(v.getName()));
        return names;
    }

    @Override
    public final void addValue(String name) {
        this.addValue(name, AttrValue.Type.DOUBLE);
    }

    public AttrValue addClass(String name) {
        addValue(name);
        return this.values.get(values.size() - 1);
    }

    public final AttrValue addClass(String name, AttrValue.Type type) {
        addValue(name, type);
        return this.values.get(values.size() - 1);
    }

    public final void addClasses(AttrValue.Type type, String... names) {
        for (String name : names) {
            addValue(name, type);
        }
    }

    public final void addClasses(String... names) {
        for (String name : names) {
            addValue(name);
        }
    }

    public final void addValue(String name, AttrValue.Type type) {
        AttrValue value = new AttrValue(name, this, type);
        int index = getDomainSize();
        value.setIndexOfValue(index);
        values.add(value);
    }

    public final void removeValue(int index) {
        values.remove(index);
        for (int i = index; i < values.size(); i++) {
            values.get(i).setIndexOfValue(i);
        }
    }

    public final void addValue(AttrValue value) {
        int index = getDomainSize();
        value.setAttribute(this);
        value.setIndexOfValue(index);
        values.add(value);
    }

    /**
     * vrati pocet moznych hodnôt atributy, resp fuzzy mnozin. napr attr: vyska
     * hodnoty maly, stredny, vysoky tak vrati 3
     *
     * @return pocet hodnot, ktore atribut moze nadobudat
     */
    public int getDomainSize() {
        return values.size();
    }

    /**
     * vrati pocet instancii v datasete
     * <br> ekvivalentne getDataCount
     *
     * @return
     */
    @Override
    public int getDataCount() {
        if (getDomain() == null || getDomain().isEmpty()) {
            return 0;
        } else {
            return getDomain().get(0).getDataCount();
        }
    }

    public void addFuzzyRow(double... rowFuzzyValues) {
        if (values.size() != rowFuzzyValues.length) {
            throw new Error("Wrong count of rowFuzzyValues. Count of rowFuzzyValues must equals to count of attribute values");
        }
        for (int i = 0; i < rowFuzzyValues.length; i++) {
            values.get(i).addVaule(rowFuzzyValues[i]);
        }
    }

    public void addFuzzyRow(List<Double> rowFuzzyValues) {
        if (values.size() != rowFuzzyValues.size()) {
            throw new Error("Wrong count of rowFuzzyValues. Count of rowFuzzyValues must equals to count of attribute terms");
        }
        for (int i = 0; i < rowFuzzyValues.size(); i++) {
            values.get(i).addVaule(rowFuzzyValues.get(i));
        }
    }

    public void addFuzzyTerms(List<Double> rowFuzzyValues) {
        if (values.size() < rowFuzzyValues.size()) {
            throw new Error("Wrong count of rowFuzzyValues. Count of rowFuzzyValues must be less then count of attribute terms");
        }
        for (int i = 0; i < values.size(); i++) {
            if (i < rowFuzzyValues.size()) {
                values.get(i).addVaule(rowFuzzyValues.get(i));
            } else {
                values.get(i).addVaule(0d);
            }
        }
    }

    public void addFuzzyTerms(double[] rowFuzzyValues) {
        if (values.size() < rowFuzzyValues.length) {
            throw new Error("Wrong count of rowFuzzyValues. Count of rowFuzzyValues must be less then count of attribute terms");
        }
        for (int i = 0; i < values.size(); i++) {
            if (i < rowFuzzyValues.length) {
                values.get(i).addVaule(rowFuzzyValues[i]);
            } else {
                values.get(i).addVaule(0d);
            }
        }
    }

    @Override
    public void setClassName(int indexOfClass, String name) {
        this.values.get(indexOfClass).setName(name);
    }

    @Override
    public List<AttrValue> getDomain() {
        return values;
    }

    public AttrValue getAttrValue(int index) {
        return values.get(index);
    }

    public AttrValue getFirstAttrValue() {
        return values.get(0);
    }

    @Override
    public DoubleVector getRow(int rowIndex) {
        DoubleVector row = new DoubleVector(values.size());
        for (AttrValue v : values) {
            row.add(v.get(rowIndex));
        }
        return row;
    }

    public double getValue(int attrValueIndex, int rowIndex) {
        return getAttrValue(attrValueIndex).get(rowIndex);
    }

    public BoxedRow getBoxedRow(int rowIndex) {
        return new BoxedRow(rowIndex, getValues());
    }

    @Override
    public String toString() {
        return "Fuzzy{" + getName() + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.getName());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final FuzzyAttr other = (FuzzyAttr) obj;
        return Objects.equals(this.getName(), other.getName());
    }

    void cutValuesToSize(int size) {
        values.parallelStream().forEach((value) -> {
            value.cutValuesToSize(size);
        });
    }

    public String getAttrValuesString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getDataCount(); i++) {
            sb.append(getRow(i)).append("\n");
        }
        return sb.toString();
    }

    @Override
    public void print() {
        System.out.println(getAttrValuesString());
    }

    @Override
    public String getRowString(int i) {
        return ProjectUtils.listToString(getRow(i));
    }

    @Override
    public String getUnformatedRowString(int i) {
        String s = "";
        for (int j = 0; j < values.size(); j++) {
            AttrValue v = values.get(j);
            s += v.get(i);
            if (j < values.size() - 1) {
                s += ",";
            }
        }

        return s;
    }

    @Override
    public FuzzyAttr getEmptyCopy() {
        FuzzyAttr attr = new FuzzyAttr(getName());
        attr.setAttributeIndex(getAttributeIndex());
        attr.setOutputAttr(false);
        for (AttrValue value : values) {
            attr.addValue(value.getName(), value.getType());
        }
        return attr;
    }

    @Override
    public void addRow(List<Double> index) {
        this.addFuzzyRow(index);
    }

    public void addRow(String value) {
        for (AttrValue value1 : values) {
            if (value1.getName().equalsIgnoreCase(value)) {
                value1.addVaule(1);
            } else {
                value1.addVaule(0);
            }
        }
    }

    @Override
    public int getType() {
        return Attribute.FUZZY;
    }

    @Override
    public FuzzyAttr getRawCopy() {
        FuzzyAttr a = getEmptyCopy();
        a.getDomain().clear();
        for (AttrValue value : values) {
            AttrValue val = new AttrValue(value);
            val.setName(value.getName());
            a.addValue(val); //TODO problem
        }
        return a;
    }

    @Override
    public int getRowLength() {
        return values.size();
    }

    @Override
    public List<AttrValue> getValues() {
        return getDomain();
    }

    @Override
    public String getAttrValueName(int index) {
        return getAttrValue(index).getName();
    }

    @Override
    public void removeRow(int index) {
        for (AttrValue value : values) {
            value.getValues().remove(index);
        }
    }

    @Override
    public double[] getMemberships(int index) {
        double[] memberships = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            memberships[i] = values.get(i).get(index);
        }
        return memberships;
    }

    public class BoxedRow {

        private int index;
        private List<AttrValue> vectors;

        private BoxedRow(int index, List<AttrValue> vectors) {
            this.index = index;
            this.vectors = vectors;
        }

        public double size() {
            return vectors.size();
        }

        public double get(int i) {
            return vectors.get(i).get(index);
        }

        public DoubleVector get() {
            DoubleVector dv = new DoubleVector(vectors.size());
            for (AttrValue vector : vectors) {
                dv.addNum(vector.get(index));
            }
            return dv;
        }

    }

}
