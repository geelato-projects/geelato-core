package org.geelato.core.env.entity;

import java.util.List;

public class User {
    private String userId;
    private String userName;
    private String loginName;
    private String defaultOrgId;
    private String defaultOrgName;

    private List<UserOrg> orgs;
    private List<UserRole> roles;



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDefaultOrgId() {
        return defaultOrgId;
    }

    public void setDefaultOrgId(String defaultOrgId) {
        this.defaultOrgId = defaultOrgId;
    }

    public String getDefaultOrgName() {
        return defaultOrgName;
    }

    public void setDefaultOrgName(String defaultOrgName) {
        this.defaultOrgName = defaultOrgName;
    }

    public List<UserOrg> getOrgs() {
        return orgs;
    }

    public void setOrgs(List<UserOrg> orgs) {
        this.orgs = orgs;
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
}
