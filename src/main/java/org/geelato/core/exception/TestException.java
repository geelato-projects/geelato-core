package org.geelato.core.exception;


public class TestException  extends  RuntimeException{

    public TestException(String msg){
        this.msg=msg;
    }
    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
