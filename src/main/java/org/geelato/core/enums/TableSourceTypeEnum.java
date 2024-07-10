package org.geelato.core.enums;

import org.geelato.utils.StringUtils;

/**
 * @author diabl
 */
public enum TableSourceTypeEnum {
    CREATION("模型创建", "creation"),
    SYSTEM("系统内置", "system"),
    PLATFORM("平台内置", "platform");

    private final String label;// 选项内容
    private final String value;// 选项值

    TableSourceTypeEnum(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public static String getLabel(String value) {
        if (StringUtils.isNotBlank(value)) {
            for (TableSourceTypeEnum enums : TableSourceTypeEnum.values()) {
                if (enums.getValue().equals(value)) {
                    return enums.getLabel();
                }
            }
        }
        return null;
    }
}