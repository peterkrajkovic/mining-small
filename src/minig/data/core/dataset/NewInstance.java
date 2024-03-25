/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.core.dataset;

import java.util.ArrayList;
import java.util.List;
import minig.data.core.attribute.AttrValue;
import minig.data.core.attribute.Attribute;
import minig.data.core.attribute.CategoricalAttr;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.attribute.LinguisticAttr;
import minig.data.core.attribute.NumericAttr;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;
import projectutils.ConsolePrintable;
import projectutils.ProjectUtils;
import projectutils.structures.DoubleVector;

/**
 *
 * @author jrabc
 */
public class NewInstance extends Instance implements ConsolePrintable {

    private DataSet dataset;
    private List<Object> values;

    public NewInstance(DataSet dt, List<Object> values) {
        this.dataset = dt;
        this.values = values;
    }

    public NewInstance(List<Object> values) {
        this.values = values;
    }

    public NewInstance(DataSet dt) {
        this.dataset = dt;
    }

    @Override
    public <T> T getOutputValue() {
        int index = dataset.getOutputAttrIndex();
        return (T) values.get(index);
    }

    @Override
    public List<Object> getValues() {
        return values;
    }

    @Override
    public void setDataset(DataSet dataset) {
        this.dataset = dataset;
    }

    @Override
    public <T> T getValue(int index) {
        return (T) values.get(index);
    }

    @Override
    public List getFeaturesWithoutOutAttr() {
        List attrs = new ArrayList(dataset.getAtributteCount());
        for (int i = 0; i < dataset.getAtributteCount(); i++) {
            Attribute attr = dataset.getAttribute(i);
            if (!attr.isOutputAttr()) {
                attrs.add(values.get(i));
            }
        }
        return attrs;
    }

    public int getClassIndex() {
        int i = dataset.getOutbputAttribute().getAttributeIndex();
        if (dataset.getOutbputAttribute().isFuzzy()) {
            Object obj = values.get(i);
            List<Double> values = (List<Double>) obj;
            return ProjectUtils.getMaxValueIndex(values);
        } else if (dataset.getOutbputAttribute().isLinguistic()) {
            return dataset.getOutbputAttribute().linguistic().getAttrValueIndex((String) values.get(i));
        }
        return -1;
    }

    @Override
    public double[] getInputNumericData() {
        double[] arr = new double[dataset.getInputAttrCount()];
        for (int i = 0; i < dataset.getAtributteCount(); i++) {
            Attribute attr = dataset.getAttribute(i);
            if (attr.isInputAttr() && attr.isNumeric()) {
                arr[i] = getValue(i);
            }
        }
        return arr;
    }

    @Override
    public Object[] getInputData() {
        Object[] arr = new Object[dataset.getInputAttrCount()];
        for (int i = 0; i < dataset.getAtributteCount(); i++) {
            Attribute attr = dataset.getAttribute(i);
            if (attr.isInputAttr()) {
                arr[i] = getValue(i);
            }
        }
        return arr;
    }

    @Override
    public double[] getNumericData() {
        double[] arr = new double[dataset.getInputAttrCount()];
        for (int i = 0; i < dataset.getAtributteCount(); i++) {
            Attribute attr = dataset.getAttribute(i);
            if (attr.isNumeric()) {
                arr[i] = getValue(i);
            }
        }
        return arr;
    }

    @Override
    public List<Object> getFeatureVector() {
        return values;
    }

    @Override
    public List<Double> getAttrValueVector() {
        List<Double> attrs = new ArrayList(dataset.getAtributteCount());
        for (int i = 0; i < dataset.getAtributteCount(); i++) {
            Attribute attr = dataset.getAttribute(i);
            if (Attribute.isNumeric(attr)) {
                attrs.add((Double) values.get(i));
            } else if (Attribute.isFuzzy(attr)) {
                for (AttrValue attrValue : ((FuzzyAttr) attr).getDomain()) {
                    attrs.add(attrValue.get(i));
                }
            }
        }
        return attrs;
    }

