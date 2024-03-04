/*
iris 2 2 5 3
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.toFuzzzy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import minig.data.core.attribute.AttrValue;
import minig.data.core.attribute.Attribute;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.attribute.NumericAttr;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;
import minig.data.fuzzification.Fuzzification;
import minig.data.fuzzification.LingvisticToFuzzy;
import minig.data.fuzzification.NumericToFuzzy;
import minig.data.fuzzification.functionsset.FuzzyMapper;
import minig.data.fuzzification.functionsset.Triangular;
import minig.data.fuzzification.fuzzification_old.clusteringOneDimensional.CM.Cluster;
import static minig.data.fuzzification.fuzzification_old.clusteringOneDimensional.CM.KMeansOneDimension.continueAfterCenterValidation;
import projectutils.ProjectUtils;
import projectutils.stat.MinElement;
import projectutils.structures.BoxedDoubleArray;
import projectutils.structures.DoubleVector;

/**
 *
 * @author jrabc
 */
public class NumericToFuzzyKmeans implements NumericToFuzzy {

    private NumericAttr inputAttrtibute;
    private FuzzyAttr outputAttribute;
    private double[] values = null;
    private int maxTermCount = 30;
    private int minTermCount = 2;
    private FuzzyMapper fuzzyMapper = new Triangular();
    private int maxRuns = 1000;

    private boolean missingValues = false;

    public NumericToFuzzyKmeans() {
    }

    public NumericToFuzzyKmeans(Attribute outputAttribute) {
        if (outputAttribute.isFuzzy()) {
            this.outputAttribute = (FuzzyAttr) outputAttribute;
        } else if (outputAttribute.isLinguistic()) {
            LingvisticToFuzzy lf = new LingvisticToFuzzy(outputAttribute.linguistic());
            this.outputAttribute = lf.toFuzzy();
        }
    }

    public NumericToFuzzyKmeans(Attribute attribute, Attribute outputAttribute) {
        this.outputAttribute = (FuzzyAttr) outputAttribute;
        this.inputAttrtibute = attribute.numeric();
    }

    public NumericToFuzzyKmeans(NumericAttr attribute, FuzzyAttr outputAttribute) {
        this.outputAttribute = outputAttribute;
        this.inputAttrtibute = attribute;
    }

    public void setFuzzyMapper(FuzzyMapper fuzzyMapper) {
        this.fuzzyMapper = fuzzyMapper;
    }

    public int getMaxRuns() {
        return maxRuns;
    }

    public void setMaxRuns(int maxRuns) {
        this.maxRuns = maxRuns;
    }

    private void init(NumericAttr attribute) {
        this.inputAttrtibute = attribute;
        double max = attribute.getStat().getMax();
        double min = attribute.getStat().getMin();
        values = new double[attribute.getDataCount()];
        for (int i = 0; i < attribute.getDataCount(); i++) {
            double x = attribute.getValues().get(i);
            if (Double.isNaN(x)) {
                missingValues = true;
            } else {
                x = (x - min) / (max - min);
            }
            values[i] = x;
        }
        // Arrays.sort(values);
    }

    public int getMaxTermCount() {
        return maxTermCount;
    }

    public void setOutputAttribute(FuzzyAttr outputAttribute) {
        this.outputAttribute = outputAttribute;
    }

    public void setOutputAttribute(Attribute outputAttribute) {
        this.outputAttribute = (FuzzyAttr) outputAttribute;
    }

    public void setMaxTermCount(int maxTermCount) {
        this.maxTermCount = maxTermCount;
    }

    public NumericToFuzzyKmeans(NumericAttr attribute, FuzzyAttr outputAttribute, int maxTermsCount) {
        init(attribute);
        this.outputAttribute = outputAttribute;
    }

    public int getMinTermCount() {
        return minTermCount;
    }

    public void setMinTermCount(int minTermCount) {
        this.minTermCount = minTermCount;
    }

    @Override
    public FuzzyAttr toFuzzy() {
        init(inputAttrtibute);
        int Q = fuzzyMapper.getTermsCount(minTermCount) - 1;
        if (minTermCount > maxTermCount) {
            throw new Error("Min terms count must be bigger than max terms count");
        } else if (minTermCount == maxTermCount) {
            return transformationWithIndexes(Q);
        }
        double entropyBefore;
        double entrohyLast = Double.MAX_VALUE;
        MinElement<FuzzyAttr> e = new MinElement();

        while (Q < maxTermCount) {
            intervals.clear();
            entropyBefore = entrohyLast;
            FuzzyAttr attr = transformationWithIndexes(Q++);
            entrohyLast = Cluster.getWeightedEntrophy(intervals, attr, outputAttribute);

            e.add(entrohyLast, attr);
            if (entropyBefore < entrohyLast) {
                break;
            }
        }
        return e.getElement();
    }

