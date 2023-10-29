package org.geelato.core.exception;


import org.geelato.core.constants.ApiResultCode;

public class TestException extends RuntimeException {
    private String msg;
    private int code;

    public TestException() {
        super();
    }

    public TestException(String msg) {
        super(msg);
        this.msg = msg;
        this.code = ApiResultCode.ERROR;
    }

    public TestException(String msg, int code) {
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