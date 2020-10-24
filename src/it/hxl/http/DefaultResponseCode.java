package it.hxl.http;

/**
 * @author Zephyr
 * @date 2019/9/1
 */
public enum DefaultResponseCode implements ResponseCode {

    /**
     * NOT_LOGGED_IN
     */
    NOT_LOGGED_IN(101, "未登录"),

    /**
     * INSUFFICIENT_AUTHORITY
     */
    INSUFFICIENT_AUTHORITY(102, "权限不足"),

    /**
     * SUCCESS
     */
    SUCCESS(200, "请求成功"),

    /**
     * NOT_FOUND
     */
    NOT_FOUND(404, "内容不存在"),

    /**
     * ERROR
     */
    ERROR(500, "网络异常"),

    ERROR_CUSTOM(500, "{}"),

    /**
     * NOT_IMPLEMENTED
     */
    NOT_IMPLEMENTED(501, "NOT_IMPLEMENTED"),

    /**
     * ILLEGAL_ARGUMENT
     */
    ILLEGAL_ARGUMENT(100, "参数异常"),

    ILLEGAL_ARGUMENT_CUSTOM(100, "{}"),

    /**
     * SIGNATURE_ERROR
     */
    SIGNATURE_ERROR(103, "签名错误"),
    ;

    int code;

    String message;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    DefaultResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
