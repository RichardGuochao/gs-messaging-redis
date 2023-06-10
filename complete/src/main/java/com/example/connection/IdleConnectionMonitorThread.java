package com.example.connection;

import org.apache.http.conn.HttpClientConnectionManager;

import java.util.concurrent.TimeUnit;

public class IdleConnectionMonitorThread extends Thread{
    private final HttpClientConnectionManager httpClientConnectionManager;
    private volatile boolean shutdown;

    public IdleConnectionMonitorThread(HttpClientConnectionManager httpClientConnectionManager) {
        super();
        this.httpClientConnectionManager = httpClientConnectionManager;
    }

    @Override
    public void run() {
        try{
            while (!shutdown) {
                synchronized (this) {
                    wait(5000);
                    //close expired connections
                    httpClientConnectionManager.closeExpiredConnections();

                    // close idle connections longer than 30 seconds.
                    httpClientConnectionManager.closeIdleConnections(30, TimeUnit.SECONDS);
                }
            }
        } catch (Exception e) {
            //terminate
        }
    }

    public void shutDown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }

}
