package org.geelato.core.service;

import org.geelato.core.TestHelper;
import org.geelato.core.gql.parser.JsonTextSaveParser;
import org.geelato.core.gql.parser.SaveCommand;
import org.geelato.core.meta.MetaManager;
import org.geelato.core.meta.model.entity.DemoEntity;
import org.geelato.core.mvc.Ctx;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author geemeta
 */
@RunWith(SpringRunner.class)
public class RuleServiceTest {

    private RuleService ruleService = new RuleService();

    @Test
    public void recursiveSave() {
        MetaManager.singleInstance().parseOne(DemoEntity.class);
        String json = TestHelper.getText("./gql/parser/saveJsonText3.json");
        SaveCommand saveCommand = new JsonTextSaveParser().parse(json, new Ctx());
        ruleService.recursiveSave(saveCommand);
        Assert.assertEquals("张三", saveCommand.getCommands().get(0).getValueMap().get("name"));
        Assert.assertEquals("code1234", saveCommand.getCommands().get(1).getCommands().get(0).getValueMap().get("code"));
        Assert.assertEquals("张三", saveCommand.getCommands().get(1).getCommands().get(0).getValueMap().get("name"));

    }
}