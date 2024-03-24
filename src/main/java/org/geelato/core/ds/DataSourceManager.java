package org.geelato.core.ds;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.geelato.core.AbstractManager;
import org.geelato.core.constants.MetaDaoSql;
import org.geelato.core.meta.annotation.Entity;
import org.geelato.core.orm.Dao;
import org.mvel2.MacroProcessor;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DataSourceManager extends AbstractManager {

    private Dao dao;
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(DataSourceManager.class);
    private static DataSourceManager instance;

    private final Map<String, DataSource> dataSourceMap=new HashMap<>();
    private final Map<Object, Object> dymanicDataSourceMap=new HashMap<>();

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

    public void parseDataSourceMeta(){
        List<Map<String,Object>> dbConenctList=dao.getJdbcTemplate().queryForList("");
        for (Map<String,Object> dbConnectMap:dbConenctList){
            String app=dbConnectMap.get("app").toString();
            DataSource dataSource=buildDataSource(dbConnectMap);
            dataSourceMap.put(app,dataSource);
            dymanicDataSourceMap.put(app,dataSource);
        }
    }
    public Map<Object, Object> getDymanicDataSourceMap(){
        return dymanicDataSourceMap;
    }
    public DataSource getDataSource(String app){
        return dataSourceMap.get(app);
    }
    private DataSource buildDataSource(Map dbConnectMap){
        HikariConfig config = new HikariConfig();
        String serverHost="";
        String serverPort="";
        String dbName="";
        String commonParams="useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true";
        String jdbcUrl=String.format("jdbc:mysql://%s:%s/%s?%s",serverHost,serverPort,dbName,commonParams);
        String dbUserName=dbConnectMap.get("db_user_name").toString();
        String dbPassWord=dbConnectMap.get("db_password").toString();
        String dbDriver="com.mysql.cj.jdbc.Driver";
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(dbUserName);
        config.setPassword(dbPassWord);
        config.setDriverClassName(dbDriver);


        return new HikariDataSource(config);
    }
}
