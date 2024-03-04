/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluation.performance;

import java.io.IOException;
import java.util.List;
import minig.models.Classifier;
import minig.classification.fdt.FuzzyDecisionTree;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;
import minig.classification.fdt.FDTu;
import projectutils.ConsolePrintable;
import projectutils.ProjectUtils;
import minig.data.core.attribute.CategoricalAttr;

/**
 *
 * @author jrabc
 */
public class ConfusionMatrix implements ConsolePrintable {

    private DataSet dataset;
    private Classifier classifier;

    private double[][] matrix;

    private int maxStringLength = 5;

    public ConfusionMatrix(Classifier classifier, DataSet testingDataset) {
        this.classifier = classifier;
        dataset = testingDataset;
        final CategoricalAttr outputAttr = dataset.getOutbputAttribute();
        matrix = new double[outputAttr.getDomainSize()][outputAttr.getDomainSize()];
    }

    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

    public ConfusionMatrix(int classCount) {
        matrix = new double[classCount][classCount];
    }

    public Classifier getClassifier() {
        return classifier;
    }

    public void average(ConfusionMatrix m) {
        double[][] matrix2 = m.matrix;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = (matrix[i][j] + matrix2[i][j]) / 2;
            }
        }
    }

    public double get(int i, int j) {
        return matrix[i][j];
    }

    public void set(int i, int j, double value) {
        matrix[i][j] = value;
    }

    public void add(ConfusionMatrix m) {
        double[][] matrix2 = m.matrix;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] += matrix2[i][j];
            }
        }
    }

    public double getCountOfInstances() {
        double sum = 0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                sum += matrix[i][j];
            }
        }
        return sum;
    }

    public double getCorrectlyClassified() {
        double sum = 0;
        for (int i = 0; i < matrix.length; i++) {
            sum += matrix[i][i];
        }
        return sum;
    }

    public void addResult(int predictedClassIndex, int correctClassIndex) {
        matrix[predictedClassIndex][correctClassIndex] += 1;
    }

    public double[][] getConfusionMatrix() {
        return matrix;
    }

    @Override
    public String toString() {
        List<String> head = getMatrixHead();
        StringBuilder sb = new StringBuilder();
        sb.append("Predicated/True").append(System.lineSeparator());
        sb.append(String.format("%" + maxStringLength + "s", ""));
        for (String string : head) {
            sb.append(String.format("%" + maxStringLength + "s", string));
        }
        sb.append(System.lineSeparator());
        for (int row = 0; row < matrix.length; row++) {
            sb.append(String.format("%" + maxStringLength + "s  ", head.get(row)));
            for (int col = 0; col < matrix[row].length; col++) {
                sb.append(ProjectUtils.formatDouble(matrix[row][col])).append(" ");
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    public List<String> getMatrixHead() {
        List c = dataset.getOutbputAttribute().getDomainNames();
        return c;
    }

    public static void main(String[] args) throws IOException {
        DataSet dts = DatasetFactory.getDataset(DatasetFactory.GOLF_LINGVISTIC);
        dts.removeAttribue(1);
        dts.removeAttribue(1);
        dts.setOutputAttrIndex(dts.getAtributteCount() - 1);
        //     System.out.println(dt.getStringDataSet());
        DataSet ff = dts.getFuzzyDataset(3, 5);
        ff.print();
        FuzzyDecisionTree tree = new FDTu(0.0, 1, ff);
        tree.buildModel();
        System.out.println(tree.toString());
        ConfusionMatrix ce = new ConfusionMatrix(tree, ff);
        ce.print();
    }
}
