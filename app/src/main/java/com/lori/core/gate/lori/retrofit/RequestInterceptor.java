package com.lori.core.gate.lori.retrofit;

import com.lori.core.service.SessionService;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

/**
 * @author artemik
 */
@Singleton
public class RequestInterceptor implements Interceptor {

    @Inject
    SessionService sessionService;

    @Inject
    public RequestInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        request = addSessionParameter(request);
        return chain.proceed(request);
    }

    private Request addSessionParameter(Request request) {
        if (sessionService.getSession() == null) {
            return request;
        }

        HttpUrl newUrl = request.url().newBuilder()
                .addQueryParameter("s", sessionService.getSession().toString())
                .build();

        return request.newBuilder()
                .url(newUrl)
                .build();
    }
}
