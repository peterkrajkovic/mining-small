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
//import minig.classification.fdt.FDTu;
//import minig.classification.mdd.MDD;
//import minig.classification.mdd.MDDnode;
//import minig.data.core.dataset.DataSet;
//import minig.data.core.dataset.NewInstance;
//import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;
//import minig.data.operators.RandomSpliter;
//import projectutils.ProjectUtils;
//import visualization.graphviz.script.GraphvizScript;
//
///**
// *
// * @author jrabc
// */
//public class MDDGraphviz {
//
//    private MDD mdd;
//
//    public MDDGraphviz() {
//    }
//
//    public void setTree(MDD fdt) {
//        this.mdd = fdt;
//    }
//
//    public static Graphviz getGraphviz(MDD fdt) {
//
//        HashMap<Integer, Node> nodes = new HashMap<>();
//        for (MDDnode treenode : fdt) {
//            Node n = getInternalNode(treenode);
//            if (!nodes.containsKey(treenode.getId())) {
//                nodes.put(treenode.getId(), n);
//            } else {
//                n = nodes.get(treenode.getId());
//            }
//            int i = 0;
//            for (MDDnode child : treenode.getChildren()) {
//                Node childc;
//                childc = getInternalNode(child);
//                String shortName = treenode.getAsocAttr().getName().substring(0, 1);
//                String index = (treenode.getAsocAttr().getAttributeIndex() + 1) + "," + (i++ + 1);
//                nodes.put(child.getId(), childc);
//                nodes.put(treenode.getId(), n.link(Link.to(childc).with(Style.SOLID, Label.html(getTable(shortName, index)), Color.BLACK)));
//                n = nodes.get(treenode.getId());
//            }
//        }
//
//        final Graph g = graph("ex1").directed().with(new ArrayList<>(nodes.values()));
//        Graphviz viz = Graphviz.fromGraph(g);
//        return viz;
//    }
//
//    private static String getTable(String name, String index) {
//        return " <TABLE border=\"0\" cellborder=\"0\" cellspacing=\"0\">\n"
//                + "    <TR><TD rowspan=\"2\">"+name+"</TD><TD>"+"<br/>"+"</TD></TR>\n"
//                + "    <TR><TD>"+index+"</TD></TR>\n"
//                + "    </TABLE>";
//    }
//
//    private static Node getInternalNode(MDDnode treenode) {
//        if (treenode.isLeaf()) {
//            return node(String.valueOf(treenode.getId())).with(
//                    Label.html(
//                            (int) treenode.getOutputClass() + ""
//                    ),
//                    Shape.RECORD).with("style", "rounded,filled,dashed").with("fillcolor", "white").with("margin", "0.2,0.1");
//        } else {
//            return node(String.valueOf(treenode.getId())).with(
//                    Label.html(
//                            treenode.getAsocAttr().getName()
//                    ),
//                    Shape.RECORD).with("style", "rounded,filled,bold").with("fillcolor", "gray91").with("margin", "0.1,0.1");
//        }
//    }
//
//    public void show() {
//        AwtVisualizer.display(getGraphviz(mdd));
//    }
//
//    public static void main(String[] args) {
//        DataSet dts = DatasetFactory.getDataset(DatasetFactory.CARS);
//        dts.lingvisticToFuzzy();
//        RandomSpliter sp = new RandomSpliter(dts, 0.9, 1l);
//        sp.getTestingDataset().print();
//        dts.setOutputAttrIndex(dts.getAtributteCount() - 1);
//        FDTu regTree = new FDTu(sp.getTrainingDataset());
//        regTree.setAlfa(0.1);
//        regTree.buildModel();
//        regTree.print();
//
//        ProjectUtils.toClipboard(GraphvizScript.code(regTree, false));
//        NewInstance ii = new NewInstance(Arrays.asList(Arrays.asList(0.3d, 0.3d, 0.33d), Arrays.asList(1d, 0d, 0d), Arrays.asList(1d, 0d), Arrays.asList(1d, 0d)));
//        ii.setDataset(dts);
//
//        MDDGraphviz g = new MDDGraphviz();
//        MDD m = new MDD(regTree);
//        g.setTree(m);
//        g.show();
//
//    }
//}
