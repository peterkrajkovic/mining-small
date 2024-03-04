/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.core.dataset;

import projectutils.ConsolePrintable;
import java.util.ArrayList;
import java.util.List;
import minig.data.core.attribute.AttrValue;
import minig.data.core.attribute.Attribute;
import minig.data.core.attribute.CategoricalAttr;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.attribute.LinguisticAttr;
import minig.data.core.attribute.LingvisticValue;
import minig.data.core.attribute.NumericAttr;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;
import projectutils.ErrorMessages;

/**
 *
 * @author Jan Rabcan {jrabcanj@gmail.com}
 */
public class DatasetInstance extends Instance implements ConsolePrintable {

    private DataSet dataset;
    private int index;

    DatasetInstance(DataSet dt, int rowIndex) {
        this.dataset = dt;
        this.index = rowIndex;
    }

    @Override
    public <T> T getOutputValue() {
        return (T) dataset.getOutbputAttribute().getRow(index);
    }

    @Override
    public List<Object> getValues() {
        return dataset.getRow(index);
    }

    /**
     * Method set dataset which instance comes from.
     *
     * @param dataset dataset which will be set to the instance
     */
    @Override
    public void setDataset(DataSet dataset) {
        this.dataset = dataset;
    }

    /**
     * Method returns dataset which instance comes from.
     *
     * @return returns dataset which instance comes from.
     */
    public DataSet getDataset() {
        return dataset;
    }

    @Override
    public <T> T getValue(int index) {
        return (T) dataset.getAttribute(index).getRow(this.index);
    }

    public double getValueOfNumericAttr(Attribute attribute) {
        Attribute attr = dataset.getAttribute(attribute.getAttributeIndex());
        if (attr.isNumeric()) {
            return dataset.getNumericAttrs().get(attr.getSubListIndex()).get(this.index);
        } else {
            throw new Error(ErrorMessages.ATTR_MUST_BE_NUMERIC);
        }
    }

    /**
     * Method returns values of discrete attributes. For example, if the datset
     * contains attributes Speed {slow, fast} and CarType {Truck, Van, Small
     * Car}. The list for instance where speed=slow and CarType=Truck is
     * following: [1,0 , 1,0,0]
     *
     * @param outputAttr if true, the output attribute will be included in the
     * returned list.
     * @return The list of discrete values
     */
    public List<Integer> getDiscreteValues(boolean outputAttr) {
        ArrayList values = new ArrayList();
        for (Attribute attribute : dataset.getAttributes()) {
            if (!outputAttr && attribute.isOutputAttr()) {
                continue;
            }
            if (attribute.isLinguistic()) {
                for (LingvisticValue lingvisticValue : attribute.linguistic().getDomain()) {
                    if (lingvisticValue.is(index)) {
                        values.add(1);
                    } else {
                        values.add(0);
                    }
                }
            } else if (attribute.isFuzzy()) {
                attribute.fuzzy().forEachAttrValue((t) -> {
                    values.add(t.get(index));
                });
            }
        }
        return values;
    }

    /**
     * Method returns index of instance in the dataset.
     *
     * @return Method returns index of instance in the dataset.
     */
    public int getIndex() {
        return index;
    }

    void setIndex(int newIndex) {
        this.index = newIndex;
    }

    /**
     * attribute should be from dataset, where instance belong. It is not
     * mandatory, attribute is used only for find attribute by index;
     *
     * @param attr
     * @param classIndex
     */
    public void setValue(LinguisticAttr attr, int classIndex) {
        dataset.getLiguisticAttrs().get(attr.getSubListIndex()).getValues().setNum(index, classIndex);
    }

    @Override
    public List getFeaturesWithoutOutAttr() {
        List attrs = new ArrayList(dataset.getAtributteCount());
        for (int i = 0; i < dataset.getAtributteCount(); i++) {
            Attribute attr = dataset.getAttribute(i);
            if (!attr.isOutputAttr()) {
                attrs.add(getValue(i));
            }
        }
        return attrs;
    }

