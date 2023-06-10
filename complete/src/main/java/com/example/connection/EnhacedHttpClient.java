package com.example.connection;

import com.google.gson.Gson;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

@Component
public class EnhacedHttpClient {
    private final CloseableHttpClient httpClient;
    private final IdleConnectionMonitorThread idleConnectionMonitorThread;
    private final ResponseHandler<String> responseHandler;

    public EnhacedHttpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000).build();

        //set connection keep Alive Strategy. We have high volume connetions to connect to the server more frequently.
        ConnectionKeepAliveStrategy connectionKeepAliveStrategy = new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse httpResponse, HttpContext httpContext) {
                HeaderElementIterator headerElementIterator = new BasicHeaderElementIterator(
                        httpResponse.headerIterator(HTTP.CONN_KEEP_ALIVE)
                );
                while (headerElementIterator.hasNext()) {
                    HeaderElement headerElement = headerElementIterator.nextElement();
                    String paramName = headerElement.getName();
                    String paramValue = headerElement.getValue();
                    if (paramValue != null && "timeout".equalsIgnoreCase(paramName)) {
                        return Long.parseLong(paramValue) * 1000;
                    }
                }
                return 60 * 1000;
            }
        };

        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        poolingHttpClientConnectionManager.setMaxTotal(500);
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(100);

        httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig)
                .setConnectionManager(poolingHttpClientConnectionManager)
                .setKeepAliveStrategy(connectionKeepAliveStrategy)
                .build();

        idleConnectionMonitorThread = new IdleConnectionMonitorThread(poolingHttpClientConnectionManager);
        idleConnectionMonitorThread.run();

        responseHandler = new BasicResponseHandler();
    }

    public HttpClient custom() {
        return this.httpClient;
    }

    public String get(String url) {
        HttpGet httpGet = new HttpGet();
        httpGet.setURI(URI.create(url));
        try {
            String response = httpClient.execute(httpGet, responseHandler);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T get(String url, Class<? extends  T> responseType) {
        HttpGet httpGet = new HttpGet();
        httpGet.setURI(URI.create(url));
        try {
            T response = httpClient.execute(httpGet, new AbstractResponseHandler<T>() {
                @Override
                public T handleEntity(HttpEntity httpEntity) throws IOException {
                    return new Gson().fromJson(httpEntity.toString(), responseType);
                }
            });
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void shutDown() {
        idleConnectionMonitorThread.shutDown();
    }
}
