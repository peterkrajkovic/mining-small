/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.classification;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author jrabc
 */
public class ClassifyEvaluator {

    private List<ClassificationResult> results = new ArrayList<>(100);

    public void addResult(List<Double> result) {
        this.results.add(new ClassificationResult(result));
    }

    public void addResult(Double result) {
        this.results.add(new ClassificationResult(result));
    }

    public void addResult(ClassificationResult result) {
        this.results.add(result);
    }

    public int getReusltsCount() {
        return results.size();
    }

    public int getClassCount() {
        if (results.isEmpty()) {
            return 0;
        } else {
            return results.get(0).getClassCount();
        }

    }

    public ClassificationResult getRow(int rowIndex) {
        return results.get(rowIndex);
    }

    public List<Double> elavulate() {
        List<Double> result = new LinkedList();
        for (int j = 0; j < getClassCount(); j++) {
            double classSum = 0;
            for (int i = 0; i < getReusltsCount(); i++) {
                classSum += getRow(i).getResultAtClass(j);
            }
            result.add(classSum / getReusltsCount());
        }
        return result;
    }
}
