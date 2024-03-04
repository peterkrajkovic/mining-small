/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.classification.fdt.rules;

import java.util.ArrayList;
import minig.data.core.attribute.AttrValue;
import projectutils.ProjectUtils;
import java.util.List;
import minig.classification.fdt.DTNode;
import minig.classification.trees.Tree;
import minig.data.core.dataset.Instance;

/**
 *
 * @author jrabc
 */
public final class ClassificationRule {

    private final List<? extends DTNode> nodes;
    private final List<AttrValue> attrVal;

    public ClassificationRule(List<? extends DTNode> rule, Tree tree) {
        this.nodes = rule;
        attrVal = new ArrayList<>(rule.size());
        for (int i = 1; i < rule.size(); i++) {
            AttrValue val = rule.get(i).getIntputBranchValue();
            //val.getAttribute().setDataset(fff);
            attrVal.add(val);
        }
    }

    public double getPredicatedValue() {
        return nodes.get(nodes.size() - 1).getPredictedValue();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < nodes.size(); i++) {

            String asocAttr = nodes.get(i - 1).getAsocAttr().getName();
            String val = nodes.get(i).getIntputBranchValue().getName();
            if (i < nodes.size() - 1) {
                sb.append("if(").append(asocAttr).append(" = ").append(val).append(")").append(" & ");
            } else {
                sb.append("if(").append(asocAttr).append(" = ").append(val).append(")");
            }
        }
        String assurancelevels = ProjectUtils.listToString(nodes.get(nodes.size() - 1).getConfidenceLevels());
        sb.append(" then B = ").append(assurancelevels);
        return sb.toString();
    }

    public List<Double> getConfidenceLevels() {
        return nodes.get(nodes.size() - 1).getConfidenceLevels();
    }

    public List<Double> getConfLevelOnLeafParent() {
        return nodes.get(nodes.size() - 2).getConfidenceLevels();
    }

    public double getWeight(Instance instance) {
        double weight = 1;
        final int s = nodes.size() - 1;
        for (int i = 0; i < s; i++) {
            if (nodes.get(i).getAsocAttr().isOutputAttr()) {
                continue;
            }
          //  weight = Math.min(weight,instance.getValueForAttrVal(attrVal.get(i)));
            weight = weight * instance.getValueForAttrVal(attrVal.get(i));
            if (weight < 0.000000001) {
                return 0;
            }
        }
        return weight;
    }

    public List<AttrValue> getAttrVal() {
        return attrVal;
    }

}
