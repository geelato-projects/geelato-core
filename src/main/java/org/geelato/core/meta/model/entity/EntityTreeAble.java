package org.geelato.core.meta.model.entity;

/**
 * 实体引入树节点id，可树结构化
 *
 * @author geemeta
 */
public interface EntityTreeAble {

    // 实现类中设置的注解模板
    // @Title(title = "树节点")
    // @Col(name = "tree_node_id")
    Long getTreeNodeId();

    Long setTreeNodeId(Long treeNodeId);
}
