package com.sy.expection;


/**
 *
 * @Description: 文件上传异常
 * @author: Aaron
 * @date: 2017年7月12日 下午6:56:09
 */
public class FastDFSException extends Exception {

    // serialVersionUID : TODO
    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")

    //错误编码
    private int errorCode = 0;

    public FastDFSException(int errorCode) {
        this.errorCode = errorCode;
    }

    public FastDFSException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public FastDFSException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public FastDFSException(Throwable cause, int errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public FastDFSException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
                            int errorCode) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
    }
}

