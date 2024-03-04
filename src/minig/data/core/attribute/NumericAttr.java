/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.core.attribute;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import minig.data.core.dataset.DataSet;
import projectutils.ProjectUtils;
import projectutils.stat.IncrementalStat;
import projectutils.structures.Vector;

/**
 *
 * @author jrabc
 */
public class NumericAttr extends Attribute<Double> implements Serializable {

    private AttrValue attrValue;

    public NumericAttr(String name, double... attrvalues) {
        super(name);
        attrValue = new AttrValue(name, this);
        attrValue.addVaule(attrvalues);
    }

    public NumericAttr(String name, List<Double> attrvalues) {
        super(name);
        attrValue = new AttrValue(name, this);
        attrValue.addVaule(attrvalues);
    }

    @Override
    @Deprecated
    /**
     * <b> not implementneted TODO <b>
     */
    public NumericAttr getRawCopy() {
        NumericAttr a = new NumericAttr(getName());
        AttrValue av = new AttrValue(attrValue);
        av.setAttribute(a);
        a.attrValue = av;
        return a;
    }

    public NumericAttr(String name) {
        super(name);
        attrValue = new AttrValue(name, this);
    }

    public NumericAttr(String name, AttrValue.Type type) {
        super(name);
        attrValue = new AttrValue(name, this, type);
    }

    public Vector getValues() {
        return attrValue.getValues();
    }

    public IncrementalStat getStat() {
        return attrValue.getStat();
    }

    public void destroyData() {
        attrValue.getValues().destroyData();
    }

    public final void addValue(double value) {
        attrValue.addVaule(value);
    }

    @Override
    public void addValue(String number) {
        addValue(number, false);
    }

    public void addValue(String number, boolean printErrLines) {
        double d;
        try {
            d = Double.parseDouble(number);
        } catch (NumberFormatException e) {
            if (printErrLines) {
                System.out.println(getDataCount() + 1);
            }
            d = Double.NaN;
        }
        attrValue.addVaule(d);
    }

    public boolean hasMissingValues() {
        return attrValue.hasMissingValues();
    }

    /**
     * if value on specified index is NaN than is replaced by defined value.
     * Else nothing happen.
     *
     * @param index
     * @param value
     */
    public final void replaceNaNValue(int index, double value) {
        if (Double.isNaN(attrValue.getValues().getNum(index))) {
            attrValue.getValues().setNum(index, value);
            attrValue.getStat().add(value);
        }
    }

    /**
     * if any value is NaN than is replaced by defined value.
     *
     * @param value
     */
    public void replaceNaNValues(double value) {
        for (int index = 0; index < attrValue.size(); index++) {
            replaceNaNValue(index, value);
        }
    }

    @Override
    public Double getRow(int rowIndex) {
        return attrValue.get(rowIndex);
    }

    /**
     * the same as getRow
     *
     * @param rowIndex
     * @return
     */
    public final double get(int rowIndex) {
        return attrValue.getValues().getNum(rowIndex);
    }

    @Override
    public final int getType() {
        return Attribute.NUMERIC;
    }

    /**
     * evivalent getInstancesCount
     *
     * @return
     */
    @Override
    public final int getDataCount() {
        return attrValue.getValues().size();
    }

    @Override
    public String getRowString(int rowIndex) {
        return ProjectUtils.formatDouble(attrValue.get(rowIndex));
    }

    @Override
    public String getUnformatedRowString(int i) {
        return attrValue.getValues().get(i).toString();
    }

    @Override
    public void print() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < attrValue.size() - 1; i++) {
            Double get = attrValue.get(i);
            sb.append(get).append(", ");
        }
        Double get = attrValue.get(attrValue.size() - 1);
        sb.append(get);
        System.out.println(sb.toString());
    }

    @Override
    public String toString() {
        return "Numeric{" + getName() + '}';
    }

    @Override
    public NumericAttr getEmptyCopy() {
        NumericAttr attr = new NumericAttr(getName());
        attr.attrValue = new AttrValue(getName(), attr);
        return attr;
    }

    public AttrValue getAttrValue() {
        return attrValue;
    }

    public double getRowValue(int index) {
        return attrValue.get(index);
    }

    @Override
    public final void addRow(Double val) {
        this.addValue(val);
    }

    @Override
    public Collection getDomain() {
        return new ArrayList(1) {
            {
                add(getAttrValue());
            }
        };
    }

    @Override
    public List<String> getDomainNames() {
        List<String> names = new ArrayList(1);
        names.add(getName());
        return names;
    }

    @Override
    public void removeRow(int index) {
        attrValue.getValues().remove(index);
    }

    public static void main(String[] args) {
        DataSet dt = new DataSet();
        dt.addAttribute(new NumericAttr("aa", 1, 2, 3, 4, 5, 6));
        dt.addAttribute(new NumericAttr("ba", 1, 2, 3, 4, 5, 6));
        dt.addAttribute(new NumericAttr("ca", 1, 2, 3, 4, 5, 6));
        DataSet dt2 = new DataSet();
        dt2.addAttribute(dt.getAttribute(2));
        System.out.println("");
    }

    @Override
    public int getRowLength() {
        return 1;
    }

}
