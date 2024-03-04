/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.classification.fdt.rules;

import projectutils.structures.DoubleVector;
import projectutils.stat.IncrementalWStat;
import minig.classification.fdt.FuzzyDecisionTree;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import minig.data.core.dataset.Instance;
import minig.classification.trees.Tree;
import projectutils.ConsolePrintable;
import minig.classification.fdt.DTNode;
import minig.data.core.dataset.Instance;
import projectutils.structures.BoxedDoubleArray;

/**
 *
 * @author jrabc
 */
public class ClassificationRules implements Serializable, ConsolePrintable {

    private List<ClassificationRule> classificationRules = new ArrayList<>();
    private Tree tree;
    private int numberOfClasses;

    public ClassificationRules(FuzzyDecisionTree tree, int numberOfClasses) {
        this.numberOfClasses = numberOfClasses;
        tree.getAllPaths().forEach(v -> {
            classificationRules.add(new ClassificationRule(v, tree));
        });
        this.tree = tree;
    }

    public ClassificationRules(Tree tree, int numberOfClasses) {
        this.numberOfClasses = numberOfClasses;
        tree.getAllPaths().forEach(v -> {
            classificationRules.add(new ClassificationRule((List<DTNode>) v, tree));
        });
        this.tree = tree;
    }

    public List<ClassificationRule> getProduceRules() {
        return classificationRules;
    }

    public Tree getTree() {
        return tree;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ClassificationRule rule : classificationRules) {
            sb.append(rule.toString()).append("\n");
        }
        return sb.toString();
    }

    public List<Double> classify(Instance a) {
        double[] weights = getWeightsOfRules(a);
        double[] newConfidanceLevels = new double[numberOfClasses];
        for (int i = 0; i < numberOfClasses; i++) {
            double b = 0;
            for (int j = 0; j < weights.length; j++) {
                double weight = weights[j];
                b += weight * classificationRules.get(j).getConfidenceLevels().get(i);
            }
            newConfidanceLevels[i] = b;
        }
        return new BoxedDoubleArray(newConfidanceLevels);
    }

    public List<Double> classifyDebug(Instance a) {
        a.print();
        
        double[] weights = getWeightsOfRules(a);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < classificationRules.size(); i++) {
            ClassificationRule rule = classificationRules.get(i);
            sb.append("[" + weights[i] + "]  ").append(rule.toString()).append("\n");
        }
        System.out.println(sb.toString());
        double[] newConfidanceLevels = new double[numberOfClasses];
        for (int i = 0; i < numberOfClasses; i++) {
            double b = 0;
            for (int j = 0; j < weights.length; j++) {
                double weight = weights[j];
                b += weight * classificationRules.get(j).getConfidenceLevels().get(i);
            }
            newConfidanceLevels[i] = b;
        }
        return new BoxedDoubleArray(newConfidanceLevels);
    }

    public List<DoubleVector> getLeafClassifications(Instance a) {
        double[] weights = getWeightsOfRules(a);
        List<DoubleVector> leafsResults = new ArrayList<>(weights.length);
        for (int i = 0; i < classificationRules.size(); i++) {
            DoubleVector newConfidanceLevels = new DoubleVector(classificationRules.get(i).getConfidenceLevels());
            newConfidanceLevels.muli(weights[i]);
            leafsResults.add(newConfidanceLevels);
        }
        return leafsResults;
    }

    public double predict(Instance a) {
        double[] weights = getWeightsOfRules(a);
        IncrementalWStat ws = new IncrementalWStat();
        for (int j = 0; j < weights.length; j++) {
            double weight = weights[j];
            double result = classificationRules.get(j).getPredicatedValue();
            ws.add(weight, result);
        }
        return ws.getMean();
    }

    private double[] getWeightsOfRules(Instance a) {
        double[] weights = new double[classificationRules.size()];
        for (int i = 0; i < classificationRules.size(); i++) {
            ClassificationRule rule = classificationRules.get(i);
            weights[i] = rule.getWeight(a);
        }
        return weights;
    }

}
