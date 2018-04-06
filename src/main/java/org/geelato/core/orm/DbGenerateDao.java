package org.geelato.core.orm;

import org.geelato.core.meta.MetaManager;
import org.geelato.core.meta.model.entity.EntityMeta;
import org.geelato.core.meta.model.field.ColumnMeta;
import org.geelato.core.meta.model.field.FieldMeta;
import org.geelato.utils.SqlParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 注意：使用前需先注入dao，见{@link #setDao(Dao)}。
 * @author geemeta
 */
@Component
public class DbGenerateDao {

    private static Logger logger = LoggerFactory.getLogger(DbGenerateDao.class);
    private static HashMap<String, Integer> defaultColumnLengthMap;
    private Dao dao;

    private MetaManager metaManager = MetaManager.singleInstance();

    public DbGenerateDao() {
        defaultColumnLengthMap = new HashMap<>();
        defaultColumnLengthMap.put("description", 1024);
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

    /**
     * 基于元数据管理，需元数据据管理器已加载、扫描元数据
     * <p>内部调用了sqlId:dropOneTable来删除表，
     * 内部调用了sqlId:createOneTable来创建表</p>
     *
     * @param dropBeforeCreate 存在表时，是否删除
     */
    public void createAllTables(boolean dropBeforeCreate) {
        Collection<EntityMeta> entityMetas = metaManager.getAll();
        if (entityMetas == null) {
            logger.warn("实体元数据为空，可能还未解析元数据，请解析之后，再执行该方法(createAllTables)");
            return;
        }
        for (EntityMeta em : entityMetas) {
            createOneTable(em, dropBeforeCreate);
        }
    }


    /**
     * 从数据库中删除实体对应的表
     * @param entityName 实体名称
     * @param dropBeforeCreate 存在表时，是否删除
     */
    public void createOneTable(String entityName, boolean dropBeforeCreate) {
        createOneTable(metaManager.getByEntityName(entityName), dropBeforeCreate);
    }

    private void createOneTable(EntityMeta em, boolean dropBeforeCreate) {
        if (dropBeforeCreate) {
            logger.info("  drop entity " + em.getTableName());
            dao.execute("dropOneTable", SqlParams.map("tableName", em.getTableName()));
        }
        logger.info("  create entity " + em.getTableName());

        ArrayList<ColumnMeta> addList = new ArrayList<>();
        ArrayList<ColumnMeta> uniqueList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("tableName", em.getTableName());
        map.put("addList", addList);
        map.put("uniqueList", uniqueList);

        for (FieldMeta fm : em.getFieldMetas()) {
            try {
                // TODO 默认字段长的设置
                if (defaultColumnLengthMap.containsKey(fm.getColumnName())) {
                    int len = defaultColumnLengthMap.get(fm.getColumnName()).intValue();
                    fm.getColumn().setCharMaxLength(len);
                    fm.getColumn().setNumericPrecision(len);
                    fm.getColumn().afterSet();
                }
                //创建表的语句中已有id，这里不再重复创建。改用应用程序的guid，这里还是加上id
//                    if (!fm.getColumn().getName().toLowerCase().equals("id"))
                addList.add(fm.getColumn());
                if (fm.getColumn().isUnique())
                    uniqueList.add(fm.getColumn());
            } catch (Exception e) {
                if (e.getMessage().indexOf("Duplicate column name") != -1)
                    logger.info("column " + fm.getColumnName() + " is exists，ignore.");
                else throw e;
            }
        }
        dao.execute("createOneTable", map);
    }
}
