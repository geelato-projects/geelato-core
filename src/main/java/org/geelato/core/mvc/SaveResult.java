package org.geelato.core.mvc;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author geemeta(已弃用)
 * @deprecated
 */
public class SaveResult {


    private Object data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
