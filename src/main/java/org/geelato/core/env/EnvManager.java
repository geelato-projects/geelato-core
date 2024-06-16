package org.geelato.core.env;


import org.geelato.core.AbstractManager;
import org.geelato.core.Ctx;
import org.geelato.core.env.entity.*;
import org.geelato.core.meta.MetaManager;
import org.geelato.core.orm.Dao;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class EnvManager  extends AbstractManager {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(EnvManager.class);
    private final Map<String ,Map<String ,SysConfig>> sysConfigClassifyMap;
    private final Map<String ,SysConfig> sysConfigMap;
    private Dao  EnvDao;
    private static EnvManager instance;

    private EnvManager(){
        logger.info("EnvManager Instancing...");
        sysConfigMap=new HashMap<>();
        sysConfigClassifyMap=new HashMap<>();

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
        LoadSysConfig();
    }

    private void LoadSysConfig() {
        String sql = "select config_key as configKey,config_value as configValue,app_Id as appId,tenant_code as tenantCode,purpose as purpose from platform_sys_config where enable_status =1 and del_status =0";
        List<SysConfig> sysConfigList = EnvDao.getJdbcTemplate().query(sql,new BeanPropertyRowMapper<>(SysConfig.class));
        for (SysConfig config:sysConfigList) {
            if(!sysConfigMap.containsKey(config.getConfigKey())){
                sysConfigMap.put(config.getConfigKey(),config);
            }
        }
    }


    public String getConfigValue(String configKey){
        if(this.sysConfigMap.containsKey(configKey)){
            return sysConfigMap.get(configKey).getConfigValue();
        }else{
            return "unable to find this config";
        }
    }

    public Map<String ,SysConfig> getConfigMap(String purpose){
        return sysConfigMap;
    }
    public User InitCurrentUser(String loginName) {
        String sql = "select id as userId,org_id as defaultOrgId,login_name as loginName," +
                "name as userName,bu_id as buId,dept_id as deptId," +
                " cooperating_org_id as cooperatingOrgId from platform_user  where login_name =?";
        User dbUser = EnvDao.getJdbcTemplate().queryForObject(sql,new BeanPropertyRowMapper<User>(User.class),new Object[]{loginName});
        dbUser.setMenus(StructUserMenu(dbUser.getUserId()));
        dbUser.setDataPermissions(StructDataPermission(dbUser.getUserId()));
        dbUser.setElementPermissions(StructElementPermission(dbUser.getUserId()));
        Ctx.setCurrentUser(dbUser);
        Ctx.setCurrentTenant("geelato");
        return dbUser;
    }

    private List<Permission> StructDataPermission(String userId) {
        String sql = "select t2.`object`  as entity,t2.rule as rule,t3.weight as role_weight from platform_role_r_permission t1 \n" +
                "left join platform_permission t2 on t1.permission_id =t2.id \n" +
                "left join platform_role t3 on t1.role_id =t3.id \n" +
                "left join platform_role_r_user t4 on t4.role_id =t3.id \n" +
                "left join platform_user t5 on t5.id =t4.user_id \n" +
                "where  t2.type='dp' and t1.del_status=0 and t2.del_status=0 and t3.del_status=0 and t4.del_status=0 and t5.id =?";
        return EnvDao.getJdbcTemplate().query(sql,
                new BeanPropertyRowMapper<>(Permission.class),new Object[]{userId});
    }

    private List<Permission> StructElementPermission(String userId) {
        List<Permission> elementPermissionList=new ArrayList<>();

        return elementPermissionList;
    }


    private List<UserMenu> StructUserMenu(String userId) {
        List<UserMenu> userMenuList=new ArrayList<>();

        UserMenu um1=new UserMenu();
        um1.setMenuUrl("");
        userMenuList.add(um1);

        return userMenuList;
    }
}
