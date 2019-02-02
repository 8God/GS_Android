package com.gaoshou.common.network;

import android.net.TrafficStats;
import android.os.Handler;
import android.os.Looper;

public class NetworkTrafficDetector {

    public interface INetworkTrafficStatusListener {

        public void onDetectSpeed(int receivingSpeed, int sendingSpeed);

        public void onDetectTime(int totalTime, int idleTime);
    }

    private int totalSeconds = 0;
    private int idleSeconds = 0;
    private long preTotalRxBytes = 0L;
    private long preTotalTxBytes = 0L;
    private long totalRxBytes = 0L;
    private long totalTxBytes = 0L;

    private static INetworkTrafficStatusListener networkTrafficStatusListener;

    public static void setNetworkTrafficStatusListener(INetworkTrafficStatusListener networkTrafficStatusListener) {
        NetworkTrafficDetector.networkTrafficStatusListener = networkTrafficStatusListener;
    }

    private volatile static NetworkTrafficDetector instance;

    private Runnable task;

    public int getPreReceivingSpeed() {
        return (int) ((TrafficStats.getTotalRxBytes() - preTotalRxBytes) / 1024);
    }

    public int getPreSendingSpeed() {
        return (int) ((TrafficStats.getTotalRxBytes() - preTotalTxBytes) / 1024);
    }

    private static Handler handler = new Handler(Looper.getMainLooper());

    protected NetworkTrafficDetector() {
        task = new Runnable() {

            @Override
            public void run() {
                int totalRxSpeed = -1;
                int totalTxSpeed = -1;

                totalRxBytes = TrafficStats.getTotalRxBytes();
                if (0L != preTotalRxBytes) {

                    totalRxSpeed = (int) ((totalRxBytes - preTotalRxBytes) / 1024);

                }
                preTotalRxBytes = totalRxBytes;

                totalTxBytes = TrafficStats.getTotalTxBytes();
                if (0L != preTotalTxBytes) {
                    totalTxSpeed = (int) ((totalTxBytes - preTotalTxBytes) / 1024);
                }
                preTotalTxBytes = totalTxBytes;

                ++totalSeconds;
                if (0 == totalRxSpeed && 0 == totalTxSpeed) {
                    ++idleSeconds;
                }

                if (null != networkTrafficStatusListener) {
                    networkTrafficStatusListener.onDetectSpeed(totalRxSpeed, totalTxSpeed);
                    networkTrafficStatusListener.onDetectTime(totalSeconds, idleSeconds);
                }

                handler.postDelayed(this, 1000);
            }
        };

    }

    public synchronized static NetworkTrafficDetector getInstance() {
        if (instance == null) {
            synchronized (NetworkTrafficDetector.class) {
                if (instance == null) {
                    instance = new NetworkTrafficDetector();
                }
            }
        }
        return instance;
    }

    public void detect() {
        handler.post(task);
    }

    public void terminate() {
        handler.removeCallbacks(task);
        totalSeconds = 0;
        idleSeconds = 0;
        preTotalRxBytes = 0L;
        preTotalTxBytes = 0L;
        totalRxBytes = 0L;
        totalTxBytes = 0L;
    }

}
