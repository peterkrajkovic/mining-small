/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.classification.mdd;

import minig.classification.fdt.FDTu;
import minig.classification.fdt.FuzzyDecisionTree;
import minig.classification.trees.ClassificationTree;
import minig.classification.trees.ClassificationTreeNode;
import minig.classification.trees.Tree;
import minig.classification.trees.TreeNode;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;
import projectutils.ConsolePrintable;
import projectutils.ProjectUtils;
import visualization.graphviz.script.GraphvizScript;

import java.util.*;
import java.util.function.Consumer;

/**
 *
 * @author rabcan
 */
public class MDD extends Tree<MDDnode> implements ConsolePrintable, Classifier {

    private MDDnode root;
    private List<MDDnode> leaves = new ArrayList<>();
    private int classCount;
    private int variableCount = 0;

    public MDD() {
    }
    
     public MDD(MDDnode root) {
        this.root = root;
        findLeaves(root,0);
    }
	
	
    @Override
    public List<Double> classify(Instance instance) {
        MDDnode n = getRoot();
        int pathIndex = 0;
        while (!n.isLeaf()) {
            CategoricalAttr asoc = n.getAsocAttr().categorical();
            if (asoc.isCategorical()) {
                pathIndex = instance.getClassIndex(asoc);
            }
            n = n.getChildren().get(pathIndex);
        }
        int domainSize = instance.getDataset().getOutbputAttribute().getDomainSize();
        DoubleVector dv = DoubleVector.zeros(domainSize);
        dv.setNum((int) n.getOutputClass(), 1);
        return dv;
    }

