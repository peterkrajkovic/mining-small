/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.toFuzzzy;

import java.util.List;
import minig.classification.fdt.FDTu;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.attribute.NumericAttr;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;
import minig.data.fuzzification.Fuzzification;
import minig.data.fuzzification.NumericToFuzzy;
import minig.data.fuzzification.intervals.BetweenClassIntervals;
import projectutils.ProjectUtils;

/**
 *
 * @author jrabc
 */
public class NumericToFuzzyMemberships implements NumericToFuzzy {

    private NumericAttr input;
    private FuzzyAttr output;
    private BetweenClassIntervals intervals;
    private double fuzziness = 2;

    public NumericToFuzzyMemberships(NumericAttr input, FuzzyAttr output) {
        this.input = input;
        this.output = output;
    }

    public double getFuzziness() {
        return fuzziness;
    }

    public void setFuzziness(double fuzziness) {
        this.fuzziness = fuzziness;
    }

    public NumericToFuzzyMemberships(FuzzyAttr output) {
        this.output = output;
    }

    @Override
    public void setFuzzifiedAttr(NumericAttr attr) {
        this.input = attr;
    }

    public FuzzyAttr toFuzzy() {
        FuzzyAttr attr = new FuzzyAttr(input.getName());

        intervals = new BetweenClassIntervals(input, output);
        List<Double> ints = intervals.getIntervals();
        for (int i = 0; i < output.getDomainSize(); i++) {
            attr.addValue(ProjectUtils.formatDouble(ints.get(i)));
        }
        if (input.hasMissingValues()) {
            attr.addValue("NaN");
        }
        int dataCount = input.getDataCount();
        double min = input.getStat().getMin();
        double max = input.getStat().getMax();
        double sum;
        for (int i = 0; i < dataCount; i++) {
            double x = input.getRow(i);
            if (Double.isNaN(x)) {
                int j;
                for (j = 0; j < ints.size(); j++) {
                    attr.getAttrValue(j).addVaule(0d);
                }
                attr.getAttrValue(j ).addVaule(1d);
            }
            x = (x - min) / (max - min);
            sum = 0;
            int j;
            for (j = 0; j < ints.size(); j++) {
                double c = ints.get(j);
                double dist = 1 / Math.pow(Math.abs(x - c), fuzziness);
                sum = sum + dist;
                attr.getAttrValue(j).addVaule(dist);
            }
            for (j = 0; j < ints.size(); j++) {
                double u = attr.getAttrValue(j).get(i);
                attr.getAttrValue(j).setValueAt(i, u / sum);
            }
            if (input.hasMissingValues()) {
                attr.getAttrValue(j ).addVaule(0d);
            }
        }
        return attr;
    }

    public static void main(String[] args) {
        DataSet dt = DatasetFactory.getDataset(0);
        dt.lingvisticToFuzzy();
        NumericAttr attr = (NumericAttr) dt.getAttribute(2);
        NumericToFuzzyMemberships nf = new NumericToFuzzyMemberships(attr, (FuzzyAttr) dt.getOutbputAttribute());
        nf.setFuzziness(3);
        FDTu dta = new FDTu();
        dta.setDataset(Fuzzification.byMembershipToClasses(dt));
        dta.buildModel();
        dta.print();

        ProjectUtils.toClipboard(dta.getGraphvizCode());
    }

}
