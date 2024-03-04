/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.operators;

import projectutils.ErrorMessages;
import java.util.ArrayList;
import java.util.List;
import minig.data.core.attribute.AttrValue;
import minig.data.core.attribute.Attribute;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.NewInstance;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;
import projectutils.StopWatch;

/**
 *
 * @author jrabc
 */
public class ClassSpliter extends DataOperator {

    private List<DataSet> datasets;
    private ArrayList<Double> datasetWeights;
    private ArrayList<Double> classSizes;

    public ClassSpliter(DataSet dataset) {
        super(dataset);
          dataset.lingvisticToFuzzy();
        if (!dataset.isOutputAttributeSet()) {
            throw new Error(ErrorMessages.OUTPUT_ATTRIBUTE_MUST_BE_SET);
        }
        if (Attribute.isNumeric(dataset.getOutbputAttribute())) {
            throw new Error(ErrorMessages.OUTPUT_ATTRIBUTE_MUST_BE_LINGVISTIC);
        }
        FuzzyAttr a = (FuzzyAttr) dataset.getOutbputAttribute();
        datasets = new ArrayList<>(a.getDomainSize());
        datasetWeights = new ArrayList<>(a.getDomainSize());
        classSizes = new ArrayList<>(a.getDomainSize());
        for (AttrValue val : ((FuzzyAttr) getDataset().getOutbputAttribute()).getDomain()) {
            datasetWeights.add(val.getStat().getSum() / getDataset().getDataCount());
            classSizes.add(val.getStat().getSum());
        }
        split();
    }

    public ArrayList<Double> getClassSizes() {
        return classSizes;
    }

    public ArrayList<Double> getDatasetWeights() {
        return datasetWeights;
    }

    private final void split() {
        FuzzyAttr output = (FuzzyAttr) getDataset().getOutbputAttribute();
        for (AttrValue clazz : output.getDomain()) {
            DataSet dt = getDataset().getEmptyCopy();
            for (int i = 0; i < clazz.getDataCount(); i++) {
                double c = clazz.get(i);
                if (c != 0) {
                    NewInstance ins = getDataset().getInstance(i);
                    //ins.mulValues(c);
                    dt.addInstance(ins);
                }
            }
            datasets.add(dt);
        }
    }

    public List<DataSet> getDatasets() {
        return datasets;
    }
    
    public static void main(String[] args) {
        DataSet dt = DatasetFactory.getDataset(0);
        ClassSpliter cs = new ClassSpliter(dt.getFuzzyDataset());
        StopWatch sw = new StopWatch();
        cs.split();
        System.out.println(sw.getCurrentTime());
        System.out.println(cs.getDatasets().get(1));
    }

}
