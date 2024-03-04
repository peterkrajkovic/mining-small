/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluation.performance;

import java.io.IOException;
import java.util.List;
import minig.classification.fdt.FDTu;
import minig.classification.fdt.FuzzyDecisionTree;
import minig.data.core.attribute.CategoricalAttr;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;
import minig.models.Classifier;
import projectutils.ConsolePrintable;
import static projectutils.ProjectUtils.getMaxValueIndex;

/**
 * Positive class must be on index 0
 *
 * @author rabcan
 */
public class BinaryClassificationPerformace implements ConsolePrintable {

    private DataSet dataset;
    private double totoalClassified;
    private Classifier classifier;
    private ConfusionMatrix matrix;

    /**
     * Positive class must be on index 0
     */
    public BinaryClassificationPerformace() {
    }

    public void setModel(Classifier classifier) {
        this.classifier = classifier;
        if (matrix != null) {
            this.matrix.setClassifier(classifier);
        }
    }

    public void setTestingDataset(DataSet testingDataset) {
        dataset = testingDataset;
    }

    public Classifier getClassifier() {
        return classifier;
    }

    public ConfusionMatrix getConfusionMatrix() {
        return matrix;
    }

    public void evalueate() {
        totoalClassified = 0;
        matrix = new ConfusionMatrix(classifier, dataset);
        CategoricalAttr output = dataset.getOutbputAttribute();
        for (int i = 0; i < dataset.getDataCount(); i++) {
            totoalClassified++;
            int predictedClass = getMaxValueIndex(getClassifier().classify(dataset.getInstance(i)));
            int correctClass = output.getClassIndex(i);
            matrix.addResult(predictedClass, correctClass);
        }
    }

    public List<ClassAnalyzer> getClassAnalyzers() {
        return null;
    }

    public double tp() {
        return matrix.get(0, 0);
    }

    public double fp() {
        return matrix.get(0, 1);
    }

    public double fn() {
        return matrix.get(1, 0);
    }

    public double tn() {
        return matrix.get(1, 1);
    }

    /**
     *
     * @return Accuracy = (TP + TN)/(TP + TN + FP + FN)
     */
    public double getAccuracy() {
        return (tp() + tn()) / (tp() + tn() + fp() + fn());
    }

    /**
     *
     * @return MCC = TP*TN - FP*FN / sqrt((TP+FP)(TP+FN)(TN+FP)(TN+FN))
     */
    public double getMCC() {
        return tp() * tn() - fp() * fn() / Math.sqrt((tp() + fp()) * (tp() + fn()) * (tn() + fp()) * (tn() + fn()));
    }

    /**
     *
     * @return Sensitivity = Recall = TP/(TP + FN)
     */
    public double getSensitivity() {
        return tp() / (tp() + fn());
    }

    /**
     *
     * @return tn()/(tn() + fp())
     */
    public double getSpecificity() {
        return tn() / (tn() + fp());
    }

    /**
     *
     * @return TP/(TP + FP)
     */
    public double getPrecision() {
        return tp() / (tp() + fp());
    }

    /**
     *
     * @return F1 Score = 2TP/(2TP + FP + FN)
     */
    public double getF1Score() {
        return (2 * tp()) / ((2 * tp()) + fp() + fn());
    }

    /**
     *
     * @return Jaccard index = TP/(TP + FP + FN)
     */
    public double getJaccardIndex() {
        return tp() / (tp() + fp() + fn());
    }

    /**
     *
     * @return Youden's index = Specificity + Sensitivity - 1
     */
    public double getYoudensIndex() {
        return getSpecificity() + getSensitivity() - 1;
    }

    /**
     *
     * @return NPV = TN / (TN + FN)
     */
    public double getNegativePredictiveValue() {
        return tn() / (tn() + fn());
    }

    /**
     *
     * @return FPR = FP / (FP + TN)
     */
    public double getFalsePositiveRate() {
        return fp() / (fp() + tn());
    }

    /**
     *
     * @return FDR = FP / (FP + TP)
     */
    public double FalseDiscoveryRate() {
        return fp() / (fp() + tp());
    }

    /**
     *
     * @return FN / (FN + TP)
     */
    public double getFalseNegativeRate() {
        return fn() / (fn() + tp());
    }

    public void printDescription() {
        System.out.println(getDescription());
    }

    /**
     *
     * @return Diagnostic odds ratio = DOR = (TP/FN)/(FP/TN)
     */
    public double getDOR() {
        return (tp() / fn()) / (fp() / tn());
    }

    public String getDescription() {
        String s = "";
        s += format("");
        s += format("Condition positive");
        s += formatLn("Condition negative");
        s += format("Predicted positive");
        s += format("True positive");
        s += formatLn("False positive");
        s += format("Predicted negative");
        s += format("False negative");
        s += formatLn("True negative");
        s += formatLn("");
        s += formatLn("");
        s += formatLn("Sensitivity = Recall = TP/(TP + FN)");
        s += formatLn("Specificity = TN/(TN + FP)");
        s += formatLn("Precision = TP/(TP + FP)");
        s += formatLn("Accuracy = (TP + TN)/(TP + TN + FP + FN)");
        s += formatLn("F1 Score = 2TP/(2TP + FP + FN)");
        s += formatLn("Diagnostic odds ratio = DOR = (TP/FN)/(FP/TN)");
        s += formatLn("MCC = (TP*TN - FP*FN) / sqrt((TP+FP)(TP+FN)(TN+FP)(TN+FN)) ");
        s += formatLn("Youden's index = Specificity + Sensitivity - 1");
        s += formatLn("Jaccard index = TP/(TP + FP + FN)");

        s += formatLn("False Positive Rate = FP / (FP + TN) ");
        s += formatLn("False Discovery Rate = FP / (FP + TP) ");
        s += formatLn("False Negative Rate =  FN / (FN + TP) ");
        s += formatLn("Negative Predictive Value = TN / (TN + FN) ");
        s += formatLn("iagnostic odds ratio = DOR = (TP/FN)/(FP/TN) ");
        
        return s;
    }

    private String format(String s) {
        return String.format("%-25s|", s);
    }

    private String formatLn(String s) {
        return String.format("%-25s" + System.lineSeparator(), s);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(500);
        sb.append("CLASSIFIER: ").append(classifier.getClass().getSimpleName()).append(System.lineSeparator());;
        sb.append("Training instances: ").append(classifier.getDataset().getDataCount()).append(System.lineSeparator());
        sb.append("Testing  instances: ").append((int) totoalClassified).append(System.lineSeparator());
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

        BinaryClassificationPerformace bc = new BinaryClassificationPerformace();
        bc.printDescription();
    }
}
