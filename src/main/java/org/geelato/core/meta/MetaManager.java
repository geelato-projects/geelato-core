package org.geelato.core.meta;

import org.geelato.core.meta.annotation.Entity;
import org.geelato.core.meta.model.field.ColumnMeta;
import org.geelato.core.meta.model.field.FieldMeta;
import org.geelato.core.meta.model.entity.EntityMeta;
import org.geelato.core.meta.model.entity.TableMeta;
import org.geelato.utils.ClassScanner;
import org.geelato.utils.MapUtils;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author geemeta
 */
public class MetaManager {

    private static Lock lock = new ReentrantLock();
    private static MetaManager instance;
    private org.slf4j.Logger logger = LoggerFactory.getLogger(MetaManager.class);
    private HashMap<String, EntityMeta> entityMetadataMap = new HashMap<String, EntityMeta>();
    // key：entityName,value：boolean
//    private HashMap<String, Boolean> entityCacheAble = new HashMap<String, Boolean>();
    // TODO 多库同表名的场景暂未支持
    private HashMap<String, EntityMeta> tableNameMetadataMap = new HashMap<String, EntityMeta>();
    private static HashMap<String, String> entityFieldNameTitleMap = new HashMap<String, String>();
    private Map<String, FieldMeta> commonFieldMetas = new HashMap<>();


    private MetaManager() {
        // 解析内置的类
        logger.info("解析内置的类包含注解{}的实体！！", Entity.class);
        parseOne(ColumnMeta.class);
        parseOne(TableMeta.class);
        // 内置默认的公共字段
        addCommonFieldMeta("name", "name", "名称");
        addCommonFieldMeta("type", "type", "类型");
        addCommonFieldMeta("creator", "creator", "创建者");
        addCommonFieldMeta("updater", "updater", "更新者");
        addCommonFieldMeta("create_at", "createAt", "创建日期");
        addCommonFieldMeta("update_at", "updateAt", "更新日期");
        addCommonFieldMeta("description", "description", "描述", 1024);
        addCommonFieldMeta("id", "id", "序号");
        addCommonFieldMeta("title", "title", "标题");
        addCommonFieldMeta("password", "password", "密码");
        addCommonFieldMeta("login_name", "loginName", "登录名");
    }

    /**
     * 添加公共字段
     *
     * @param columnName 列名
     * @param fieldName  字段名
     * @param title      字段显示名，如中文名
     */
    public void addCommonFieldMeta(String columnName, String fieldName, String title) {
        FieldMeta fieldMeta = new FieldMeta(columnName, fieldName, title);
        commonFieldMetas.put(fieldName, fieldMeta);
    }

    /**
     * 添加公共字段
     *
     * @param columnName    列名
     * @param fieldName     字段名
     * @param title         字段显示名，如中文名
     * @param charMaxLength 字符长
     */
    public void addCommonFieldMeta(String columnName, String fieldName, String title, int charMaxLength) {
        FieldMeta fieldMeta = new FieldMeta(columnName, fieldName, title);
        fieldMeta.getColumn().setCharMaxLength(charMaxLength);
        commonFieldMetas.put(fieldName, fieldMeta);
    }

    /**
     * @param columnName
     * @return
     */
    public FieldMeta getCommonFieldMeta(String columnName) {
        return commonFieldMetas.get(columnName);
    }

    public static MetaManager singleInstance() {
        lock.lock();
        if (instance == null) instance = new MetaManager();
        lock.unlock();
        return instance;
    }

//    public void setApplicationContext(ApplicationContext applicationContext) {
//        this.applicationContext = applicationContext;
//        if (dao == null) dao = applicationContext.getBean(JdbcTemplate.class);
//    }

    public EntityMeta get(Class clazz) {
        String entityName = MetaRelf.getEntityName(clazz);
        if (entityMetadataMap.containsKey(entityName)) {
            return entityMetadataMap.get(entityName);
        } else {
            Iterator<String> it = entityMetadataMap.keySet().iterator();
            logger.warn("Key({}) not found in entityMetadataMap.keySet:", clazz.getName());
            while (it.hasNext()) {
                logger.warn(it.next());
            }
            return null;
        }
    }

    public Map<String, Object> newDefaultEntity(String entityName) {
        return newDefaultEntity(getByEntityName(entityName));
    }

