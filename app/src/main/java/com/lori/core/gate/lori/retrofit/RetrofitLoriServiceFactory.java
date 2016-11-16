package com.lori.core.gate.lori.retrofit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.TimeZone;

/**
 * @author artemik
 */
@Singleton
public class RetrofitLoriServiceFactory {

    @Inject
    RequestInterceptor interceptor;

    @Inject
    public RetrofitLoriServiceFactory() {
    }

    public RetrofitLoriService create(String baseUrl) {
        return new Retrofit.Builder()
                .client(createClient())
                .addConverterFactory(new ToStringConverterFactory()) // For plain text request and responses.
                .addConverterFactory(createJacksonFactory())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build()
                .create(RetrofitLoriService.class);
    }

    private OkHttpClient createClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(createLoggingInterceptor())
                .build();
    }

    private JacksonConverterFactory createJacksonFactory() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setTimeZone(TimeZone.getDefault());
        return JacksonConverterFactory.create(mapper);
    }

    private HttpLoggingInterceptor createLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }
}
