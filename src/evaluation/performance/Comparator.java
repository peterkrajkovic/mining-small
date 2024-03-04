/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluation.performance;

import evaluation.performance.ClassificationPerformance;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import minig.models.Classifier;
import minig.data.core.dataset.DataSet;
import minig.data.operators.RandomSpliter;
import projectutils.ConsolePrintable;

/**
 *
 * @author jrabc
 */
public class Comparator implements ConsolePrintable {

    private DataSet dataset;
    private List<Classifier> classifiers = new LinkedList<>();
    private List<ClassificationPerformance> performances;

    public void addClassifier(Classifier classifier) {
        classifiers.add(classifier);
    }

    public Comparator(DataSet dataset) {
        this.dataset = dataset;
    }

    public Comparator() {
    }

    public DataSet getDataset() {
        return dataset;
    }

    public void setDataset(DataSet dataset) {
        this.dataset = dataset;
    }

    public List<ClassificationPerformance> getPerformances() {
        return performances;
    }

    public List<ClassificationPerformance> createComparison(double teriningPercentage, int iterationsCount) {
        if (performances == null) {
            performances = new ArrayList<>(classifiers.size());
        } else {
            performances.clear();
        }
        for (int i = 0; i < iterationsCount; i++) {
            int j = 0;
            RandomSpliter rs = new RandomSpliter(dataset, teriningPercentage, i);
            for (int k = 0; k < classifiers.size(); k++) {
                Classifier classifier = classifiers.get(k);
                classifier.setDataset(rs.getTrainingDataset());
                classifier.buildModel();
                ClassificationPerformance p = new ClassificationPerformance(classifier, rs.getTestingDataset());
                if (performances.size() < classifiers.size()) {
                    performances.add(p);
                } else {
                    performances.get(j++).add(p);
                }
            }
        }
        return performances;
    }

    public List<ClassificationPerformance> createComparison(double teriningPercentage) {
        RandomSpliter rs = new RandomSpliter(dataset, teriningPercentage, 1);
        if (performances == null) {
            performances = new ArrayList<>(classifiers.size());
        } else {
            performances.clear();
        }
        int j = 0;
        for (Classifier classifier : classifiers) {
            classifier.setDataset(rs.getTrainingDataset());
            classifier.buildModel();
            ClassificationPerformance p = new ClassificationPerformance(classifier, rs.getTestingDataset());
            if (performances.size() < classifiers.size()) {
                performances.add(p);
            } else {
                performances.get(j++).add(p);
            }
        }
        return performances;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(80);
        for (ClassificationPerformance p : performances) {
            sb.append(p).append(System.lineSeparator());
        }
        return sb.toString();
    }

}
