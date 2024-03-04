/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.core.attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import minig.data.core.dataset.DatasetInstance;
import minig.data.fuzzification.LingvisticToFuzzy;
import projectutils.structures.IntegerVector;

/**
 *
 * @author jrabc
 */
public class LinguisticAttr extends CategoricalAttr<String> {

    private List<LingvisticValue> attrValues = new ArrayList();
    private HashMap<String, Integer> attrValuesMap = new HashMap<>();

    private IntegerVector values = new IntegerVector();
    public final int type = 2;

    public LinguisticAttr(String name, List<String> values) {
        super(name);
        for (String value : values) {
            addValue(value);
        }
    }

    @Deprecated
    public void setCapacity(int capacity) {
        if (!values.isEmpty()) {
            throw new Error("Column of attribute contains values. Values must be empty");
        }
        values = IntegerVector.zeros(capacity);
    }

    public LinguisticAttr(String name) {
        super(name);
    }

    public LinguisticAttr(String name, String... classes) {
        super(name);
        this.attrValues = new ArrayList(classes.length);
        for (String ligValue : classes) {
            addAttrValue(ligValue);
        }
    }

    public LinguisticAttr(String name, boolean add, String... rows) {
        super(name);
        this.addRows(rows);
    }

    public LingvisticValue getAttrValue(int index) {
        return attrValues.get(index);
    }

    @Override
    public String getAttrValueName(int i) {
        return attrValues.get(i).getName();
    }

    public String getDomainName(int domainindex) {
        return attrValues.get(domainindex).getName();
    }

    @Override
    public void destroyData() {
        values.destroyData();
    }

    //TODO vrati lingvisticValue, ktora nemusi zodpovedat value atributu
    public final void addAttrValue(String className) {
        if (!this.attrValuesMap.containsKey(className)) {
            final LingvisticValue lingvisticValue = new LingvisticValue(this, className);
            this.attrValues.add(lingvisticValue);
            final int index = attrValues.size() - 1;
            attrValuesMap.put(className, index);
            lingvisticValue.setValIndex(index);
        }
    }

    public final LingvisticValue addAttrValueNoCheck(String className) {
        final LingvisticValue lingvisticValue = new LingvisticValue(this, className);
        if (!this.attrValues.contains(lingvisticValue)) {
            this.attrValues.add(lingvisticValue);
            lingvisticValue.setValIndex(attrValues.size() - 1);
        }
        return lingvisticValue;
    }

    public void addAttrValues(String... className) {
        for (String string : className) {
            addAttrValue(string);
        }
    }

    @Override
    public LinguisticAttr getRawCopy() {
        LinguisticAttr a = getEmptyCopy();
        a.values = this.values;
        a.attrValues = this.attrValues;
        a.setAttributeIndex(getAttributeIndex());
        return a;
    }

    @Override
    public final void setAttributeIndex(int index) {
        super.setAttributeIndex(index);
        for (LingvisticValue attrValue : attrValues) {
            attrValue.setAttribute(this);
        }
    }

    @Override
    public List<LingvisticValue> getDomain() {
        return attrValues;
    }

    @Override
    public final int getDomainSize() {
        return attrValues.size();
    }

    @Override
    public List<String> getDomainNames() {
        return attrValues.stream().map((t) -> {
            return t.getName();
        }).collect(Collectors.toList());
    }

    public final List<LingvisticValue> getClasses() {
        return attrValues;
    }

    @Override
    public final IntegerVector getValues() {
        return values;
    }

    @Override
    public final void addValue(String value) {
        addAttrValue(value);
        int clasIndex = attrValuesMap.get(value);
        values.add(clasIndex);
    }

    public FuzzyAttr transformToFuzzyAttribute() {
        FuzzyAttr a = new FuzzyAttr(getName());

        return a;
    }

    @Override
    public int getDataCount() {
        return values.size();
    }

    @Override
    public String getRowString(int i) {
        int clsIndex = values.getPoint(i);
        return attrValues.get(clsIndex).getName();
    }

    @Override
    public String getUnformatedRowString(int i) {
        return getRowString(i);
    }

    @Override
    public String getRow(int index) {
        int clsIndex = values.getPoint(index);
        return attrValues.get(clsIndex).getName();
    }

    @Override
    public int getClassIndex(int row) {
        return values.getNum(row);
    }

    public int get(int row) {
        return values.getNum(row);
    }

    public int getAttrValueIndex(String attrValName) {
        for (LingvisticValue val : attrValues) {
            if (val.getName().equals(attrValName)) {
                return val.getValIndex();
            }
        }
        return -1;
    }

    @Override
    public void print() {
        String s = getName().toUpperCase() + "\n";
        StringBuilder sb = new StringBuilder();
        for (Integer value : values) {
            sb.append(attrValues.get(value)).append("\n");
        }
        System.out.println(s + sb.toString());
    }

    @Override
    public String toString() {
        return "Lingvistic{" + getName() + '}';
    }

    @Override
    public LinguisticAttr getEmptyCopy() {
        LinguisticAttr attr = new LinguisticAttr(getName());
        for (LingvisticValue attrValue : attrValues) {
            attr.addAttrValue(attrValue.getName());
        }
        attr.values = new IntegerVector();
        attr.setOutputAttr(false);
        return attr;
    }

    @Override
    public void addRow(String val) {
        this.addValue(val);
    }

    public void addRow(Integer classIndex) {
        if (classIndex > attrValues.size() - 1) {
            throw new Error("Unknow class index: " + classIndex + " for attribute: " + getName().toUpperCase() + ", during adding row");
        }
        values.add(classIndex);
    }

    public void addRows(String... val) {
        for (String string : val) {
            this.addRow(string);
        }
    }

    public void addRows(int... val) {
        for (int v : val) {
            addRow(v);
        }
    }

    public FuzzyAttr toFuzzy() {
        LingvisticToFuzzy ltf = new LingvisticToFuzzy(this);
        return ltf.toFuzzy();
    }

    @Override
    public int getType() {
        return Attribute.LINGUISTIC;
    }

    @Override
    public int getRowLength() {
        return 1;
    }

    @Override
    public void removeRow(int index) {
        values.remove(index);
    }

    @Override
    public double[] getMemberships(int index) {
        double[] memberships = new double[attrValues.size()];
        memberships[getValues().getNum(index)] = 1;
        return memberships;
    }

    public static void main(String[] args) {
        LinguisticAttr attr = new LinguisticAttr("AATR", "A", "B", "C");
        attr.addRows(0, 1, 1, 2, 1, 1, 1, 2);
        attr.print();
        for (int i = 0; i < attr.getDataCount(); i++) {
            System.out.println(Arrays.toString(attr.getMemberships(i)));
        }
    }

    @Override
    public int getClassIndex(DatasetInstance ins) {
        return getClassIndex(ins.getIndex());
    }

    @Override
    public void setClassName(int index, String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
