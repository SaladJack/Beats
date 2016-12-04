package com.saladjack.core.bean.data;

import com.saladjack.core.bean.WikiSubBean;

import java.util.List;

/**
 * @author: saladjack
 * @date: 2016/8/18
 * @desciption: 包含歌曲信息的专辑播放列表
 */
public class AlbumDetailData extends ResponseData<Object, Object> {
    private List<WikiSubBean> subs;

    public List<WikiSubBean> getSubs() {
        return subs;
    }

    public void setSubs(List<WikiSubBean> subs) {
        this.subs = subs;
    }
}
