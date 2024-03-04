/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluation.performance;

/**
 *
 * @author rabcan
 */
public class BinaryClassificationStat {

    private double totoalClassified;
    private ConfusionMatrix matrix;

    /**
     * Positive class must be on index 0
     */
    public BinaryClassificationStat() {
        matrix = new ConfusionMatrix(2);
    }

    public BinaryClassificationStat(int classCount) {
        matrix = new ConfusionMatrix(classCount);
    }

    public ConfusionMatrix getConfusionMatrix() {
        return matrix;
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

    public void setTP(double value) {
        matrix.set(0, 0, value);
    }

    public void setFP(double value) {
        matrix.set(0, 1, value);
    }

    public void setFN(double value) {
        matrix.set(1, 0, value);
    }

    public void setTN(double value) {
        matrix.set(1, 1, value);
    }

    /**
     *
     * @return Accuracy = (TP + TN)/(TP + TN + FP + FN)
     */
    public double getAccuracy() {
        return (tp() + tn()) / (tp() + tn() + fp() + fn());
    }

    /**
     * Accuracy can be a misleading metric for imbalanced data sets. Consider a
     * sample with 95 negative and 5 positive values. Classifying all values as
     * negative in this case gives 0.95 accuracy score. There are many metrics
     * that don't suffer from this problem. For example, balanced accuracy
     * normalizes true positive and true negative predictions by the number of
     * positive and negative samples, respectively, and divides their sum by two
     *
     * @return Balanced Accuracy = (TP + TN)/(TP + TN + FP + FN)
     */
    public double getBalancedAccuracy() {
        return (getSensitivity() + getSpecificity()) / 2;
    }

    /**
     *
     * @return MCC = TP*TN - FP*FN / sqrt((TP+FP)(TP+FN)(TN+FP)(TN+FN))
     */
    public double getMCC() {
        return ((tp() * tn()) - (fp() * fn())) / (Math.sqrt((tp() + fp()) * (tp() + fn()) * (tn() + fp()) * (tn() + fn())));
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
     * @return Diagnostic odds ratio = DOR = (TP/FN)/(FP/TN)
     */
    public double getDOR() {
        return (tp() / fn()) / (fp() / tn());
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
    public double getFalseDiscoveryRate() {
        return fp() / (fp() + tp());
    }

    public double getFalseOmissionRate() {
        return 1 - getNegativePredictiveValue();
    }

    public double getMarkedness() {
        return getPrecision() + getNegativePredictiveValue() - 1;
    }

    public double getPrevalence() {
        return (tp() + fp()) / (tp() + tn() + fp() + fn());
    }

    /**
     * (LR+)
     *
     * @return
     */
    public double getPositiveLikelihoodRatio() {
        return getSensitivity() / getFalsePositiveRate();
    }

    public double getNegativeLikelihoodRatio() {
        return getFalseNegativeRate() / getSpecificity();
    }

    public double getFowlkesMallowsIndex() {
        return Math.sqrt(getPrecision() * getSensitivity());
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
        s += formatLn("Accuracy = (TP + TN)/(TP + TN + FP + FN)");
        s += formatLn("Balanced Accuracy = (Specificity + Sensitivity) / 2 ");

        s += formatLn("Sensitivity (TPR) = Recall = TP/(TP + FN)");
        s += formatLn("Specificity (TNR) = TN/(TN + FP)");
        s += formatLn("Precision (PPV) = TP/(TP + FP)");
        s += formatLn("Fowlkes–Mallows index = sqrt(PPV*TPR) ");

        s += formatLn("F1 Score = 2TP/(2TP + FP + FN)");

        s += formatLn("MCC = (TP*TN - FP*FN) / sqrt((TP+FP)(TP+FN)(TN+FP)(TN+FN)) ");
        s += formatLn("Youden's index = Specificity + Sensitivity - 1");
        s += formatLn("Jaccard index = TP/(TP + FP + FN)");

        s += formatLn("Negative Predictive Value (NPV) = TN / (TN + FN) ");
        s += formatLn("False Discovery Rate = FP / (FP + TP) ");
        s += formatLn("False Positive Rate (FPR) = FP / (FP + TN) ");
        s += formatLn("False Negative Rate (FNR) =  FN / (FN + TP) ");
        s += formatLn("Prevalance =  P / P + N");
        s += formatLn("False omission rate (FOR) = 1 - NPV ");

        s += formatLn("Markedness (MK) =  PPV + NPV − 1");

        s += formatLn("Negative likelihood ratio (LR−) = FNR/TNR ");
        s += formatLn("Positive likelihood ratio (LR+) = TPR/FPR ");
        s += formatLn("Diagnostic odds ratio = DOR = LR+/LR- =(TP/FN)/(FP/TN) ");

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
        sb.append("Testing  instances: ").append((int) totoalClassified).append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(this.matrix.toString());
        return sb.toString();
    }
}
