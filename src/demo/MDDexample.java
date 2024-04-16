package demo;


import minig.classification.fdt.FDTo;
import minig.classification.fdt.FuzzyDecisionTree;
import minig.classification.mdd.MDD;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;
import projectutils.ProjectUtils;
import reliability.StructFunctionClassifier;
import visualization.graphviz.script.GraphvizScript;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author jrabc
 */
public class MDDexample {
    
    public static void main(String[] args) {
        
        DataSet dataset = DatasetFactory.getDataset(DatasetFactory.DataSetCode.IRIS);
        //dataset.print();
        dataset = dataset.toFuzzyDataset(3);
        //dataset.print();
        
      
        FuzzyDecisionTree fdt = new FDTo(); //new FDTo();
        fdt.setDataset(dataset);
        fdt.buildModel();
        //fdt.print();
        
        MDD decisionDiagram = new MDD(fdt);
        String code = GraphvizScript.code(decisionDiagram);
        ProjectUtils.toClipboard(code);
        //--kod v premennej code je mozne nakopirovat do stranky a diagram sa zobrazi:
        //                https://dreampuf.github.io/GraphvizOnline/
        //--kod by mal byt ulozeny aj v clipboard (po zbehnuti kodu staci stlacit ctrl+v)
        
        //--vypocet indexov
        decisionDiagram.setLogicalLevels();
        code = GraphvizScript.code(decisionDiagram);
        ProjectUtils.toClipboard(code);
        DPLDexamples dpldexamples = new DPLDexamples();
        //MDD dl = DPLDexamples.DPLD(decisionDiagram, 1, 0, 1);
        //code = GraphvizScript.code(dl);
        //ProjectUtils.toClipboard(code);


//        long startTime = System.currentTimeMillis();
//        var SItable = DPLDexamples.SICalculation(decisionDiagram);
//        long endTime = System.currentTimeMillis();
//        long totalTime = endTime - startTime;
//        System.out.println("Total time spent custom MDD: " + totalTime + " milliseconds");
//        System.out.println("vypocet pomocou custom MDD");
//        for (String key : SItable.keySet()) {
//            System.out.println(SItable.get(key) + " " + key);
//        }
//        System.out.println("vypocet pomocou tabulky");
//        startTime = System.currentTimeMillis();
//        StructFunctionClassifier cls = new StructFunctionClassifier(decisionDiagram, fdt.getDataset());
//        cls.printImportance();
//        endTime = System.currentTimeMillis();
//        totalTime = endTime - startTime;
//        System.out.println("Total time spent tabulka: " + totalTime + " milliseconds");
//
//
//
//        startTime = System.currentTimeMillis();
//        var SI = dpldexamples.SICalculationUsingSatisfyCount(decisionDiagram);
//        endTime = System.currentTimeMillis();
//        totalTime = endTime - startTime;
//        System.out.println("Total time spent satisfy: " + totalTime + " milliseconds");
//        System.out.println("vypocet pomocou MDD satisfy");
//        for (String key : SI.keySet()) {
//            System.out.println(SI.get(key) + " " + key);
//        }

        StructFunctionClassifier stf = new StructFunctionClassifier(decisionDiagram, dataset);
        //System.out.println(stf.derivate(3,0,1));
        //System.out.println(stf.derivate(3,0,2));

        //System.out.println(Arrays.toString(stf.getVector()));

        System.out.println("vypocet pomocou tabulky");
        System.out.println("index 2: 0->1, " + stf.derivate(3,0,1));
        System.out.println("index 2: 0->2, " + stf.derivate(3,0,2));
        System.out.println("index 2: 1->2, " + stf.derivate(3,1,2));
        stf.printImportance();
        //startTime = System.currentTimeMillis();
        //double si01 = DPLDexamples.derivate(decisionDiagram, 3,0,1);
       // double si02 = DPLDexamples.derivate(decisionDiagram, 3,0,2);
       // double si03 = DPLDexamples.derivate(decisionDiagram, 3,1,2);
//        endTime = System.currentTimeMillis();
//        totalTime = endTime - startTime;
//
//        System.out.println("Total time spent: " + totalTime + " milliseconds");
//
//        startTime = System.currentTimeMillis();
        DPLDexamples dplDexamples = new DPLDexamples();
        var der = dplDexamples.SICalculationUsingSatisfyCount(decisionDiagram);
        for (var key : der.keySet()) {
            System.out.println(key + ": " + der.get(key));
        }
        double si01S = dplDexamples.derivateUsingSatisfyCount(decisionDiagram, 3, 0,1);
        //System.out.println(si01S);
        double si02S = dplDexamples.derivateUsingSatisfyCount(decisionDiagram, 3, 0,2);
        double si03S = dplDexamples.derivateUsingSatisfyCount(decisionDiagram, 3, 1,2);
//        endTime = System.currentTimeMillis();
//        totalTime = endTime - startTime;
//
//        System.out.println("Total time spent: " + totalTime + " milliseconds");
        System.out.println("vypocet pomocou satisfyCount");
        System.out.println(si01S + " " + si02S + " " + si03S + " dokopy: " + (si01S + si02S + si03S));
        //System.out.println("vypocet pomocou custom algoritmu");
        //System.out.println(si01 + " " + si02 + " " + si03 + " dokopy: " + (si01 + si02 + si03));


        DPLDunordered unordered = new DPLDunordered();
        //decisionDiagram.setRandomLogicalLevels();
        double ss1 = unordered.derivateUsingSatisfyCount(decisionDiagram,3,0,1);
        double ss2 = unordered.derivateUsingSatisfyCount(decisionDiagram,3,0,2);
        double ss3 = unordered.derivateUsingSatisfyCount(decisionDiagram,3,1,2);
        System.out.println("vypocet pomocou neutriedeneho");
        System.out.println(ss1 + " " + ss2 + " " + ss3 + " dokopy: " + (ss1 + ss2 + ss3));
    }

}
