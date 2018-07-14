package org.geelato.core.meta;

import org.geelato.core.meta.model.entity.DemoEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author geemeta
 */
@RunWith(SpringRunner.class)
public class MetaManagerTest {

    @Test
    public void parseOne() {
        MetaManager.singleInstance().parseOne(DemoEntity.class);
    }
}