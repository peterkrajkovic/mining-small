///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package visualization.graphviz;
//
//import guru.nidi.graphviz.attribute.Color;
//import guru.nidi.graphviz.attribute.Label;
//import guru.nidi.graphviz.attribute.Shape;
//import guru.nidi.graphviz.attribute.Style;
//import guru.nidi.graphviz.engine.Graphviz;
//import static guru.nidi.graphviz.model.Factory.graph;
//import static guru.nidi.graphviz.model.Factory.node;
//import guru.nidi.graphviz.model.Graph;
//import guru.nidi.graphviz.model.Link;
//import guru.nidi.graphviz.model.Node;
//import java.awt.Frame;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import minig.classification.fdt.FDTNode;
//import minig.classification.fdt.FDTu;
//import minig.classification.fdt.FuzzyDecisionTree;
//import minig.data.core.dataset.DataSet;
//import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;
//import projectutils.ListUtils;
//import projectutils.ProjectUtils;
//
///**
// *
// * @author rabcan
// */
//public class FDTGraph extends Frame {
//
//    private FuzzyDecisionTree fdt;
//
//    public FDTGraph() {
//    }
//
//    public void setFdt(FuzzyDecisionTree fdt) {
//        this.fdt = fdt;
//    }
//
//    public static Graphviz getGraphviz(FuzzyDecisionTree fdt) {
//
//        List<String> classes = fdt.getDataset().getOutbputAttribute().getDomainNames();
//
//        HashMap<Integer, Node> nodes = new HashMap<>();
//        for (FDTNode treenode : fdt) {
//            Node n = getInternalNode(treenode, classes);
//            if (!nodes.containsKey(treenode.getId())) {
//                nodes.put(treenode.getId(), n);
//            } else {
//                n = nodes.get(treenode.getId());
//            }
//            int i = 0;
//            for (FDTNode child : treenode.getChildren()) {
//                Node childc;
//                ;
//                childc = getInternalNode(child, classes);
//                String branchName = treenode.getAsocAttr().getValues().get(i++).getName();
//                nodes.put(child.getId(), childc);
//                nodes.put(treenode.getId(), n.link(Link.to(childc).with(Style.BOLD, Label.of(branchName), Color.BLACK)));
//                n = nodes.get(treenode.getId());
//            }
//        }
//
//        final Graph g = graph("ex1").directed()
//                //.graphAttr().with(Rank.dir(Rank.RankDir.LEFT_TO_RIGHT))
//                //      .nodeAttr().with(Font.name("arial"))
//                //    .linkAttr().with("class", "link-class")
//                .with(new ArrayList<>(nodes.values()));
//        Graphviz viz = Graphviz.fromGraph(g);
//        return viz;
//    }
//
//    private static Node getInternalNode(FDTNode treenode, List<String> classes) {
//        if (!treenode.isLeaf()) {
//            return node(String.valueOf(treenode.getId())).with(
//                    Label.html(
//                            "{<b>" + treenode.getAsocAttr().getName() + "</b>"
//                            + "|"
//                            + "I = " + ProjectUtils.roundAndFormat(treenode.getCriterionValue(), 3)
//                            + "<br/>"
//                            + "f = " + ProjectUtils.roundAndFormat(treenode.getFrequence(), 3)
//                            + "<br/>"
//                            + ListUtils.listToString(treenode.getConfidenceLevels()) + "}"
//                    ),
//                    Shape.RECORD).with("style", "rounded,filled,dashed").with("fillcolor", "white").with("margin", "0.1,0.1");
//        } else {
//            int classIndex = ProjectUtils.getMaxValueIndex(treenode.getConfidenceLevels());
//            return node(String.valueOf(treenode.getId())).with(
//                    Label.html("{<b>" + classes.get(classIndex) + "</b>"
//                            + "|"
//                            + "f = " + ProjectUtils.roundAndFormat(treenode.getFrequence(), 3)
//                            + "<br/>"
//                            + ListUtils.listToString(treenode.getConfidenceLevels()) + "}"
//                    ),
//                    Shape.RECORD).with("style", "rounded,filled,bold").with("fillcolor", "gray91").with("margin", "0.1,0.1");
//        }
//    }
//
//    public void show() {
//        AwtVisualizer.display(getGraphviz(fdt));
//    }
//
//    public static void main(String[] args) {
//        DataSet dt = DatasetFactory.getDataset(DatasetFactory.DataSetCode.IRIS).toFuzzyDataset(3);
//        FuzzyDecisionTree fdt = new FDTu();
//        fdt.setDataset(dt);
//        fdt.buildModel();
//
//        FDTGraph g = new FDTGraph();
//        g.setFdt(fdt);
//        g.show();
//
//    }
//}
