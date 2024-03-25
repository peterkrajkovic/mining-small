package demo;

import minig.classification.mdd.MDD;
import minig.classification.mdd.MDDnode;

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
        HashMap<Integer, Integer> si = new HashMap<>();
        int allCombinations = 1;

        for (MDDnode node : mdd) {
            if (node.getAsocAttr() != null) {
                int index = node.getAsocAttr().getAttributeIndex();
                int changes = 0;

                if (!ids.contains(index)) {
                    ids.add(index);
                    int decisions = node.getAsocAttr().getDomainSize();
                    allCombinations = allCombinations * decisions;

                    for (int i = 0; i < decisions - 1; i++) {
                        for (int j = i + 1; j < decisions; j++) {
                            MDD dpld = DPLD(mdd, index, i , j);
                            changes += getChanges(dpld);
                        }
                    }
                }
                si.put(index, changes);
            }
        }
        for (int key : si.keySet()) {
            System.out.println("index: " + key + ", changes: " + si.get(key));
        }
        System.out.println("all combinations: " + allCombinations);
        HashMap <Integer, Double> updatedSI = new HashMap<Integer, Double>();
        for (int key : si.keySet()) {
            double value = si.get(key) / (double)allCombinations;
            updatedSI.put(key, value);
        }
        return updatedSI;
    }

    private static int getChanges(MDD dpld) {
        int changes = 0;
        for (MDDnode node : dpld) {
            if (node.getAsocAttr() != null) {
                for (MDDnode child : node.getChildren()) {
                    if (child.isLeaf() && child.getOutputClass() == 0) {
                        changes++;
                    }
                }
            }
        }

        return changes;
    }


}
