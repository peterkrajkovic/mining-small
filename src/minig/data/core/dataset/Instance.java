/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.core.dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import minig.data.core.attribute.AttrValue;
import minig.data.core.attribute.Attribute;
import minig.data.core.attribute.CategoricalAttr;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.attribute.LinguisticAttr;
import minig.data.core.attribute.NumericAttr;
import projectutils.ConsolePrintable;
import projectutils.stat.Max;
import projectutils.structures.BoxedDoubleArray;

/**
 *
 * @author Jan Rabcan {jrabcanj@gmail.com}
 */
public abstract class Instance implements ConsolePrintable {

    /**
     * If output attribute is set, then the output value is returned, else null.
     *
     * @param <T> in case of numeric attribute, doulbe is returned.
     * @return depends on the type of attribute. <BR>
     * -Fuzzy attr = doublevector -Num attr = double -ligvs attr = ? [need check
     * :P]
     */
    public abstract <T> T getOutputValue();

    /**
     *
     * @return values of instance in List.
     */
    public abstract List<Object> getValues();

    /**
     *
     * @param attr attribute which value will be returned
     * @return value of the attribute
     */
    public abstract Object getValueOfAttribute(Attribute attr);

    public abstract Object[] getInputData();

    /**
     *
     * @param attr
     * @return index of value for attribute specified by parameter
     */
    public abstract int getClassIndex(CategoricalAttr attr);

    /**
     *
     * @param attrIndex index of the attribute which value will be returned
     * @return value of the attribute
     */
    public abstract Object getValueOfAttribute(int attrIndex);

    /**
     *
     * @param val attrValue object for which value be returned.
     * @return value of the AttrValue
     */
    public abstract double getValueForAttrVal(AttrValue val);

    /**
     * Method set dataset which instance comes from.
     *
     * @param dataset dataset which will be set to the instance
     */
    public abstract void setDataset(DataSet dataset);

    /**
     * Returns an instance's attribute value.
     *
     * @param <T> type of returned value (eq. for NumericAttr: Double)
     * @param index index of returned value (agrees with attribute index)
     * @return an instance's attribute value at specified index.
     */
    public abstract <T> T getValue(int index);

    /**
     * Returns an instance's attribute value.
     *
     * @param attr attribute which value will be returned.
     * @return an instance's attribute value of specified numerical attribute.
     */
    public abstract double getValue(NumericAttr attr);

    /**
     * Returns an instance's attribute value.
     *
     * @param attr attribute which value will be returned.
     * @return an instance's attribute value of specified fuzzy attribute.
     */
    public abstract List<Double> getValue(FuzzyAttr attr);

    /**
     * Returns the value of attribute attr.
     *
     * @param attr attribute which value will be returned
     * @return Returns the value of specified linguistic attribute
     */
    public abstract String getValue(LinguisticAttr attr);

    /**
     * Method returns values in the List. The output attribute is ignored
     *
     * @return values in the List. The output attribute is ignored
     */
    public abstract List getFeaturesWithoutOutAttr();

    /**
     * Method returns values in the double array. The output attribute is
     * ignored
     *
     * @return values in the double array. The output attribute is ignored
     */
    public abstract double[] getInputNumericData();

    /**
     * attribute should be from dataset, where instance belong. It is not
     * mandatory, attribute is used only for find attribute by index;
     *
     * @param attr
     * @param classIndex
     */
    public abstract void setValue(LinguisticAttr attr, int classIndex);

    /**
     * Method returns values double array. All attributes must be numerical.
     *
     * @return values in the double array.
     */
    public abstract double[] getNumericData();

    /**
     * Method returns values in the List.
     *
     * @return values in the List.
     */
    public abstract List<Object> getFeatureVector();

    /**
     * Method returns values in the List. Each attrValue (term) of fuzzy
     * attribute is added separately.
     *
     * @return values in the List.
     */
    public abstract List<Double> getAttrValueVector();

    /**
     * Method returns values in the List. Each attrValue (term) of fuzzy
     * attribute is added separately.
     *
     * @return values in the List without output attribute.
     */
    public abstract List<Double> getAttrValueVectorWithoutOutputAttr();

    public abstract DataSet getDataset();

