package org.geelato.core.meta;


import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.geelato.core.gql.TypeConverter;
import org.geelato.core.meta.annotation.*;
import org.geelato.core.meta.model.entity.TableForeign;
import org.geelato.core.meta.model.field.FieldMeta;
import org.geelato.core.meta.model.entity.EntityMeta;
import org.geelato.core.meta.model.entity.TableMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hongxueqian on 14-3-23.
 */
public class MetaRelf {

    private static ApplicationContext applicationContext;
    private static Logger logger = LoggerFactory.getLogger(MetaRelf.class);
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // 一些类型默认的长度
    public static Map<String, Long> dataTypeDefaultMaxLengthMap = new HashedMap();

    static {
        // 最大长度255个字元(2^8-1)
        dataTypeDefaultMaxLengthMap.put("tinyText", 65535L);
        // 最大长度65535个字元(2^16-1)
        dataTypeDefaultMaxLengthMap.put("text", 65535L);
        // 最大长度 16777215 个字元(2^24-1)
        dataTypeDefaultMaxLengthMap.put("mediumText", 16777215L);
        // 最大长度4294967295个字元 (2^32-1)
        dataTypeDefaultMaxLengthMap.put("longText", 4294967295L);
    }

    /**
     * 如果在spring环境下，可以设置该值，以便可直接获取spring中已创建的bean，不需重新创建
     *
     * @param context
     */
    public static void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    private static Object getBean(Class clazz) {
        if (applicationContext == null) {
            try {
                return clazz.newInstance();
            } catch (InstantiationException e) {
                logger.error("创建对象失败！", e);
            } catch (IllegalAccessException e) {
                logger.error("创建对象失败！", e);
            }
            return null;
        } else {
            return applicationContext.getBean(clazz);
        }
    }

