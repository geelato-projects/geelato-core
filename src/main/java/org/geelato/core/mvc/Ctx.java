package org.geelato.core.mvc;

import org.geelato.core.env.EnvManager;
import org.geelato.core.env.entity.User;

import java.util.HashMap;

/**
 * 上下文件参数，可用于sqlTemplate解析构建时的语句的默认内置参数
 *
 * @author geemeta
 *
 */
public class Ctx extends HashMap<String, String> {

    public Ctx(){
        this.put("userId",getCurrentUser().getUserId());
        this.put("tenantCode",getCurrentTenantCode());
    }

    public User getCurrentUser(){
        return EnvManager.singleInstance().getCurrentUser();
    }

    public String getCurrentTenantCode() {
        return EnvManager.singleInstance().getCurrentTenantCode();
    }
}
