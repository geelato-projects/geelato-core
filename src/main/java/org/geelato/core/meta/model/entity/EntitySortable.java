package org.geelato.core.meta.model.entity;

/**
 * 实体可排序
 *
 * @author geemeta
 */
public interface EntitySortable {

    // 实现类中设置的注解模板
    // @Title(title = "次序")
    // @Col(name = "seq")
    long getSeq();

    void setSeq(long seq);
}
