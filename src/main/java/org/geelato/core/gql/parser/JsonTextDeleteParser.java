package org.geelato.core.gql.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.geelato.core.mvc.Ctx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author itechgee
 * 解析json字符串，并返回参数map
 */
public class JsonTextDeleteParser {

    private static Logger logger = LoggerFactory.getLogger(JsonTextDeleteParser.class);
    private final static String KW_BIZ = "@biz";
    private final static String KEYWORD_FLAG = "@";
    private final static String FILTER_FLAG = "\\|";
    private final static String SUB_ENTITY_FLAG = "~";

    public DeleteCommand parse(String jsonText, Ctx ctx) {
        JSONObject jo = JSON.parseObject(jsonText);
        CommandValidator validator = new CommandValidator();
        if (jo.size() != 2 || !jo.containsKey(KW_BIZ)) {
            validator.appendMessage("查询的jsonText格式有误，有且只有两个顶元素，且一个为：" + KW_BIZ + "。");
            Assert.isTrue(validator.isSuccess(), validator.getMessage());
        }
        // TODO biz怎么用起来
        String biz = jo.getString(KW_BIZ);
        jo.remove(KW_BIZ);
        String key = jo.keySet().iterator().next();
        return parse(key, jo.getJSONObject(key), validator);
    }

    private DeleteCommand parse(String commandName, JSONObject jo, CommandValidator validator) {

        Assert.isTrue(validator.validateEntity(commandName), validator.getMessage());

        DeleteCommand command = new DeleteCommand();
        command.setEntityName(commandName);
//        if (jo.keySet().size() == 0) return command;

        FilterGroup fg = new FilterGroup();
        command.setWhere(fg);

//        HashMap<String,Object> params = new HashMap<>();

        jo.keySet().forEach(key -> {
            if (key.startsWith(KEYWORD_FLAG) && StringUtils.hasText(jo.getString(key))) {
                //TODO 格式不对 throw
            } else if (key.startsWith(SUB_ENTITY_FLAG)) {
                // 解析子实体
                command.getCommands().add(parse(key.substring(1), jo.getJSONObject(key), validator));
            } else {
                parseWhere(fg, key, jo, validator);
            }
        });

        Assert.isTrue(validator.isSuccess(), validator.getMessage());
        return command;
    }


    protected void parseWhere(FilterGroup fg, String key, JSONObject jo, CommandValidator validator) {
        //where子句过滤条件
        String[] ary = key.split(FILTER_FLAG);
        String field = ary[0];
        validator.validateField(field, "where");
        if (ary.length == 1) {
            //等值
            fg.addFilter(field, FilterGroup.Operator.eq, jo.getString(key));
        } else if (ary.length == 2) {
            //大于、小于...
            String fn = ary[1];
            if (!FilterGroup.Operator.contains(fn)) {
                validator.appendMessage("[");
                validator.appendMessage(key);
                validator.appendMessage("]");
                validator.appendMessage("不支持");
                validator.appendMessage(fn);
                validator.appendMessage(";只支持");
                validator.appendMessage(FilterGroup.Operator.getOperatorStrings());
            } else {
                FilterGroup.Operator operator = FilterGroup.Operator.fromString(fn);
                fg.addFilter(field, operator, jo.getString(key));
            }
        } else {
            //TODO 格式不对 throw
        }
    }
}
