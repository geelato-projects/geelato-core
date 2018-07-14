package org.geelato.core.script.rule;

import org.geelato.core.AbstractManager;
import org.jeasy.rules.mvel.MVELRule;
import org.jeasy.rules.mvel.MVELRuleFactory;

import java.io.*;

/**
 * @author geemeta
 */
public class BizMvelRuleManager extends AbstractManager {
    @Override
    public void loadDb(String sqlId) {
        String ruleText = "xxxx";
        StringReader reader = new StringReader(ruleText);
        MVELRule alcoholRule = MVELRuleFactory.createRuleFrom(reader);
    }

    @Override
    public void parseFile(File file) throws IOException {
        MVELRule alcoholRule = MVELRuleFactory.createRuleFrom(new FileReader("fileX"));

    }

    @Override
    public void parseStream(InputStream is) throws IOException {

    }
}
