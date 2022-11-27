package org.geelato.core.meta.model;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverter;
import org.geelato.core.meta.MetaManager;
import org.geelato.core.meta.model.entity.EntityMeta;
import org.geelato.core.meta.model.field.FieldMeta;
import org.apache.commons.beanutils.BeanUtils;
import org.geelato.core.orm.DateTimeConverter;
import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author geemeta
 */
public class CommonRowMapper<T> implements RowMapper<T> {
    private static MetaManager metaManager = MetaManager.singleInstance();
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public CommonRowMapper() {
        ConvertUtils.register(new DateLocaleConverter(), Date.class);
//        ConvertUtils.register(new Converter() {
//            @Override
//            public <T> T convert(Class<T> type, Object value) {
//
//                try {
//                    return (T) sdf.parse(value.toString());
//                } catch (ParseException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }, Date.class);
        ConvertUtils.deregister(Date.class);
        ConvertUtils.register(new DateTimeConverter(), Date.class);

    }

    @Override
    public T mapRow(ResultSet resultSet, int i) throws SQLException {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        String tableName = rsmd.getTableName(1);
        EntityMeta em = metaManager.get(tableName);

        Converter c = ConvertUtils.lookup(Date.class);
        Converter d = ConvertUtils.lookup(java.sql.Date.class);

        T bean = null;
        if (em.getEntityType() != null) {
            try {
                bean = (T) em.getEntityType().newInstance();
                for (int _iterator = 0; _iterator < rsmd.getColumnCount(); _iterator++) {
                    // getting the SQL column name
                    String columnName = rsmd.getColumnName(_iterator + 1);
                    // reading the value of the SQL column
                    Object columnValue = resultSet.getObject(_iterator + 1);
                    // iterating over outputClass attributes to check if
                    // any attribute has 'Column' annotation with
                    // matching 'name' value
                    for (FieldMeta fm : em.getFieldMetas()) {
                        if (columnName.equals(fm.getColumnName())) {
                            BeanUtils.setProperty(bean, fm.getFieldName(), columnValue);
                            break;
                        }
                    }
                }
                return bean;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            //map?
        }
        return null;
    }
}
