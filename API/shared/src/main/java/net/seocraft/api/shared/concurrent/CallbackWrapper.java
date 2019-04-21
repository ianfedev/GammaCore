package net.seocraft.api.shared.concurrent;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import javax.annotation.Nullable;

public class CallbackWrapper {
    private static <T> FutureCallback<T> wrapCallback(Callback<T> callback) {
        return new FutureCallback<T>() {
            @Override
            public void onSuccess(@Nullable T t) {
                callback.call(t);
            }

            @Override
            public void onFailure(Throwable throwable) {
                callback.handleException(throwable);
            }
        };
    }

    public static <T> void addCallback(ListenableFuture<T> futureUtils, Callback<T> callback) {
        Futures.addCallback(futureUtils, wrapCallback(callback), MoreExecutors.directExecutor());
    }

}
