/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.fuzzification_old.DatasetFuzzification;

import minig.data.core.attribute.AttrValue;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.attribute.NumericAttr;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;
import minig.data.fuzzification.fuzzification_old.FCMeanFuzzyificator;
import projectutils.ProjectUtils;
import projectutils.stat.MaxElement;
import projectutils.structures.Vector;

/**
 *
 * @author jrabc
 */
public class FC_Means_cH extends DatasetFuzzificator {

    private double fuzziness = 2;
    private Criterion criterion = Criterion.GainRatio;

    public enum Criterion {
        InformationGain,
        GainRatio;
    }

    public FC_Means_cH(DataSet inputDataset) {
        super(inputDataset);
    }

    public void setCriterion(Criterion criterion) {
        this.criterion = criterion;
    }

    public void setFuzziness(double fuzziness) {
        this.fuzziness = fuzziness;
    }

    @Override
    FuzzyAttr fuzzyfiyNumeric(NumericAttr attr) {

        int Q = getMinTermCount();
        int limit = getMaxTermCount();
        double entropyBefore;
        double entrohyLast = Double.MIN_VALUE;
        if (limit == Q) {
            setStrictTermCount(true);
        }
        MaxElement<FuzzyAttr> attrBest = new MaxElement<>();
        do {
            FCMeanFuzzyificator fcm = new FCMeanFuzzyificator(attr, Q++);
            fcm.setFuzziness(fuzziness);
            entropyBefore = entrohyLast;
            final FuzzyAttr fuzzyAttr = fcm.getFuzzyAttr(attr);
            if (!isStrictTermCount()) {
                entrohyLast = getEntropy(fcm);
            }
            attrBest.add(entrohyLast, fuzzyAttr);
            if (--limit == 0) {
                break;
            }
        } while (isFinish(entropyBefore, entrohyLast, limit--) == false);
        return attrBest.getElement();
    }

    private double getEntropy(FCMeanFuzzyificator fcm) {
        if (isStrictTermCount()) {
            return 0;
        }
        double entrohyLast;
        final double entropy = fcm.getEntropy();
        entrohyLast = (getEntropy(getOutputAttribute()) - fcm.getConditionalEntropy(getOutputAttribute()));
        if (criterion.equals(Criterion.GainRatio)) {
            entrohyLast /= entropy;
        }
        if (Double.isNaN(entrohyLast)) {
            return 0;
        }
        return entrohyLast;
    }

    public double getEntropy(FuzzyAttr outputAttr) {
        double entropy = 0;
        for (AttrValue attrValue : outputAttr.getDomain()) {
            Vector dv = attrValue.getValues();
            double x = dv.sum() / dv.size();
            entropy += -x * ProjectUtils.log2(x);
        }
        return entropy;
    }

    private boolean isFinish(double entropyBefore, double entrohyLast, int limit) {
        if (isStrictTermCount()) {
            return true;
        }
        return entropyBefore > entrohyLast;
    }

    public static void main(String[] args) {
        final DataSet dataset = DatasetFactory.getDataset(DatasetFactory.IRIS);
        FC_Means_cH dda = new FC_Means_cH(dataset);
        dda.setFuzziness(2);
        dda.setCriterion(Criterion.GainRatio);
        DataSet fa = dda.getFuzzyDataset();
        fa.print();

    }

}
