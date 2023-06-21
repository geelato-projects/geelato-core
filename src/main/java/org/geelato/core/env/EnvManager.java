package org.geelato.core.env;


import org.geelato.core.env.entity.User;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class EnvManager {
    private User currentUser;
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
    public  void EnvInit(){
        //TODO Init Env
    }

    public void InitCurrentUser(String userId) {
        //TODO 根据用户ID产生当前用户信息
        User user=new User();
        user.setUserId(userId);
        currentUser=user;
    }

    public User  getCurrentUser() {
        return currentUser;
    }
}
