package org.geelato.core.gql.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.geelato.core.meta.MetaManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author geelato
 * 解析json字符串，并返回参数map
 */
public class JsonTextQueryParser {

    private static Logger logger = LoggerFactory.getLogger(JsonTextQueryParser.class);
    private static MetaManager metaManager = MetaManager.singleInstance();
    //page_num即offset，记录位置
    private final static String KEYWORD_FLAG = "@";
    private final static String FILTER_FLAG = "\\|";
    // 可对@fs中的字段进行重命名，字段原名+一到多个空格+字段重命名
    private final static String ALIAS_FLAG = "[\\s]+";
    private final static String SUB_ENTITY_FLAG = "~";
    private final static String KW_PAGE = "@p";
    private final static String KW_FIELDS = "@fs";
    private final static String KW_ORDER_BY = "@order";
    private final static String KW_GROUP_BY = "@group";
    private final static String KW_HAVING = "@having";
    private final static String KW_KEY = "@key";

    private static Map<String, String> orderMap = null;

    static {
        orderMap = new HashMap<>(2);
        orderMap.put("+", "asc");
        orderMap.put("-", "desc");
    }

    /**
     * 批量查询解析
     *
     * @param queryJsonText
     * @return
     */
    public List<QueryCommand> parseMulti(String queryJsonText) {
        JSONArray ja = JSON.parseArray(queryJsonText);
        CommandValidator validator = new CommandValidator();
        List list = new ArrayList<QueryCommand>(ja.size());
        for (Object obj : ja) {
            JSONObject jo = (JSONObject) obj;
            if (jo.size() != 1) {
                validator.appendMessage("一个实体查询jsonText，有且只有一个根元素。");
                Assert.isTrue(validator.isSuccess(), validator.getMessage());
            }
            String key = jo.keySet().iterator().next();
            list.add(parse(key, jo.getJSONObject(key), validator));
        }
        return list;
    }

    /**
     * 单查询解析
     *
     * @param queryJsonText
     * @return
     */
    public QueryCommand parse(String queryJsonText) {
        JSONObject jo = JSON.parseObject(queryJsonText);
        CommandValidator validator = new CommandValidator();
        if (jo.size() != 1) {
            validator.appendMessage("一个实体查询jsonText，有且只有一个根元素。");
            Assert.isTrue(validator.isSuccess(), validator.getMessage());
        }
        String key = jo.keySet().iterator().next();
        return parse(key, jo.getJSONObject(key), validator);
    }

    private QueryCommand parse(String entityName, JSONObject jo, CommandValidator validator) {
        Assert.isTrue(validator.validateEntity(entityName), validator.getMessage());

        QueryCommand command = new QueryCommand();
        command.setEntityName(entityName);
//        if (jo.keySet().size() == 0) return command;

        FilterGroup fg = new FilterGroup();
        command.setWhere(fg);

//        HashMap<String,Object> params = new HashMap<>();

        jo.keySet().forEach(key -> {
            if (key.startsWith(KEYWORD_FLAG) && StringUtils.hasText(jo.getString(key))) {
                // segments e.g. {"name|+","code|-"}
                String[] segments = jo.getString(key).split(",");
                //关键字
                switch (key) {
                    case KW_FIELDS:
                        String[] fieldNames = new String[segments.length];
                        for (int i = 0; i < segments.length; i++) {
                            String[] ary = segments[i].split(ALIAS_FLAG);
                            if (ary.length == 1) {
                                validator.validateField(ary[0], KW_FIELDS);
                                fieldNames[i] = ary[0];
                            }else if (ary.length == 2) {
                                validator.validateField(ary[0], KW_FIELDS);
                                fieldNames[i] = ary[0];
                                command.getAlias().put(ary[0],ary[1]);
                            } else {
                                validator.appendMessage(KW_FIELDS);
                                validator.appendMessage("的值格式有误，正确如：name userName,age,sex，其中userName为重命名。");
                            }
                        }
                        command.setFields(fieldNames);
                        break;
                    case KW_ORDER_BY:
                        StringBuilder sb = new StringBuilder();
                        for (String order : segments) {
                            String[] strs = order.split(FILTER_FLAG);
                            if (strs.length == 2 && orderMap.containsKey(strs[1])) {
                                validator.validateField(strs[0], KW_ORDER_BY);
                                sb.append(sb.length() == 0 ? " " : ",");
                                sb.append(validator.getColumnName(strs[0]));
                                sb.append(" ");
                                sb.append(orderMap.get(strs[1]));
                            } else {
                                validator.appendMessage(KW_ORDER_BY);
                                validator.appendMessage("的值格式有误，正确如：age|+,name|-。");
                            }
                        }
                        if (sb.length() > 0)
                            command.setOrderBy(sb.toString());
                        break;
                    case KW_GROUP_BY:
                        validator.validateField(segments, KW_GROUP_BY);
                        command.setGroupBy(jo.getString(key));
                        break;
                    case KW_HAVING:
                        //@TODO
                        break;
                    case KW_PAGE:
                        command.setQueryForList(true);
                        String[] page = jo.getString(KW_PAGE).split("[ ]*,[ ]*");
                        boolean isSuccess = true;
                        if (page.length == 2 && org.apache.commons.lang3.StringUtils.isNumeric(page[0]) && org.apache.commons.lang3.StringUtils.isNumeric(page[1])) {
                            command.setPageNum(Integer.parseInt(page[0]));
                            command.setPageSize(Integer.parseInt(page[1]));
                            if (command.getPageNum() <= 0 || command.getPageSize() <= 0) {
                                isSuccess = false;
                            }
                        } else {
                            isSuccess = false;
                        }
                        if (!isSuccess) {
                            validator.appendMessage("[");
                            validator.appendMessage(KW_PAGE);
                            validator.appendMessage("]格式有误，正确格式为“第几页，每页记录数”，从第1页开始，如：1,10；");
                        }
                        break;
                    default:
                        validator.appendMessage("[");
                        validator.appendMessage(key);
                        validator.appendMessage("]");
                        validator.appendMessage("不支持;");
                }
            } else if (key.startsWith(SUB_ENTITY_FLAG)) {
                //解析子实体
                command.getCommands().add(parse(key.substring(1), jo.getJSONObject(key), validator));
            } else {
                //where子句过滤条件
                String[] ary = key.split(FILTER_FLAG);
                String field = ary[0];
                //TODO
//                if(!"draw".equals(field)){
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

//            }
        });

        Assert.isTrue(validator.isSuccess(), validator.getMessage());
        return command;
    }


}
