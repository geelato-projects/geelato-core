package org.geelato.core.meta;

import org.geelato.core.meta.model.entity.EntityLiteMeta;
import org.geelato.core.meta.model.entity.EntityMeta;
import org.geelato.core.meta.model.view.ViewMeta;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ViewManager {
    private static Lock lock = new ReentrantLock();
    private static ViewManager instance;
    private org.slf4j.Logger logger = LoggerFactory.getLogger(ViewManager.class);
    private HashMap<String, ViewMeta> viewMetadataMap = new HashMap<String, ViewMeta>();

    public static ViewManager singleInstance() {
        lock.lock();
        if (instance == null) {
            instance = new ViewManager();
        }
        lock.unlock();
        return instance;
    }
    public void addViewMeta(String viewName,ViewMeta viewMeta){
        if(!viewMetadataMap.containsKey(viewName)){
            viewMetadataMap.put(viewName,viewMeta);
        }
    }
    public ViewMeta getByViewName(String viewName) {
        if (viewMetadataMap.containsKey(viewName)) {
            return viewMetadataMap.get(viewName);
        } else {
            Iterator<String> it = viewMetadataMap.keySet().iterator();
            logger.warn("Key({}) not found in viewMetadataMap.keySet:", viewName);
            while (it.hasNext()) {
                logger.warn(it.next());
            }
            return null;
        }
    }



}
