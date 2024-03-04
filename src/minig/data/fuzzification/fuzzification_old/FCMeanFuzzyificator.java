/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.fuzzification_old;

import java.util.List;
import minig.data.fuzzification.fuzzification_old.clusteringOneDimensional.FCM.FCM;
import minig.data.fuzzification.fuzzification_old.clusteringOneDimensional.FCM.FCMmerging;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.attribute.NumericAttr;
import minig.data.fuzzification.fuzzification_old.clusteringOneDimensional.FCM.FCluster;
import projectutils.ProjectUtils;
import projectutils.structures.DoubleVector;

/**
 *
 * @author jrabc
 */
public class FCMeanFuzzyificator implements Fuzzyficator {

    private FCM fuzzyCMean;
    private List<Double> values;
    private int termsCount;
    private double fuzzyness = 2;
    private int maxRuns = 60000;

    public FCMeanFuzzyificator(List<Double> values, int termsCount) {
        this.values = values;
        this.termsCount = termsCount;
    }

    public FCMeanFuzzyificator(List<Double> values) {
        this.values = values;
    }

    public FCMeanFuzzyificator(NumericAttr attr) {
        this.values = attr.getValues();
    }

    public FCMeanFuzzyificator(NumericAttr attr, int termsCount) {
        this.values = attr.getValues();
        this.termsCount = termsCount;
    }

    public void setFuzziness(double m) {
        fuzzyness = m;
    }

    public void setMaxRuns(int maxRuns) {
        this.maxRuns = maxRuns;
    }

    @Override
    public FuzzyAttr getFuzzyAttr(NumericAttr attr) {
        this.fuzzyCMean = new FCM(values, termsCount);
        fuzzyCMean.setM(fuzzyness);
        fuzzyCMean.setMaxIteration(maxRuns);
        fuzzyCMean.clustering();
        FuzzyAttr fattr = Fuzzyficator.super.getEmptyFuzzyAttr(attr);
        for (int i = 0; i < values.size(); i++) {
            fattr.addFuzzyRow(fuzzyCMean.getPartitions(i));
        }
        for (int i = 0; i < fuzzyCMean.getCenters().size(); i++) {
            Double c = fuzzyCMean.getCenters().get(i);
            fattr.getAttrValue(i).setName(ProjectUtils.formatDouble(c));
        }
        return fattr;
    }

    @Override
    public int getTermsCount() {
        return fuzzyCMean.getClusterCount();
    }

    public double getConditionalEntropy(FuzzyAttr outputAttribute) {
        return fuzzyCMean.getEntopy(outputAttribute);
    }

    /**
     *
     * @return entropy of the fuzzified input attribute (obtained by partition
     * matrix)
     */
    public double getEntropy() {
        double entropy = 0;
        for (FCluster cluster : fuzzyCMean.getClusters()) {
            final double x = ((DoubleVector) cluster.getPartitions()).sum() / values.size();
            entropy += -x * ProjectUtils.log2(x);
        }
        return entropy;
    }

    @Override
    //TODO
    public List<Double> getFuzzyTerm(double number) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
