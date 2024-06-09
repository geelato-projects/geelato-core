package org.geelato.core.orm;

import org.geelato.core.constants.ApiResultCode;
import org.geelato.core.exception.CoreException;

public class DaoException extends CoreException {
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

}
