package com.waslabank.wasslabank.networkUtils;

import android.os.Build;

import com.waslabank.wasslabank.ui.MyApp;
import com.waslabank.wasslabank.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class NetworkClient {
    private static Retrofit retrofit = null;
    private static int REQUEST_TIMEOUT = 60;
    private static OkHttpClient okHttpClient;
    public static Retrofit getClient() {

        if (okHttpClient == null)
            initOkHttp();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Connector.ConnectionServices.BaseURL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    public static Interceptor interceptor() {
        return chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    //        .addHeader("Accept", "application/json,text/plain,*/*")

                    .addHeader("AndroidVersion", Build.VERSION.SDK_INT + "")
                    .addHeader("AppId", 1 + "")
                    .addHeader("DeviceName", Build.MODEL + "")
                    .addHeader("DeviceType", "Android")
                    .addHeader("Content-Type", "application/json");
            Request request = requestBuilder.build();
            try {
                String url = request.url().toString();
                LogUtil.error("urlHelper", url);
                LogUtil.error("bodyHelper", bodyToString(request.body()) + "");
                LogUtil.error("headerHelper", request.headers().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return chain.proceed(request);
        };
    }

    public static Cache cache() {
        return new Cache(new File(MyApp.getAppContext().getCacheDir(), "okhttp_cach"), 10 * 1000 * 1000); // 10MB
    }




        private static void initOkHttp() {
            OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                    .addInterceptor(interceptor())
                    .addInterceptor(new LoggingInterceptor())
                    .cache(cache());
            okHttpClient = httpClient.build();
        }

    public static class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            // LogUtil.error("LoggingInterceptor", "inside intercept callback");
            Request request = chain.request();
            long t1 = System.nanoTime();
            String requestLog = String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers());
            if (request.method().compareToIgnoreCase("post") == 0) {
                requestLog = "\n" + requestLog + "\n" + bodyToString(request.body());
            }
            //     LogUtil.error("TAG", "request" + "\n" + requestLog);

            Response response = chain.proceed(request);
            long t2 = System.nanoTime();

            String responseLog = String.format("Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers());

            String bodyString = response.body().string();
//            Log.e("TAG", "response only" + "\n" + bodyString);
//            Log.e("TAG", "response" + "\n" + responseLog + "\n" + bodyString);

            //   LogUtil.error("TAG", "response only" + "\n" + bodyString);
            // GlameraApp.getAppInstance().getDatabaseHelper().setDataRequestLogger(response.request().url().toString(), request.headers().toString(), bodyToString(request.body()),bodyString);
            return response.newBuilder()
                    .body(ResponseBody.create(response.body().contentType(), bodyString))
                    .build();
        }
    }


    public static Connector.ConnectionServices getApiService() {
        return getClient().create(Connector.ConnectionServices.class);
    }
}
