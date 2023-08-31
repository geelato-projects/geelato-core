package org.geelato.core.env.entity;


import org.geelato.core.enums.permission.DataPermissionEnum;
import org.geelato.core.env.EnvManager;
import org.geelato.core.gql.parser.FilterGroup;

public class DataPermission {

    private String entity;

    private DataPermissionEnum dataPermission;


    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public DataPermissionEnum getDataPermission() {
        return dataPermission;
    }

    public void setDataPermission(DataPermissionEnum dataPermission) {
        this.dataPermission = dataPermission;
    }

}
