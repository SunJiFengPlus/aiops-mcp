package com.test.aiops.mcp.util;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Slf4j
public class OkHttpUtil {
    private static final OkHttpClient okHttpClient = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static String post(String url, String body) {
        RequestBody requestBody = RequestBody.create(body, JSON);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            }
            log.error("HTTP请求失败, url: {}, statusCode: {}, body: {}", url, response.code(), response.body().string());
            return null;
        } catch (IOException e) {
            log.error("HTTP请求失败, url: {}, body: {}", url, body, e);
            return null;
        }
    }
}
