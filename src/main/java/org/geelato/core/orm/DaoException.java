package org.geelato.core.orm;

import org.geelato.core.constants.ApiResultCode;

public class DaoException extends  RuntimeException{
    private String msg;
    private int code;

    public DaoException() {
        super();
    }

    public DaoException(String msg) {
        super(msg);
        this.msg = msg;
        this.code = ApiResultCode.ERROR;
    }

    public DaoException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
