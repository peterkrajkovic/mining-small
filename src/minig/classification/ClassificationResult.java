/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.classification;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author jrabc
 */
public class ClassificationResult {

    private List<Double> result = new LinkedList<>();
    private double weight = 1;

    public ClassificationResult(List<Double> result) {
        this.result = result;
    }

    public ClassificationResult(Double ...result) {
        for (Double res : result) {
            this.result.add(res);
        }
    }

    public ClassificationResult(List<Double> result, double weight) {
        this.result = result;
        this.weight = weight;
    }

    public List<Double> getResult() {
        return result;
    }

    public int getClassCount() {
        return result.size();
    }

    public Double getResultAtClass(int classIndex) {
        return result.get(classIndex);
    }
}
