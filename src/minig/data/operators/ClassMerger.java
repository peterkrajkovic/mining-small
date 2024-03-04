/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.operators;

import projectutils.stat.IncrementalStat;
import java.util.Arrays;
import java.util.List;
import minig.data.core.attribute.AttrValue;
import minig.data.core.attribute.Attribute;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;

/**
 *
 * @author jrabc
 */
public class ClassMerger {

    private FuzzyAttr attribute;
    private MergeFunction mergeFunction = MergeFunction.max;

    public enum MergeFunction {
        max, min, sum, avg;
    }

    public ClassMerger(FuzzyAttr attribute) {
        this.attribute = attribute;
    }

    public ClassMerger(Attribute attribute) {
        if (Attribute.isFuzzy(attribute)) {
            this.attribute = (FuzzyAttr) attribute;
        } else {
            throw new Error(projectutils.ErrorMessages.ATTR_MUST_BE_FUZZY);
        }

    }

    public ClassMerger() {
    }

    public FuzzyAttr getAttribute() {
        return attribute;
    }

    public void setAttribute(FuzzyAttr attribute) {
        this.attribute = attribute;
    }

    public void setMergeFunction(MergeFunction mergeFunction) {
        this.mergeFunction = mergeFunction;
    }

    public FuzzyAttr merge(String name, int... indices) {
        AttrValue val;
        switch (mergeFunction) {
            case max:
            default:
                val = mergeMax(indices);
                break;
            case avg:
                val = mergeAvg(indices);
                break;
            case min:
                val = mergeMin(indices);
                break;
            case sum:
                val = mergeSum(indices);
                break;
        }
        val.setName(name);
        Arrays.sort(indices);
        for (int i = indices.length - 1; i >= 0; i--) {
            int index = indices[i];
            attribute.removeValue(index);
        }
        attribute.addValue(val);
        return attribute;
    }

    public FuzzyAttr merge() {
        String name = "";
        int[] indices = new int[attribute.getDomainSize()];
        for (int i = 0; i < attribute.getDomainSize(); i++) {
            indices[i] = i;
        }
        for (int i : indices) {
            name += attribute.getAttrValue(i).getName() + " ";
        }
        return this.merge(name, indices);
    }

    public FuzzyAttr merge(int... indices) {
        String name = "";
        for (int i : indices) {
            name += attribute.getAttrValue(i).getName() + " ";
        }
        return this.merge(name, indices);
    }

    private AttrValue mergeSum(int... indices) {
        AttrValue vala = new AttrValue();
        for (int i = 0; i < attribute.getDataCount(); i++) {
            double u = 0;
            for (int indice : indices) {
                u += attribute.getAttrValue(indice).get(i);
            }
            vala.addVaule(u);
        }
        return vala;
    }

    private AttrValue mergeMax(int... indices) {
        AttrValue val = new AttrValue();
        for (int i = 0; i < attribute.getDataCount(); i++) {
            double u = Double.NEGATIVE_INFINITY;
            for (int indice : indices) {
                double d = attribute.getAttrValue(indice).get(i);
                if (d > u) {
                    u = attribute.getAttrValue(indice).get(i);
                }
            }
            val.addVaule(u);
        }
        return val;
    }

    private AttrValue mergeMin(int... indices) {
        AttrValue val = new AttrValue();
        for (int i = 0; i < attribute.getDataCount(); i++) {
            double u = Double.MAX_VALUE;
            for (int indice : indices) {
                double d = attribute.getAttrValue(indice).get(i);
                if (d < u) {
                    u = attribute.getAttrValue(indice).get(i);
                }
            }
            val.addVaule(u);
        }
        return val;
    }

    private AttrValue mergeAvg(int... indices) {
        AttrValue val = new AttrValue();
        IncrementalStat iw = new IncrementalStat();
        for (int i = 0; i < attribute.getDataCount(); i++) {
            iw.reset();
            for (int indice : indices) {
                double u = attribute.getAttrValue(indice).get(i);
                iw.add(u);
            }
            val.addVaule(iw.getMean());
        }
        return val;
    }

    public static void main(String[] args) {
        DataSet dt = DatasetFactory.getDataset(DatasetFactory.LIESKOVSKY_SEM);
        Attribute a = dt.getAttribute(1);
        a.print();
        ClassMerger cm = new ClassMerger((FuzzyAttr) a);
        cm.setMergeFunction(MergeFunction.sum);
        cm.merge(0, 1).print();
    }

}
