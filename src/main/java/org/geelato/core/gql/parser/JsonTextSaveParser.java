package org.geelato.core.gql.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.geelato.core.meta.MetaManager;
import org.geelato.core.mvc.Ctx;
import org.geelato.utils.UIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author itechgee
 * 解析json字符串，并返回参数map
 */
public class JsonTextSaveParser {

    private static Logger logger = LoggerFactory.getLogger(JsonTextSaveParser.class);
    private MetaManager metaManager = MetaManager.singleInstance();

    private final static String SUB_ENTITY_FLAG = "#";
    private final static String KW_BIZ = "@biz";

    /**
     * @param jsonText
     * @param ctx
     * @return
     */
    public SaveCommand parse(String jsonText, Ctx ctx) {
        JSONObject jo = JSON.parseObject(jsonText);
        CommandValidator validator = new CommandValidator();
        if (jo.size() != 2 || !jo.containsKey(KW_BIZ)) {
            validator.appendMessage("查询的jsonText格式有误，有且只有两个顶元素，且一个为：" + KW_BIZ + "。");
            Assert.isTrue(validator.isSuccess(), validator.getMessage());
        }
        // TODO biz暂未实现
        String biz = jo.getString(KW_BIZ);
        jo.remove(KW_BIZ);
        String entityName = jo.keySet().iterator().next();
        return parse(ctx, entityName, jo.getJSONObject(entityName), validator);
    }

    /**
     * 递归解析保存操作命令，里面变更在执行期再解析，不在此解析
     *
     * @param ctx
     * @param commandName
     * @param jo
     * @param validator
     * @return
     */
    private SaveCommand parse(Ctx ctx, String commandName, JSONObject jo, CommandValidator validator) {
        Assert.isTrue(validator.validateEntity(commandName), validator.getMessage());
        SaveCommand command = new SaveCommand();
        command.setEntityName(commandName);

        Map<String, Object> params = new HashMap();

        jo.keySet().forEach(key -> {
            if (key.startsWith(SUB_ENTITY_FLAG)) {
                // 解析子实体
                // 子实体是数组还是实体
                Object sub = jo.get(key);
                if (sub instanceof JSONObject) {
                    SaveCommand subCommand = parse(ctx, key.substring(1), (JSONObject) sub, validator);
                    subCommand.setParentCommand(command);
                    command.getCommands().add(subCommand);
                } else if (sub instanceof JSONArray) {
                    ((JSONArray) sub).forEach(subJo -> {
                        SaveCommand subCommand = parse(ctx, key.substring(1), (JSONObject) subJo, validator);
                        subCommand.setParentCommand(command);
                        command.getCommands().add(subCommand);
                    });
                } else {
                    validator.appendMessage(key + "的值应为object或array");
                }
            } else {
                // 字段
                validator.validateField(key, "字段");
                params.put(key, jo.getString(key));
            }
        });
        Assert.isTrue(validator.isSuccess(), validator.getMessage());

        String[] fields = new String[params.keySet().size()];
        params.keySet().toArray(fields);
        String PK = validator.getPK();

        if (validator.hasPK(fields) && StringUtils.hasText(jo.getString(PK))) {
            //update
            FilterGroup fg = new FilterGroup();
            fg.addFilter(PK, jo.getString(PK));
            command.setWhere(fg);
            command.setCommandType(CommandType.Update);
            Object pkValue = params.remove(PK);
            if (validator.hasKeyField("updateAt")) params.put("updateAt", new Date());
            if (validator.hasKeyField("updater")) params.put("updater", ctx.get("userId"));
            String[] updateFields = new String[params.keySet().size()];
            params.keySet().toArray(updateFields);
            command.setFields(updateFields);
            command.setValueMap(params);
            command.setPK(jo.getString(PK));
        } else {
            //insert
            command.setCommandType(CommandType.Insert);

            //commandName==entityName
            Map<String, Object> entity = metaManager.newDefaultEntity(commandName);
            entity.putAll(params);
            entity.put(PK, UIDGenerator.generate(1));
            if (entity.containsKey("createAt")) entity.put("createAt", new Date());
            if (entity.containsKey("creator")) entity.put("creator", ctx.get("userId"));
            if (entity.containsKey("updateAt")) entity.put("updateAt", new Date());
            if (entity.containsKey("updater")) entity.put("updater", ctx.get("userId"));

            String[] insertFields = new String[entity.size()];
            entity.keySet().toArray(insertFields);
            command.setFields(insertFields);
            command.setValueMap(entity);
            command.setPK(entity.get(PK).toString());
        }
        return command;
    }
}
