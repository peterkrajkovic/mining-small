package demo;

import minig.classification.mdd.MDD;
import minig.classification.mdd.MDDnode;
import projectutils.ProjectUtils;
import visualization.graphviz.script.GraphvizScript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class DPLDexamples {


    public static MDD IDPLDTYPEIII(MDD mdd, int index, int from, int to, int j) {

        //transformation functions
        Function<Integer, Integer> lowerThanOne = x -> (x < j) ? 1 : 0;
        Function<Integer, Integer> atLeastOne = x -> (x >= j) ? 1 : 0;

        //apply function
        BinaryOperator<Integer> binaryAND = (x, y) -> x == 1 && y == 1 ? 1 : 0;

        //dpld computation
        DPLD dpld  = new DPLD();
        MDD result = dpld.UniversalIDPLD(mdd, index, from, to, lowerThanOne,atLeastOne, binaryAND);

        return result;
    }

    public static MDD IDPLDTYPEI(MDD mdd, int index, int from, int to, int j) {

        //transformation functions
        Function<Integer, Integer> equalsJ = x -> (x == j) ? 1 : 0;
        Function<Integer, Integer> lowerThanJ = x -> (x < j) ? 1 : 0;

        //apply function
        BinaryOperator<Integer> binaryAND = (x, y) -> x == 1 && y == 1 ? 1 : 0;

        //dpld computation
        DPLD dpld  = new DPLD();
        MDD result = dpld.UniversalIDPLD(mdd, index, from, to, equalsJ, lowerThanJ, binaryAND);

        return result;
    }

    public static MDD DPLD(MDD mdd, int index, int from, int to) {

        //apply function
        BinaryOperator<Integer> equals = (x, y) -> Objects.equals(x, y) ? 1 : 0;

        //dpld computation
        DPLD dpld  = new DPLD();
        MDD result = dpld.UniversalDPLD(mdd, index, from, to, equals);

        return result;
    }


    public static HashMap<Integer, Double> SICalculation(MDD mdd) {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        HashMap<Integer,Integer> decisions = new HashMap<Integer, Integer>();
        HashMap<Integer,Integer> decisionWeights = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> si = new HashMap<>();
        int allCombinations = 1;
        for (MDDnode node : mdd) {
            if (node.getAsocAttr() != null) {
                int index = node.getAsocAttr().getAttributeIndex();
                if (!ids.contains(index)) {
                    ids.add(index);
                    int dec = node.getAsocAttr().getDomainSize();
                    allCombinations = allCombinations * dec;
                    decisions.put(index, dec);
                }
            }
        }
        decisionWeights.put(0,1);
        for (int i = 1; i < decisions.size(); i++) {
            decisionWeights.put(i, decisionWeights.get(i - 1) * decisions.get(i - 1));
        }
        ids = new ArrayList<>();

        for (MDDnode node : mdd) {
            if (node.getAsocAttr() != null) {
                int index = node.getAsocAttr().getAttributeIndex();
                int changes = 0;

                if (!ids.contains(index)) {
                    ids.add(index);
                    int size = node.getAsocAttr().getDomainSize();
                    HashMap<Integer,Integer> newDecisionWeights = new HashMap<>();
                    newDecisionWeights.put(0,1);
                    for (int k = 1; k < decisionWeights.size(); k++) {
                        if (index <= k) {
                            if (k == 1) {
                                newDecisionWeights.put(1,1);
                            } else {
                                newDecisionWeights.put(k, newDecisionWeights.get(k - 2) * decisions.get(k - 2));
                            }
                        } else {
                            newDecisionWeights.put(k, newDecisionWeights.get(k - 1) * decisions.get(k - 1));
                        }
                    }

                    for (int i = 0; i < size; i++) {
                        for (int j = i + 1; j < size; j++) {
                            MDD dpld = DPLD(mdd, index, i , j);
                            String code;
                            code = GraphvizScript.code(dpld);
                            ProjectUtils.toClipboard(code);
                            changes += getChanges(dpld, newDecisionWeights);
                        }
                    }
                    si.put(index, changes);
                }

            }
        }

        HashMap <Integer, Double> updatedSI = new HashMap<Integer, Double>();
        for (int key : si.keySet()) {
            double value = si.get(key) / ((double)allCombinations / decisions.get(key));
            updatedSI.put(key, value);
        }
        return updatedSI;
    }

    private static int getChanges(MDD dpld, HashMap<Integer, Integer> decisionWeight) {
        int changes = 0;
        for (MDDnode node : dpld) {
            if (node.getAsocAttr() != null) {
                for (MDDnode child : node.getChildren()) {
                    if (child.isLeaf() && child.getOutputClass() == 0) {
                        changes += decisionWeight.get(node.getAsocAttr().getAttributeIndex());
                    }
                }
            }
        }

        return changes;
    }


}
