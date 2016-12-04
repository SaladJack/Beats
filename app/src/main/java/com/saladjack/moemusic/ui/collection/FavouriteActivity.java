package com.saladjack.moemusic.ui.collection;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.saladjack.core.bean.WikiBean;
import com.saladjack.core.bean.event.FavEvent;
import com.saladjack.core.http.RxBus;
import com.saladjack.core.mvp.views.FavouriteIView;
import com.saladjack.core.utils.MoeLogger;
import com.saladjack.moemusic.R;
import com.saladjack.moemusic.ui.AbstractAppActivity;
import com.saladjack.moemusic.ui.adapters.FavouriteAdapter;
import com.saladjack.moemusic.ui.widgets.recyclerview.RefreshRecyclerView;

import java.util.List;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * @author: saladjack
 * @date: 2016/9/27
 * @desciption: 用户收藏的专辑和电台
 */

public class FavouriteActivity extends AbstractAppActivity implements RefreshRecyclerView.RefreshListener, FavouriteIView {

    public static void open(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, FavouriteActivity.class);
        context.startActivity(intent);
    }

    private Toolbar toolbar;
    private RefreshRecyclerView refreshView;
    private FavouriteAdapter favouriteAdapter;
    private FavouritePresenter favouritePresenter;

    private CompositeSubscription allSubscription = new CompositeSubscription();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_favourite);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        favouritePresenter = new FavouritePresenter(this);

        initRefreshView();

        allSubscription.add(RxBus.getDefault()
                .toObservable(FavEvent.class).subscribe(new Action1<FavEvent>() {
                    @Override
                    public void call(FavEvent favEvent) {
                        onEvent(favEvent);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        MoeLogger.e(throwable.toString());
                    }
                }));
    }

    private void initRefreshView() {
        favouriteAdapter = new FavouriteAdapter(this);
        refreshView = (RefreshRecyclerView) findViewById(R.id.refresh_view);
        refreshView.setAdapter(favouriteAdapter);
        refreshView.setRefreshListener(this);
        refreshView.setLoadEnable(false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        refreshView.setLayoutManager(gridLayoutManager);
        refreshView.startSwipeAfterViewCreate();
    }

    @Override
    public void onSwipeRefresh() {
        favouritePresenter.requestFavWikis();
    }

    @Override
    public void onLoadMore() {
        favouritePresenter.loadMore();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_favourite, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (item.getItemId() == R.id.action_all) {
            favouriteAdapter.setMode(FavouriteAdapter.ALL_MODE);
            refreshView.notifyDataSetChanged();
            return true;
        }
        if (item.getItemId() == R.id.action_album) {
            favouriteAdapter.setMode(FavouriteAdapter.ALBUM_MODE);
            refreshView.notifyDataSetChanged();
            return true;
        }
        if (item.getItemId() == R.id.action_radio) {
            favouriteAdapter.setMode(FavouriteAdapter.RADIO_MODE);
            refreshView.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onEvent(FavEvent favEvent) {
        favouriteAdapter.updateWikiFav(favEvent.getWikiId(), favEvent.isFav());
    }

    @Override
    public void getWikiBean(List<WikiBean> wikis, boolean add, boolean hasMore) {
        if (add) {
            favouriteAdapter.addData(wikis);
        } else {
            favouriteAdapter.setData(wikis);
        }
        refreshView.notifySwipeFinish();
        refreshView.notifyLoadMoreFinish(hasMore);
    }

    @Override
    public void fail(String msg) {
        refreshView.notifySwipeFinish();
        refreshView.notifyLoadMoreFinish(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!allSubscription.isUnsubscribed()) {
            allSubscription.unsubscribe();
        }
    }
}
