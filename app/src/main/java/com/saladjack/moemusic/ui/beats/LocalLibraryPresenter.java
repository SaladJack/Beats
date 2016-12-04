package com.saladjack.moemusic.ui.beats;

import android.content.Context;

import com.saladjack.core.bean.Album;
import com.saladjack.core.bean.Artist;
import com.saladjack.core.bean.Song;
import com.saladjack.core.cache.LocalMusicLibrary;
import com.saladjack.core.mvp.views.LocalIView;
import com.saladjack.core.utils.MoeLogger;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author: saladjack
 * @date: 2016/10/4
 * @desciption:
 */

public class LocalLibraryPresenter {

    private LocalIView.LocalMusic localMusic;
    private LocalIView.LocalAlbum localAlbum;
    private LocalIView.LocalArtist localArtist;

    private Context context;

    public LocalLibraryPresenter(LocalIView.LocalMusic localMusic, Context context) {
        this.localMusic = localMusic;
        this.context = context;
    }

    public LocalLibraryPresenter(LocalIView.LocalAlbum localAlbum, Context context) {
        this.localAlbum = localAlbum;
        this.context = context;
    }

    public LocalLibraryPresenter(LocalIView.LocalArtist localArtist, Context context) {
        this.localArtist = localArtist;
        this.context = context;
    }

    public void requestMusic() {
        Observable.create(
                new Observable.OnSubscribe<List<Song>>() {
                    @Override
                    public void call(Subscriber<? super List<Song>> subscriber) {
                        List<Song> songs = LocalMusicLibrary.getAllSongs(context);
                        subscriber.onNext(songs);
                    }
                }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Song>>() {
                    @Override
                    public void call(List<Song> songs) {
                        if (localMusic != null)
                            localMusic.getLocalMusic(songs);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        MoeLogger.e(throwable.toString());
                    }
                });
    }

    public void requestAlbum() {
        Observable.create(
                new Observable.OnSubscribe<List<Album>>() {
                    @Override
                    public void call(Subscriber<? super List<Album>> subscriber) {
                        List<Album> albums = LocalMusicLibrary.getAllAlbums(context);
                        subscriber.onNext(albums);
                    }
                }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Album>>() {
                    @Override
                    public void call(List<Album> albums) {
                        if (localAlbum != null)
                            localAlbum.getLocalAlbum(albums);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        MoeLogger.e(throwable.toString());
                    }
                });
    }

    public void requestArtist() {
        Observable.create(
                new Observable.OnSubscribe<List<Artist>>() {
                    @Override
                    public void call(Subscriber<? super List<Artist>> subscriber) {
                        List<Artist> artists = LocalMusicLibrary.getAllArtists(context);
                        subscriber.onNext(artists);
                    }
                }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Artist>>() {
                    @Override
                    public void call(List<Artist> artists) {
                        if (localArtist != null)
                            localArtist.getLocalArtist(artists);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        MoeLogger.e(throwable.toString());
                    }
                });
    }
}
