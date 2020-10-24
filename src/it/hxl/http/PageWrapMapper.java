package it.hxl.http;


import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * The class Page wrap mapper.
 */
public class PageWrapMapper {


    /**
     * Instantiates a new page wrap mapper.
     */
    private PageWrapMapper() {
    }

    /**
     * Wrap ERROR. code=100
     *
     * @param <T> the type parameter
     * @return the page wrapper
     */
    public static <T, U> PageWrapper<T, U> illegalArgument() {
        return wrap(DefaultResponseCode.ILLEGAL_ARGUMENT, null, null);
    }

    /**
     * Wrap ERROR. code=500
     *
     * @param <T> the type parameter
     * @return the page wrapper
     */
    public static <T, U> PageWrapper<T, U> error() {
        return wrap(DefaultResponseCode.ERROR, null, null);
    }

    /**
     * Wrap ERROR. code=500
     *
     * @param e the e extends Exception
     * @return the page wrapper
     */
    public static <E extends Exception> PageWrapper<E, ?> error(E e) {
        return new PageWrapper<>(DefaultResponseCode.ERROR, e, null);
    }

    /**
     * Wrap SUCCESS. code=200
     *
     * @param <T> the type parameter
     * @return the page wrapper
     */
    public static <T, U> PageWrapper<T, U> ok() {
        return new PageWrapper<>();
    }

    /**
     * Wrap SUCCESS. code=200
     *
     * @param <T> the type parameter
     * @return the page wrapper
     */
    public static <T, U> PageWrapper<T, U> ok(IPage<U> page) {
        return new PageWrapper<>(DefaultResponseCode.SUCCESS, null, page);
    }

    /**
     * Wrap SUCCESS. code=200
     *
     * @param <T> the type parameter
     * @return the page wrapper
     */
    public static <T, U> PageWrapper<T, U> ok(IPage<U> page, T result) {
        return new PageWrapper<>(DefaultResponseCode.SUCCESS, result, page);
    }

    /**
     * Wrap.
     *
     * @param <T>          the type parameter
     * @param responseCode the responseCode
     * @return the page wrapper
     */
    public static <T, U> PageWrapper<T, U> wrap(ResponseCode responseCode) {
        return wrap(responseCode, null, null);
    }

    private static <T, U> PageWrapper<T, U> wrap(ResponseCode responseCode, T o, IPage<U> page) {
        return new PageWrapper<>(responseCode, o, page);
    }

    /**
     * Wrap data with default code=200.
     *
     * @param <T>  the type parameter
     * @param o    the o
     * @param page the page
     * @return the page wrapper
     */
    public static <T, U> PageWrapper<T, U> wrap(T o, IPage<U> page) {
        return wrap(DefaultResponseCode.SUCCESS, o, page);
    }


    /**
     * Un wrapper.
     *
     * @param <T>     the type parameter
     * @param wrapper the wrapper
     * @return the e
     */
    public static <T, U> T unWrap(PageWrapper<T, U> wrapper) {
        return wrapper.getResult();
    }
}
