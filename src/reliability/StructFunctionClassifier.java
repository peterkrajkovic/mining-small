/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reliability;

import java.util.List;
import minig.classification.fdt.FDTu;
import minig.classification.fdt.FuzzyDecisionTree;
import minig.classification.mdd.MDD;
import minig.data.core.attribute.Attribute;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.NewInstance;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;
import minig.models.Classifier;
import projectutils.ArrayUtils;
import projectutils.ProjectUtils;
import projectutils.stat.combinations.IterativeCartesianProduct;
import visualization.graphviz.script.GraphvizScript;

/**
 *
 * @author jrabc
 */
public class StructFunctionClassifier {

    private Classifier cls;
    private DataSet dataset;
    private List<Attribute> inputAttrs;
    private NewInstance instance;

    public StructFunctionClassifier(Classifier classifier) {
        this.cls = classifier;
        dataset = classifier.getDataset();
        inputAttrs = dataset.getInputAttrs();
        instance = dataset.getInstance(0);
    }

    public StructFunctionClassifier(Classifier classifier, DataSet dt) {
        this.cls = classifier;
        dataset = dt;
        inputAttrs = dataset.getInputAttrs();
        instance = dataset.getInstance(0);
    }

    public IterativeCartesianProduct getCombinationIterator() {
        IterativeCartesianProduct product = new IterativeCartesianProduct();
        for (Attribute attribute : inputAttrs) {
            product.addContainer(ArrayUtils.makeIntegerSequence(0, attribute.getDomainSize() - 1));
        }
        return product;
    }
    public int[] getVector() {
        IterativeCartesianProduct stat = getCombinationIterator();
        int[] vector = new int[(int) stat.getCombinationCount()];
        NewInstance instance = dataset.getInstance(0);
        int i =0;
        for (Object[] objects : stat) {
            setInstance(objects, instance);
            int newPerformance = ProjectUtils.getMaxValueIndex(cls.classify(instance));
            vector[i++] = newPerformance;
        }
        return vector;
    }

    private void setInstance(Object[] combination, NewInstance instance) {
        for (int i = 0; i < combination.length; i++) {
            Object object = combination[i];
            setValue(instance, i, (int) object);
        }
    }

    private NewInstance getInstance(Object[] combination) {
        setInstance(combination, instance);
        return instance;
    }

    /**
     *
     * @return percentual changes in output
     */
    public double derivate(int componentIndex, int stateFrom, int stateTo) {
        IterativeCartesianProduct combinations = getCombinations(componentIndex, stateFrom);
        double change = 0;
        int all = 0;
        for (Object[] combination : combinations) {
            all++;
            NewInstance instance = getInstance(combination);
            int oldPerformance = ProjectUtils.getMaxValueIndex(cls.classify(instance));
            setValue(instance, componentIndex, stateTo);
            int newPerformance = ProjectUtils.getMaxValueIndex(cls.classify(instance));
            if (oldPerformance != newPerformance) {
                change++;
            }
        }
        return change / all;
    }

    /**
     *
     * @return percentual changes in output
     */
    public double derivate(int componentIndex, int stateFrom, int stateTo, int systemStateFrom, int systemStateTo) {
        IterativeCartesianProduct combinations = getCombinations(componentIndex, stateFrom);
        double change = 0;
        int all = 0;
        for (Object[] combination : combinations) {

            NewInstance instance = getInstance(combination);
            int oldPerformance = ProjectUtils.getMaxValueIndex(cls.classify(instance));
            if (oldPerformance == systemStateFrom) {
                all++;
                setValue(instance, componentIndex, stateTo);
                int newPerformance = ProjectUtils.getMaxValueIndex(cls.classify(instance));
                if (newPerformance == systemStateTo) {
                    change++;
                }
            }
        }
        return change / all;
    }

    public double derivate(int componentIndex) {
        double si = 0;
        final Attribute component = dataset.getAttribute(componentIndex);
        for (int i = 0; i < component.getDomainSize(); i++) {
            for (int j = 0; j < component.getDomainSize(); j++) {
                if (i == j) {
                    continue;
                }
                si += derivate(componentIndex, i, j);
            }

        }
        return si;
    }

    public void printImportance() {
        for (int cmpIndex = 0; cmpIndex < inputAttrs.size(); cmpIndex++) {
            System.out.println(ProjectUtils.formatDouble(derivate(cmpIndex)) + " " + inputAttrs.get(cmpIndex).getName());
        }
    }

    public IterativeCartesianProduct getCombinations(int componentIndex, int componentState) {
        IterativeCartesianProduct product = new IterativeCartesianProduct();
        for (Attribute attribute : inputAttrs) {
            if (attribute.getAttributeIndex() == componentIndex) {
                product.addObjContainer(componentState);
            } else {
                product.addContainer(ArrayUtils.makeIntegerSequence(0, attribute.getDomainSize() - 1));
            }
        }
        return product;
    }
//    

    private void setValue(NewInstance instance, int i, int object) {
        if (inputAttrs.get(i).isFuzzy()) {
            instance.setValue(inputAttrs.get(i).fuzzy(), object);
        } else if (inputAttrs.get(i).isLinguistic()) {
            instance.setValue(inputAttrs.get(i).linguistic(), object);
        }
    }

    public static void main(String[] args) {

//        DataSet iris = DatasetFactory.getDataset(0);
//        iris.setOutputAttr();
//        iris = Fuzzification.byKMeansWH(iris, new Triangular());
//        FuzzyDecisionTree fdt = new FDTu();
//        fdt.setDataset(iris);
//        fdt.buildModel();
//
//        StructFunctionClassifier cls = new StructFunctionClassifier(fdt);
//        double d = cls.derivate(0, 0, 1);
//        System.out.println(d);
//
//        StructureFunctionCls f = ReliabilityUtils.structureFunction(fdt);
//        System.out.println(f.getSIChange(0, 0, 1));
        DataSet dataset = DatasetFactory.getDataset(DatasetFactory.DataSetCode.IRIS);
        //dataset.print();
        dataset = dataset.toFuzzyDataset(3);
        //dataset.print();

        FuzzyDecisionTree fdt = new FDTu(); //new FDTo();
        fdt.setDataset(dataset);
        fdt.buildModel();
        //fdt.print();

        MDD decisionDiagram = new MDD(fdt);
        String code = GraphvizScript.code(decisionDiagram);
        ProjectUtils.toClipboard(code);

        StructFunctionClassifier cls = new StructFunctionClassifier(decisionDiagram, fdt.getDataset());
        System.out.println(cls.derivate(3, 2, 1));
        System.out.println(cls.derivate(3, 2, 1, 2, 1));
    }

}
