/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluation.performance;

/**
 *
 * @author jrabc
 */
public class ClassAnalyzer {

    private double falsePossitive;
    private double falseNegateive;
    private double truePossitive;
    private double trueNegative;
    private ClassificationPerformance perfomance;

    private final String className;
    private int classIndex;

    ClassAnalyzer(String className, int classIndex, ClassificationPerformance perfomance) {
        this.className = className;
        this.classIndex = classIndex;
        this.perfomance = perfomance;
    }

    void average(ClassAnalyzer c) {
        falsePossitive = (falsePossitive + c.falsePossitive) / 2;
        falseNegateive = (falseNegateive + c.falseNegateive) / 2;
        truePossitive = (truePossitive + c.truePossitive) / 2;
        trueNegative = (trueNegative + c.trueNegative) / 2;
    }

    public void reset() {
        falsePossitive = 0;
        falseNegateive = 0;
        truePossitive = 0;
        trueNegative = 0;
    }

//      Realita         Predikcia       
//TP    [ma chripku = ma chripku]
//TN	[nema chripku = nema chripku]
//FP	[nema chripku = ma chripku]
//FN	[ma chripku = nema chripku]
    public void addClassifyResult(int predictedClassIndex, int correctClassIndex) {
        if (predictedClassIndex == classIndex && correctClassIndex == classIndex) {
            truePossitive++;
        } else if (predictedClassIndex != classIndex && correctClassIndex != classIndex) {
            trueNegative++;
        } else if (predictedClassIndex == classIndex && correctClassIndex != classIndex) {
            falsePossitive++;
        } else if (predictedClassIndex != classIndex && correctClassIndex == classIndex) {
            falseNegateive++;
        }
    }

    /**
     * sensitivity or true positive rate (TPR)
     *
     * @return
     */
    public double getSensitivity() {
        if (truePossitive + falseNegateive == 0) {
            return 0;
        }
        final double[][] confusionMatrix = perfomance.getConfusionMatrix().getConfusionMatrix();
        double ret = 0;
        for (int i = 0; i < confusionMatrix.length; i++) {
            ret += confusionMatrix[i][classIndex];
        }
        ret = confusionMatrix[classIndex][classIndex] / ret;
        return ret;
    }

    /**
     * specificity (SPC) or true negative rate
     *
     * @return
     */
    public double getSpecificity() {
        if (trueNegative + falsePossitive == 0) {
            return 0;
        }
        final double[][] confusionMatrix = perfomance.getConfusionMatrix().getConfusionMatrix();
        double ret = 0;
        for (int i = 0; i < confusionMatrix.length; i++) {
            ret += confusionMatrix[classIndex][i];
        }
        if (ret == 0) {
            return 0;
        }
        ret = confusionMatrix[classIndex][classIndex] / ret;
        return ret;
    }

    /**
     * precision or positive predictive value (PPV)
     *
     * @return
     */
    public double getPrecision() {
        double ppv = truePossitive / (truePossitive + falsePossitive);
        return ppv;
    }

    /**
     * negative predictive value (NPV)
     *
     * @return
     */
    public double negativePredictiveValue() {
        double npv = trueNegative / (trueNegative + falseNegateive);
        return npv;
    }

    /**
     * fall-out or false positive rate (FPR)
     *
     * @return 1 - getSpecificity()
     */
    public double falsePositiveRate() {
        return 1 - getSpecificity();
    }

    /**
     * false negative rate (FNR)
     *
     * @return 1 - getSensitivity();
     */
    public double falseNegativeRate() {
        return 1 - getSensitivity();
    }

    /**
     * false negative rate (FNR)
     *
     * @return 1 - getPrecision();
     */
    public double falseDiscoveryRate() {
        return 1 - getPrecision();
    }

    /**
     * (TP + TN)/(TP+TN+FN+FP)
     *
     * @return
     */
    public double getClassAccuracy() {
        double accuracy = (truePossitive + trueNegative)
                / (truePossitive + trueNegative + falseNegateive + falsePossitive);
        return accuracy;
    }

    public double getClassError() {
        double accuracy = (falsePossitive + falseNegateive)
                / (truePossitive + trueNegative + falseNegateive + falsePossitive);
        return accuracy;
    }

    /**
     * F1 score is the harmonic mean of precision and sensitivity
     *
     * @return F1 score
     */
    public double getF1Score() {
        double tp2 = 2 * truePossitive;
        return (tp2) / (tp2 + falseNegateive + falsePossitive);
    }

    public String getClassName() {
        return className;
    }

    public int getClassIndex() {
        return classIndex;
    }

}
