/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visualization.graphviz.script;

import java.util.List;
import minig.classification.fdt.FDTNode;
import minig.classification.fdt.FuzzyDecisionTree;
import projectutils.ProjectUtils;

/**
 *
 * @author rabcan
 */
public class FDTGraphvizScript {

    int fontSize = 26;
    String fontType = "Cambria";
    private FuzzyDecisionTree fdt;

    private boolean horizontal = false;

    public FDTGraphvizScript(FuzzyDecisionTree fdt) {
        this.fdt = fdt;
    }

    public static String code(FuzzyDecisionTree fdt, boolean shortPrint) {
        FDTGraphvizScript code = new FDTGraphvizScript(fdt);
        return code.getGraphvizCode(shortPrint);
    }

    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    public String code(FuzzyDecisionTree fdt) {
        FDTGraphvizScript code = new FDTGraphvizScript(fdt);
        return code.getGraphvizCode();
    }

    public String code(FuzzyDecisionTree fdt, int fontSize, boolean shortPrint) {
        FDTGraphvizScript code = new FDTGraphvizScript(fdt);
        return code.getGraphvizCode(fontSize, shortPrint);
    }

    public String getGraphvizCode(boolean shortPrint) {
        return getGraphvizCode(20, shortPrint);
    }

    public String getGraphvizCode() {
        return getGraphvizCode(20, false);
    }

    public String getGraphvizCode(int fontSize, boolean shortPrint) {
        StringBuilder sb = new StringBuilder(10000);
        sb.append("digraph tree {\n"
                + "     fontname=\"Cambria\"\n"
                //  + "     rankdir=LR \n" //HORIZONTAL
                + "     overlap=prism \n"
                + "     overlap_scaling=0.01 \n"
                + "     ratio=0.0 \n"
                + "     nodesep=auto \n" //nodesep=0.5
                + "     graph [dpi = 60 ];\n");

        sb.append("node[ fontname=\"" + fontType + "\", fontsize=\"" + fontSize + "\"]\n\n");
        sb.append("edge[ fontname=\"" + fontType + "\", fontsize=\"" + fontSize + "\"]\n\n");
        if (horizontal) {
            sb.append(" rankdir=LR\n");
        }

        sb.append(System.lineSeparator());
        fdt.forEachNode((node) -> {
            if (shortPrint) {
                sb.append(System.lineSeparator()).append(getshortNodeCode(node, fontSize));
            } else {
                sb.append(System.lineSeparator()).append(getNodeCode(node, fdt.getDataset().getOutbputAttribute().categorical().getDomainNames()));
            }
        });
        sb.append(System.lineSeparator()).append("}");
        return sb.toString();
    }

    public String getNodeCode(FDTNode node, List<String> classes) {

        String s;

        if (node.isIsLeeaf()) {
            int classIndex = ProjectUtils.getMaxValueIndex(node.getConfidenceLevels());
            s = node.getId() + " [fillcolor=gray91, style=\"filled,bold\", shape=record, "
                    + "label=\"{"
                    + classes.get(classIndex) + "|"
                    + "f = " + ProjectUtils.formatDouble(ProjectUtils.round(node.getFrequence(), 3)) + "\\n"
                    + ProjectUtils.listToString(node.getConfidenceLevels())
                    + "}\"];";
//            s =node.getId() + " [color=gray91, style=\"rounded,filled\", fontname=\"" + fontType + "\", fontsize=\"" + fontSize + "\", style=\"rounded,filled\", shape=record, "
//                    + "label=\"{"
//                    + "Class: " + ProjectUtils.getMaxValueIndex(node.getConfidenceLevels()) + "\\n"
//                    + "f = " + formatDouble(getFrequence()) + "\\n"
//                    +ProjectUtils.listToString(confidanceLevels)
//                    + "}\"];";
        } else {
            s = node.getId() + " [style=\"dashed\", shape=record "
                    + "label=\"{"
                    + node.getAsocAttr().getName() + "|"
                    + "f = " + ProjectUtils.formatDouble(ProjectUtils.round(node.getFrequence(), 3)) + "\\n"
                    + ProjectUtils.listToString(node.getConfidenceLevels())
                    + "}\"];";
        }

        for (FDTNode child : node.getChildren()) {
            s += System.lineSeparator();
            int index = child.getChildIndex();
            String name = child.getParent().getAsocAttr().getValues().get(index).getName();
            s += node.getId() + " -> " + child.getId() + "[label=<<I> " + name + "</I>" + ">]";
        }
        return s;
    }

    public String getshortNodeCode(FDTNode node, int fontSize) {

        String s;
        String fontType = "Cambria";
        if (node.isIsLeeaf()) {
            s = node.getId() + " [fillcolor=gray91, margin=\"0.2,0.2\", style=\"filled,bold\", shape=record, "
                    + "label=<{"
                    + "Class: " + "B" + "<sub>" + (ProjectUtils.getMaxValueIndex(node.getConfidenceLevels()) + 1) + "</sub>" + "|"
                    // + "Class: " + ProjectUtils.getMaxValueIndex(confidanceLevels) + 1 + "|"
                    + "<I>f</I> = " + ProjectUtils.formatDouble(node.getFrequence()) + "<BR/>"
                    + ProjectUtils.listToString(node.getConfidenceLevels())
                    + "}>];";
        } else {
            String shortName = node.getAsocAttr().getName().substring(0, 1);
            String index = (node.getAsocAttr().getAttributeIndex() + 1) + "";

            //  0 [style=rounded, shape=record, fontsize="14" , label=<{<B><I>A</I><sub>1</sub></B>|f = 1.000<BR/>[0.500, 0.500]}>];
            s = node.getId() + " [style=dashed, margin=\"0.2,0.2\", shape=record"
                    + ",label=<{"
                    + shortName + "<sub>" + index + "</sub>" + "|"
                    + "<I>f</I> = " + ProjectUtils.formatDouble(node.getFrequence()) + "<BR/>"
                    + ProjectUtils.listToString(node.getConfidenceLevels())
                    + "}>];";
        }
        for (FDTNode child : node.getChildren()) {
            s += System.lineSeparator();
            String shortName = node.getAsocAttr().getName().substring(0, 1);
            String index = (node.getAsocAttr().getAttributeIndex() + 1) + "," + (child.getChildIndex() + 1);
            s += node.getId() + " -> " + child.getId() + " ["
                    + "label=< " + shortName + "<sub>" + index + "</sub>" + "<BR/><BR/>" + ">]";
        }
        return s;
    }

}