    public Map<String, Object> newDefaultEntity(Class clazz) {
        return newDefaultEntity(get(clazz));
    }

    /**
     * 基于元数据，创建默认实体（Map），并以各字段的默认值填充
     *
     * @param em 实体元数据
     * @return 返回填充后的map
     */
    public Map<String, Object> newDefaultEntity(EntityMeta em) {
        HashMap<String, Object> map = new HashMap(em.getFieldMetas().size());
        for (FieldMeta fm : em.getFieldMetas()) {
            ColumnMeta cm = fm.getColumn();
            map.put(fm.getFieldName(), cm.getDefaultValue());
        }
        return map;
    }


    /**
     * @param entityName 实体名称，若是java元数据，则entityName为长类名（包名+类名）
     * @return 实体元数据
     */
    public EntityMeta getByEntityName(String entityName) {
        if (entityMetadataMap.containsKey(entityName)) {
            return entityMetadataMap.get(entityName);
        } else {
            Iterator<String> it = entityMetadataMap.keySet().iterator();
            logger.warn("Key({}) not found in entityMetadataMap.keySet:", entityName);
            while (it.hasNext()) {
                logger.warn(it.next());
            }
            return null;
        }
    }

    public boolean containsEntity(String entityName) {
        return entityMetadataMap.containsKey(entityName);
    }

    public EntityMeta get(String tableName) {
        if (tableNameMetadataMap.containsKey(tableName)) {
            return tableNameMetadataMap.get(tableName);
        } else {
            Iterator<String> it = tableNameMetadataMap.keySet().iterator();
            logger.warn("Key({}) not found in tableNameMetadataMap.keySet:", tableName);
            while (it.hasNext()) {
                logger.warn(it.next());
            }
            return null;
        }
    }

    public Collection<EntityMeta> getAll() {
        return entityMetadataMap.values();
    }

    public Collection<String> getAllEntityNames() {
        return entityMetadataMap.keySet();
    }

//    public Class getMappedEntity(String tableName) {
//        if (tableNameMetadataMap.containsKey(tableName)) {
//            return tableNameMetadataMap.getBizRuleScriptManager(tableName).getEntityType();
//        } else {
//            Iterator<String> it = tableNameMetadataMap.keySet().iterator();
//            logger.warn("Key({}) not found in tableNameMetadataMap.keySet:", tableName);
//            while (it.hasNext()) {
//                logger.warn(it.next());
//            }
//            return null;
//        }
//    }

    /**
     * 检索批定包名中包含所有的包javax.persistence.Entity的类，并进行解析
     *
     * @param parkeName
     */
    private void scanAndParse(String parkeName) {
        logger.debug("开始从包{}中扫描到包含注解{}的实体......", parkeName, Entity.class);
        List<Class<?>> classes = ClassScanner.scan(parkeName, true, Entity.class);
        if (classes == null) {
            logger.info("从包{}中未扫描到包含注解{}的实体！！", parkeName, Entity.class);
            return;
        }
        for (Class<?> clazz : classes) {
            parseOne(clazz);
        }
    }

    /**
     * @param packageName            扫描的包名
     * @param isUpdateMetadataFormDb 是否同时从数据库的元数据表中更新元数据信息，如字段长度
     */
    public void scanAndParse(String packageName, boolean isUpdateMetadataFormDb) {
        scanAndParse(packageName);
        //@TODO updateMetadataFromDbAfterParse的取值
        if (isUpdateMetadataFormDb) updateMetadataFromDbAfterParse(null);
    }

