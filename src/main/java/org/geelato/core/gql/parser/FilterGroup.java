package org.geelato.core.gql.parser;


import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The filter operator (comparison).
 * The supported operators are:
 * "eq" (equal to), "neq" (not equal to),
 * "lt" (less than), "lte" (less than or equal to),
 * "gt" (greater than),"gte" (greater than or equal to),
 * "startswith", "endswith", "contains".
 * The last three are supported only for string fields.
 *
 * @author geemeta
 */
public class FilterGroup {

    private Logic logic = Logic.and;
    private List<Filter> filters;
    private HashMap<String, Object> params;
    private int renameIndex = 1;

    public Logic getLogic() {
        return logic;
    }

    public void setLogic(Logic logic) {
        this.logic = logic;
    }

    public List<Filter> getFilters() {
        if (filters == null) {
            filters = new ArrayList<Filter>();
        }
        return filters;
    }

//    public void setFilters(List<Filter> filters) {
//        this.filters = filters;
//    }

    /**
     * 如果filter中的field存在同名时，自动重命名
     *
     * @param filter
     */
    public FilterGroup addFilter(Filter filter) {
        if (this.filters == null) {
            this.filters = new ArrayList<Filter>();
            this.params = new HashMap<String, Object>();
        }
        this.filters.add(filter);
        if (params.containsKey(filter.getField())) {
            filter.setField(filter.getField());
            renameIndex++;
        }
        params.put(filter.getField() + renameIndex, filter.getValue());
        return this;
    }

    public FilterGroup addFilter(String field, Operator operator, String value) {
        addFilter(new Filter(field, operator, value));
        return this;
    }

    public FilterGroup addFilter(String field, String value) {
        addFilter(new Filter(field, Operator.eq, value));
        return this;
    }

    public HashMap<String, Object> getParams() {
        return params;
    }

    public Filter newFilter() {
        return new Filter();
    }

    public Filter newFilter(String field, Operator operator, String value) {
        return new Filter(field, operator, value);
    }

    public class Filter {
        public Filter() {
        }

        public Filter(String field, Operator operator, String value) {
            this.setField(field);
            this.setOperator(operator);
            this.setValue(value);
        }

        private String field;
        private Operator operator;
        private String value;
        //        private String columnName;
        private boolean isRefField;
        private String refEntityName;

//        public String getColumnName() {
//            return columnName;
//        }
//
//        public void setColumnName(String columnName) {
//            this.columnName = columnName;
//        }

        public String getField() {
            return field;
        }

        /**
         * @param field tableName.fieldName or fieldName
         * @return
         */
        public Filter setField(String field) {
            if (StringUtils.hasText(field) && field.indexOf(".") != -1) {
                String[] arrays = field.split(".");
                this.isRefField = true;
                this.field = arrays[1];
                this.refEntityName = arrays[0];
            } else {
                this.isRefField = false;
                this.field = field;
                this.refEntityName = "";
            }
            return this;
        }

        public Operator getOperator() {
            return operator;
        }

        public Filter setOperator(Operator operator) {
            this.operator = operator;
            return this;
        }

        public String getValue() {
            return value;
        }

        public Filter setValue(String value) {
            this.value = value;
            return this;
        }

        /**
         * 基于setField的值，若为tableName.fieldName，则为true
         */
        public boolean isRefField() {
            return isRefField;
        }

        public String getRefEntityName() {
            return this.refEntityName;
        }

        //filter: { field: "name", operator: "startswith", value: "Jane" }
    }


    public enum Operator {
        eq("eq"), neq("neq"), lt("lt"), lte("lte"), gt("gt"), gte("gte"), startWith("startwith"), endWith("endwith"), contains("contains"), in("in");

        private String text;

        Operator(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }

        // Implementing a fromString method on an enum type
        private static final Map<String, Operator> stringToEnum = new HashMap<String, Operator>();
        private static String operatorStrings = null;

        static {
            StringBuilder sb = new StringBuilder();
            for (Operator operator : values()) {
                stringToEnum.put(operator.toString(), operator);
                sb.append(operator.toString());
                sb.append(",");
            }
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1);
            }
            operatorStrings = sb.toString();
        }

        public static boolean contains(String symbol) {
            return stringToEnum.containsKey(symbol);
        }

        public static String getOperatorStrings() {
            return operatorStrings;
        }

        public static Operator fromString(String symbol) {
            return stringToEnum.get(symbol.toLowerCase());
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public enum Logic {
        or("or"), and("and");
        private String text;

        Logic(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }

        // Implementing a fromString method on an enum type
        private static final Map<String, Logic> stringToEnum = new HashMap<String, Logic>();

        static {
            // Initialize map from constant name to enum constant
            for (Logic logic : values()) {
                stringToEnum.put(logic.toString(), logic);
            }
        }

        public static Logic fromString(String symbol) {
            return stringToEnum.get(symbol.toLowerCase());
        }

        @Override
        public String toString() {
            return text;
        }
    }


}
