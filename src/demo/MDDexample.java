package demo;


import minig.classification.fdt.FDTo;
import minig.classification.mdd.MDD;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;
import reliability.StructFunctionClassifier;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 * @author jrabc
 */
public class MDDexample {

    public static void main(String[] args) {

        DataSet dataset = DatasetFactory.getDataset(DatasetFactory.DataSetCode.HEART_DISEASES);
        //dataset.print();
        dataset = dataset.toFuzzyDataset(3);
        //dataset.print();
        FDTo fdt = new FDTo();
        fdt.setDataset(dataset);
        fdt.buildModel();
        //fdt.print();

        MDD decisionDiagram = new MDD(fdt);
        decisionDiagram.setUniqueNodes();
        //String code = GraphvizScript.code(decisionDiagram);
        //ProjectUtils.toClipboard(code);
        //--kod v premennej code je mozne nakopirovat do stranky a diagram sa zobrazi:
        //                https://dreampuf.github.io/GraphvizOnline/
        //--kod by mal byt ulozeny aj v clipboard (po zbehnuti kodu staci stlacit ctrl+v)

        //--vypocet indexov

        //decisionDiagram.setLogicalLevels();
        ImportanceIndex importanceIndex = new ImportanceIndex();

        /* Porovnanie rychlosti vypoctu */
//        long startTime = System.currentTimeMillis();
//        long endTime;
//        long totalTime;
//
//        System.out.println("vypocet pomocou tabulky");
//        startTime = System.currentTimeMillis();
//        StructFunctionClassifier cls = new StructFunctionClassifier(decisionDiagram, fdt.getDataset());
//        cls.printImportance();
//        endTime = System.currentTimeMillis();
//        totalTime = endTime - startTime;
//        System.out.println("Total time spent tabulka: " + totalTime + " milliseconds");
//
//        startTime = System.currentTimeMillis();
//        var SI = importanceIndex.SICalculationUsingSatisfyCount(decisionDiagram);
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

        System.out.println("vypocet pomocou tabulky:");
        stf.printImportance();

        System.out.println("vypocet pomocou MDD:");
        var der = importanceIndex.SICalculationUsingSatisfyCount(decisionDiagram);
        for (var key : der.keySet()) {
            System.out.printf("%.3f %s%n", der.get(key), key);
        }

        /*pre verifikaciu */
//        ArrayList<String> names = new ArrayList<String>(){{
//            add("age");
//            add("sex");
//            add("chest");
//            add("resting_blood_pressure");
//            add("serum_cholestoral");
//            add("fasting_blood_sugar");
//            add("resting_electrocardiographic_results");
//            add("maximum_heart_rate_achieved");
//            add("exercise_induced_angina");
//            add("oldpeak");
//            add("slope");
//            add("number_of_major_vessels");
//            add("thal");
//        }};;
//
//       for (var name : names) {
//            System.out.printf("%.3f %s%n", der.get(name), name);
//        }


        //System.out.println(stf.derivate(3,0,1,1,1));
        //System.out.println(importanceIndex.derivateUsingSatisfyCount(decisionDiagram,3,0,1,1,1));


//        double si01S = importanceIndex.derivateUsingSatisfyCount(decisionDiagram, 3, 0,1);
//        double si02S = importanceIndex.derivateUsingSatisfyCount(decisionDiagram, 3, 0,2);
//        double si03S = importanceIndex.derivateUsingSatisfyCount(decisionDiagram, 3, 1,2);
//        System.out.println(si01S + " " + si02S + " " + si03S + " dokopy: " + (si01S + si02S + si03S));


        //DPLDunordered unordered = new DPLDunordered();
        //decisionDiagram.setRandomLogicalLevels();
//        double ss1 = unordered.derivateUsingSatisfyCount(decisionDiagram,3,0,1);
//        double ss2 = unordered.derivateUsingSatisfyCount(decisionDiagram,3,0,2);
//        double ss3 = unordered.derivateUsingSatisfyCount(decisionDiagram,3,1,2);
//        System.out.println("vypocet pomocou neutriedeneho");
//        System.out.println(ss1 + " " + ss2 + " " + ss3 + " dokopy: " + (ss1 + ss2 + ss3));
    }

}
