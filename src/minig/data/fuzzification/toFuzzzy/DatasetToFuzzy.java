/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.toFuzzzy;

import minig.data.fuzzification.NumericToFuzzy;
import minig.data.fuzzification.LingvisticToFuzzy;
import minig.classification.fdt.FuzzyDecisionTree;
import minig.classification.fdt.FDTu;
import minig.data.core.attribute.Attribute;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.attribute.LinguisticAttr;
import minig.data.core.attribute.NumericAttr;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;
import minig.data.fuzzification.functionsset.Gaussian;
import minig.data.fuzzification.functionsset.Hard;
import minig.data.fuzzification.functionsset.ZShaped;
import minig.data.fuzzification.intervals.BetweenClassIntervals;

/**
 *
 * @author jrabc
 */
public class DatasetToFuzzy {

    private NumericToFuzzy numericToFuzzy;
    private DataSet dt;

    public DatasetToFuzzy(NumericToFuzzy numericToFuzzy, DataSet dt) {
        this.numericToFuzzy = numericToFuzzy;
        this.dt = dt;
    }

    public DataSet getDataset() {
        DataSet dataset = new DataSet();
        dataset.setName(dt.getName());
        for (int i = 0; i < dt.getAtributteCount(); i++) {
            Attribute attribute = dt.getAttribute(i);
            if (Attribute.isLingvistic(attribute)) {
                LingvisticToFuzzy ltf = new LingvisticToFuzzy((LinguisticAttr) attribute);
                FuzzyAttr attr = ltf.toFuzzy();
                dataset.addAttribute(attr);
            } else if (Attribute.isNumeric(attribute)) {
                numericToFuzzy.setFuzzifiedAttr((NumericAttr) attribute);
                FuzzyAttr attr = numericToFuzzy.toFuzzy();
                dataset.addAttribute(attr);
            } else {
                dataset.addAttribute(attribute);
            }
        }
        if (dt.isOutputAttributeSet()) {
            dataset.setOutputAttrIndex(dt.getOutputAttrIndex());
        }
        if (dataset.isEmpty()) {
            dataset.initDatasetInstances();
        }
        return dataset;
    }


}
