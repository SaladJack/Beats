package com.saladjack.core.bean.data;

import com.saladjack.core.bean.PixivBean;

import java.util.List;

/**
 * @author: saladjack
 * @Date: 2016/10/28.
 * @description: p站每日排行榜
 */

public class PixivData {

    private List<PixivBean> contents;

    public List<PixivBean> getContents() {
        return contents;
    }

    public void setContents(List<PixivBean> contents) {
        this.contents = contents;
    }
}
