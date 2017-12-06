package com.zinier.base.api;

import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.zinier.base.BaseSingtelApp;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
import okio.BufferedSource;

public class ServerErrorInterceptor implements Interceptor {

    private static final String TAG = ServerErrorInterceptor.class.getSimpleName();
    private Gson gson;

    public ServerErrorInterceptor(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        Log.d(TAG, "intercept: " + response.code());
        if (!response.isSuccessful()) {

            if (response.code() == 401) {
                Intent intentError = new Intent();
                intentError.setAction(Error401.ERROR_401);
                BaseSingtelApp.getContext().sendBroadcast(intentError);
            } else {
                    parseServerError(response);
            }
        }

        return response;
    }

    private void parseServerError(Response response) throws IOException {
        ServerError serverError;
        String bodyAsString = "";
        try {
            BufferedSource source = response.body().source();
            bodyAsString = source.readUtf8();

            serverError = gson.fromJson(bodyAsString, ServerError.class);
        } catch (JsonSyntaxException | JsonIOException e) {
            Log.d(TAG, "Parse error \n" + bodyAsString, e);
            serverError = new ServerError("Something was wrong. Please, try again");
        }
        Log.d(TAG, "Parse error \n" + bodyAsString);
        EventBus.getDefault().post(serverError);
        throwException(new ServerErrorException(serverError));
    }

    private void throwException(IOException e) throws IOException {
        throw e;
    }

}