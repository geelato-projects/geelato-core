package org.geelato.core.graal;

import org.geelato.core.AbstractManager;
import org.geelato.core.meta.MetaManager;
import org.geelato.core.meta.annotation.Entity;
import org.geelato.core.meta.model.entity.TableMeta;
import org.geelato.core.meta.model.field.ColumnMeta;
import org.geelato.utils.ClassScanner;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraalManager extends AbstractManager {
    private static GraalManager instance;
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(GraalManager.class);

    private final Map<String,Object> graalServiceMap=new HashMap<>();
    private final Map<String,Object> graalVariableMap=new HashMap<>();
    private GraalManager() {
        logger.info("GraalManager Instancing...");
    }
    public static GraalManager singleInstance() {
        lock.lock();
        if (instance == null) {
            instance = new GraalManager();
        }
        lock.unlock();
        return instance;
    }

    public void initGraalService(String parkeName){
        logger.info("开始从包{}中扫描到包含注解{}的实体......", parkeName, GraalService.class);
        List<Class<?>> classes = ClassScanner.scan(parkeName, true, GraalService.class);
        for (Class<?> clazz : classes) {
            try {
                initGraalServiceBean(clazz);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void initGraalVariable(String parkeName){
        logger.info("开始从包{}中扫描到包含注解{}的实体......", parkeName, GraalVariable.class);
        List<Class<?>> classes = ClassScanner.scan(parkeName, true, GraalVariable.class);
        for (Class<?> clazz : classes) {
            try {
                initGraalVariableBean(clazz);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void initGraalServiceBean(Class<?> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        GraalService graalService = clazz.getAnnotation(GraalService.class);
        if (graalService != null) {
            String serviceName=graalService.name();
            Object serviceBean= clazz.getDeclaredConstructor().newInstance();
            graalServiceMap.put(serviceName,serviceBean);
        }
    }

    private void initGraalVariableBean(Class<?> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        GraalVariable graalVariable = clazz.getAnnotation(GraalVariable.class);
        if (graalVariable != null) {
            String variableName=graalVariable.name();
            Object variableBean= clazz.getDeclaredConstructor().newInstance();
            graalVariableMap.put(variableName,variableBean);
        }
    }

    public Map<String,Object> getGraalServiceMap(){
        return graalServiceMap;
    }
    public Map<String,Object> getGraalVariableMap(){
        return graalVariableMap;
    }
}