    @Override
    public double[] getInputNumericData() {
        double[] arr = new double[dataset.getInputAttrCount()];
        int added = 0;
        for (int i = 0; i < dataset.getAtributteCount(); i++) {
            Attribute attr = dataset.getAttribute(i);
            if (attr.isInputAttr() && attr.isNumeric()) {
                arr[added++] = getValue(i);
            }
        }
        return arr;
    }

    @Override
    public Object[] getInputData() {
        Object[] arr = new Object[dataset.getInputAttrCount()];
        int added = 0;
        for (int i = 0; i < dataset.getAtributteCount(); i++) {
            Attribute attr = dataset.getAttribute(i);
            if (attr.isInputAttr()) {
                arr[added++] = getValue(i);
            }
        }
        return arr;
    }

    @Override
    public double[] getNumericData() {
        double[] arr = new double[dataset.getNumAttrCount()];
        int added = 0;
        for (int i = 0; i < dataset.getAtributteCount(); i++) {
            Attribute attr = dataset.getAttribute(i);
            if (attr.isNumeric()) {
                arr[added++] = getValue(i);
            }
        }
        return arr;
    }

    @Override
    public List<Object> getFeatureVector() {
        return getValues();
    }

    @Override
    public List<Double> getAttrValueVector() {
        List<Double> attrs = new ArrayList<>(dataset.getAtributteCount());
        for (int i = 0; i < dataset.getAtributteCount(); i++) {
            Attribute attr = dataset.getAttribute(i);
            if (Attribute.isNumeric(attr)) {
                attrs.add((Double) getValue(index));
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
        List<Double> attrs = new ArrayList<>(dataset.getInputAttrCount());
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
     * Attribute must by from dataset
     *
     * @param attr
     * @return
     */
    @Override
    public Object getValueOfAttribute(Attribute attr) {
        return dataset.getAttribute(attr.getAttributeIndex()).getRow(index);
    }

    @Override
    public Object getValueOfAttribute(int attrIndex) {
        return getValue(attrIndex);
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
        return getFeatureVector().toString();
    }

    @Override
    public String toInstanceString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dataset.getAtributteCount(); i++) {
            Object get = getValue(i);
            Attribute attr = dataset.getAttribute(i);
            sb.append(attr.getAttributeIndex()).append(attr.getName()).append(get).append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }

    @Override
    //TODO FIX
    public int getClassIndex(CategoricalAttr attr) {
        if (attr.getType() > 0) {
            CategoricalAttr cattr = dataset.getAttribute(attr.getAttributeIndex());
            return cattr.getClassIndex(index);
        } else {
            return attr.getClassIndex(index);
        }
    }

    public int getClassIndex(LinguisticAttr attr) {
        return dataset.getLiguisticAttrs().get(attr.getSubListIndex()).getClassIndex(index);
    }

    /**
     *
     * @return clas index of output attr
     */
    public int getClassIndex() {
        return ((CategoricalAttr) dataset.getOutbputAttribute()).getClassIndex(index);
    }

    public FuzzyAttr.BoxedRow getValueInBoxedRow(FuzzyAttr attr) {
        return dataset.getFuzzyAttrs().get(attr.getSubListIndex()).getBoxedRow(index);
    }

    @Override
    public double getValue(NumericAttr attr) {
        return dataset.getNumericAttrs().get(attr.getSubListIndex()).get(index);
    }

    @Override
    public String getValue(LinguisticAttr attr) {
        return dataset.getLiguisticAttrs().get(attr.getSubListIndex()).getRowString(index);
    }

    @Override
    public List<Double> getValue(FuzzyAttr attr) {
        return dataset.getFuzzyAttrs().get(attr.getSubListIndex()).getRow(index);
    }

    public static void main(String[] args) {
        DataSet dt = DatasetFactory.getDataset(DatasetFactory.GOLF_LINGVISTIC);
        dt.print(3);
        System.out.println(dt.getDatasetInstances().get(0).getDiscreteValues(false));
        System.out.println(dt.getDatasetInstances().get(1).getDiscreteValues(true));
        System.out.println(dt.getDatasetInstances().get(2).getDiscreteValues(true));

        System.out.println(dt.getDatasetInstances().get(2).getClassIndex(dt.getOutbputAttribute()));
        System.out.println(dt.getInstance(2).getClassIndex(dt.getOutbputAttribute()));

    }

}
