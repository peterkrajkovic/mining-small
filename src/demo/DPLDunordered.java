package demo;

import minig.classification.mdd.MDD;
import minig.classification.mdd.MDDnode;
import minig.data.core.attribute.Attribute;
import projectutils.ProjectUtils;
import visualization.graphviz.script.GraphvizScript;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class DPLDunordered {


    //computation helper attributes
    private HashMap<MDDnode, MDDnode> memo;
    private ArrayList<MDDnode> terminals;
    private HashMap<Tuple, MDDnode> applyCache;
    private HashMap<MDD.InternalNodeKey, MDDnode> uniqueNodesTable;
    private ArrayList<MDDnode> uniqueNodes;
    private int satisfyCount = 0;


    public MDD UniversalDPLD(MDD mdd, int index, int from, int to, BinaryOperator<Integer> applyFunction) {

        //cofactor computation
        MDD lhsCofactor = COFACTOR(mdd, index, from);
        MDD rhsCofactor = COFACTOR(mdd, index, to);
        String code = GraphvizScript.code(lhsCofactor);
        ProjectUtils.toClipboard(code);
        code = GraphvizScript.code(rhsCofactor);
        ProjectUtils.toClipboard(code);
        if (lhsCofactor == null || rhsCofactor == null) {
            return null;
        }
        //diagrams combination
        MDD result = APPLY(lhsCofactor, rhsCofactor, applyFunction);
        return result;
    }

    public MDD COFACTOR(MDD diagram, int index, int a) {
        MDDnode root = diagram.getRoot();

        if (root.isLeaf()) {
            return diagram;

        } else if (root.getAsocAttr().getAttributeIndex() == index) {

            var sons = root.getChildren();
            if (sons.size() > a) {
                return new MDD(sons.get(a));
            }

        } else {
            memo = new HashMap<>();
            return new MDD(COFACTORSTEP(root, index, a));
        }

        return null;
    }

    public MDDnode COFACTORSTEP(MDDnode node, int i, int a) {
        // Check if the cofactor of the node has already been computed
        if (memo.containsKey(node)) {
            return memo.get(node);
        }

        // Check if the node is a terminal node
        if (node.isLeaf()) {
            return node;
        }

        int index = node.getAsocAttr().getAttributeIndex();

        // Check if the index of the node is equal to i
        if (index == i) {
            var sons = node.getChildren();

            if (sons.size() > a) {
                return sons.get(a);
            } else {
                return null;
            }
        }

        var sons = node.getChildren();

        // Compute the cofactor of the node
        int mj = sons.size();
        MDDnode[] cofactoredSons = new MDDnode[mj];
        for (int k = 0; k < mj; k++) {
            MDDnode oldSon = sons.get(k);
            cofactoredSons[k] = COFACTORSTEP(oldSon, i, a);
        }

        // Create new internal node
        MDDnode newNode = new MDDnode();
        for (int k = 0; k < mj; k++) {
            newNode.addChild(cofactoredSons[k]);
        }
        newNode.setId(node.getId());
        newNode.setAsocAttr(node.getAsocAttr());
        // Memorize the result
        memo.put(node, newNode);

        return newNode;
    }

    public MDD transform(MDD diagram, Function<Integer, Integer> gamma) {
        MDDnode root = diagram.getRoot();
        memo = new HashMap();
        terminals = new ArrayList();
        uniqueNodesTable = new HashMap<>();
        MDDnode newRoot = TRANSFORMSTEP(root, gamma);
        MDD newMDD = new MDD(newRoot);
        return newMDD;
    }

    public MDDnode TRANSFORMSTEP(MDDnode node, Function<Integer, Integer> gamma) {
        if (node.isLeaf()) {
            int newOutput = gamma.apply((int) node.getOutputClass());
            return CreateTerminalNode(node.getAsocAttr(), newOutput);
        }
        // Check if the transform of the node has already been computed
        if (memo.containsKey(node)) {
            return memo.get(node);
        }

        var sons = node.getChildren();
        MDDnode[] newSons = new MDDnode[sons.size()];

        for (int k = 0; k < sons.size(); k++) {
            MDDnode oldSon = sons.get(k);
            newSons[k] = TRANSFORMSTEP(oldSon, gamma);
        }

        MDDnode newNode = CreateInternalNode(node, newSons);
        // Memorize the result
        memo.put(node, newNode);

        return newNode;
    }

    public MDDnode CreateInternalNode(MDDnode oldNode, MDDnode[] sons) {
        //check if node already exists
        int[] sonsIndexes = new int[sons.length];
        for (int i = 0; i < sons.length; i++) {
            sonsIndexes[i] = sons[i].getId();
        }
        MDD.InternalNodeKey nodeKey = new MDD.InternalNodeKey(oldNode.getAsocAttr().getAttributeIndex(), sonsIndexes);
        if (this.uniqueNodesTable.containsKey(nodeKey)) {
            return uniqueNodesTable.get(nodeKey);
        }
        //create new node
        MDDnode node = new MDDnode();
        for (MDDnode son : sons) {
            node.addChild(son);
        }
        node.setId(oldNode.getId());
        node.setAsocAttr(oldNode.getAsocAttr());
        return node;
    }

    public MDDnode CreateTerminalNode(Attribute a, int outputClass) {
        //check if terminal node already exists
        for (MDDnode k : this.terminals) {
            if ((int) k.getOutputClass() == outputClass) {
                return k;
            }
        }

        //create new node
        MDDnode node = new MDDnode();
        node.setOutputClass(outputClass);
        node.setAsocAttr(a);
        terminals.add(node);
        return node;
    }

    public MDD APPLY(MDD left, MDD right, BinaryOperator<Integer> op) {
        applyCache = new HashMap<>();
        this.terminals = new ArrayList<MDDnode>();
        uniqueNodesTable = new HashMap<>();
        MDDnode root = APPLYSTEP(left.getRoot(), right.getRoot(), op);
        return new MDD(root);
    }

    MDDnode APPLYSTEP(MDDnode left, MDDnode right, BinaryOperator<Integer> op) {
        if (applyCache.containsKey(new Tuple(left, right))) {
            return applyCache.get(new Tuple(left, right));
        }
        //TODO not working
        MDDnode node;
        if (left.isLeaf() && right.isLeaf()) {
            int outputClass = op.apply((int) left.getOutputClass(), (int) right.getOutputClass());
            node = CreateTerminalNode(left.getAsocAttr(), outputClass);

        } else if (right.isLeaf()) {
            var leftChildren = left.getChildren();
            MDDnode[] sons = new MDDnode[leftChildren.size()];
            for (int k = 0; k < leftChildren.size(); k++) {
                sons[k] = APPLYSTEP(leftChildren.get(k), right, op);
            }
            node = CreateInternalNode(left, sons);

        } else if (left.isLeaf()) {
            var rightChildren = right.getChildren();
            var sons = new MDDnode[rightChildren.size()];
            for (int k = 0; k < rightChildren.size(); k++) {
                sons[k] = APPLYSTEP(left, rightChildren.get(k), op);
            }
            node = CreateInternalNode(right, sons);
        } else {
            //TODO problem part
            int ilhs = left.getLevel();
            int irhs = right.getLevel();

            MDDnode[] sons;
            if (ilhs > irhs) {
                var leftChildren = left.getChildren();
                sons = new MDDnode[leftChildren.size()];
                for (int k = 0; k < leftChildren.size(); k++) {
                    sons[k] = APPLYSTEP(leftChildren.get(k), right, op);
                }
                node = CreateInternalNode(left, sons);
            } else if (ilhs == irhs && left.getChildren().size() == right.getChildren().size()) {
                var rightChildren = right.getChildren();
                var leftChildren = left.getChildren();
                sons = new MDDnode[rightChildren.size()];
                for (int k = 0; k < rightChildren.size(); k++) {
                    sons[k] = APPLYSTEP(leftChildren.get(k), rightChildren.get(k), op);
                }
                node = CreateInternalNode(right, sons);
            } else {
                var rightChildren = right.getChildren();
                sons = new MDDnode[rightChildren.size()];
                for (int k = 0; k < rightChildren.size(); k++) {
                    sons[k] = APPLYSTEP(left, rightChildren.get(k), op);
                }
                node = CreateInternalNode(right, sons);
            }

        }
        applyCache.put(new Tuple(left, right), node);
        return node;
    }


    public MDD DPLD(MDD mdd, int index, int from, int to) {

        //apply function
        BinaryOperator<Integer> equals = (x, y) -> Objects.equals(x, y) ? 1 : 0;

        //dpld computation
        MDD result = UniversalDPLD(mdd, index, from, to, equals);

        return result;
    }

    public double derivateUsingSatisfyCount(MDD diagram, int index, int from, int to) {
        MDD derivated = DPLD(diagram, index, from, to);
        String code = GraphvizScript.code(derivated);
        ProjectUtils.toClipboard(code);
        int changes = satisfyCountTop(derivated, index, diagram.getUniqueNodes(), 0);

        int all = 0;
        //computes whole domain
        for (MDDnode node : diagram.getUniqueNodes()) {
            if (node.getAsocAttr().getAttributeIndex() != index) {
                if (all == 0) {
                    all = node.getAsocAttr().getDomainSize();
                } else {
                    all *= node.getAsocAttr().getDomainSize();
                }
            }
        }
        System.out.println("index: " + index + " from: " + from + " to: " + to + "all: " + all + "changes: " + changes);
        return (double) changes / all;
    }

    public int satisfyCountTop(MDD diagram, int index, ArrayList<MDDnode> uniqueNodes, int value) {
        this.uniqueNodes = uniqueNodes;

        //finds leaf representing changes, in this case with 0 as outputclass
        MDDnode leaf = null;
        for (MDDnode node : diagram.getLeaves()) {
            if (node.getOutputClass() == value) {
                leaf = node;
                break;
            }
        }
        int count = 0;
        satisfyCount = 0;

        if (leaf != null) {
            char[] bitRepre = new char[uniqueNodes.size()];
            int in = 0;
            for (MDDnode n : this.uniqueNodes) {
                if (n.getAsocAttr().getAttributeIndex() == index) {
                    break;
                }
                in++;
            }
            bitRepre[in] = '1';
            satisfyCountTopStep(leaf, bitRepre);
            count = this.satisfyCount;
        }

        return count;
    }

    private void satisfyCountTopStep(MDDnode node, char[] bitRepre) {
        char[] newIndexes = Arrays.copyOf(bitRepre, bitRepre.length);

        //leaf is not in uniqueNodes
        if (!node.isLeaf()) {
            int index = 0;
            for (MDDnode n : this.uniqueNodes) {
                if (n.getAsocAttr().getAttributeIndex() == node.getAsocAttr().getAttributeIndex()) {
                    break;
                }
                index++;
            }
            newIndexes[index] = '1';
        }

        //is root, calculate changes
        if (node.getParents().size() == 0) {
            int count = 1;
            for (int i = 0; i < newIndexes.length; i++) {
                if (newIndexes[i] != '1') {
                    count *= uniqueNodes.get(i).getAsocAttr().getDomainSize();
                }
            }
            this.satisfyCount += count;
        }

        //step up
        for (int k = 0; k < node.getParents().size(); k++) {
            var parent = node.getParents().get(k);
            satisfyCountTopStep(parent, newIndexes);
        }
    }


    public static class Tuple<T> {
        T x1;
        T x2;

        Tuple(T x1, T x2) {
            this.x1 = x1;
            this.x2 = x2;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof DPLD.Tuple other)) {
                return false;
            }

            return (this.x1.equals(other.x1) && this.x2.equals(other.x2)) || (this.x2.equals(other.x1) && this.x1.equals(other.x2));
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 67 * hash + Objects.hashCode(this.x1);
            hash = 67 * hash + Objects.hashCode(this.x2);
            return hash;
        }
    }
}