    /**
     * 从数据库的元数据表中更新元数据信息，如字段长度
     * 注：需在scanAndParse之后执行才有效
     *
     * @param columns 待更新的列
     */
    public void updateMetadataFromDbAfterParse(List<HashMap> columns) {
        for (HashMap map : columns) {
            String TABLE_NAME = map.get("TABLE_NAME").toString();
            EntityMeta entityMapping = null;
            for (EntityMeta obj : entityMetadataMap.values()) {
                if (obj.getTableName().equalsIgnoreCase(TABLE_NAME)) {
                    entityMapping = obj;
                    break;
                }
            }
//                Metadata metadata = entityMetadataMap.values().stream().filter(p -> p.getTableName().equalsIgnoreCase(TABLE_NAME)).findFirst().getBizRuleScriptManager();
            if (entityMapping == null) {
                continue;
            }
            String COLUMN_NAME = map.get("COLUMN_NAME").toString();
            String COLUMN_COMMENT = MapUtils.getOrDefaultString(map, "COLUMN_COMMENT", "");
            String ORDINAL_POSITION = MapUtils.getOrDefaultString(map, "ORDINAL_POSITION", "");
            String COLUMN_DEFAULT = MapUtils.getOrDefaultString(map, "COLUMN_DEFAULT", "");
            String IS_NULLABLE = MapUtils.getOrDefaultString(map, "IS_NULLABLE", "NO");
            String DATA_TYPE = MapUtils.getOrDefaultString(map, "DATA_TYPE", "varchar");
            String CHARACTER_MAXIMUM_LENGTH = MapUtils.getOrDefaultString(map, "CHARACTER_MAXIMUM_LENGTH", "20");
            String CHARACTER_OCTET_LENGTH = MapUtils.getOrDefaultString(map, "CHARACTER_OCTET_LENGTH", "24");
            String NUMERIC_PRECISION = MapUtils.getOrDefaultString(map, "NUMERIC_PRECISION", "8");
            String NUMERIC_SCALE = MapUtils.getOrDefaultString(map, "NUMERIC_SCALE", "2");
            String DATETIME_PRECISION = MapUtils.getOrDefaultString(map, "DATETIME_PRECISION", "");
            String COLUMN_TYPE = MapUtils.getOrDefaultString(map, "COLUMN_TYPE", "");
            String COLUMN_KEY = MapUtils.getOrDefaultString(map, "COLUMN_KEY", "");
            String EXTRA = MapUtils.getOrDefaultString(map, "EXTRA", "");

            FieldMeta fm = entityMapping.getFieldMetaByColumn(COLUMN_NAME);
            if (fm != null) {
                //TODO 确认还有哪些字段属性需设置
//                logger.debug("字段" + TABLE_NAME + "." + fm.getColumn().getName() + "长度：" + Integer.parseInt(CHARACTER_MAXIMUM_LENGTH));
                fm.getColumn().setCharMaxLength(Integer.parseInt(CHARACTER_MAXIMUM_LENGTH));
                fm.getColumn().setNullable("NO".equalsIgnoreCase(IS_NULLABLE) ? false : true);
                fm.getColumn().setExtra(EXTRA);
//                        fm.getColumn().setDefaultValue();
                fm.getColumn().setNumericPrecision(Integer.parseInt(NUMERIC_PRECISION));
                fm.getColumn().setNumericScale(Integer.parseInt(NUMERIC_SCALE));
                fm.getColumn().setComment(COLUMN_COMMENT);
                if (StringUtils.isEmpty(fm.getColumn().getTitle())) fm.getColumn().setTitle(COLUMN_COMMENT);
                fm.getColumn().setOrdinalPosition(Integer.parseInt(ORDINAL_POSITION));

            }


        }
    }

    /**
     * 解析一个类，并将其加入到实体元数据缓存中
     *
     * @param clazz 待解析的类
     */
    public void parseOne(Class clazz) {
        String entityName = MetaRelf.getEntityName(clazz);
        if (!entityMetadataMap.containsKey(entityName)) {
            EntityMeta entityMeta = MetaRelf.getEntityMeta(clazz);
            entityMetadataMap.put(entityMeta.getEntityName(), entityMeta);
            tableNameMetadataMap.put(entityMeta.getTableName(), entityMeta);
            if (logger.isDebugEnabled()) {
                logger.debug("success in parsing class:{}", clazz.getName());
                for (FieldMeta fm : entityMeta.getFieldMetas()) {
                    if (!entityFieldNameTitleMap.containsKey(fm.getFieldName()))
                        entityFieldNameTitleMap.put(fm.getFieldName(), fm.getTitle());
                    if (!entityFieldNameTitleMap.containsKey(fm.getColumnName()))
                        entityFieldNameTitleMap.put(fm.getColumnName(), fm.getTitle());
//                    if (logger.isDebugEnabled())
//                        logger.debug("field:column >>>" + fm.getFieldName() + ":" + fm.getColumnName());
                }
            }
        }
    }

}
