package org.geelato.core.meta.model.parser;

import org.apache.commons.beanutils.PropertyUtils;
import org.geelato.core.Ctx;
import org.geelato.core.gql.parser.CommandType;
import org.geelato.core.gql.parser.FilterGroup;
import org.geelato.core.gql.parser.SaveCommand;
import org.geelato.core.meta.MetaManager;
import org.geelato.core.meta.model.entity.EntityMeta;
import org.geelato.core.meta.model.entity.IdEntity;
import org.geelato.core.meta.model.field.FieldMeta;
import org.geelato.utils.StringUtils;
import org.geelato.utils.UIDGenerator;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author geemeta
 */
public class EntitySaveParser {
    private MetaManager metaManager = MetaManager.singleInstance();

    public SaveCommand parse(IdEntity object, Ctx ctx) {
        EntityMeta entityMeta = metaManager.get(object.getClass());
        SaveCommand command = new SaveCommand();
        command.setEntityName(entityMeta.getEntityName());


        Map entity = new HashMap(entityMeta.getFieldMetas().size());
        try {
            for (FieldMeta fm : entityMeta.getFieldMetas()) {
                entity.put(fm.getFieldName(), PropertyUtils.getProperty(object, fm.getFieldName()));
            }
            String PK = entityMeta.getId().getFieldName();
            if (StringUtils.isNotBlank(object.getId())) {
                command.setCommandType(CommandType.Update);

                FilterGroup fg = new FilterGroup();
                fg.addFilter(PK, String.valueOf(entity.get(PK)));
                command.setWhere(fg);
                command.setCommandType(CommandType.Update);

                if (entity.containsKey("updateAt")) {
                    entity.put("updateAt", new Date());
                }
                if (entity.containsKey("updater")) {
                    entity.put("updater", ctx.get("userId"));
                }
                if (entity.containsKey("updaterName")) {
                    entity.put("updaterName", ctx.get("userName"));
                }

                String[] updateFields = new String[entity.size()];
                entity.keySet().toArray(updateFields);
                command.setFields(updateFields);
                command.setValueMap(entity);
            } else {
                command.setCommandType(CommandType.Insert);
                entity.put(PK, UIDGenerator.generate());
                if (entity.containsKey("createAt")) {
                    entity.put("createAt", new Date());
                }
                if (entity.containsKey("creator")) {
                    entity.put("creator", ctx.get("userId"));
                }
                if (entity.containsKey("creatorName")) {
                    entity.put("creatorName", ctx.get("userName"));
                }
                if (entity.containsKey("updateAt")) {
                    entity.put("updateAt", new Date());
                }
                if (entity.containsKey("updater")) {
                    entity.put("updater", ctx.get("userId"));
                }
                if (entity.containsKey("updaterName")) {
                    entity.put("updaterName", ctx.get("userName"));
                }
                if (entity.containsKey("buId")) {
                    entity.put("buId", ctx.getCurrentUser().getBuId());
                }
                if (entity.containsKey("deptId")) {
                    entity.put("deptId", ctx.getCurrentUser().getDefaultOrgId());
                }

                String[] insertFields = new String[entity.size()];
                entity.keySet().toArray(insertFields);
                command.setFields(insertFields);
                command.setValueMap(entity);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return command;
    }


}
