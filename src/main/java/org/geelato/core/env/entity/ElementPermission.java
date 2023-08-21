package org.geelato.core.env.entity;

import org.geelato.core.enums.permission.ElementPermissionEnum;

public class ElementPermission {

    private String elementKey;

    private ElementPermissionEnum elementPermission;

    public String getElementKey() {
        return elementKey;
    }

    public void setElementKey(String elementKey) {
        this.elementKey = elementKey;
    }

    public ElementPermissionEnum getElementPermission() {
        return elementPermission;
    }

    public void setElementPermission(ElementPermissionEnum elementPermission) {
        this.elementPermission = elementPermission;
    }
}
