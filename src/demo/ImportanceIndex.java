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

public class ImportanceIndex {

    /*helper attributes and methods for calculation*/
    private HashMap<MDDnode, Integer> memo;
    private HashMap<Integer, Integer> logicalLevelMemo;
    private ArrayList<MDDnode> uniqueNodes = new ArrayList<>();

    public  int getLevel(int attributeIndex) {
        return this.logicalLevelMemo.get(attributeIndex);
    }
    public int getLeafLevel() {
        return this.logicalLevelMemo.size();
    }

    /**
     * Calculates structural indexes of every component using diagram search
     * @param mdd MDD to be derivated
     * @return table with level:index structure
     */
    public  HashMap<String, Double> SICalculationUsingSatisfyCount(MDD mdd) {
        logicalLevelMemo = mdd.getLogicalLevelMemo();
        ArrayList<Integer> ids = new ArrayList<Integer>();
        HashMap<Integer, Integer> decisions = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> si = new HashMap<>();
        int allCombinations = 1;
        for (MDDnode node : mdd.getUniqueNodes()) {
                int level = getLevel(node.getAsocAttr().getAttributeIndex());
                if (!ids.contains(level)) {
                    ids.add(level);
                    int dec = node.getAsocAttr().getDomainSize();
                    allCombinations = allCombinations * dec;
                    decisions.put(level, dec);
                }

        }
        ids = new ArrayList<>();

        for (MDDnode node : mdd.getUniqueNodes()) {
                int index = node.getAsocAttr().getAttributeIndex();
                int changes = 0;
                int logicalLevel = getLevel(node.getAsocAttr().getAttributeIndex());
                if (!ids.contains(index)) {
                    ids.add(index);
                    int size = node.getAsocAttr().getDomainSize();

                    for (int i = 0; i < size; i++) {
                        for (int j = 0; j < size; j++) {
                            DPLD derivatives = new DPLD();
                            derivatives.setLogicalLevelMemo(this.logicalLevelMemo);
                            MDD dpld = derivatives.standardDPLD(mdd, index, i, j);
                            //String code = GraphvizScript.code(dpld);
                            //ProjectUtils.toClipboard(code);
                            int newChanges = satisfyCount(dpld, index, mdd.getUniqueNodes(),0);
                            changes += newChanges;
                        }
                    }
                    si.put(logicalLevel, changes);
                }
        }

        HashMap <String, Double> updatedSI = new HashMap<>();
        for (int key : si.keySet()) {
            //System.out.println("key: " + key + " all: "+ allCombinations + " decisions: " + decisions.get(key));
            double value = si.get(key) / ((double)allCombinations / decisions.get(key));
            String name = null;
                for (MDDnode node : mdd) {
                    if (node.getAsocAttr() != null) {
                        if (getLevel(node.getAsocAttr().getAttributeIndex()) == key) {
                            name = node.getAsocAttr().getName();
                            break;
                        }
                    }
                }
            updatedSI.put(name, value);
        }
        return updatedSI;
    }

    /**
     * Calculates structural indexes of every component using components domain sizes
     * @param mdd MDD to be derivated
     * @return table with level:index structure
     */
    public  HashMap<String, Double> SICalculation(MDD mdd) {
        logicalLevelMemo = mdd.getLogicalLevelMemo();
        ArrayList<Integer> ids = new ArrayList<Integer>();
        HashMap<Integer, Integer> decisions = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> decisionWeights = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> si = new HashMap<>();
        int allCombinations = 1;
        for (MDDnode node : mdd) {
            if (node.getAsocAttr() != null) {
                int index = getLevel(node.getAsocAttr().getAttributeIndex());
                if (!ids.contains(index)) {
                    ids.add(index);
                    int dec = node.getAsocAttr().getDomainSize();
                    allCombinations = allCombinations * dec;
                    decisions.put(index, dec);
                }
            }
        }
        decisionWeights.put(decisions.size() - 1, 1);
        for (int i = decisions.size() - 2; i >= 0; i--) {
            decisionWeights.put(i, decisionWeights.get(i + 1) * decisions.get(i + 1));
        }
        ids = new ArrayList<>();

        for (MDDnode node : mdd) {
            if (node.getAsocAttr() != null) {
                int index = node.getAsocAttr().getAttributeIndex();
                int changes = 0;
                int logicalLevel = getLevel(node.getAsocAttr().getAttributeIndex());
                if (!ids.contains(index)) {
                    ids.add(index);
                    int size = node.getAsocAttr().getDomainSize();
                    HashMap<Integer, Integer> newDecisionWeights = new HashMap<>();
                    newDecisionWeights.put(decisions.size() - 1, 1);
                    for (int k = decisionWeights.size() - 2; k >= 0; k--) {
                        if (getLevel(node.getAsocAttr().getAttributeIndex()) >= k) {
                            if (k == decisionWeights.size() - 2) {
                                newDecisionWeights.put(k, 1);
                            } else {
                                newDecisionWeights.put(k, newDecisionWeights.get(k + 2) * decisions.get(k + 2));
                            }
                        } else {
                            newDecisionWeights.put(k, newDecisionWeights.get(k + 1) * decisions.get(k + 1));
                        }
                    }

                    for (int i = 0; i < size; i++) {
                        for (int j = i + 1; j < size; j++) {
                            DPLD derivatives = new DPLD();
                            derivatives.setLogicalLevelMemo(this.logicalLevelMemo);
                            MDD dpld = derivatives.standardDPLD(mdd, index, i, j);
                            //String code;
                            //code = GraphvizScript.code(dpld);
                            //ProjectUtils.toClipboard(code);
                            int newChanges = getChanges(dpld, newDecisionWeights);
                            //System.out.println(logicalLevel + ", from: " + i + " to:" + j + " changes: " + newChanges);
                            changes += newChanges;
                        }
                    }
                    si.put(logicalLevel, changes);
                }

            }
        }
        HashMap <String, Double> updatedSI = new HashMap<>();
        for (int key : si.keySet()) {
            //System.out.println("key: " + key + " all: "+ allCombinations + " decisions: " + decisions.get(key));
            double value = si.get(key) / ((double)allCombinations / decisions.get(key));
            String name = null;
            for (MDDnode node : mdd.getUniqueNodes()) {
                if (getLevel(node.getAsocAttr().getAttributeIndex()) == key) {
                    name = node.getAsocAttr().getName();
                    break;
                }
            }
            updatedSI.put(name, value);
        }
        return updatedSI;
    }

