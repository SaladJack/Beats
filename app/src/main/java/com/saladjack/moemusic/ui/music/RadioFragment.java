package com.saladjack.moemusic.ui.music;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.saladjack.core.bean.WikiBean;
import com.saladjack.core.mvp.views.RadioIView;
import com.saladjack.moemusic.MoeApplication;
import com.saladjack.moemusic.R;
import com.saladjack.moemusic.ui.BaseFragment;
import com.saladjack.moemusic.ui.adapters.RadioAdapter;
import com.saladjack.moemusic.ui.widgets.recyclerview.RefreshRecyclerView;

import java.util.List;

/**
 * @author: saladjack
 * @Date: 2016/7/9.
 * @description: 电台界面
 */
public class RadioFragment extends BaseFragment implements RefreshRecyclerView.RefreshListener, RadioIView {

    public static final String TITLE = MoeApplication.getInstance().getString(R.string.radio);

    private RefreshRecyclerView refreshView;
    private RadioAdapter radioAdapter;
    private RadioPresenter radioPresenter;

    public static RadioFragment newInstance() {
        RadioFragment fragment = new RadioFragment();
        return fragment;
    }

    public RadioFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        radioPresenter = new RadioPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parentView = inflater.inflate(R.layout.fragment_radio, container, false);
        return parentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshView = (RefreshRecyclerView) view.findViewById(R.id.refresh_view);
        radioAdapter = new RadioAdapter(getActivity());
        refreshView.setAdapter(radioAdapter);
        refreshView.setLoadEnable(false);
        refreshView.startSwipeAfterViewCreate();
        refreshView.setRefreshListener(this);
    }

    @Override
    public void onSwipeRefresh() {
        radioPresenter.requestRadios();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void getMusics(List<WikiBean> hotRadios, List<WikiBean> newRadios) {
        if (hotRadios != null) {
            radioAdapter.setHotRadios(hotRadios);
        }
        if (newRadios != null) {
            radioAdapter.setNewRadios(newRadios);
        }
        refreshView.notifySwipeFinish();
    }

    @Override
    public void loadMusicFail(String msg) {
        refreshView.notifySwipeFinish();
    }

}
