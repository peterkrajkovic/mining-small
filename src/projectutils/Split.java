/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils;

import java.util.LinkedList;
import java.util.List;
import minig.data.core.attribute.AttrValue;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.dataset.DataSet;
import projectutils.BranchValues;
import static projectutils.ProjectUtils.log;
import static projectutils.ProjectUtils.log2;

/**
 *
 * @author jrabc
 */
public class Split {

//    private static double getHi(FuzzyAttr attribute, int indexVal, int indexOutput, BranchValues sp) {
//        FuzzyDataset dataSet = attribute.getDataset();
//        BranchValues v = new BranchValues(sp);
//        v.addAttrVal(attribute.getAttrValue(indexVal));
//        double sumproduct = v.getSumProduct();
//        double i = log(dataSet.getDataCount()) - log(sumproduct);
//        v.addAttrVal(dataSet.getOutbputAttribute().getAttrValue(indexOutput));
//        double m = v.getSumProduct();
//        double iBaI = log(dataSet.getDataCount()) - log(m) - i;
//        double hi = m * iBaI;
//        return hi;
//    }
    private static double getHi(AttrValue inputAttrVal, AttrValue outputAttrVal, Union v) {
        double m;
        double sumproduct;
        if (v.getCount() == 0) {
            sumproduct = inputAttrVal.getSum();
            m = v.getStaticUnion(inputAttrVal, outputAttrVal);
        } else {
            sumproduct = v.getSumproductOf(inputAttrVal);
            m = v.getSumproductOf(inputAttrVal, outputAttrVal);
        }
        double hi = m * (-log2(m) + log2(sumproduct));
        return hi;
    }

    /**
     *
     * @param attribute investigated attribute
     * @param BranchSequence Sequence of branch from the root to the current
     * node
     * @param outParamBranchBEntrophy This list is filled during the method
     * execution. After the method execution, it contains the branch entropy for
     * each children of node
     * @return Split of attribute
     */
    public static double getEntropy(FuzzyAttr attribute, Union BranchSequence, List<Double> outParamBranchBEntrophy) {
        double sumH = 0;
        double branchEntrophy = 0;

        FuzzyAttr B = (FuzzyAttr) attribute.getDataset().getOutbputAttribute();
        for (int j = 0; j < attribute.getDomainSize(); j++) {
            for (int i = 0; i < B.getDomainSize(); i++) {
                AttrValue outputVal = B.getAttrValue(i);
                AttrValue inputVal = attribute.getAttrValue(j);
                //branchEntrophy += getHi(attribute, j, i, BranchValues);
                double ret = getHi(inputVal, outputVal, BranchSequence);
                branchEntrophy += ret;
            }
            outParamBranchBEntrophy.add(branchEntrophy);
            sumH += branchEntrophy;
            branchEntrophy = 0;
        }
        return sumH;
    }

    /**
     * shanon entropy of dataset H(S) in C45
     *
     * @param dt
     * @param br
     * @return
     */
    public static double getDatasetEntropy(DataSet dt, BranchValues br) {
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

    public static double getGinyDataset(DataSet dt, BranchValues br) {
        FuzzyAttr outputAttr = dt.getOutbputAttribute();
        double giny = 0;
        double dataCount;
        if (br != null) {
            dataCount = br.getSumProduct();
        } else {
            dataCount = dt.getDataCount();
        }
        for (AttrValue outputAttrVal : outputAttr.getDomain()) {
            double sum;
            if (br == null || br.getCount() == 0) {
                sum = outputAttrVal.getSum();
            } else {
                sum = br.getSumproductOf(outputAttrVal);
            }
            if (sum == 0) {
                giny += 0;
                continue;
            }
            double p = sum / dataCount;
            giny += Math.pow(p, 2);
        }
        return 1 - giny;
    }

    public static double getGinyGain(FuzzyAttr inputAttribute, FuzzyAttr outputAttr, BranchValues br, double hs) {
        double entropy = 0;
        double atrributeCardinality = 0;
        for (AttrValue inputAttrval : inputAttribute.getDomain()) {
            if (br == null) {
                atrributeCardinality += inputAttrval.getSum();
            } else {
                atrributeCardinality += br.getSumproductOf(inputAttrval);
            }
        }
        for (AttrValue inputAttrval : inputAttribute.getDomain()) {
            double giny = 0;
            double menovatel = 0;
            for (AttrValue outputAttrVal : outputAttr.getDomain()) {
                //-----------
                double citatel = 0;
                menovatel = 0;
                if ( br.getCount() == 0) {
                    menovatel += inputAttrval.getSum();
                    citatel += br.getStaticUnion(inputAttrval, outputAttrVal);
                } else {
                    menovatel += br.getSumproductOf(inputAttrval);
                    citatel += br.getSumproductOf(inputAttrval, outputAttrVal);
                }
                double zlomok = citatel / menovatel;
                if (menovatel == 0) {
                    zlomok = 0;
                }
                giny += Math.pow(zlomok, 2);
                //---------
            }
            if (atrributeCardinality == 0) {
                entropy += 0;
            } else {
                giny = 1 - giny;
                entropy += (menovatel / (atrributeCardinality)) * giny;
            }
        }
        return hs - entropy;
    }

    private static double getGainRatio(DataSet dt, FuzzyAttr attr, BranchValues br, double hs) {
        double entropy = 0;
        double splitInfo = 0;
        double atrributeCardinality = 0;
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
            splitInfo += (-cardinality / atrributeCardinality * log2(cardinality / atrributeCardinality));
        }
        if (splitInfo == 0) {
            return 0;
        }
        for (AttrValue inputAttrval : attr.getDomain()) { //compute conditional entropy H(S|A)
            double entropyOfInputVal = 0;
            double menovatel = 0;
            final FuzzyAttr outputAttr = dt.getOutbputAttribute().fuzzy();
            for (AttrValue outputAttrVal : outputAttr.getDomain()) {
                //-----------
                double citatel = 0;
                menovatel = 0;
                if (br == null || br.getCount() == 0) {
                    menovatel += inputAttrval.getSum();
                    citatel += br.getStaticUnion(inputAttrval, outputAttrVal);
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

    public static double getGainRatio(FuzzyAttr output, FuzzyAttr attr, BranchValues br) {
        DataSet fd = new DataSet();
        fd.addAttribute(attr);
        fd.addAttribute(output);
        fd.setOutputAttrIndex(1);
        double datasetH = getDatasetEntropy(fd, br);
        return getGainRatio(fd, attr, br, datasetH);
    }

    public static double getGainRatio(FuzzyAttr output, FuzzyAttr attr, BranchValues br, double HS) {
        DataSet fd = new DataSet();
        fd.addAttribute(attr);
        fd.addAttribute(output);
        fd.setOutputAttrIndex(1);
        return getGainRatio(fd, attr, br, HS);
    }

}
