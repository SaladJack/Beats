package com.saladjack.moemusic.ui.pixiv;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.saladjack.core.bean.PixivBean;
import com.saladjack.core.mvp.views.PixivIView;
import com.saladjack.moemusic.MoeApplication;
import com.saladjack.moemusic.R;
import com.saladjack.moemusic.ui.BaseFragment;
import com.saladjack.moemusic.ui.adapters.PixivAdapter;
import com.saladjack.moemusic.ui.widgets.recyclerview.RefreshRecyclerView;

import java.util.List;

/**
 * @author: saladjack
 * @Date: 2016/7/9.
 * @description: 分区界面
 */
public class PixivFragment extends BaseFragment implements PixivIView, RefreshRecyclerView.RefreshListener {

    public static final String TITLE = MoeApplication.getInstance().getString(R.string.pixiv);
    private RefreshRecyclerView refreshView;

    private PixivPresenter pixivPresenter;
    private PixivAdapter pixivAdapter;

    public static PixivFragment newInstance() {
        PixivFragment fragment = new PixivFragment();
        return fragment;
    }


    public PixivFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pixivPresenter = new PixivPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parentView = inflater.inflate(R.layout.fragment_area, container, false);
        refreshView = (RefreshRecyclerView) parentView.findViewById(R.id.refresh_view);

        pixivAdapter = new PixivAdapter(getActivity());
        refreshView.setRefreshListener(this);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL);
        //staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        refreshView.setLayoutManager(staggeredGridLayoutManager);
        refreshView.setAdapter(pixivAdapter);
        refreshView.setLoadEnable(false);
        refreshView.startSwipeAfterViewCreate();
        return parentView;
    }


    @Override
    public void getPixivPicture(List<PixivBean> pixivBeen) {
        pixivAdapter.setData(pixivBeen);
        refreshView.notifySwipeFinish();
    }

    @Override
    public void fail(String msg) {
        refreshView.notifySwipeFinish();
    }

    @Override
    public void onSwipeRefresh() {
        pixivPresenter.requestPixiv();
    }

    @Override
    public void onLoadMore() {

    }
}
