package org.geelato.core.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author geemeta
 */
public class ApiResult<E> {
    private String message = "";
    private int code = ApiResultCode.SUCCESS;
    private String type = ApiResultType.SUCCESS;
    private E result;

    public ApiResult() {
    }

    public ApiResult(E result) {
        setResult(result);
    }

    public ApiResult(E result, String msg, int code) {
        setCode(code);
        setMessage(msg);
        setResult(result);
    }

    public String getMessage() {
        return message;
    }

    public ApiResult<E> setMessage(String message) {
        this.message = this.message;
        return this;
    }

    public int getCode() {
        return code;
    }

    public ApiResult<E> setCode(int code) {
        this.code = code;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public E getResult() {
        return result;
    }

    public ApiResult<E> setResult(E result) {
        this.result = result;
        return this;
    }

    /**
     * 设置编码为ApiResultCode.ERROR
     *
     * @return ApiResult
     */
    public ApiResult<E> error() {
        this.code = ApiResultCode.ERROR;
        this.type = ApiResultType.ERROR;
        return this;
    }

    /**
     * 设置编码为ApiResultCode.SUCCESS
     *
     * @return ApiResult
     */
    public ApiResult<E> success() {
        this.code = ApiResultCode.SUCCESS;
        this.type = ApiResultType.SUCCESS;
        return this;
    }

    /**
     * 设置编码为ApiResultCode.WARNING
     *
     * @return ApiResult
     */
    public ApiResult<E> warning() {
        this.code = ApiResultCode.WARNING;
        this.type = ApiResultType.WARNING;
        return this;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return this.code == ApiResultCode.SUCCESS;
    }
    @JsonIgnore
    public boolean isError() {
        return this.code == ApiResultCode.ERROR;
    }
    @JsonIgnore
    public boolean isWarning() {
        return this.code == ApiResultCode.WARNING;
    }

}
