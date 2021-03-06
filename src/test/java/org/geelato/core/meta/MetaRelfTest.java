package org.geelato.core.meta;

import org.geelato.core.meta.model.entity.DemoEntity;
import org.geelato.core.meta.model.field.FieldMeta;
import org.junit.Test;

import java.util.HashMap;

/**
 * @author geemeta
 */
public class MetaRelfTest {

    @Test
    public void getColumnFieldMetas() {
        HashMap<String, FieldMeta> metaMap = MetaRelf.getColumnFieldMetas(DemoEntity.class);
        assert metaMap.get("content").getColumn().getCharMaxLength() > 1000;
    }
}