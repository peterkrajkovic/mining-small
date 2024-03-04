/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visualization.graphviz.script;

import minig.classification.mdd.MDD;
import minig.classification.mdd.MDDnode;
import projectutils.ProjectUtils;

/**
 *
 * @author rabcan
 */
public class MDDGraphvizScript {

    private MDD fdt;

    public MDDGraphvizScript(MDD fdt) {
        this.fdt = fdt;
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
                //+ "     rankdir=LR \n" //HORIZONTAL
                + "     overlap=prism \n"
                + "     overlap_scaling=0.01 \n"
                + "     ratio=0.0 \n"
                + "     graph [dpi = 100 ];");
        fdt.forEachNode((node) -> {
            if (shortPrint) {
                sb.append(System.lineSeparator()).append(getshortNodeCode(node, fontSize));
            } else {
                sb.append(System.lineSeparator()).append(getNodeCode(node, ""));
            }
        });
        sb.append(System.lineSeparator()).append("}");
        return sb.toString();
    }

    public String getNodeCode(MDDnode node, String classs) {
        String s;
        int fontSize = 26;
        String fontType = "Cambria";
        if (node.isLeaf()) {
            s = node.getId() + " [fillcolor=gray91, style=\"rounded,filled,bold\", fontname=\"" + fontType + "\", fontsize=\"" + fontSize + "\", shape=record, "
                    + "label=\"{"
                    + (int)node.getOutputClass() 
                    + "}\"];";
        } else {
            s = node.getId() + " [style=rounded, shape=record, fontsize=\"" + fontSize + "\" ,fontname=\"" + fontType + "\" "
                    + "label=\"{"
                    + node.getAsocAttr().getName() 
                    + "}\"];";
        }

        for (int i = 0; i < node.getChildren().size(); i++) {
            MDDnode child = node.getChildren().get(i);
            s += System.lineSeparator();
            String shortName = node.getAsocAttr().getName().substring(0, 1);
            String index = (node.getAsocAttr().getAttributeIndex() + 1) + "," + (i + 1);
            s += node.getId() + " -> " + child.getId() + "[ fontsize=\"" + fontSize + "\" ,fontname=\"" + fontType + "\", "
                    + "label=< " + shortName + "<sub>" + index + "</sub>" + ">]";
        }
        return s;
    }

    public String getshortNodeCode(MDDnode node, int fontSize) {

        String s;
        String fontType = "Cambria";
        if (node.isLeaf()) {
            s = node.getId() + " [fillcolor=gray91, style=\"rounded,filled,bold\", fontname=\"" + fontType + "\", fontsize=\"" + fontSize + "\", shape=record, "
                    + "label=<{"
                    + "Value: " + ProjectUtils.formatDouble(node.getOutputClass()) + "|"
                    // + "Class: " + ProjectUtils.getMaxValueIndex(confidanceLevels) + 1 + "|"
                    + "}>];";
        } else {
            String shortName = node.getAsocAttr().getName().substring(0, 1);
            String index = (node.getAsocAttr().getAttributeIndex() + 1) + "";

            //  0 [style=rounded, shape=record, fontsize="14" , label=<{<B><I>A</I><sub>1</sub></B>|f = 1.000<BR/>[0.500, 0.500]}>];
            s = node.getId() + " [style=rounded, shape=record, fontsize=\"" + fontSize + "\" ,fontname=\"" + fontType + "\" "
                    + ",label=<{"
                    + shortName + "<sub>" + index + "</sub>" + "|"
                    + ProjectUtils.formatDouble(node.getOutputClass())
                    + "}>];";
        }
        for (int i = 0; i < node.getChildren().size(); i++) {
            MDDnode child = node.getChildren().get(i);
            s += System.lineSeparator();
            String shortName = node.getAsocAttr().getName().substring(0, 1);
            String index = (node.getAsocAttr().getAttributeIndex() + 1) + "," + (i + 1);
            s += node.getId() + " -> " + child.getId() + "[ fontsize=\"" + fontSize + "\" ,fontname=\"" + fontType + "\", "
                    + "label=< " + shortName + "<sub>" + index + "</sub>" + ">]";
        }
        return s;
    }

}
