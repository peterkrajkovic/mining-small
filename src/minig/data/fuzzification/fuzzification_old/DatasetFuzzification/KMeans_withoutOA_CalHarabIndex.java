/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.fuzzification_old.DatasetFuzzification;

import minig.data.fuzzification.fuzzification_old.TriangularFuzzificator;
import java.util.LinkedList;
import java.util.List;
import projectutils.AttrUtils;
import minig.classification.fdt.FDTo;
import minig.data.fuzzification.fuzzification_old.clusteringOneDimensional.CM.KMeansOneDimension;
import minig.data.fuzzification.fuzzification_old.clusteringOneDimensional.CM_entropy.KMeans;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.attribute.NumericAttr;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;

/**
 *
 * @author jrabc
 */
public class KMeans_withoutOA_CalHarabIndex extends DatasetFuzzificator {

    public KMeans_withoutOA_CalHarabIndex(DataSet inputDataset) {
        super(inputDataset);
    }


    @Override
    public FuzzyAttr fuzzyfiyNumeric(NumericAttr attr) {
        int Q = getMinTermCount();
        int limit = getMaxTermCount();
        double entropyBefore;
        double entrohyLast = Double.MIN_VALUE;
        LinkedList<FuzzyAttr> attributes = new LinkedList<>();
        do {
            KMeans cls = new KMeans(attr.getValues(), Q++, getOutputAttribute());
            entropyBefore = entrohyLast;
            List clusters = cls.clustering();
            TriangularFuzzificator tf = new TriangularFuzzificator(clusters);
            attributes.add(tf.getFuzzyAttr(attr));
            entrohyLast = cls.getIndexCalinskiHarabasz();
        } while (isFinish(entropyBefore, entrohyLast, limit--));
        return attributes.get(attributes.size() - 2);
    }

    private boolean isFinish(double entropyBefore, double entrohyLast, int limit) {
        if (isStrictTermCount()) {
            return !(limit <= 1);
        }
        return entropyBefore < entrohyLast && limit > 1;
    }

    public static FuzzyAttr getAttr(NumericAttr attr, int limit) {
        int Q = 2;
        double entropyBefore;
        double entrohyLast = Double.MAX_VALUE;
        LinkedList<FuzzyAttr> attributes = new LinkedList<>();
        do {
            KMeansOneDimension cls = new KMeansOneDimension(attr.getValues(), Q++);
            entropyBefore = entrohyLast;
            List clusters = cls.clustering();
            TriangularFuzzificator tf = new TriangularFuzzificator(clusters);
            attributes.add(tf.getFuzzyAttr(attr));
            entrohyLast = AttrUtils.experimentEntropy(attributes.getLast());
            System.out.println(entrohyLast);
        } while (entropyBefore > entrohyLast && limit > 1);
        return attributes.get(attributes.size() - 2);
    }

    public static void main(String[] args) {
        DatasetFuzzificator dda = new KMeans_withoutOA_CalHarabIndex(DatasetFactory.getDataset(DatasetFactory.IRIS));
        //dda.setMaxTermCount(100);
       // dda.setStrictTermCount(true);
        DataSet fa = dda.getFuzzyDataset(3);
        System.out.println(fa.toString());
        FDTo a = new FDTo(fa);
        a.buildModel();

    }
}
