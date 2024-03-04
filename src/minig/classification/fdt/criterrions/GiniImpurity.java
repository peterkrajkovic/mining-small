/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.classification.fdt.criterrions;

import java.io.IOException;
import java.util.List;
import minig.classification.fdt.FDTNode;
import minig.classification.fdt.FDTu;
import minig.data.core.attribute.AttrValue;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;
import projectutils.BranchValues;
import static projectutils.ProjectUtils.pow2;
import projectutils.Union;

/**
 *
 * @author jrabc
 */
public class GiniImpurity extends FuzzyCriterion {

    @Override
    public void findAssocAttr(FDTNode node, List<FuzzyAttr> freeAttrs) {
        double maxEntropy = Double.NEGATIVE_INFINITY;
        FuzzyAttr asocAttr = null;
        double hs = getHS(getDataset(), node.getBranchValues());
        for (int i = 0; i < freeAttrs.size(); i++) {
            FuzzyAttr attr = freeAttrs.get(i);
            double entropy = getGinyImpurity(attr, getDataset().getOutbputAttribute(), node.getBranchValues(), hs);
            if (entropy > maxEntropy) {
                asocAttr = attr;
                maxEntropy = entropy;
            }
        }
        node.setEntropy(maxEntropy);
        node.setAsocAttr(asocAttr);

    }

    public static double getHS(DataSet dt, Union br) {
        FuzzyAttr outputAttr = dt.getOutbputAttribute();
        double giny = 1;
        double dataCount;
        if (!(br == null || br.getCount() == 0)) {
            dataCount = br.getSumProduct();
        } else {
            dataCount = dt.getDataCount();
        }
        for (int i = 0; i < outputAttr.getDomain().size(); i++) {
            AttrValue outputAttrVal = outputAttr.getDomain().get(i);
            double sum;
            if (br == null || br.getCount() == 0) {
                sum = outputAttrVal.getSum();
            } else {
                sum = br.getSumproductOf(outputAttrVal);
            }
            double p = sum / dataCount;
            giny -= pow2(p);
        }
        return giny;
    }
    

    public static double getH(FuzzyAttr outputAttr, BranchValues br) {
        double giny = 1;
        double dataCount;
        if (!(br == null || br.getCount() == 0)) {
            dataCount = br.getSumProduct();
        } else {
            dataCount = outputAttr.getDataCount();
        }
        for (int i = 0; i < outputAttr.getDomain().size(); i++) {
            AttrValue outputAttrVal = outputAttr.getDomain().get(i);
            double sum;
            if (br == null || br.getCount() == 0) {
                sum = outputAttrVal.getSum();
            } else {
                sum = br.getSumproductOf(outputAttrVal);
            }
            double p = sum / dataCount;
            giny -= pow2(p);
        }
        return giny;
    }

    public double getGinyImpurity(FuzzyAttr inputAttribute, FuzzyAttr outputAttr, Union u, double hs) {
        double entropy = 0;
        double atrributeCardinality = 0;
        for (AttrValue inputAttrval : inputAttribute.getDomain()) {
            if (isRoot(u)) {
                atrributeCardinality += inputAttrval.getSum();
            } else {
                atrributeCardinality += u.getSumproductOf(inputAttrval);
            }
        }
        for (AttrValue inputAttrval : inputAttribute.getDomain()) {
            double giny = 1;
            double menovatel = 0, citatel;
            for (AttrValue outputAttrVal : outputAttr.getDomain()) {
                //-----------
                citatel = 0;
                menovatel = 0;
                if (isRoot(u)) {
                    citatel += u.getSumProdOf(inputAttrval, outputAttrVal);
                    menovatel += inputAttrval.getSum();
                } else {
                    menovatel += u.getSumproductOf(inputAttrval);
                    citatel += u.getSumproductOf(inputAttrval, outputAttrVal);
                }
                double probSum;
                if (menovatel == 0) {
                    probSum = 0;
                } else {
                    probSum = citatel / menovatel;
                }
                giny -= pow2(probSum);
                //---------
            }
            if (atrributeCardinality == 0) {
                entropy += 0;
            } else {
                entropy += (menovatel / (atrributeCardinality)) * (giny);
            }
        }
        return hs - entropy;
    }

    private boolean isRoot(Union u) {
        return u == null || u.getCount() == 0;
    }

    public static void main(String[] args) throws IOException {
        DataSet dts = DatasetFactory.getDataset(DatasetFactory.GOLF_LINGVISTIC);
        dts.print();
        dts.removeAttribue("TEMPERATURE");
        dts.removeAttribue("HUMIDITY");
        dts.setOutputAttrIndex(dts.getAtributteCount() - 1);
        System.out.println(dts.toFuzzyDataset(3));
        DataSet ff = dts.toFuzzyDataset(3);
        for (int i = 0; i < 10; i++) {
            FDTu tree = new FDTu(0.0, 1, ff);
            tree.setCriterion(new GiniImpurity());
            tree.buildModel();
            System.out.println(tree.toString());
            System.out.println("");
        }
    }

    @Override
    public double getCriterionValue(FDTNode node, FuzzyAttr attr) {
        double hs = getHS(getDataset(), node.getBranchValues());
        double e = getGinyImpurity(attr, getDataset().getOutbputAttribute(), node.getBranchValues(), hs);
        return e;// / getH(attr, node.getBranchValues());
    }

    @Override
    public double getCriterionValue(FuzzyAttr output, FuzzyAttr input) {
        double hs = getH(output, null);
        double entropy = getGinyImpurity(input, output, new BranchValues(), hs);
        return entropy;
    }
}
