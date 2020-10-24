package it.hxl.http;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The class Page wrapper.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PageWrapper<T, U> extends Wrapper<T> {

    private static final long serialVersionUID = 666985064788933395L;

    private IPage<U> page;

    /**
     * Instantiates a new Page wrapper.
     */
    PageWrapper() {
        super();
    }


    /**
     * Instantiates a new Page wrapper.
     *
     * @param responseCode the responseCode
     */
    public PageWrapper(ResponseCode responseCode) {
        super(responseCode);
    }

    /**
     * Instantiates a new pageWrapper default code=200
     *
     * @param page the page
     */
    public PageWrapper(IPage<U> page) {
        super(DefaultResponseCode.SUCCESS);
        this.page = page;
    }

    /**
     * Instantiates a new Page wrapper.
     *
     * @param responseCode the responseCode
     * @param result       the result
     * @param page         the page
     */
    PageWrapper(ResponseCode responseCode, T result, IPage<U> page) {
        super(responseCode, result);
        this.page = page;
    }

    /**
     * Sets the 分页数据 , 返回自身的引用.
     *
     * @param page the page
     * @return the page wrapper
     */
    public PageWrapper<?, U> page(IPage<U> page) {
        this.page = page;
        return this;
    }

    /**
     * Sets the 结果数据 , 返回自身的引用.
     *
     * @param result the result
     * @return the page wrapper
     */
    @Override
    public PageWrapper<T, ?> result(T result) {
        super.setResult(result);
        return this;
    }
}
