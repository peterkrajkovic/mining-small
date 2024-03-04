/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluation.performance;

import projectutils.ConsolePrintable;
import java.io.IOException;
import java.util.List;
import minig.classification.fdt.FuzzyDecisionTree;
import minig.data.core.dataset.DataSet;
import projectutils.ProjectUtils;
import minig.models.Classifier;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;
import minig.classification.fdt.FDTu;
import static projectutils.ProjectUtils.getMaxValueIndex;
import projectutils.stat.IncrementalStat;
import minig.data.core.attribute.CategoricalAttr;

/**
 *
 * @author jrabc
 */
public class ClassificationPerformance implements ConsolePrintable, Performance<Classifier> {

    private DataSet dataset;
    private double correctlyClassified;
    private double totoalClassified;
    private Classifier classifier;
    private ConfusionMatrix matrix;
    private boolean init = false;

    private IncrementalStat accuracyStat;
    private IncrementalStat errorStat;

    public ClassificationPerformance(Classifier classifier, DataSet testingDataset) {
        this.classifier = classifier;
        dataset = testingDataset;
        matrix = new ConfusionMatrix(classifier, dataset);
        resetAndEvaluate();
    }

    public ClassificationPerformance() {
    }

    @Override
    public void setModel(Classifier classifier) {
        this.classifier = classifier;
        if (matrix != null) {
            this.matrix.setClassifier(classifier);
        }
    }

    public void init() {
        init = true;
        correctlyClassified = 0;
        totoalClassified = 0;
        matrix = new ConfusionMatrix(classifier, dataset);
        accuracyStat = new IncrementalStat();
        errorStat = new IncrementalStat();
    }

    @Override
    public void setTestingDataset(DataSet testingDataset) {
        dataset = testingDataset;
    }

    //TODO check averaging of totoalClassified, correctlyClassified
    public void average(ClassificationPerformance p) {
        correctlyClassified = (correctlyClassified + p.correctlyClassified) / 2;
        matrix.average(p.getConfusionMatrix());
        totoalClassified = p.getConfusionMatrix().getCountOfInstances();
        correctlyClassified = p.getConfusionMatrix().getCorrectlyClassified();
    }

    public void add(ClassificationPerformance p) {
        totoalClassified += p.getConfusionMatrix().getCountOfInstances();
        correctlyClassified += p.getConfusionMatrix().getCorrectlyClassified();
        matrix.add(p.getConfusionMatrix());
    }

    public Classifier getClassifier() {
        return classifier;
    }

    public ConfusionMatrix getConfusionMatrix() {
        return matrix;
    }

    @Override
    public final double resetAndEvaluate() {
        init();
        return evatluateAndSave();
    }

    @Override
    public final double appendToEvaluation() {
        if (!init) {
            init();
        }
        return evatluateAndSave();
    }

    protected double evatluateAndSave() {
        final double acc = evalueate();
        accuracyStat.add(acc);
        errorStat.add(1 - acc);
        return acc;
    }

    protected double evalueate() {
        CategoricalAttr output = dataset.getOutbputAttribute();
        double correct = 0;
        for (int i = 0; i < dataset.getDataCount(); i++) {
            totoalClassified++;
            int predictedClass = getMaxValueIndex(getClassifier().classify(dataset.getInstance(i)));
            int correctClass = output.getClassIndex(i);
            if (correctClass == predictedClass) {
                correctlyClassified++;
                correct++;
            }
            matrix.addResult(predictedClass, correctClass);
        }
        return (correct) / dataset.getDataCount();
    }

    public List<ClassAnalyzer> getClassAnalyzers() {
        return null;
    }

    public double getTotalAccuracy() {
        return ((double) correctlyClassified) / totoalClassified;
    }

    public double getTotalError() {
        return 1 - ((double) correctlyClassified) / totoalClassified;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(500);
        sb.append("CLASSIFIER: ").append(classifier.getClass().getSimpleName()).append(System.lineSeparator());;
        sb.append("Training instances: ").append(classifier.getDataset().getDataCount()).append(System.lineSeparator());
        sb.append("Testing  instances: ").append((int) totoalClassified).append(System.lineSeparator());
        sb.append("Total Accuracy: ").append(ProjectUtils.formatDouble(getTotalAccuracy())).append(System.lineSeparator());
        sb.append("Total Error   : ").append(ProjectUtils.formatDouble(getTotalError())).append(System.lineSeparator());
        sb.append("Accuracy      : ").append(ProjectUtils.formatDouble(accuracyStat.getMean())).
                append("    +/-").append(ProjectUtils.formatDouble(accuracyStat.getStdDev())).
                append(System.lineSeparator());
        sb.append("Error         : ").append(ProjectUtils.formatDouble(errorStat.getMean())).
                append("    +/-").append(ProjectUtils.formatDouble(errorStat.getStdDev())).
                append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(this.matrix.toString());
        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        DataSet dts = DatasetFactory.getDataset(DatasetFactory.GOLF_LINGVISTIC);
        dts.removeAttribue(1);
        dts.removeAttribue(1);
        dts.setOutputAttrIndex(dts.getAtributteCount() - 1);
        //     System.out.println(dt.getStringDataSet());

        DataSet ff = dts.getFuzzyDataset(2, 5);
        FuzzyDecisionTree tree = new FDTu(0.0001, 0.999, ff);
        tree.buildModel();

        ClassificationPerformance ce = new ClassificationPerformance(tree, ff);
        ce.getConfusionMatrix().print();

        tree = new FDTu(0.6, 0.5, ff);
        tree.buildModel();

        ClassificationPerformance cex = new ClassificationPerformance(tree, ff);
        cex.getConfusionMatrix().print();

        cex.add(ce);
        cex.getConfusionMatrix().print();
    }
}
