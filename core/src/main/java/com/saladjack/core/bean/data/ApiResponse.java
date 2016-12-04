package com.saladjack.core.bean.data;

/**
 * 通用的接口数据模板<br />
 * common api response template
 *
 * @author: saladjack
 * @Date: 2015/10/21 0021-上午 10:05
 */
public class ApiResponse<T> {

    private T response;

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }
}
