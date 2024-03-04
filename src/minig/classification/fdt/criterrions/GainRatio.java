/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.classification.fdt.criterrions;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import minig.classification.fdt.FDTNode;
import minig.classification.fdt.FDTu;
import minig.data.core.attribute.AttrValue;
import minig.data.core.attribute.Attribute;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;
import projectutils.BranchValues;
import static projectutils.ProjectUtils.log;
import static projectutils.ProjectUtils.log2;
import projectutils.Union;

/**
 *
 * @author jrabc
 */
public class GainRatio extends FuzzyCriterion {

    @Override
    public void findAssocAttr(FDTNode node, List<FuzzyAttr> freeAttrs) {
        double maxEntropy = Double.NEGATIVE_INFINITY;
        FuzzyAttr asocAttr = null;
        double hs = getHs(getDataset(), node.getBranchValues());
        for (FuzzyAttr attr : freeAttrs) {
            double entropy = getGainRatio(getDataset(), attr, node.getBranchValues(), hs);
            if (entropy > maxEntropy) {
                asocAttr = attr;
                maxEntropy = entropy;
            }
        }
        node.setEntropy(maxEntropy);
        node.setAsocAttr(asocAttr);

    }

    private static double getGainRatio(DataSet dt, FuzzyAttr attr, Union br, double hs) {
        double entropy = 0;
        double splitInfo = 0;
        double atrributeCardinality = 0;
        final FuzzyAttr outbputAttribute = dt.getOutbputAttribute();
        List<Double> cardinalities = new LinkedList<>();
        for (AttrValue inputAttrval : attr.getDomain()) { //compute cardinality of input attribute and split info
            double cardinality = 0;
            if (br == null || br.getCount() == 0) {
                cardinality = inputAttrval.getSum();
            } else {
                cardinality = br.getSumproductOf(inputAttrval);
            }
            atrributeCardinality += cardinality;
            cardinalities.add(cardinality);
        }
        for (double cardinality : cardinalities) {
            splitInfo += (-cardinality / atrributeCardinality) * log2(cardinality / atrributeCardinality);
        }
        if (splitInfo == 0 || atrributeCardinality == 0) {
            return 0;
        }
        for (AttrValue inputAttrval : attr.getDomain()) { //compute conditional entropy H(S|A)
            double entropyOfInputVal = 0;
            double menovatel = 0;

            for (AttrValue outputAttrVal : outbputAttribute.getDomain()) {
                //-----------
                double citatel = 0;
                menovatel = 0;
                if ( br.getCount() == 0) {
                    menovatel += inputAttrval.getSum();
                    citatel += br.getSumProdOf(inputAttrval, outputAttrVal);
                } else {
                    menovatel += br.getSumproductOf(inputAttrval);
                    citatel += br.getSumproductOf(inputAttrval, outputAttrVal);
                }
                double zlomok = citatel / menovatel;
                if (menovatel == 0) {
                    zlomok = 0;
                }
                entropyOfInputVal += -1 * (zlomok) * log2(zlomok);
                //---------
            }
            if (atrributeCardinality == 0) {
                entropy += 0;
            } else {
                entropy += (menovatel / (atrributeCardinality)) * entropyOfInputVal;
            }
        }
        double gainRatio = (hs - entropy) / splitInfo;
        return gainRatio;
    }

    private double getHs(DataSet dt, Union br) {
        FuzzyAttr outputAttr = dt.getOutbputAttribute();
        double entropy = 0;
        double dataCount;
        if (br == null || br.getCount() == 0) {
            dataCount = dt.getDataCount();
        } else {
            dataCount = br.getSumProduct();
        }
        for (AttrValue outputAttrVal : outputAttr.getDomain()) {
            double sum;
            if (br == null || br.getCount() == 0) {
                sum = outputAttrVal.getSum();
            } else {
                sum = br.getSumproductOf(outputAttrVal);
            }
            double p = sum / dataCount;
            entropy += -p * log2(p);
        }
        return entropy;
    }

    public static void main(String[] args) throws IOException {
        DataSet dts = DatasetFactory.getDataset(DatasetFactory.TAE);
        dts.setOutputAttrIndex(dts.getAtributteCount() - 1);
        System.out.println(dts.toFuzzyDataset(3));
        DataSet ff = dts.toFuzzyDataset(3);
        for (int i = 0; i < 10; i++) {
            FDTu tree = new FDTu(0.0, 1, ff);
            tree.setCriterion(new GainRatio());
            tree.buildModel();
            System.out.println(tree.toString());
            System.out.println("");
        }
    }

    @Override
    public double getCriterionValue(FDTNode node, FuzzyAttr attr) {
        double hs = getHs(getDataset(), node.getBranchValues());
        double ret = getGainRatio(getDataset(), attr, node.getBranchValues(), hs);
        return ret;
    }

    @Override
    public double getCriterionValue(FuzzyAttr output, FuzzyAttr input) {
        double hs = getHs(getDataset(), null);
        double ret = getGainRatio(getDataset(), input, null, hs);
        return ret;
    }
}
