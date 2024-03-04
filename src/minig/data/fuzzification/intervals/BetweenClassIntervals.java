/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.intervals;

import projectutils.ErrorMessages;
import projectutils.stat.IncrementalWStat;
import projectutils.ProjectUtils;
import java.util.ArrayList;
import java.util.List;
import minig.data.core.attribute.AttrValue;
import minig.data.core.attribute.Attribute;
import minig.data.core.attribute.CategoricalAttr;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.attribute.LinguisticAttr;
import minig.data.core.attribute.NumericAttr;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;
import minig.data.fuzzification.LingvisticToFuzzy;

/**
 *
 * @author jrabc
 */
public class BetweenClassIntervals implements IntervalFinder {

    private NumericAttr input;
    private FuzzyAttr output;

    public BetweenClassIntervals(NumericAttr input, FuzzyAttr output) {
        this.input = input;
        this.output = output;
    }

    private void init(FuzzyAttr output) {
        this.output = output;
    }

    private void init(LinguisticAttr output) {
        LingvisticToFuzzy f = new LingvisticToFuzzy(output);
        this.output = f.toFuzzy();
    }

    public BetweenClassIntervals(Attribute output) {
        if (Attribute.isFuzzy(output)) {
            init(output.fuzzy());
        } else if (Attribute.isLingvistic(output)) {
            init(output.linguistic());
        } else {
            throw new Error(ErrorMessages.ATTR_MUST_BE_CATEGORICAL);
        }
    }

    public void setInputAttr(NumericAttr input) {
        this.input = input;
    }

    @Override
    public List<Double> getIntervals() {
        List<Double> intervals = new ArrayList<>(output.getDomainSize());
        int datacount = input.getDataCount();
        double min = input.getStat().getMin();
        double max = input.getStat().getMax();
        for (AttrValue attrValue : output.getDomain()) {
            IncrementalWStat iw = new IncrementalWStat();
            for (int i = 0; i < datacount; i++) {
                double w = attrValue.get(i);
                double x = input.get(i);
                x = (x - min) / (max - min);
                iw.add(w, x);
            }
            intervals.add(iw.getMean());
        }
        return intervals;
    }

    public static void main(String[] args) {
        DataSet dt = DatasetFactory.getDataset(0);
        dt.lingvisticToFuzzy();
        IntervalFinder iv = new BetweenClassIntervals((NumericAttr) dt.getAttribute(2), (FuzzyAttr) dt.getOutbputAttribute());
        System.out.println(iv.getIntervals());
    }

}
