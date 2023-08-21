package org.geelato.core.env;


import org.geelato.core.enums.permission.DataPermissionEnum;
import org.geelato.core.enums.permission.ElementPermissionEnum;
import org.geelato.core.env.entity.DataPermission;
import org.geelato.core.env.entity.ElementPermission;
import org.geelato.core.env.entity.User;
import org.geelato.core.env.entity.UserMenu;
import org.geelato.core.orm.Dao;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class EnvManager {

    private Dao  EnvDao;

    private User currentUser;
    private String currentTenantCode;
    private static Lock lock = new ReentrantLock();

    private static EnvManager instance;

    private EnvManager(){
    }

    public static EnvManager singleInstance() {
        lock.lock();
        if (instance == null) {
            instance = new EnvManager();
        }
        lock.unlock();
        return instance;
    }
    public void SetDao(Dao dao){
        this.EnvDao=dao;
    }
    public  void EnvInit(){
        //TODO Init Env
    }

    public void InitCurrentUser(String loginName) {
        String sql = "select id as userId,org_id as defaultOrgId,login_name as loginName,name as userName,bu_id as buId,dept_id as deptId from platform_user  where login_name =?";
        User dbUser = EnvDao.getJdbcTemplate().queryForObject(sql,new BeanPropertyRowMapper<User>(User.class),new Object[]{loginName});
        this.currentTenantCode="geelato";
        dbUser.setMenus(StructUserMenu(dbUser.getUserId()));
        dbUser.setDataPermissions(StructDataPermission(dbUser.getUserId()));
        dbUser.setElementPermissions(StructElementPermission(dbUser.getUserId()));
        currentUser=dbUser;
    }

    private List<DataPermission> StructDataPermission(String userId) {
        List<DataPermission> dataPermissionList=new ArrayList<>();
        DataPermission dp1=new DataPermission();
        dp1.setEntity("");
        dp1.setDataPermission(DataPermissionEnum.All);
        dataPermissionList.add(dp1);

        DataPermission dp2=new DataPermission();
        dp2.setEntity("");
        dp2.setDataPermission(DataPermissionEnum.Myself);
        dataPermissionList.add(dp2);

        DataPermission dp3=new DataPermission();
        dp3.setEntity("");
        dp3.setDataPermission(DataPermissionEnum.MyDept);
        dataPermissionList.add(dp3);

        return dataPermissionList;
    }

    private List<ElementPermission> StructElementPermission(String userId) {
        List<ElementPermission> elementPermissionList=new ArrayList<>();

        ElementPermission ep1=new ElementPermission();
        ep1.setElementKey("");
        ep1.setElementPermission(ElementPermissionEnum.Visable);
        elementPermissionList.add(ep1);

        return elementPermissionList;
    }


    private List<UserMenu> StructUserMenu(String userId) {
        List<UserMenu> userMenuList=new ArrayList<>();

        UserMenu um1=new UserMenu();
        um1.setMenuUrl("");
        userMenuList.add(um1);

        return userMenuList;
    }

    public User  getCurrentUser() {
        return currentUser;
    }

    public String  getCurrentTenantCode() {
        return currentTenantCode;
    }
}