    public boolean hasMissingValues() {
        final List<Attribute> attributes = getDataset().getAttributes();
        for (int i = 0; i < attributes.size(); i++) {
            Attribute attr = attributes.get(i);
            if (attr.isNumeric()) {
                if (Double.isNaN(getValue(attr.numeric()))) {
                    return true;
                }
            } else if (attr.isLinguistic()) {
                if (getValue(attr.linguistic()).trim().equals("?")) {
                    return true;
                }
            } else if (attr.isFuzzy()) {
                if (getValue(attr.fuzzy()).contains(Double.NaN)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public abstract String toString();

    public abstract String toInstanceString();

    public static List<Instance> where(List<? extends Instance> instances, CategoricalAttr attr, int classIndex) {
        List<Instance> inst = new ArrayList<>();
        for (Instance instance : instances) {
            if (instance.getClassIndex(attr) == classIndex) {
                inst.add(instance);
            }
        }
        return inst;
    }

    public static List<DatasetInstance> wheredt(List<DatasetInstance> instances, CategoricalAttr attr, int classIndex) {
        List<DatasetInstance> inst = new ArrayList<>();
        for (DatasetInstance instance : instances) {
            if (instance.getClassIndex(attr) == classIndex) {
                inst.add(instance);
            }
        }
        return inst;
    }

    public static int countWithoutNaN(List<Instance> instances, NumericAttr attr) {
        if (!attr.hasMissingValues()) {
            return instances.size();
        }
        int totalCount = 0;
        for (Instance instance : instances) {
            if (Double.isNaN(instance.getValue(attr))) {
                totalCount++;
            }
        }
        return totalCount;
    }

    public static List<Instance> whereLessThan(List<Instance> instances, NumericAttr attr, double border) {
        List<Instance> inst = new ArrayList<>();
        for (Instance instance : instances) {
            if (instance.getValue(attr) < border) {
                inst.add(instance);
            }
        }
        return inst;
    }

    public static List<Instance>[] split(List<Instance> instances, NumericAttr attr, double splitPoint) {
        ArrayList<Instance> inst = new ArrayList<>();
        ArrayList<Instance> inst2 = new ArrayList<>();
        for (Instance instance : instances) {
            if (instance.getValue(attr) < splitPoint) {
                inst.add(instance);
            } else {
                inst2.add(instance);
            }
        }
        ArrayList<Instance>[] s = new ArrayList[2];
        s[0] = inst;
        s[1] = inst2;
        return s;
    }


    public static List<Instance> where(List<Instance> instances, Predicate<Instance> pred) {
        List<Instance> inst = new ArrayList<>();
        for (Instance instance : instances) {
            if (pred.test(instance)) {
                inst.add(instance);
            }
        }
        return inst;
    }

    /**
     * Labels are computed by class indices. Not suitable for Fuzzy Attributes
     *
     * @param instances
     * @param output
     * @return
     */
    public static List<Double> computeLabels(List<Instance> instances, CategoricalAttr output) {
        double[] arr = new double[output.getDomainSize()];
        if (instances.isEmpty()) {
            for (int i = 0; i < arr.length; i++) {
                arr[i] = 1d / output.getDomainSize();
            }
        } else {
            for (Instance instance : instances) {
                arr[instance.getClassIndex(output)]++;
            }
            for (int i = 0; i < arr.length; i++) {
                arr[i] /= instances.size();
            }
        }
        return new BoxedDoubleArray(arr);
    }

    /**
     *
     * @param instances
     * @param output
     * @param inputParamMax - Input parameter. The maximal value of confidence
     * level is saved into this parameter.
     * @return
     */
    public static List<Double> computeLabels(List<Instance> instances, CategoricalAttr output, Max inputParamMax) {
        double[] arr = new double[output.getDomainSize()];
        if (instances.isEmpty()) {
            for (int i = 0; i < arr.length; i++) {
                arr[i] = 1d / output.getDomainSize();
            }
        } else {
            for (Instance instance : instances) {
                arr[instance.getClassIndex(output)]++;
            }
            for (int i = 0; i < arr.length; i++) {
                arr[i] /= instances.size();
                inputParamMax.add(arr[i]);
            }
        }
        return new BoxedDoubleArray(arr);
    }

    public static double[] toArray(List<Instance> instances, NumericAttr attr) {
        double[] arr = new double[instances.size()];
        int i = 0;
        for (Instance instance : instances) {
            arr[i++] = instance.getValue(attr);
        }
        return arr;
    }

}
