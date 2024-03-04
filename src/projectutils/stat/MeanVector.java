/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils.stat;

import java.util.Arrays;
import projectutils.structures.DoubleVector;
import java.util.List;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.dataset.NumericAttrRowIndexer;
import projectutils.ConsolePrintable;

/**
 *
 * @author Jan Rabcan {jrabcanj@gmail.com}
 */
public class MeanVector implements ConsolePrintable{

    private double[] sums;
    private int count;

    public MeanVector(int count) {
        sums = new double[count];
    }

    public void add(double... values) {
        for (int i = 0; i < values.length; i++) {
            sums[i] += values[i];
        }
        count++;
    }

    public void add(List<Double> values) {
        int i = 0;
        for (Double value : values) {
            sums[i++] += value;
        }
        count++;
    }

    public void add(FuzzyAttr.BoxedRow row) {
        for (int i = 0; i < row.size(); i++) {
            sums[i] += row.get(i);
        }
        count++;
    }

    public void add(List<Double> values, double w) {
        int i = 0;
        for (Double value : values) {
            sums[i++] += w * value;
        }
        count += w;
    }

    public void add(double[] values, double w) {
        int i = 0;
        for (Double value : values) {
            sums[i++] += w * value;
        }
        count += w;
    }

    public void addRowFromNumericIndexer(NumericAttrRowIndexer indexer) {
        int i = 0;
        for (int j = 0; j < indexer.rowSize(); j++) {
            sums[i++] += indexer.get(j);
        }
        count++;
    }

    public int getCount() {
        return count;
    }

    public double[] getMeans() {
        double[] m = new double[sums.length];
        for (int i = 0; i < m.length; i++) {
            m[i] = sums[i] / count;
        }
        return m;
    }

    public double[] getSums() {
        return sums;
    }

    public void reset() {
        sums = new double[sums.length];
        count = 0;
    }

    @Override
    public String toString() {
        return "sums=" + Arrays.toString(sums) + System.lineSeparator() + "count=" + count + System.lineSeparator() + "Means" + Arrays.toString(getMeans());
    }

}