    /**
     * Calculates structural index of component using components domain sizes
     * @param diagram MDD to be derivated
     * @param componentIndex index of component by which derivation is done
     * @param stateFrom  component state
     * @param stateTo component state
     * @return structural index
     */
    public double derivateUsingTables(MDD diagram, int componentIndex, int stateFrom, int stateTo) {
        this.logicalLevelMemo = diagram.getLogicalLevelMemo();
        ArrayList<Integer> ids = new ArrayList<Integer>();
        HashMap<Integer,Integer> decisions = new HashMap<Integer, Integer>();
        HashMap<Integer,Integer> decisionWeights = new HashMap<Integer, Integer>();
        int indexDomainSize = 0;
        int indexLogicalLevel = 0;
        int allCombinations = 1;

        //fill decisions table
        for (MDDnode node : diagram.getUniqueNodes()) {
                int level = getLevel(node.getAsocAttr().getAttributeIndex());
                if (!ids.contains(level)) {
                    if (node.getAsocAttr().getAttributeIndex() == componentIndex) {
                        indexLogicalLevel = level;
                        indexDomainSize = node.getAsocAttr().getDomainSize();
                    }
                    ids.add(level);
                    int dec = node.getAsocAttr().getDomainSize();
                    allCombinations = allCombinations * dec;
                    decisions.put(level, dec);
                }
        }
        //fill decisionWeights
        decisionWeights.put(decisions.size()-1, 1);
        for (int i = decisions.size() - 2; i >= 0; i--) {
            decisionWeights.put(i, decisionWeights.get(i + 1) * decisions.get(i + 1));
        }

        //update decisionWeights
        HashMap<Integer,Integer> newDecisionWeights = new HashMap<>();
        newDecisionWeights.put(decisions.size() - 1, 1);
        for (int k = decisionWeights.size() - 2; k >= 0; k--) {
            if (indexLogicalLevel >= k) {
                if (k == decisionWeights.size() - 2) {
                    newDecisionWeights.put(k, 1);
                } else {
                    newDecisionWeights.put(k, newDecisionWeights.get(k + 2) * decisions.get(k + 2));
                }
            } else {
                newDecisionWeights.put(k, newDecisionWeights.get(k + 1) * decisions.get(k + 1));
            }
        }

        //derivation
        DPLD derivatives = new DPLD();
        derivatives.setLogicalLevelMemo(this.logicalLevelMemo);
        MDD dpld = derivatives.standardDPLD(diagram, componentIndex, stateFrom , stateTo);

        //calculate changes
        int changes = getChanges(dpld, newDecisionWeights);
        double relativeChanges = changes / ((double) allCombinations / indexDomainSize);

        return relativeChanges;
    }

    /**
     * Helper method for derivateUsingTables
     * @param mdd derivated MDD
     * @param decisionWeight table with level:decisionWeight structure
     * @return number of changes
     */
    private int getChanges(MDD mdd, HashMap<Integer, Integer> decisionWeight) {
        int changes = 0;
        for (MDDnode node : mdd) {
            if (node.getAsocAttr() != null) {
                for (MDDnode child : node.getChildren()) {
                    if (child.isLeaf() && child.getOutputClass() == 0) {
                        changes += decisionWeight.get(getLevel(node.getAsocAttr().getAttributeIndex()));
                    }
                }
            }
        }
        return changes;
    }

