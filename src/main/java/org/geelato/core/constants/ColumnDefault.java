package org.geelato.core.constants;

import org.geelato.core.enums.DeleteStatusEnum;
import org.geelato.core.enums.EnableStatusEnum;

/**
 * @author diabl
 */
public class ColumnDefault {
    /**
     * 默认排序 默认值[999]
     */
    public static final long SEQ_NO_VALUE = 999;
    /**
     * 是否删除 字段名称
     */
    public static final String DEL_STATUS_FIELD = "delStatus";
    /**
     * 是否删除 默认值 - 未删除[0]
     */
    public static final int DEL_STATUS_VALUE = DeleteStatusEnum.NO.getCode();
    /**
     * 启用状态 字段名称
     */
    public static final String ENABLE_STATUS_FIELD = "enableStatus";
    /**
     * 启用状态 默认值 - 启用[1]
     */
    public static final int ENABLE_STATUS_VALUE = EnableStatusEnum.ENABLED.getCode();
}
