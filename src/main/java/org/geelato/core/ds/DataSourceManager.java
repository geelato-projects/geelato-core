package org.geelato.core.ds;

import org.geelato.core.AbstractManager;
import org.geelato.core.meta.annotation.Entity;
import org.slf4j.LoggerFactory;


public class DataSourceManager extends AbstractManager {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(DataSourceManager.class);
    private static DataSourceManager instance;


    public static DataSourceManager singleInstance() {
        lock.lock();
        if (instance == null) {
            instance = new DataSourceManager();
        }
        lock.unlock();
        return instance;
    }

    private DataSourceManager() {
        logger.info("DataSourceManager Instancing...");
    }
}
