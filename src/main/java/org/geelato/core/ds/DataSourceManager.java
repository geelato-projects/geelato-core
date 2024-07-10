package org.geelato.core.ds;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.geelato.core.AbstractManager;
import org.geelato.core.orm.Dao;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class DataSourceManager extends AbstractManager {


    private Dao dao;
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(DataSourceManager.class);
    private static DataSourceManager instance;

    private final static ConcurrentHashMap<String, DataSource> dataSourceMap=new ConcurrentHashMap<>();

    private final static ConcurrentHashMap<Object, Object> dynamicDataSourceMap =new ConcurrentHashMap<>();

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


    public void parseDataSourceMeta(Dao dao){
        this.dao=dao;
        List<Map<String,Object>> dbConenctList=dao.getJdbcTemplate().queryForList("SELECT * FROM platform_dev_db_connect");
        for (Map<String,Object> dbConnectMap:dbConenctList){
            String connectId=dbConnectMap.get("id").toString();
            DataSource dataSource=buildDataSource(dbConnectMap);
            dataSourceMap.put(connectId,dataSource);
            dynamicDataSourceMap.put(connectId,dataSource);
        }
    }
    public Map<Object, Object> getDynamicDataSourceMap(){
        return dynamicDataSourceMap;
    }
    public DataSource getDataSource(String connectId){
        return dataSourceMap.get(connectId);
    }
    private DataSource buildDataSource(Map dbConnectMap){
        HikariConfig config = new HikariConfig();
        String serverHost=dbConnectMap.get("db_hostname_ip").toString();
        String serverPort=dbConnectMap.get("db_port").toString();
        String dbName=dbConnectMap.get("db_name").toString();;
        String commonParams="useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true";
        String jdbcUrl=String.format("jdbc:mysql://%s:%s/%s?%s",serverHost,serverPort,dbName,commonParams);
        String dbUserName=dbConnectMap.get("db_user_name").toString();
        String dbPassWord=dbConnectMap.get("db_password").toString();
        String dbDriver="com.mysql.cj.jdbc.Driver";
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(dbUserName);
        config.setPassword(dbPassWord);
        config.setDriverClassName(dbDriver);
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(3);
        return new HikariDataSource(config);
    }
}
