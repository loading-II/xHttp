package com.net.xhttp

import androidx.lifecycle.*
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.ObservableSubscribeProxy
import com.uber.autodispose.SingleSubscribeProxy
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

////////////////////////////////////////RxJava扩展////////////////////////////////////////

fun <T> Flowable<T>.async(withDelay: Long = 0): Flowable<T> =
    this.subscribeOn(Schedulers.io()).delay(withDelay, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())

fun <T> Single<T>.async(withDelay: Long = 0): Single<T> =
    this.subscribeOn(Schedulers.io()).delay(withDelay, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())

fun <T> Single<T>.async2IO(withDelay: Long = 0): Single<T> =
    this.subscribeOn(Schedulers.io()).delay(withDelay, TimeUnit.MILLISECONDS).observeOn(Schedulers.io())

fun <T> Single<T>.async(showLoading: () -> Unit, hideLoading: () -> Unit, withDelay: Long = 0): Single<T> =
    this.subscribeOn(Schedulers.io()).delay(withDelay, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe { showLoading() }
        .doAfterTerminate { hideLoading() }


fun <T> Observable<T>.async(withDelay: Long = 0): Observable<T> =
    this.subscribeOn(Schedulers.io()).delay(withDelay, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())

fun <T> Single<T>.bindLifeCycle(owner: LifecycleOwner): SingleSubscribeProxy<T> =
    this.`as`(
        AutoDispose.autoDisposable(
            AndroidLifecycleScopeProvider.from(
                owner,
                Lifecycle.Event.ON_DESTROY
            )
        )
    )

fun <T> Observable<T>.bindLifeCycle(owner: LifecycleOwner): ObservableSubscribeProxy<T> =
    this.`as`(
        AutoDispose.autoDisposable(
            AndroidLifecycleScopeProvider.from(
                owner,
                Lifecycle.Event.ON_DESTROY
            )
        )
    )



