package org.geelato.core.env;


import org.geelato.core.env.entity.User;
import org.geelato.core.meta.model.CommonRowMapper;
import org.geelato.core.orm.Dao;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

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
        //TODO 根据用户ID产生当前用户信息
//        User user=new User();
        String sql = "select id as userId,org_id as defaultOrgId,login_name as loginName,name as userName from platform_user  where login_name =?";
        User dbUser = EnvDao.getJdbcTemplate().queryForObject(sql,new BeanPropertyRowMapper<User>(User.class),new Object[]{loginName});
        this.currentTenantCode="geelato";
        currentUser=dbUser;
    }

    public User  getCurrentUser() {
        return currentUser;
    }

    public String  getCurrentTenantCode() {
        return currentTenantCode;
    }
}