    @Override
    public <T extends DataSet> T getDataset() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void setDataset(DataSet dt) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void buildModel() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }



    // Helper method to find all leaves in the MDD
    private void findLeaves(MDDnode node, int level) {
        node.setLevel(level);
        if (node.isLeaf()) {
            if (!leaves.contains(node)){
                leaves.add(node);
            }
        } else {

            for (MDDnode child : node.getChildren()) {
                findLeaves(child, level + 1);
            }
        }
    }
    
    public MDD(ClassificationTree tree) {
        classCount = tree.getDataset().getOutbputAttribute().getDomainSize();
        ArrayDeque<NodePair> toHandle = new ArrayDeque<>();
        root = new MDDnode();
        handle(toHandle, tree.getRoot(), root);

        Set<Integer> variables = new HashSet<>();
        while (!toHandle.isEmpty()) {
            NodePair p = toHandle.poll();
            if (p.treeNode.getAsocAttr() != null) {
                variables.add(p.treeNode.getAsocAttr().getId());
            }
            handle(toHandle, p.treeNode, p.mddNode);
        }
        this.variableCount = variables.size();
        for (MDDnode leaf : this.leaves) {
            leaf.setLevel(this.variableCount);
        }

        //this.reduce();
        this.reduceInak();
    }

    @Override
    public void forEachNode(Consumer<MDDnode> action) {
        MDDnode node = getRoot();
        ArrayDeque<MDDnode> toHandle = new ArrayDeque<>();
        toHandle.add(node);
        HashSet<Integer> visited = new HashSet<>();
        while (!toHandle.isEmpty()) {
            node = toHandle.removeLast();
            for (MDDnode child : node.getChildren()) {
                boolean added = visited.add(child.getId());
                if (added) {
                    toHandle.add(child);
                }
            }
            action.accept(node);
        }
    }

    @Override
    public List<MDDnode> getLeaves() {
        return leaves;
    }

    // region reduce inak
    private void reduceInak() {
        final var reducer = new Reducer();
        final var newRoot = reducer.reduce(this.root);
        this.root = newRoot;
    }

    public static class InternalNodeKey {

        private final int index;
        private final int[] sons;

        public InternalNodeKey(int index, int[] sons) {
            this.index = index;
            this.sons = sons;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 53 * hash + this.index;
            hash = 53 * hash + Arrays.hashCode(this.sons);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final InternalNodeKey other = (InternalNodeKey) obj;
            if (this.index != other.index) {
                return false;
            }
            return Arrays.equals(this.sons, other.sons);
        }
    }

    private class Reducer {

        private final Map<InternalNodeKey, MDDnode> uniqueTable;
        private final MDDnode[] terminals;

        public Reducer() {
            this.uniqueTable = new HashMap<>();
            this.terminals = new MDDnode[MDD.this.classCount];
        }

        public MDDnode reduce(MDDnode root) {
            return this.reduceStep(root);
        }

        private MDDnode reduceStep(MDDnode node) {
            if (node.isLeaf()) {
                return this.getUniqueTerminal(node);
            } else {
                final var index = node.getAsocAttr().getId();
                final var sons = node.getChildren();
                for (int i = 0; i < sons.size(); ++i) {
                    final var oldSon = sons.get(i);
                    final var newSon = this.reduceStep(oldSon);
                    sons.set(i, newSon);
                }
                if (this.isRedundant(sons)) {
                    return sons.get(0);
                } else {
                    return this.getUniqueInternal(index, sons, node);
                }
            }
        }

        private MDDnode getUniqueTerminal(MDDnode node) {
            final var value = (int) node.getOutputClass();
            if (this.terminals[value] == null) {
                this.terminals[value] = node;
            }
            return this.terminals[value];
        }

        private MDDnode getUniqueInternal(int index, List<MDDnode> sons, MDDnode node) {
            final var sonIds = new int[sons.size()];
            for (int i = 0; i < sonIds.length; ++i) {
                sonIds[i] = sons.get(i).getId();
            }

            final var key = new InternalNodeKey(index, sonIds);
            var uniqueNode = this.uniqueTable.get(key);
            if (uniqueNode == null) {
                uniqueNode = node;
                this.uniqueTable.put(key, uniqueNode);
            }
            return uniqueNode;
        }

        private boolean isRedundant(List<MDDnode> sons) {
            final var firstId = sons.get(0).getId();
            for (MDDnode son : sons) {
                if (son.getId() != firstId) {
                    return false;
                }
            }
            return true;
        }
    }

    // end region reduce inak
    // region reduce 
    private static class ReduceKey {

        private final int[] ids;

        public ReduceKey() {
            this.ids = null;
        }

        public ReduceKey(int id) {
            this.ids = new int[]{id};
        }

        public ReduceKey(int[] ids) {
            this.ids = ids;
        }

        public int[] getIds() {
            return this.ids;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 19 * hash + Arrays.hashCode(this.ids);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ReduceKey other = (ReduceKey) obj;
            return Arrays.equals(this.ids, other.ids);
        }
    }

    private static class ReducePair implements Comparable<ReducePair> {

        private final ReduceKey key;
        private final MDDnode val;

        public ReducePair(ReduceKey key, MDDnode val) {
            this.key = key;
            this.val = val;
        }

        public ReduceKey getKey() {
            return this.key;
        }

        public MDDnode getVal() {
            return this.val;
        }

        @Override
        public int compareTo(ReducePair o) {
            int[] otherIds = o.getKey().getIds();
            int[] thisIds = this.key.getIds();

            assert (thisIds.length == otherIds.length);

            try {
                for (int i = 0; i < thisIds.length; ++i) {
                    if (thisIds[i] < otherIds[i]) {
                        return -1;
                    } else if (thisIds[i] > otherIds[i]) {
                        return 1;
                    }
                }
            } catch (Exception e) {
                int x = 10;
            }

            return 0;
        }
    }

    private void reduce() {
        // Zoznam zoznamov vrcholov pre kazdu uroven stromu.
        var levelLists = new ArrayList<ArrayList<MDDnode>>();
        for (int level = 0; level <= this.variableCount; ++level) {
            levelLists.add(new ArrayList<>());
        }
        // Naplnenie urovni.
        forEachMDDTreeNode(this.root, (node) -> levelLists.get(node.getLevel()).add(node));

        var nextId = 0;
        var uniqueTable = new HashMap<Integer, MDDnode>();

        for (int level = this.variableCount; level >= 0; --level) {
            // Pomocna mnozina na vyber unikatnych vrcholov pre danu uroven.
            var set = new ArrayList<ReducePair>();

            for (MDDnode node : levelLists.get(level)) {
                if (this.isTerminal(node)) {
                    set.add(new ReducePair(terminalNodeToKey(node), node));
                } else if (this.isRedundant(node)) {
                    node.setId(node.getChildren().get(0).getId());
                } else {
                    set.add(new ReducePair(interanlNodeToKey(node), node));
                }
            }

            // Po utriedeni budu duplicitne vrcholy za sebou s rovnakym klucom.
            Collections.sort(set);
            var oldKey = new ReduceKey();

            // Z duplicitnych vrcholov vyberie vzdy prvy.
            for (ReducePair pair : set) {
                var key = pair.getKey();
                var node = pair.getVal();

                if (key.equals(oldKey)) {
                    node.setId(nextId);
                } else {
                    ++nextId;
                    node.setId(nextId);
                    uniqueTable.put(node.getId(), node);
                    var sons = node.getChildren();
                    for (int i = 0; i < sons.size(); ++i) {
                        var oldSon = sons.get(i);
                        var newSon = uniqueTable.get(oldSon.getId());
                        sons.set(i, newSon);
                    }
                    oldKey = key;
                }
            }
        }

        var newRoot = uniqueTable.get(this.root.getId());
        this.root = newRoot;
    }

    private static ReduceKey terminalNodeToKey(MDDnode node) {
        return new ReduceKey((int) node.getOutputClass());
    }

    private static ReduceKey interanlNodeToKey(MDDnode node) {
        var ids = new int[node.getAsocAttr().getDomainSize()];
        for (int i = 0; i < ids.length; ++i) {
            ids[i] = node.getChildren().get(i).getId();
        }
        return new ReduceKey(ids);
    }

    private boolean isTerminal(MDDnode node) {
        return node.isLeaf();
    }

    private boolean isRedundant(MDDnode node) {
        int firstId = node.getChildren().get(0).getId();
        for (MDDnode son : node.getChildren()) {
            if (son.getId() != firstId) {
                return false;
            }
        }
        return true;
    }

    private static void forEachMDDTreeNode(MDDnode root, Consumer<MDDnode> action) {
        var toVisit = new ArrayList<MDDnode>();
        toVisit.add(root);
        while (!toVisit.isEmpty()) {
            var node = toVisit.get(toVisit.size() - 1);
            toVisit.remove(toVisit.size() - 1);
            for (MDDnode son : node.getChildren()) {
                toVisit.add(son);
            }
            action.accept(node);
        }
    }

    // end region reduce 
    private void handle(ArrayDeque<NodePair> toHandle, ClassificationTreeNode node, MDDnode actual) {
        actual.setAsocAttr(node.getAsocAttr());
        actual.setLevel(node.getLevel());

        if (!node.isLeaf()) {
            for (TreeNode child : node.getChildren()) {
                NodePair np = new NodePair();
                np.treeNode = (ClassificationTreeNode) child;
                np.mddNode = new MDDnode();
                np.mddNode.setAsocAttr(np.treeNode.getAsocAttr());
                actual.addChild(np.mddNode);
                toHandle.add(np);
                if (child.isLeaf()) {
                    np.mddNode.setOutputClass((int) np.treeNode.getOutputClass());
                    leaves.add(np.mddNode);
                }
            }
        }

    }

    private void mergeLeaves() {
        MDDnode[] nleaves = new MDDnode[classCount];
        for (int i = 0; i < classCount; i++) {
            nleaves[i] = new MDDnode();
            nleaves[i].setOutputClass(i);
        }
        for (MDDnode leaf : leaves) {
            MDDnode parent = leaf.getParents().get(0);
            boolean removed = parent.getChildren().remove(leaf);
            if (!removed) {
                throw new Error("ssss");
            }
            parent.addChild(nleaves[(int) leaf.getOutputClass()]);
        }
    }

    private class NodePair {

        ClassificationTreeNode treeNode;
        MDDnode mddNode;
    }

    @Override
    public MDDnode getRoot() {
        return root;
    }

    public static void main(String[] args) {
         DataSet dt = DatasetFactory.getDataset(DatasetFactory.DataSetCode.HEART_DISEASES);
         dt.print(1);

       // SimpleDatasetGenerator sg = new SimpleDatasetGenerator(54);
       // DataSet dt = sg.generate(10, 2, 1100); // generuje vstupne data pre strom. attrCount - pocet premennych, pocet synov, pocet vstupnych dat pre generovanie stromu
        dt.setOutputAttr();
        FuzzyDecisionTree u = new FDTu();
        u.setDataset(dt.getFuzzyDataset());
        u.setAlfaBeta(0.1, 0.9); // prve cislo, ak je nizsie, tak strom je hlbsi. Druhe cislo: ak je nizsie strom je mensi. Neorezany: u.setAlfaBeta(0, 1)
        u.buildModel();
        u.print();

        MDD mdd = new MDD(u);
        //mdd.mergeLeaves();
        // mdd.print();

        String s = GraphvizScript.code(mdd);
        ProjectUtils.toClipboard(s);
        System.out.println(s);
        
//                 s = GraphvizScript.code(u);
//        ProjectUtils.toClipboard(s);
//        System.out.println(s);
    }

}
