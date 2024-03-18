package demo;


import minig.classification.fdt.FDTu;
import minig.classification.fdt.FuzzyDecisionTree;
import minig.classification.mdd.MDD;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;
import projectutils.ProjectUtils;
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
        
      
        FuzzyDecisionTree fdt = new FDTu(); //new FDTo();
        fdt.setDataset(dataset);
        fdt.buildModel();
        //fdt.print();
        
        MDD decisionDiagram = new MDD(fdt);
        //String code = GraphvizScript.code(decisionDiagram);
        //ProjectUtils.toClipboard(code);
        //--kod v premennej code je mozne nakopirovat do stranky a diagram sa zobrazi:
        //                https://dreampuf.github.io/GraphvizOnline/
        //--kod by mal byt ulozeny aj v clipboard (po zbehnuti kodu staci stlacit ctrl+v)
        
        //--vypocet indexov

        DPLD dpld = new DPLD();
        String code;
        //--test algoritmov
//        MDD cofactored = dpld.COFACTOR(decisionDiagram, 3, 1);
//        code = GraphvizScript.code(cofactored);
//        ProjectUtils.toClipboard(code);
//
//        Function<Integer, Integer> lowerThanOne = x -> (x < 1) ? 1 : 0;
//        MDD transformed = dpld.TRANSFORM(cofactored, lowerThanOne);
//        code = GraphvizScript.code(transformed);
//        ProjectUtils.toClipboard(code);
        MDD dl = dpld.IDPLDTYPEIII(decisionDiagram, 3, 1, 2, 1);
        code = GraphvizScript.code(dl);
        ProjectUtils.toClipboard(code);

        
    }
    
    
    
}
