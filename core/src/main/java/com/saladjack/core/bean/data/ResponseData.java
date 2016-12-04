package com.saladjack.core.bean.data;

import com.saladjack.core.bean.InformationBean;

/**
 * @author: saladjack
 * @date: 2016/7/7
 * @desciption: T 为parameters对象，K为msg对象
 */
public class ResponseData<T, K> {

    private InformationBean<T, K> information;

    public InformationBean<T, K> getInformation() {
        return information;
    }

    public void setInformation(InformationBean<T, K> information) {
        this.information = information;
    }
}