    public FuzzyAttr toFuzzy(int termcount) {
        return transformationWithIndexes(termcount);
    }

    private void initClusters(int Q, List<Cluster> intervals) {
//        int step = values.size() /  Q;
//        if (step <= 1) {
//            step = 2;
//        }
//        int insIndex = 0;
//        for (int i = 0; i <  Q; i++) {
//            intervals.add(new Cluster(values.get(insIndex)));
//            insIndex += step;
//        }

        for (int i = 1; i <= Q; i++) {  //musi ist od 1, pre vypocet centra podla vzorca
            intervals.add(new Cluster(i, Q, null));
            // intervals.add(new Interval(values.get(i)));
        }
    }

    /**
     * uklada aj netransfromovane hodnoty do intervalov. Tie treba ak chceme
     * pocitat entropiu v dalsich algoritmoch
     *
     * @param Q pocet intervalov = pocet hodnot fuzzy atributu
     * @return
     */
    private List<Cluster> intervals = new ArrayList<>();

    private FuzzyAttr transformationWithIndexes(int Q) {
        intervals = new ArrayList<>(Q);
        //vytvorenie intervalov + vyber centier

        initClusters(Q, intervals);
        int limit = 0;

        do {
            limit++;

            intervals.forEach(interval -> interval.clearData());
            //rozhodim values do intervalov
            for (int i = 0; i < values.length; i++) {
                double x = values[i];
                Cluster.addNumberToCorrectInterval(intervals, x, i);
            }
        } while (continueAfterCenterValidation(intervals) && limit < maxRuns);//zmena centier, a ak bola zmena tak da false
        Collections.sort(intervals);
        FuzzyAttr a = new FuzzyAttr(inputAttrtibute.getName());
        DoubleVector ints = DoubleVector.zeros(Q);
        fuzzyMapper.setIntervals(ints);

        for (int i = 0; i < intervals.size(); i++) {
            Cluster interval = intervals.get(i);
            ints.set(i, interval.getCenter());
        }
        fuzzyMapper.setIntervals(ints);
        int n = fuzzyMapper.getTermsCount();
        if (fuzzyMapper.getIntervals().size() == fuzzyMapper.getTermsCount()) {
            for (int i = 0; i < n; i++) {
                a.addValue(ProjectUtils.formatDouble(denormalize(fuzzyMapper.getIntervals().get(i))));
            }
        } else {
            for (int i = 0; i < n; i++) {
                a.addValue(fuzzyMapper.getName(i));
            }
        }
        if (missingValues) {
            a.addValue("NaN");
        }
        final AttrValue attrValue = inputAttrtibute.getAttrValue();
        double min = attrValue.getStat().getMin();
        double max = attrValue.getStat().getMax();

        for (double x : attrValue.getValues()) {
            x = (x - min) / (max - min);
            if (Double.isNaN(x)) {
                a.addFuzzyTerms(getNaNRow(fuzzyMapper.getTermsCount() + 1));
            } else {
                a.addFuzzyTerms(fuzzyMapper.getFuzzyTerm(x));
            }
        }

        return a;
    }

    private List<Double> getNaNRow(int count) {
        BoxedDoubleArray arr = new BoxedDoubleArray(count);
        arr.set(count - 1, 1d);
        return arr;
    }

    @Override
    public void setFuzzifiedAttr(NumericAttr attr) {
        this.inputAttrtibute = attr;
    }

    public void setFuzzifiedAttr(Attribute attr) {
        init((NumericAttr) attr);
    }

    private double denormalize(double x) {
        return (x * (inputAttrtibute.getStat().getMax() - inputAttrtibute.getStat().getMin())) + inputAttrtibute.getStat().getMin();
    }

    public static void main(String[] args) {

        DataSet dt = new DatasetFactory().createDataset(0);
        dt.lingvisticToFuzzy();
        NumericToFuzzyKmeans nmk = new NumericToFuzzyKmeans(dt.getOutbputAttribute());
        nmk.setFuzzyMapper(new Triangular());

        nmk.setFuzzifiedAttr(dt.getAttribute(0));
        dt.getAttribute(0).addRow(Double.NaN);
        dt.getOutbputAttribute().fuzzy().addFuzzyRow(0, 1, 0);
        System.out.println(dt.getAttribute(0).getDataCount());
        nmk.toFuzzy().print();

        dt = new DatasetFactory().createDataset(0);
        dt.lingvisticToFuzzy();
        Fuzzification.byKMeansWH(dt, new Triangular()).print(); // 2 2 5 3 -> 3
    }

}
