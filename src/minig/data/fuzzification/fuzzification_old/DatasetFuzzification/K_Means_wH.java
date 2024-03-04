/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.fuzzification_old.DatasetFuzzification;

import java.util.ArrayList;
import java.util.List;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.attribute.NumericAttr;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;
import minig.data.fuzzification.fuzzification_old.Fuzzyficator;
import minig.data.fuzzification.fuzzification_old.TriangularFuzzificator;
import minig.data.fuzzification.fuzzification_old.clusteringOneDimensional.CM_entropy.KMeans;
import projectutils.ProjectUtils;

/**
 * iris 2 2 5 3
 * @author jrabc
 */
public class K_Means_wH extends DatasetFuzzificator {

    public K_Means_wH(DataSet inputDataset) {
        super(inputDataset);
    }

    @Override
    public FuzzyAttr fuzzyfiyNumeric(NumericAttr attr) {
        int Q = getMinTermCount();
        int limit = getMaxTermCount()-1;
        double entropyBefore;
        double entrohyLast = Double.MAX_VALUE;
        ArrayList<FuzzyAttr> attributes = new ArrayList<>();
        do {
            KMeans cls = new KMeans(attr.getValues(), Q++, getOutputAttribute());
            entropyBefore = entrohyLast;
            List clusters = cls.clustering();
            Fuzzyficator tf = new TriangularFuzzificator(clusters);
            NumericAttr a = new NumericAttr(attr.getName(), ProjectUtils.formFrom0To1(attr.getValues(), true));
            attributes.add(tf.getFuzzyAttr(a));
            entrohyLast = getEntropy(cls, attributes);
            // System.err.println(entrohyLast);
        } while (isFinish(entropyBefore, entrohyLast, limit--));
        return attributes.get(attributes.size() - 2);
    }

    private double getEntropy(KMeans cls, List<FuzzyAttr> attributes) {
        double entrohyLast;
        entrohyLast = cls.getWeightedEntropy(attributes.get(attributes.size()-1));
        //         System.out.println(entrohyLast);
        return entrohyLast;
    }

    private boolean isFinish(double entropyBefore, double entrohyLast, int limit) {
        if (isStrictTermCount()) {
            return !(limit <= 1);
        }
        return entropyBefore > entrohyLast && limit > 1;
    }

    public static void main(String[] args) {
//        DatasetFuzzificator dda = new K_Means_wH(DatasetFactory.getDataset(DatasetFactory.IRIS));
//        dda.setMaxTermCount(4);
//        dda.setStrictTermCount(true);
//        dda.setMinTermCount(3);
//        FuzzyDataset fa = dda.getFuzzyDataset();
//        System.out.println(fa.toString());
//        FDToCMI a = new FDToCMI(fa);
//        a.buildTree();
        DataSet fd = DatasetFactory.getDataset(DatasetFactory.IRIS);
        System.out.println(fd.getFuzzyDataset(2, 6));
    }

}