    @Override
    public List<Double> getAttrValueVectorWithoutOutputAttr() {
        List<Double> attrs = new ArrayList(dataset.getInputAttrCount());
        for (int i = 0; i < dataset.getAtributteCount(); i++) {
            Attribute attr = dataset.getAttribute(i);
            if (attr.isOutputAttr()) {
                continue;
            }
            if (Attribute.isNumeric(attr)) {
                NumericAttr nattr = (NumericAttr) attr;
                attrs.add(getValueForAttrVal(nattr.getAttrValue()));
            } else if (Attribute.isFuzzy(attr)) {
                for (AttrValue attrValue : ((FuzzyAttr) attr).getDomain()) {
                    attrs.add(getValueForAttrVal(attrValue));
                }
            }
        }
        return attrs;
    }

    /**
     *
     * @param attr
     * @return
     */
    @Override
    public Object getValueOfAttribute(Attribute attr) {
        int i = attr.getAttributeIndex();
        return values.get(i);
    }

    @Override
    public Object getValueOfAttribute(int attrIndex) {
        return values.get(attrIndex);
    }

    @Override
    public double getValueForAttrVal(AttrValue val) {
        Attribute attr = val.getAttribute();
        if (Attribute.isFuzzy(attr)) {
            List<Double> attrVals = (List<Double>) getValueOfAttribute(attr);
            return attrVals.get(val.getIndexOfValue());
        } else {
            return (double) getValueOfAttribute(attr.getAttributeIndex());
        }
    }

    @Override
    public String toString() {
        return values.toString();
    }

    public String toInstanceString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            Object get = values.get(i);
            Attribute attr = dataset.getAttribute(i);
            sb.append(attr.getAttributeIndex()).append(attr.getName()).append(get).append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        DataSet dt = DatasetFactory.getDataset(DatasetFactory.IRIS);
        // dt.toFuzzyDataset(0);
        dt.getInstance(0).getValues();
        //    dt = dt.getFuzzyDataset();
        //    dt.getFuzzyDataset();
        NewInstance i = dt.getInstance(0);
        i.getClassIndex();
        System.out.println(i.toInstanceString());
        //i.mulValues(2);
        System.out.println(i);
    }

    //TODO lingvisic attr can be analyzed by class index
    @Override
    public int getClassIndex(CategoricalAttr attr) {
        int index;
        if (attr.isFuzzy()) {
            index = ProjectUtils.getMaxValueIndex(this.<List<Double>>getValue(attr.getAttributeIndex()));
            return index;
        } else {
            LinguisticAttr a = (LinguisticAttr) attr;
            index = a.getAttrValueIndex((String.valueOf(values.get(a.getAttributeIndex()))));
            return index;
        }
    }

    @Override
    public double getValue(NumericAttr attr) {
        return (double) values.get(attr.getAttributeIndex());
    }

    @Override
    public List<Double> getValue(FuzzyAttr attr) {
        return (List<Double>) values.get(attr.getAttributeIndex());
    }

    @Override
    public String getValue(LinguisticAttr attr) {
        return (String) values.get(attr.getAttributeIndex());
    }

    //TODO value should be specified as index, not string
    @Override
    public void setValue(LinguisticAttr attr, int classIndex) {
        values.set(attr.getAttributeIndex(), attr.getAttrValue(classIndex).getName());
    }

    public void setValue(FuzzyAttr attr, int classIndex) {
        DoubleVector dv = DoubleVector.zeros(attr.getDomainSize());
        dv.setNum(classIndex, 1d);
        values.set(attr.getAttributeIndex(), dv);
    }

    public void setValue(int attrIndex, double value) {
        values.set(attrIndex, value);
    }

    @Override
    public DataSet getDataset() {
        return dataset;
    }

}
