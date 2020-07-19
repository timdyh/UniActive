package com.example.uniactive.util;

import android.content.Context;

public class RemindThread extends Thread {

    private static class Lock {
        private String email;
    }

    private final Lock lock = new Lock();
    private final Context appContext;

    public RemindThread(Context appContext) {
        this.appContext = appContext;
    }

    @Override
    public void run() {
        while (true) {
            try {
                synchronized (lock) {
                    if (lock.email == null) {
                        JSONGenerator.stopReminding();
                        break;
                    }
                }
                Thread.sleep(300000);
            } catch (InterruptedException e) {
                JSONGenerator.stopReminding();
                break;
            }
        }
    }

    public void stopReminding() {
        synchronized (lock) {
            lock.email = null;
        }
        this.interrupt();
    }

}
