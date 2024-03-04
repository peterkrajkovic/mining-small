
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.fuzzification_old.DatasetFuzzification;

import java.util.ArrayList;
import java.util.List;
import minig.data.core.attribute.Attribute;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.attribute.LinguisticAttr;
import minig.data.core.attribute.NumericAttr;
import minig.data.core.dataset.DataSet;
import minig.data.fuzzification.LingvisticToFuzzy;
import minig.data.fuzzification.toFuzzzy.NumericToFuzzyKmeans;

/**
 *
 * @author jrabc
 */
public abstract class DatasetFuzzificator {

    private DataSet inputDataset;
    private Attribute outputAttribute;

    private int outputTerms = 2;

    private int maxTermCount = 0;
    private boolean strictTermCount;
    private int minTermCount = 2;

    DatasetFuzzificator() {
    }

    DataSet getInputDataset() {
        return inputDataset;
    }

    public DatasetFuzzificator(DataSet inputDataset) {
        this.inputDataset = inputDataset;
    }

    private void doWork(DataSet inputDataset1) {
        outputAttribute = inputDataset1.getOutbputAttribute();
        if (outputTerms > 0 && outputAttribute.getType() == Attribute.NUMERIC) {
            NumericToFuzzyKmeans n = new NumericToFuzzyKmeans();
            n.setFuzzifiedAttr(outputAttribute);
            outputAttribute = n.toFuzzy(2);
        } else if (outputAttribute.getType() == Attribute.NUMERIC) {
            KMeans_withoutOA_wH.getAttr((NumericAttr) outputAttribute, getMaxTermCount());
        }
    }

    public void setMaxTermCount(int maxTermCount) {
        if (getMinTermCount() > getMaxTermCount() || maxTermCount < 2) {
            throw new Error("maxTermCount must be bigger then 2 and minTermCount");
        }
        this.maxTermCount = maxTermCount;
    }

    public int getMinTermCount() {
        return minTermCount;
    }

    public void setMinTermCount(int minTermCount) {
        this.minTermCount = minTermCount;
    }

    /**
     * method fired only if the output attribute is numeric. It sets strict
     * number of domain size of the output attribute after fuzzification
     *
     * @param count if negative, automatic selection based on entropy is used
     */
    public void setOutputAttributeTerms(int count) {
        outputTerms = count;
    }

    public boolean isStrictTermCount() {
        return strictTermCount;
    }

    /**
     * TODO prerobit na nastaveneie cez max/min term count
     *
     * @param strictTermCount
     */
    public void setStrictTermCount(boolean strictTermCount) {
        this.strictTermCount = strictTermCount;
    }

    protected final int getMaxTermCount() {
        return maxTermCount == 0 ? 25 : maxTermCount;
    }

    private FuzzyAttr getFuzzyOutputAttribute(int outputAttrTerms) {
        switch (this.outputAttribute.getType()) {
            case Attribute.FUZZY:
                return (FuzzyAttr) outputAttribute;
            case Attribute.LINGUISTIC: {
                LingvisticToFuzzy convertor = new LingvisticToFuzzy((LinguisticAttr) outputAttribute);
                return convertor.toFuzzy();
            }
            case Attribute.NUMERIC: {
                NumericToFuzzyKmeans convertor = new NumericToFuzzyKmeans((NumericAttr) outputAttribute);
                return convertor.toFuzzy();
            }
            default:
                break;
        }
        return null;
    }

    private DataSet internalTransformation(int outputAttrTerms) {
        List<Attribute> fattrs = new ArrayList<>(inputDataset.getAtributteCount());

        FuzzyAttr output = getFuzzyOutputAttribute(outputAttrTerms);
        outputAttribute = output;
        output.setOutputAttr(true);
        for (int i = 0; i < inputDataset.getAtributteCount(); i++) {
            Attribute attr = inputDataset.getAttribute(i);
            switch (attr.getType()) {
                case Attribute.FUZZY:
                    if (!attr.isOutputAttr()) {
                        fattrs.add((FuzzyAttr) attr);
                    }
                    break;
                case Attribute.LINGUISTIC:
                    if (!attr.isOutputAttr()) {
                        LingvisticToFuzzy convertor = new LingvisticToFuzzy((LinguisticAttr) attr);
                        fattrs.add(convertor.toFuzzy());
                    }
                    break;
                case Attribute.NUMERIC:
                    if (!attr.isOutputAttr()) {
                        fattrs.add(fuzzyfiyNumeric((NumericAttr) attr));
                    }
                    break;
                default:
                    break;
            }
        }
        fattrs.add(output);
        DataSet fd = new DataSet();
        fd.setName(inputDataset.getName());
        fd.addAttributes(fattrs);
        fd.setOutputAttrIndex(fattrs.indexOf(output));
        //  fd.setDataCount(inputDataset.getDataCount());
        fd.initDatasetInstances();
        return fd;
    }

    public DataSet getFuzzyDataset() {
        doWork(inputDataset);
        return internalTransformation(outputTerms);
    }

    public DataSet getFuzzyDataset(int outputTerms) {
        doWork(inputDataset);
        return internalTransformation(outputTerms);
    }

    public FuzzyAttr getOutputAttribute() {
        if (outputAttribute.getType() != Attribute.FUZZY) {
            outputAttribute = getFuzzyOutputAttribute(3);
        }
        return (FuzzyAttr) outputAttribute;
    }

    public void setOutputAttribute(Attribute outputAttribute) {
        this.outputAttribute = outputAttribute;
    }

    abstract FuzzyAttr fuzzyfiyNumeric(NumericAttr attr);

}