    /**
     * Calculates structural index of component using diagram search method
     * @param diagram
     * @param index index of component derivated
     * @param from component state
     * @param to component state
     * @return structural index
     */
    public double derivateUsingSatisfyCount(MDD diagram, int index, int from, int to) {
        this.logicalLevelMemo = diagram.getLogicalLevelMemo();

        DPLD derivatives = new DPLD();
        derivatives.setLogicalLevelMemo(this.logicalLevelMemo);
        MDD derivated = derivatives.standardDPLD(diagram,index,from,to);

        int changes = satisfyCount(derivated, index, diagram.getUniqueNodes(), 0);

        int all = 0;
        //computes whole domain
        for (MDDnode node : diagram.getUniqueNodes()) {
            if (node.getAsocAttr().getAttributeIndex() != index) {
                 if (all == 0) {
                     all = node.getAsocAttr().getDomainSize();
                 } else {
                     all *= node.getAsocAttr().getDomainSize();
                 }
            }
        }
        //System.out.println("index: " + index + " from: " + from + " to: " + to + "all: " + all + "changes: " + changes);
        return (double)changes / all;
    }

    /**
     * Calculates structural index of component using diagram search method
     * @param diagram
     * @param index index of component derivated
     * @param from component state
     * @param to component state
     * @param stateFrom system state
     * @param stateTo system state
     * @return structural index
     */
    public double derivateUsingSatisfyCount(MDD diagram, int index, int from, int to, int stateFrom, int stateTo) {
        this.logicalLevelMemo = diagram.getLogicalLevelMemo();

        DPLD derivatives = new DPLD();
        derivatives.setLogicalLevelMemo(this.logicalLevelMemo);

        //apply function
        BinaryOperator<Integer> equals = (x, y) -> (Objects.equals(x, y) || x == -1 || y == -1) ? 1 : 0;
        //left transfom function
        Function<Integer, Integer> leftTransform = (x) -> x == stateFrom ? x : -1;
        //right transform function
        Function<Integer, Integer> rightTransform = (x) -> x == stateTo ? x : -1;

        MDD derivated = derivatives.universalIDPLD(diagram,index,from,to, leftTransform, rightTransform, equals);
        String code = GraphvizScript.code(derivated);
        ProjectUtils.toClipboard(code);
        int changes = satisfyCount(derivated, index, diagram.getUniqueNodes(), 0);

        int all = 0;
        //computes whole domain
        for (MDDnode node : diagram.getUniqueNodes()) {
            if (node.getAsocAttr().getAttributeIndex() != index) {
                if (all == 0) {
                    all = node.getAsocAttr().getDomainSize();
                } else {
                    all *= node.getAsocAttr().getDomainSize();
                }
            }
        }
        //System.out.println("index: " + index + " from: " + from + " to: " + to + "all: " + all + "changes: " + changes);
        return (double)changes / all;
    }

    /**
     * Calculates number of changes in derivated MDD using diagram search
     * @param diagram derivated diagram
     * @param index index of component derivated
     * @param uniqueNodes unique nodes of starting diagram
     * @param value  output value of node representing changes in derivated MDD
     * @return number of changes
     */
    public int satisfyCount(MDD diagram, int index, ArrayList<MDDnode> uniqueNodes, int value) {
        var root = diagram.getRoot();
        memo = new HashMap<>();
        var iroot = root.getLevel();
        this.uniqueNodes = uniqueNodes;
        int logicalLevel = getLevel(index);

        var diff = domainProduct(1, iroot, logicalLevel);
        return diff * satisfyCountStep(root, logicalLevel, value);
    }

    /**
     * Helper method for SatisfyCount
     * @param node
     * @param logicalLevel
     * @param value
     */
    private int satisfyCountStep(MDDnode node, int logicalLevel, int value) {
        if (node.isLeaf() && node.getOutputClass() == value) {
            return 1;
        }
        if (node.isLeaf() && node.getOutputClass() != value) {
            return 0;
        }
        if (memo.containsKey(node)) {
            return memo.get(node);
        }
        int count = 0;
        int i = getLevel(node.getAsocAttr().getAttributeIndex());
        for (int k = 0; k < node.getChildren().size(); k++) {
            var son = node.getChildren().get(k);
                var ison = son.getAsocAttr() != null ? getLevel(son.getAsocAttr().getAttributeIndex()): getLeafLevel();
                var sonCount = satisfyCountStep(son, logicalLevel, value);
                int diff = domainProduct(i, ison, logicalLevel);
                count += diff * sonCount;
        }
        memo.put(node, count);
        return count;
    }

    /**
     * Helper method for SatisfyCountStep
     * @param i1 positive or 0
     * @param i2 i2 should be greater then i1
     * @param index index of derivated component
     * @return product of domainSizes of missing components between two levels besides component with index
     */
    private int domainProduct(int i1, int i2, int index) {
        int product = 1;
        int i = i2-1;
        while(i > i1) {
            if (i == index) {
                i--;
                continue;
            }
            int level1DomainSize = 1;
            for (MDDnode node : this.uniqueNodes) {
                if (getLevel(node.getAsocAttr().getAttributeIndex()) == i) {
                    level1DomainSize = node.getAsocAttr().getDomainSize();
                    break;
                }
            }
            product = product * level1DomainSize;
            i--;
        }
        return product;
    }
}
