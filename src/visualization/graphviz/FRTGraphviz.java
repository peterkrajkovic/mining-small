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
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import minig.classification.fdt.FDTNode;
//import minig.classification.fdt.FDTu;
//import minig.classification.fdt.FuzzyDecisionTree;
//import minig.data.core.dataset.DataSet;
//import minig.data.core.dataset.NewInstance;
//import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;
//import minig.data.operators.RandomSpliter;
//import minig.prediction.frt.FRTu;
//import projectutils.ListUtils;
//import projectutils.ProjectUtils;
//import visualization.graphviz.script.GraphvizScript;
//
///**
// *
// * @author jrabc
// */
//public class FRTGraphviz {
//
//    private FRTu fdt;
//
//    public FRTGraphviz() {
//    }
//
//    public void setTree(FRTu fdt) {
//        this.fdt = fdt;
//    }
//
//    public static Graphviz getGraphviz(FRTu fdt) {
//
//        HashMap<Integer, Node> nodes = new HashMap<>();
//        for (FDTNode treenode : fdt) {
//            Node n = getInternalNode(treenode);
//            if (!nodes.containsKey(treenode.getId())) {
//                nodes.put(treenode.getId(), n);
//            } else {
//                n = nodes.get(treenode.getId());
//            }
//            int i = 0;
//            for (FDTNode child : treenode.getChildren()) {
//                Node childc;
//                ;
//                childc = getInternalNode(child);
//                String branchName = treenode.getAsocAttr().getValues().get(i++).getName();
//                nodes.put(child.getId(), childc);
//                nodes.put(treenode.getId(), n.link(Link.to(childc).with(Style.SOLID, Label.of(branchName), Color.BLACK)));
//                n = nodes.get(treenode.getId());
//            }
//        }
//
//        final Graph g = graph("ex1").directed().with(new ArrayList<>(nodes.values()));
//        Graphviz viz = Graphviz.fromGraph(g);
//        return viz;
//    }
//
//    private static Node getInternalNode(FDTNode treenode) {
//        if (!treenode.isLeaf()) {
//            return node(String.valueOf(treenode.getId())).with(
//                    Label.html(
//                            "{<b>" + treenode.getAsocAttr().getName() + "</b> "
//                            + "|"
//                            + "f = " + ProjectUtils.roundAndFormat(treenode.getFrequence(), 3)
//                            + "<br/>"
//                            +ProjectUtils.formatDouble(treenode.getPredictedValue()) + "}"
//                    ),
//                    Shape.RECORD).with("style", "rounded,filled,dashed").with("fillcolor", "white").with("margin", "0.2,0.1");
//        } else {
//            return node(String.valueOf(treenode.getId())).with(
//                    Label.html("{<b>" + ProjectUtils.formatDouble(treenode.getPredictedValue()) + "</b> "
//                            + "|"
//                            + "f = " + ProjectUtils.roundAndFormat(treenode.getFrequence(), 3)
//                            + "<br/>"
//                            + ProjectUtils.formatDouble(treenode.getPredictedValue()) + "}"
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
//         DataSet dts = DatasetFactory.getDataset(DatasetFactory.GOLF_REGRESSION_FUZZY);
//        dts.lingvisticToFuzzy();
//        RandomSpliter sp = new RandomSpliter(dts, 0.9, 1l);
//        sp.getTestingDataset().print();
//        dts.setOutputAttrIndex(dts.getAtributteCount() - 1);
//        FRTu regTree = new FRTu(sp.getTrainingDataset());
//        regTree.setAlfa(0.1);
//        regTree.buildModel();
//        regTree.print();
//        
//        ProjectUtils.toClipboard(GraphvizScript.code(regTree, false));
//        NewInstance ii = new NewInstance(Arrays.asList(Arrays.asList(0.3d, 0.3d, 0.33d), Arrays.asList(1d, 0d, 0d), Arrays.asList(1d, 0d), Arrays.asList(1d, 0d)));
//        ii.setDataset(dts);
//        double d = regTree.predict(sp.getTestingDataset().getInstance(0));
//        System.out.println(d);
//
//        FRTGraphviz g = new FRTGraphviz();
//        g.setTree(regTree);
//        g.show();
//
//    }
//}
