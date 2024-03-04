/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visualization.graphviz.script;

import minig.classification.fdt.FuzzyDecisionTree;
import minig.classification.mdd.MDD;

/**
 *
 * @author rabcan
 */
public class GraphvizScript {

    public static String code(FuzzyDecisionTree fdt, boolean shortPrint) {
        FDTGraphvizScript code = new FDTGraphvizScript(fdt);
        return code.getGraphvizCode(shortPrint);
    }

    public static String code(FuzzyDecisionTree fdt, boolean shortPrint, int fontSize, boolean horizontal) {
        FDTGraphvizScript code = new FDTGraphvizScript(fdt);
        code.setHorizontal(horizontal);
        return code.getGraphvizCode(fontSize, shortPrint);
    }

    public static String code(FuzzyDecisionTree fdt) {
        FDTGraphvizScript code = new FDTGraphvizScript(fdt);
        return code.getGraphvizCode();
    }

    public static String codeHorizontal(FuzzyDecisionTree fdt) {
        FDTGraphvizScript code = new FDTGraphvizScript(fdt);
        return code.getGraphvizCode();
    }

    public static String code(MDD mdd) {
        MDDGraphvizScript code = new MDDGraphvizScript(mdd);
        return code.getGraphvizCode();
    }

}
