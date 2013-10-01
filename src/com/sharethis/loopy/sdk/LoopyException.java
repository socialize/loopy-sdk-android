package com.sharethis.loopy.sdk;

/**
 * @author Jason Polites
 */
public class LoopyException extends RuntimeException {

    private final int code;

    public static final int INVALID_PARAMETER = 600;
    public static final int PARAMETER_MISSING = 601;
    public static final int PARSE_ERROR = 602;
    public static final int CLIENT_TIMEOUT = 608;


    public static final int LIFECYCLE_ERROR = 1000;
    public static final int TIMEOUT = 1001;
    public static final int INTERNAL_ERROR = 1200;

    public LoopyException(int code) {
        super();
        this.code = code;
    }

    public LoopyException(String detailMessage, int code) {
        super(detailMessage);
        this.code = code;
    }

    public LoopyException(String detailMessage, Throwable throwable, int code) {
        super(detailMessage, throwable);
        this.code = code;
    }

    public LoopyException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static LoopyException wrap(Exception e, int code) {
        if(e instanceof LoopyException) {
            return (LoopyException) e;
        }
        return new LoopyException(e, code);
    }
}
