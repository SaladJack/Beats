package com.saladjack.core.bean.data;

import com.saladjack.core.bean.WikiBean;

import java.util.List;

/**
 * @author: saladjack
 * @date: 2016/8/18
 * @desciption: wiki数据
 */
public class WikiData extends ResponseData<Object, Object> {

    private List<WikiBean> wikis;

    public List<WikiBean> getWikis() {
        return wikis;
    }

    public void setWikis(List<WikiBean> wikis) {
        this.wikis = wikis;
    }
}
