package org.geelato.core.env.entity;


import org.geelato.core.Ctx;

public class Permission {

    private String entity;

    private String  rule;


    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getRuleReplaceVariable(){
       return  this.rule.replace("#currentUser.userId#", String.format("'%s'",Ctx.getCurrentUser().getUserId()))
               .replace("#currentUser.deptId#",String.format("'%s'",Ctx.getCurrentUser().getDefaultOrgId()));
    }
}
