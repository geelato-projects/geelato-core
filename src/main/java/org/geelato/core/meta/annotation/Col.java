package org.geelato.core.meta.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by hongxueqian on 14-3-23.
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Col {
    /**
     * @return (Optional) The name of the column. Defaults to the property or field name.
     */
    String name();

    /**
     * (Optional) Whether the column is a unique key.  This is a
     * shortcut for the <code>UniqueConstraint</code> annotation at the entity
     * level and is useful for when the unique key constraint
     * corresponds to only a single column. This constraint applies
     * in addition to any constraint entailed by primary key mapping and
     * to constraints specified at the entity level.
     * @return unique
     */
    boolean unique() default false;

    /**
     * (Optional) Whether the database column is nullable.
     * @return nullable
     */
    boolean nullable() default true;

    /**
     *
     * @return 数据类型
     */
    String dataType() default "";

    /**
     * @return charMaxlength
     */
    int charMaxlength() default 64;

    /**
     * @return numericPrecision
     */
    int numericPrecision() default 20;

    /**
     * @return numericScale
     */
    int numericScale() default 0;

    /**
     * @return datetimePrecision
     */
    int datetimePrecision() default 0;

}