    /**
     * 循环向上转型, 获取对象的DeclaredFields
     * <p>
     * 如向上转型到Object仍无法找到, 返回null.
     */
    public static HashMap<String, Field> getAccessibleFields(final Object obj) {
        Assert.notNull(obj, "object can't be null");
        HashMap<String, Field> fieldMap = new HashMap<String, Field>();
        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            Field[] fields = superClass.getDeclaredFields();
            for (Field field : fields) {
                if (!fieldMap.containsKey(field.getName()))
                    fieldMap.put(field.getName(), field);
            }
        }
        return fieldMap;
    }

    public static TableMeta getTableMeta(final Object obj) {
        return getTableMeta(obj.getClass());
    }

    /**
     * 基于类的title注解，解析出表元数据
     * @param clazz
     * @return
     */
    public static TableMeta getTableMeta(Class clazz) {
        Title title = (Title) clazz.getAnnotation(Title.class);
        // Entity entity = (Entity) clazz.getAnnotation(Entity.class);
        // if(entity!=null){
        // }
        return new TableMeta(getTableName(clazz), title != null ? title.title():"", getEntityName(clazz), title != null ? title.description() : "");
    }

    public static EntityMeta getEntityMeta(Class clazz) {
        EntityMeta em = new EntityMeta();
        em.setId(getId(clazz));
        em.setTableMeta(getTableMeta(clazz));
        em.setEntityName(em.getTableMeta().getEntityName());
        em.setEntityType(clazz);
        Collection<TableForeign> tableForeigns = new ArrayList<>();
        HashMap<String, FieldMeta> map = getColumnFieldMetas(clazz, tableForeigns);
        em.setFieldMetas(map.values());
        em.setTableForeigns(tableForeigns);
        if (em.getFieldMetas() != null)
            for (FieldMeta fm : em.getFieldMetas()) {
                fm.getColumn().setTableName(em.getTableMeta().getTableName());
            }
        em.setDictDataSourceMap(getDictDataSourceMap(clazz));
        return em;
    }

    /**
     * 基于注解@Entity,按以下顺序获取，有值则返回：
     * table name 到 entity name 到 simple name of class
     *
     * @param clazz
     * @return 表名
     */
    public static String getTableName(Class clazz) {
        Entity entity = (Entity) clazz.getAnnotation(Entity.class);
        if (entity == null)
            return clazz.getSimpleName();
        if (StringUtils.hasText(entity.table())) {
            return entity.table();
        } else if (StringUtils.hasText(entity.name())) {
            return entity.name();
        } else {
            return clazz.getSimpleName();
        }
    }

    /**
     * 基于注解@Entity,按以下顺序获取，有值则返回：
     * model name -> name of class (with package name)
     *
     * @param clazz
     * @return 实体名
     */
    public static String getEntityName(Class clazz) {
        Entity entity = (Entity) clazz.getAnnotation(Entity.class);
        if (entity == null)
            return clazz.getSimpleName();
        if (StringUtils.hasText(entity.name())) {
            return entity.name();
        } else {
            return clazz.getName();
        }
    }


    public static FieldMeta getId(final Object obj) {
        Assert.notNull(obj, "object can't be null");
        return getId(obj.getClass());
    }

    public static FieldMeta getId(Class clazz) {
        for (Class<?> searchType = clazz; searchType != Object.class; searchType = searchType.getSuperclass()) {
            Method[] methods = searchType.getDeclaredMethods();
            for (Method method : methods) {
                Id id = method.getAnnotation(Id.class);
                if (id != null) {
                    String fieldName = method.getName().substring(3);
                    String firstChar = "" + fieldName.charAt(0);
                    fieldName = fieldName.replaceFirst(firstChar, firstChar.toLowerCase());
                    Title cn = method.getAnnotation(Title.class);
                    String title = cn != null ? (StringUtils.isEmpty(cn.title()) ? fieldName : cn.title()) : fieldName;
                    String columnName = fieldName;
                    Col col = method.getAnnotation(Col.class);
                    if (col != null) {
                        columnName = col.name();
                    }
                    return new FieldMeta(columnName, fieldName, title);
                }
            }
        }
        throw new RuntimeException("No @Id founded from " + clazz.getName() + "!");
    }

    public static HashMap<String, FieldMeta> getColumnFieldMetas(final Object obj) {
        Assert.notNull(obj, "object can't be null");
        return getColumnFieldMetas(obj.getClass());
    }

    /**
     * 解析get**方法或is**方法的映射，其它的，如set**方法不解析
     *
     * @param clazz
     * @return *
     */
    public static HashMap<String, FieldMeta> getColumnFieldMetas(Class clazz) {
        return getColumnFieldMetas(clazz, null);
    }

    /**
     * 解析get**方法或is**方法的映射，其它的，如set**方法不解析
     * @param clazz
     * @param tableForeigns 不为null时，解析表外键
     * @return
     */
    public static HashMap<String, FieldMeta> getColumnFieldMetas(Class clazz, Collection<TableForeign> tableForeigns) {
        Object bean = getBean(clazz);
        HashMap<String, FieldMeta> map = new HashMap<String, FieldMeta>();
        for (Class<?> searchType = clazz; searchType != Object.class; searchType = searchType.getSuperclass()) {
            Method[] methods = searchType.getDeclaredMethods();
            for (Method method : methods) {
                try {
                    if (!method.getName().startsWith("get") && !method.getName().startsWith("is")) continue;
                    String fieldName = "";
                    //去掉get三个字符
                    if (method.getName().startsWith("get"))
                        fieldName = method.getName().substring(3);
                    else if (method.getName().startsWith("is"))
                        fieldName = method.getName().substring(2);
                    //首字符变小写
                    fieldName = firstCharToLow(fieldName);
//                    String firstChar = "" + fieldName.charAt(0);
//                    fieldName = fieldName.replaceFirst(firstChar, firstChar.toLowerCase());
                    if (!map.containsKey(fieldName)) {
                        //如果列中有@Transient，则跳过
                        if (method.getAnnotation(Transient.class) == null) {
                            //列，可能包括名为id的列
                            Col column = method.getAnnotation(Col.class);

                            //列的中文信息
                            Title cn = method.getAnnotation(Title.class);
                            String title = cn != null ? (StringUtils.isEmpty(cn.title()) ? fieldName : cn.title()) : fieldName;
                            String description = cn != null ? cn.description() : "";
                            FieldMeta cfm = null;
                            if (column != null && column.name() != null) {
                                cfm = new FieldMeta(column.name(), fieldName, title);
                            } else {
                                cfm = new FieldMeta(fieldName, fieldName, title);
                            }
                            //cfm.setCol(column);
                            if (column != null) {
                                cfm.getColumn().setNullable(column.nullable());
                                cfm.getColumn().setUnique(column.unique());
                                cfm.getColumn().setName(column.name());
                                cfm.getColumn().setNumericPrecision(column.numericPrecision());
                                cfm.getColumn().setNumericScale(column.numericScale());
                                cfm.getColumn().setIsRefColumn(column.isRefColumn());
                                cfm.getColumn().setRefLocalCol(column.refLocalCol());
                                cfm.getColumn().setRefColName(column.refColName());
                                cfm.getColumn().setRefTables(column.refTables());
                                // charMaxlength 未设置值时，则依据dataType的获取length默认值
                                cfm.getColumn().setCharMaxLength(column.charMaxlength() > 0 ? column.charMaxlength() : MapUtils.getLong(dataTypeDefaultMaxLengthMap, column.dataType(), 64L));
                                cfm.getColumn().setDataType(column.dataType());
                                try {
                                    Object defaultValue = method.invoke(bean);
                                    if (defaultValue != null)
                                        cfm.getColumn().setDefaultValue(String.valueOf(method.invoke(bean)));
                                } catch (IllegalAccessException e) {
                                    logger.error("获取默认值失败:" + clazz.getName() + ">" + fieldName, e);
                                } catch (InvocationTargetException e) {
                                    logger.error("获取默认值失败:" + clazz.getName() + ">" + fieldName, e);
                                }
//                                logger.debug("column.dataType() >>"+column.name()+">>{}",column.dataType());

                                //解析外键
                                if (tableForeigns != null) {
                                    ForeignKey foreignKey = method.getAnnotation(ForeignKey.class);
                                    if(foreignKey != null){
                                        TableForeign tableForeign = new TableForeign();
                                        tableForeign.setMainTable(getEntityName(clazz));
                                        tableForeign.setMainTableCol(column.name());
                                        tableForeign.setForeignTable(getEntityName(foreignKey.fTable()));
                                        if (foreignKey.fCol().length() == 0) {
                                            tableForeign.setForeignTableCol(getId(clazz).getColumnName());
                                        } else {
                                            tableForeign.setForeignTableCol(foreignKey.fCol());
                                        }
                                        tableForeigns.add(tableForeign);
                                    }
                                }
                            }
                            cfm.getColumn().setDescription(description);
                            cfm.setFieldType(method.getReturnType());
//                            logger.debug("cfm.getColumn().getDataType())>>{}",cfm.getColumn().getDataType());
                            //如果未指定类型（如特殊的json），则采用方法的返回类型
                            //TODO 会导致 cfm.setFieldType与cfm.getColumn().getDataType())不一致,需为mybatis自定义一个JSON类型
                            if (StringUtils.isEmpty(cfm.getColumn().getDataType()))
                                cfm.getColumn().setDataType(TypeConverter.toSqlTypeString(method.getReturnType()));
                            cfm.getColumn().afterSet();
                            map.put(fieldName, cfm);
                        }
                    }
                } catch (RuntimeException e) {
                    logger.error("解析" + clazz.getName() + "失败！method:" + method.getName());
                    throw e;
                }
            }
        }
        return map;
    }

    /**
     * 解析get**方法或is**方法的映射，其它的不解析
     *
     * @param clazz
     * @return *
     */
    public static HashMap<String, DictDataSource> getDictDataSourceMap(Class clazz) {
        HashMap<String, DictDataSource> map = new HashMap<String, DictDataSource>();
        for (Class<?> searchType = clazz; searchType != Object.class; searchType = searchType.getSuperclass()) {
            Method[] methods = searchType.getDeclaredMethods();
            for (Method method : methods) {
                try {
                    String fieldName = getFieldNameByGetMethod(method.getName());
                    if (fieldName == null) continue;
                    if (!map.containsKey(fieldName)) {
                        DictDataSrc ds = method.getAnnotation(DictDataSrc.class);
                        if (ds != null) {
                            DictDataSource dds = new DictDataSource();
                            dds.setGroup(ds.group());
                            dds.setCode(ds.code());
                            map.put(fieldName, dds);
                        }
                    }
                } catch (RuntimeException e) {
                    logger.error("解析" + clazz.getName() + "失败！method:" + method.getName());
                    throw e;
                }
            }

        }
        return map;
    }

    /**
     * @param before 不能为空
     * @param after  不能为空，且与before为相同类型
     * @return 对象的差异值
     * @Param ignoreFieldMap 不需要检查比较的字段
     */
    public static String compareEntityValue(Object before, Object after, Map<String, String> ignoreFieldMap) {
        Assert.notNull(before, "不能为空");
        Assert.notNull(after, "不能为空");
        Assert.isTrue(before.getClass().equals(after.getClass()), "before与after为相同类型");
        HashMap<String, Field> beforeFieldHashMap = getAccessibleFields(before);
        if (beforeFieldHashMap.values().size() == 0) return "";
        HashMap<String, Field> afterFieldHashMap = getAccessibleFields(after);
        StringBuilder jsonResult = new StringBuilder();
        jsonResult.append("[");
        for (Field field : beforeFieldHashMap.values()) {
            if (ignoreFieldMap != null && ignoreFieldMap.containsKey(field.getName())) continue;
            Field afterField = null;
            try {
                field.setAccessible(true);
                Object beforeValueObject = field.get(before);
                String beforeValue = beforeValueObject == null ? "" : (beforeValueObject instanceof Date ? DATE_FORMAT.format(beforeValueObject) : beforeValueObject.toString());

                afterField = afterFieldHashMap.get(field.getName());
                afterField.setAccessible(true);
                Object afterValueObject = afterField == null ? "" : afterField.get(after);
                String afterValue = afterValueObject == null ? "" : (afterValueObject instanceof Date ? DATE_FORMAT.format(afterValueObject) : afterValueObject.toString());
                if (!beforeValue.equals(afterValue)) {
                    jsonResult.append("{\"field\":\"");
                    jsonResult.append(field.getName());
                    jsonResult.append("\",\"from\":\"" + beforeValue + "\",\"to\":\"" + afterValue + "\"},");
                }
            } catch (IllegalAccessException e) {
                logger.error("", e);
            } finally {
                field.setAccessible(false);
                if (afterField != null) afterField.setAccessible(true);
            }
        }
        jsonResult.deleteCharAt(jsonResult.length() - 1);
        //TODO error 目前新增实体时，结果为"]"，需完善
        return jsonResult.append("]").toString();
    }


    private static String firstCharToLow(String str) {
        String firstChar = "" + str.charAt(0);
        return str.replaceFirst(firstChar, firstChar.toLowerCase());
    }

    private static String getFieldNameByGetMethod(String methodName) {
        if (!methodName.startsWith("get") && !methodName.startsWith("is")) return null;
        String fieldName = "";
        //去掉get三个字符
        if (methodName.startsWith("get"))
            fieldName = methodName.substring(3);
        if (methodName.startsWith("is"))
            fieldName = methodName.substring(2);
        //首字符变小写
        return firstCharToLow(fieldName);
    }

}
