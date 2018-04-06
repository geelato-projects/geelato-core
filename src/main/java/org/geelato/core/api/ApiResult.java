package org.geelato.core.api;

/**
 * @author geemeta
 *
 */
public class ApiResult<E> {
    private String msg = "";
    private String code = ApiResultCode.SUCCESS;
    private E data;

    public ApiResult() {
    }

    public ApiResult(E data, String msg, String code) {
        setCode(code);
        setMsg(msg);
        setData(data);
    }

    public String getMsg() {
        return msg;
    }

    public ApiResult<E> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public String getCode() {
        return code;
    }

    public ApiResult<E> setCode(String code) {
        this.code = code;
        return this;
    }

    public E getData() {
        return data;
    }

    public ApiResult<E> setData(E data) {
        this.data = data;
        return this;
    }

}
