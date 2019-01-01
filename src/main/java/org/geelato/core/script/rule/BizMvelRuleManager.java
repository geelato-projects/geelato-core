package org.geelato.core.script.rule;

import org.apache.commons.collections.map.HashedMap;
import org.geelato.core.AbstractManager;
import org.geelato.core.meta.model.entity.Resources;
import org.geelato.core.util.StringUtils;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.mvel.MVELRule;
import org.jeasy.rules.mvel.MVELRuleFactory;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * @author geemeta
 */
public class BizMvelRuleManager extends AbstractManager {
    private Map<String, Rule> ruleMap = new HashedMap();


    public MVELRule getRule(String ruleCode) {
        return (MVELRule) ruleMap.get(ruleCode);
    }

    /**
     * @param sqlId 为空时，从默认的资源表platform_resources中获取
     */
    @Override
    public void loadDb(String sqlId) {
        if (StringUtils.isEmpty(sqlId)) {
            List<Map<String, Object>> mapList = dao.queryForMapList(Resources.class, "type", "biz-rule-mvel");
            for (Map<String, Object> map : mapList) {
                String ruleMvel = map.get("content").toString();
                Rules rules = MVELRuleFactory.createRulesFrom(new StringReader(ruleMvel));
                for (Rule rule : rules) {
                    ruleMap.put(rule.getName(), rule);
                }
            }
        }
    }

    @Override
    public void parseFile(File file) throws IOException {
        //TODO 若是mvel、若是目录
        Rules rules = MVELRuleFactory.createRulesFrom(new FileReader(file));
        for (Rule rule : rules) {
            ruleMap.put(rule.getName(), rule);
        }
    }

    @Override
    public void parseStream(InputStream is) throws IOException {

    }
}
