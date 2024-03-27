package demo;


import minig.classification.fdt.FDTu;
import minig.classification.fdt.FuzzyDecisionTree;
import minig.classification.mdd.MDD;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;
import projectutils.ProjectUtils;
import reliability.StructFunctionClassifier;
import visualization.graphviz.script.GraphvizScript;

import java.util.HashMap;

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
        
      
        FuzzyDecisionTree fdt = new FDTu(); //new FDTo();
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
        HashMap<Integer, String> logicalIndexAttributes = decisionDiagram.setLogicalLevels();
        //code = GraphvizScript.code(decisionDiagram);
        //ProjectUtils.toClipboard(code);
        //MDD dl = DPLDexamples.DPLD(decisionDiagram, 1, 0, 1);
        //code = GraphvizScript.code(dl);
        //ProjectUtils.toClipboard(code);
        System.out.println("vypocet pomocou tabulky");
        var SItable = DPLDexamples.SICalculation(decisionDiagram);
        StructFunctionClassifier cls = new StructFunctionClassifier(decisionDiagram, fdt.getDataset());
        cls.printImportance();
        //System.out.println(cls.derivate(3, 0, 1));
        System.out.println("vypocet pomocou MDD");
        for (int key : SItable.keySet()) {
            System.out.println(SItable.get(key) + " " + logicalIndexAttributes.get(key));
        }
        double si01 = DPLDexamples.derivate(decisionDiagram, 3,0,1);
        double si02 = DPLDexamples.derivate(decisionDiagram, 3,0,2);
        double si03 = DPLDexamples.derivate(decisionDiagram, 3,1,2);
        System.out.println(si01 + " " + si02 + " " + si03 + " dokopy: " + (si01 + si02 + si03));
    }
    
    
    
}
