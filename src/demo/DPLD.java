/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demo;

import minig.classification.mdd.MDD;
import minig.classification.mdd.MDDnode;
import minig.data.core.attribute.Attribute;
import projectutils.ProjectUtils;
import visualization.graphviz.script.GraphvizScript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;

/**
 *
 * @author petok
 */
public class DPLD {
    
    private HashMap<MDDnode, MDDnode> memo;
    private ArrayList<MDDnode> terminals;
    private HashMap<Tuple, MDDnode> applyCache;
    
public MDD IDPLDTYPEIII(MDD mdd, int index, int from, int to, int j) {
    MDD lhsCofactor = COFACTOR(mdd, index, from - 1);
    MDD rhsCofactor = COFACTOR(mdd, index, to - 1);
    String code = GraphvizScript.code(lhsCofactor);
    ProjectUtils.toClipboard(code);

    code = GraphvizScript.code(rhsCofactor);
    ProjectUtils.toClipboard(code);
    if (lhsCofactor == null || rhsCofactor == null){
        return null;
    }

    Function<Integer, Integer> lowerThanOne = x -> (x < j) ? 1 : 0;
    Function<Integer, Integer> atLeastOne = x -> (x >= j) ? 1 : 0;
    //transformation
    MDD lhs = transformII(lhsCofactor, lowerThanOne);
    MDD rhs = transformII(rhsCofactor, atLeastOne);
    BinaryOperator<Integer> binaryAND = (x, y) -> x.intValue() == 1 && y.intValue() == 1 ? 1 : 0;
    code = GraphvizScript.code(lhs);
    ProjectUtils.toClipboard(code);

    code = GraphvizScript.code(rhs);
    ProjectUtils.toClipboard(code);
    MDD result = APPLY(lhs, rhs, binaryAND);

    return result;

}
    
    public  MDD COFACTOR(MDD diagram, int index, int a) {
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

        // Check if the index of the node is greater than i
        if (index < i) {
            return node;
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
        for(int k = 0; k < mj; k++){
            newNode.addChild(cofactoredSons[k]);
        }
        newNode.setId(node.getId());
        newNode.setAsocAttr(node.getAsocAttr());

        // Memorize the result
        memo.put(node, newNode);

        return newNode;
    }

    public MDD transformII (MDD diagram, Function<Integer, Integer> gamma) {
        for(var leaf : diagram.getLeaves()) {
            leaf.setOutputClass(gamma.apply((int)leaf.getOutputClass()));
        }
        MDD d = new MDD(diagram.getRoot());
        return d;
    }
    
    public MDD TRANSFORM(MDD diagram, Function<Integer, Integer> gamma) {
        MDDnode root = diagram.getRoot();
        memo = new HashMap();
        terminals = new ArrayList();
        MDDnode newRoot = TRANSFORMSTEP(root, gamma);
        MDD newMDD = new MDD(newRoot);
        return newMDD;
    }

    public MDDnode TRANSFORMSTEP(MDDnode node, Function<Integer, Integer> gamma) {
        if (node.isLeaf()) {
            int newOutput = gamma.apply((int)node.getOutputClass());
            return CreateTerminalNode(node.getId(), node.getAsocAttr(), newOutput);
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
        newNode.setAsocAttr(node.getAsocAttr());
        // Memorize the result
        memo.put(node, newNode);
        
        return newNode;
    }
    
    public MDDnode CreateInternalNode(MDDnode oldNode, MDDnode[] sons) {
        MDDnode node = new MDDnode();
        for (MDDnode son : sons) {
            node.addChild(son);
        }
        node.setId(oldNode.getId());
        node.setAsocAttr(oldNode.getAsocAttr());
        
        return node;
    }
    
    public MDDnode CreateTerminalNode(int id, Attribute a, int outputClass) {
            for (MDDnode k : this.terminals) {
                if ((int) k.getOutputClass() == outputClass) {
                    return k;
                }
            }
        
        MDDnode node = new MDDnode();
        node.setId(id);
        node.setOutputClass(outputClass);
        node.setAsocAttr(a);
        terminals.add(node);
        return node;
    }
    
    public MDD APPLY(MDD left, MDD right, BinaryOperator<Integer> op) {
        applyCache = new HashMap<>();
        this.terminals = new ArrayList<MDDnode>();
        MDDnode root = APPLYSTEP(left.getRoot(), right.getRoot(), op);
        return new MDD(root);
    }

    MDDnode APPLYSTEP(MDDnode left, MDDnode right, BinaryOperator<Integer> op) {
        if (applyCache.containsKey(new Tuple(left, right))) {
            return applyCache.get(new Tuple(left, right));
        }

        MDDnode node;
        if (left.isLeaf() && right.isLeaf()) {
            node = new MDDnode();
            node.setOutputClass(op.apply((int)left.getOutputClass(),(int)right.getOutputClass()));
            
        } else if (left.isLeaf() || right.isLeaf()) {
            node = left.isLeaf() ? CreateTerminalNode(left.getId(), left.getAsocAttr(), (int)left.getOutputClass()) : CreateTerminalNode(right.getId(),right.getAsocAttr(),(int)right.getOutputClass());
            
        } else {
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
                node.setLevel(ilhs);
            } else {
                var rightChildren = right.getChildren();
                sons = new MDDnode[rightChildren.size()];
                for (int k = 0; k < rightChildren.size(); k++) {
                    sons[k] = APPLYSTEP(left, rightChildren.get(k), op);
                }
                node = CreateInternalNode(right, sons);
                node.setLevel(irhs);
            }

        }
        applyCache.put(new Tuple(left, right), node);
        return node;
    }
    
    public class Tuple {
        final MDDnode x1;
        final MDDnode x2;
        
        Tuple(MDDnode x1, MDDnode x2){
            this.x1 = x1;
            this.x2 = x2;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Tuple other)) {
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