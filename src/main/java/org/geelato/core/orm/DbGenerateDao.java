package org.geelato.core.orm;

import com.alibaba.fastjson2.JSONObject;
import org.geelato.core.meta.MetaManager;
import org.geelato.core.meta.model.connect.ConnectMeta;
import org.geelato.core.meta.model.entity.EntityMeta;
import org.geelato.core.meta.model.entity.TableForeign;
import org.geelato.core.meta.model.entity.TableMeta;
import org.geelato.core.meta.model.field.ColumnMeta;
import org.geelato.core.meta.model.field.FieldMeta;
import org.geelato.utils.SqlParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 注意：使用前需先注入dao，见{@link #setDao(Dao)}。
 *
 * @author geemeta
 */
@Component
public class DbGenerateDao {

    private static Logger logger = LoggerFactory.getLogger(DbGenerateDao.class);
    private static HashMap<String, Integer> defaultColumnLengthMap;
    private static HashMap<String, Boolean> defaultColumnNullMap;
    private static HashMap<String, String> defaultColumnDataTypeMap;
    private static HashMap<String, String> defaultColumnTitleMap;
    private Dao dao;

    private MetaManager metaManager = MetaManager.singleInstance();

    public DbGenerateDao() {
        InitDefaultColumn();
    }

    private void InitDefaultColumn(){
        //todo chengx: init default column datatype,null,title

        //init default column length
        defaultColumnLengthMap = new HashMap<>();
        defaultColumnLengthMap.put("id", 1024);
        defaultColumnLengthMap.put("createAt", 1024);
        defaultColumnLengthMap.put("updateAt", 1024);
        defaultColumnLengthMap.put("creator", 1024);
        defaultColumnLengthMap.put("updater", 1024);
        defaultColumnLengthMap.put("bu", 1024);
        defaultColumnLengthMap.put("del_status", 1024);
    }

    /**
     * @param dao
     */
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public Dao getDao() {
        return dao;
    }

    public void createAllTables(boolean dropBeforeCreate) {
        this.createAllTables(dropBeforeCreate, null);
    }

    /**
     * 基于元数据管理，需元数据据管理器已加载、扫描元数据
     * <p>内部调用了sqlId:dropOneTable来删除表，
     * 内部调用了sqlId:createOneTable来创建表</p>
     * 创建完表之后，将元数据信息保存到数据库中
     *
     * @param dropBeforeCreate     存在表时，是否删除
     * @param ignoreEntityNameList
     */
    public void createAllTables(boolean dropBeforeCreate, List<String> ignoreEntityNameList) {
        Collection<EntityMeta> entityMetas = metaManager.getAll();
        if (entityMetas == null) {
            logger.warn("实体元数据为空，可能还未解析元数据，请解析之后，再执行该方法(createAllTables)");
            return;
        }
        for (EntityMeta em : entityMetas) {
            boolean isIgnore = false;
            if (ignoreEntityNameList != null) {
                for (String ignoreEntityName : ignoreEntityNameList) {
                    if (em.getEntityName().indexOf(ignoreEntityName) != -1) {
                        isIgnore = true;
                        break;
                    }
                }
            }
            if (!isIgnore) {
                createOrUpdateOneTable(em, dropBeforeCreate);
            } else {
                logger.info("ignore createTable for entity: {}.", em.getEntityName());
            }
        }
        // 先清空元数据
//        this.dao.getJdbcTemplate().execute("TRUNCATE TABLE platform_dev_column");
//        this.dao.getJdbcTemplate().execute("TRUNCATE TABLE platform_dev_table");
//        this.dao.getJdbcTemplate().execute("TRUNCATE TABLE platform_dev_db_connect");

        // 创建数据库连接
        // TODO 改成数据文件中获取
        ConnectMeta connectMeta = new ConnectMeta();
        connectMeta.setDbName("geelato");
        connectMeta.setDbConnectName("geelato-local");
        connectMeta.setDbHostnameIp("127.0.0.1");
        connectMeta.setDbUserName("sa");
        connectMeta.setDbPort(3306);
        connectMeta.setDbSchema("geelato");
        connectMeta.setDbType("Mysql");
        connectMeta.setEnableStatus(1);
        connectMeta.setDbPassword("123456");
        Map connectMetaMap = this.dao.save(connectMeta);

        // 保存所有的数据表元数据
        this.saveJavaMetaToDb(Long.parseLong(connectMetaMap.get("id").toString()), entityMetas);
    }

    /**
     * 将元数据信息保存到服务端，一般用于开发环境初始化，创建完表之后执行
     *
     * @param entityMetas
     */
    private void saveJavaMetaToDb(Long id, Collection<EntityMeta> entityMetas) {
        for (EntityMeta em : entityMetas) {
            TableMeta tm = em.getTableMeta();
            tm.setConnectId(id);
            tm.setLinked(1);
            tm.setEnableStatus(1);
            Map table = dao.save(tm);
            for (FieldMeta fm : em.getFieldMetas()) {
                ColumnMeta cm = fm.getColumn();
                cm.setTableId(table.get("id").toString());
                cm.setLinked(1);
                // 已有name不需再设置
                // cm.setTableId(em.getTableMeta().getTableName());
                dao.save(cm);
            }
            // 保存外键关系
            for (TableForeign ft : em.getTableForeigns()) {
                ft.setEnableStatus(1);
                dao.save(ft);
            }
        }
    }


    /**
     * 从数据库中删除实体对应的表
     *
     * @param entityName       实体名称
     * @param dropBeforeCreate 存在表时，是否删除
     */
    public void createOrUpdateOneTable(String entityName, boolean dropBeforeCreate) {
        //createOrUpdateOneTable(metaManager.getByEntityName(entityName,false), dropBeforeCreate);

        createOrUpdateOneTable(metaManager.getByEntityName(entityName,false));
    }

    private void createOrUpdateOneTable(EntityMeta em) {
        boolean isExistsTable = true;
        Map existscolumnMap = new HashMap();
        List<Map<String, Object>> columns = dao.queryForMapList("queryColumnsByTableName", SqlParams.map("tableName", em.getTableName()));
        if (columns == null || columns.size() == 0) {
            isExistsTable = false;
        } else {
            for (Map<String, Object> columnMap : columns) {
                existscolumnMap.put(columnMap.get("COLUMN_NAME"), columnMap);
            }
        }
        // 通过create table创建的字段
        ArrayList<JSONObject> createList = new ArrayList<>();
        // 通过alert table创建的字段
        ArrayList<JSONObject> addList = new ArrayList<>();
        // 通过alert table修改的字段
        ArrayList<JSONObject> modifyList = new ArrayList<>();
        // 通过alert table删除的字段
        ArrayList<JSONObject> deleteList = new ArrayList<>();
        ArrayList<JSONObject> uniqueList = new ArrayList<>();

        for (FieldMeta fm : em.getFieldMetas()) {
            try {
                JSONObject jsonColumn = JSONObject.parseObject(JSONObject.toJSONString(fm.getColumn()));
                if (existscolumnMap.containsKey(fm.getColumnName())) {
                    modifyList.add(jsonColumn);
                } else {
                    addList.add(jsonColumn);
                }
                createList.add(jsonColumn);
                if (fm.getColumn().isUnique())
                    uniqueList.add(jsonColumn);
            } catch (Exception e) {
                if (e.getMessage().indexOf("Duplicate column name") != -1)
                    logger.info("column " + fm.getColumnName() + " is exists，ignore.");
                else throw e;
            }
        }
        if(isExistsTable){
            createTable(em.getTableName(),createList);
        }else{
            upgradeTable(em.getTableName(),addList,modifyList,deleteList);
        }
    }
    private void createOrUpdateOneTable(EntityMeta em, boolean dropBeforeCreate) {

        if (dropBeforeCreate) {
            logger.info("  drop entity " + em.getTableName());
            dao.execute("dropOneTable", SqlParams.map("tableName", em.getTableName()));
        }
        logger.info("  create or update an entity " + em.getTableName());

        // 检查表是否存在，或取已存在的列元数据
        boolean isExistsTable = true;
        Map existscolumnMap = new HashMap();
        List<Map<String, Object>> columns = dao.queryForMapList("queryColumnsByTableName", SqlParams.map("tableName", em.getTableName()));
        if (columns == null || columns.size() == 0) {
            isExistsTable = false;
        } else {
            for (Map<String, Object> columnMap : columns) {
                existscolumnMap.put(columnMap.get("COLUMN_NAME"), columnMap);
            }
        }
        // 通过create table创建的字段
        ArrayList<JSONObject> createList = new ArrayList<>();
        // 通过alert table创建的字段
        ArrayList<JSONObject> addList = new ArrayList<>();
        // 通过alert table修改的字段
        ArrayList<JSONObject> modifyList = new ArrayList<>();
        // 通过alert table删除的字段
        ArrayList<JSONObject> deleteList = new ArrayList<>();
        ArrayList<JSONObject> uniqueList = new ArrayList<>();

        for (FieldMeta fm : em.getFieldMetas()) {
            try {
                if (defaultColumnLengthMap.containsKey(fm.getColumnName())) {
                    int len = defaultColumnLengthMap.get(fm.getColumnName()).intValue();
                    fm.getColumn().setCharMaxLength(len);
                    fm.getColumn().setNumericPrecision(len);
                    fm.getColumn().afterSet();
                }

                JSONObject jsonColumn = JSONObject.parseObject(JSONObject.toJSONString(fm.getColumn()));

                if (existscolumnMap.containsKey(fm.getColumnName())) {
                    modifyList.add(jsonColumn);
                } else {
                    addList.add(jsonColumn);
                }
                createList.add(jsonColumn);
                if (fm.getColumn().isUnique())
                    uniqueList.add(jsonColumn);
            } catch (Exception e) {
                if (e.getMessage().indexOf("Duplicate column name") != -1)
                    logger.info("column " + fm.getColumnName() + " is exists，ignore.");
                else throw e;
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("tableName", em.getTableName());
        map.put("createList", createList);
        map.put("addList", addList);
        map.put("modifyList", modifyList);
        map.put("deleteList", deleteList);
        map.put("uniqueList", uniqueList);
        map.put("foreignList", em.getTableForeigns());
        map.put("existsTable", isExistsTable);

        dao.execute("createOrUpdateOneTable", map);
    }

    //创建表
    private void createTable(String tableName,List<JSONObject> createColumnList){
        Map<String, Object> map = new HashMap<>();
        ArrayList<JSONObject> defaultColumnList = getDefaultColumn();
        createColumnList.addAll(defaultColumnList);  //add default column
        ArrayList<JSONObject> uniqueColumnList = getDefaultUniqueColumn();
        map.put("tableName",tableName);
        map.put("createList",createColumnList);
        map.put("uniqueList", uniqueColumnList);
        dao.execute("createOneTable", map);
    }

    ///更新表
    private void upgradeTable(String tableName,List<JSONObject> addList,List<JSONObject> modifyList,List<JSONObject> deleteList){
        Map<String, Object> map = new HashMap<>();
        map.put("tableName", tableName);
        map.put("addList", addList);
        map.put("modifyList", modifyList);
        map.put("deleteList", deleteList);

        dao.execute("upgradeOneTable", map);
    }
    private ArrayList<JSONObject> getDefaultColumn() {
        ArrayList<JSONObject> defaultColumnList = new ArrayList<>();
        List<ColumnMeta> defaultColumnMetaList= MetaManager.singleInstance().getDefualtColumnMeta();
        for (ColumnMeta columnMeta:defaultColumnMetaList){
            JSONObject jsonColumn = JSONObject.parseObject(JSONObject.toJSONString(columnMeta));
            defaultColumnList.add(jsonColumn);
        }

        return defaultColumnList;
    }

    private ArrayList<JSONObject> getDefaultUniqueColumn() {
        ArrayList<JSONObject> defaultUniqueColumnLsit = new ArrayList<>();
        List<ColumnMeta> defaultColumnMetaList= MetaManager.singleInstance().getDefualtColumnMeta();
        for (ColumnMeta columnMeta:defaultColumnMetaList){
            JSONObject jsonColumn = JSONObject.parseObject(JSONObject.toJSONString(columnMeta));
            defaultUniqueColumnLsit.add(jsonColumn);
        }
        return defaultUniqueColumnLsit;
    }




    public void createOrUpdateView(String view, String sql) {
        Map<String, Object> map = new HashMap<>();
        map.put("viewName",view);
        map.put("viewSql",sql);   //TODO 对sql进行检查
        dao.execute("createOneView", map);
    }
}
