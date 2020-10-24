package it.hxl.http;


import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * The class Wrap mapper.
 */
public class WrapMapper {

    /**
     * Instantiates a new wrap mapper.
     */
    private WrapMapper() {
    }

    /**
     * Wrap.
     *
     * @param <O>          the element type
     * @param responseCode the responseCode
     * @return the wrapper
     */
    public static <O> Wrapper<O> wrap(ResponseCode responseCode) {
        return wrap(responseCode, null);
    }

    /**
     * Wrap.
     *
     * @param <O>          the element type
     * @param responseCode the responseCode
     * @param o            the o
     * @return the wrapper
     */
    public static <O> Wrapper<O> wrap(ResponseCode responseCode, O o) {
        return new Wrapper<>(responseCode, o);
    }

    public static <O> Wrapper<O> wrap(ResponseCode responseCode, O o, Object... args) {
        return new Wrapper<>(responseCode, o, args);
    }

    /**
     * Wrap ERROR. code=500
     *
     * @param <O> the element type
     * @return the wrapper
     */
    public static <O> Wrapper<O> error(ResponseCode responseCode, Object... args) {
        return new Wrapper<>(responseCode, args);
    }

    /**
     * Wrap.
     *
     * @param e the e
     * @return the wrapper
     */
    public static Wrapper<String> error(Exception e) {
        return new Wrapper<>(DefaultResponseCode.ERROR, e.getMessage());
    }

    /**
     * Wrap.
     *
     * @param message the e
     * @return the wrapper
     */
    public static Wrapper<String> error(String message) {
        return new Wrapper<>(DefaultResponseCode.ERROR, message);
    }

    /**
     * Un wrapper.
     *
     * @param <O>     the element type
     * @param wrapper the wrapper
     * @return the e
     */
    public static <O> O unWrap(Wrapper<O> wrapper) {
        return wrapper.getResult();
    }

    /**
     * Wrap ERROR. code=100
     *
     * @param <O> the element type
     * @return the wrapper
     */
    public static <O> Wrapper<O> illegalArgument() {
        return wrap(DefaultResponseCode.ILLEGAL_ARGUMENT);
    }

    /**
     * Wrap ERROR. code=100
     *
     * @param message illegalArgument message
     * @return the wrapper
     */
    public static <O> Wrapper<O> illegalArgument(String message) {
        return new Wrapper<>(DefaultResponseCode.ILLEGAL_ARGUMENT.getCode(), message);
    }

    /**
     * Wrap ERROR. code=500
     *
     * @param <O> the element type
     * @return the wrapper
     */
    public static <O> Wrapper<O> error() {
        return wrap(DefaultResponseCode.ERROR);
    }


    /**
     * Wrap SUCCESS. code=200
     *
     * @return the wrapper
     */
    public static Wrapper ok() {
        return new Wrapper();
    }

    /**
     * Ok wrapper.
     *
     * @param <O> the type parameter
     * @param o   the o
     * @return the wrapper
     */
    public static <O> Wrapper<O> ok(O o) {
        return new Wrapper<>(DefaultResponseCode.SUCCESS, o);
    }


    /**
     * ok wrapper
     *
     * @param key
     * @param value
     * @return
     */
    public static Wrapper<Map<String, Object>> ok(@NotNull String key, Object value) {
        Map<String, Object> map = new HashMap<>(1);
        map.put(key, value);
        return new Wrapper<>(DefaultResponseCode.SUCCESS, map);
    }
}
