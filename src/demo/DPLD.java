/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import minig.classification.mdd.MDD;
import minig.classification.mdd.MDDnode;

/**
 *
 * @author petok
 */
public class DPLD {
    
    private HashMap<MDDnode, MDDnode> memo;
    private ArrayList<MDDnode> terminals;
    private HashMap<Tuple, MDDnode> applyCache;
    
public MDD IDPLDTYPEIII(MDD mdd, int index, int from, int to, int j) {
    MDD lhsCofactor = COFACTOR(mdd, index, from);
    MDD rhsCofactor = COFACTOR(mdd, index, to);
    
    Function<Integer, Integer> lowerThanOne = x -> (x < j) ? 1 : 0;
    Function<Integer, Integer> atLeastOne = x -> (x >= j) ? 1 : 0;
    //transformation
    MDD lhs = TRANSFORM(lhsCofactor, lowerThanOne);
    MDD rhs = TRANSFORM(rhsCofactor, atLeastOne);
    
    MDD result = lhs;//APPLY(lhs, rhs, <Integer, Integer> -> Integer);
    
    return result;

}
    
    public  MDD COFACTOR(MDD diagram, int i, int a) {
        MDDnode root = diagram.getRoot();

        if (root.isLeaf()) {
            return diagram;
            
        } else if (root.getId() == i) {
            
            var sons = root.getChildren();
            if (sons.size() > a) {
                return new MDD(sons.get(a));
            }
            
        } else {
            memo = new HashMap<>();
            return new MDD(COFACTORSTEP(root, i, a));
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

        int index = node.getId();
        
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
        if (index > i) {
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
            return CreateTerminalNode(node, gamma);
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
        MDDnode node = new MDDnode();
        for (MDDnode son : sons) {
            node.addChild(son);
        }
        node.setId(oldNode.getId());
        node.setAsocAttr(oldNode.getAsocAttr());
        
        return node;
    }
    
    public MDDnode CreateTerminalNode(MDDnode oldNode, Function<Integer, Integer> gamma) {
        
        int newOutput = gamma.apply((int)oldNode.getOutputClass());
        
        for (MDDnode k : terminals){
            if ((int)k.getOutputClass() == newOutput){
                return k;
            }
        }
        
        MDDnode node = new MDDnode();
        node.setId(oldNode.getId());
        node.setOutputClass(newOutput);
        return node;
    }
    
    public MDD APPLY(MDD left, MDD right, BinaryOperator<Integer> op) {
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
            
        //} else if (ISABSORBINGTTERMINAL(op, left) || ISABSORBINGTTERMINAL(op, right)) {
           // node = CREATETERMINALNODE(ABSORBINGELEMENT(op));
            
        } else {
            int ilhs = left.getChildren().size();
            int irhs = right.getChildren().size();
            int i = Math.min(ilhs, irhs);
            MDDnode[] sons = new MDDnode[i];
            for (int k = 0; k < i; k++) {
                if (ilhs < irhs) {
                    sons[k] = APPLYSTEP(left.getChildren().get(k), right, op);
                } else {
                    sons[k] = APPLYSTEP(left, right.getChildren().get(k), op);
                }
            }
            
            node = CreateInternalNode(new MDDnode(), sons);
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
            if (obj == null || !(obj instanceof Tuple)) {
                return false;
            }

            Tuple other = (Tuple) obj;
            return this.x1.equals(other.x1) && this.x2.equals(other.x2);
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