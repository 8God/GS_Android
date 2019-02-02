package com.gaoshou.common.update;

import java.util.Map;

import android.app.Notification;

public class NotifiHolder {
    private String downloadFilePath;
    private String fileTitle;
    private String curDownloadSpeed;
    private int downloadState;
    private int totalByte;
    private int curReadByte;
    private int curProgress;
    private int notifiID;
    public Notification notification;
    private Map<String, Object> additionalArgsMap;

    public String getDownloadFilePath() {
        return downloadFilePath;
    }

    public void setDownloadFilePath(String downloadFilePath) {
        this.downloadFilePath = downloadFilePath;
    }

    public String getFileTitle() {
        return fileTitle;
    }

    public void setFileTitle(String fileTitle) {
        this.fileTitle = fileTitle;
    }

    public String getCurDownloadSpeed() {
        return curDownloadSpeed;
    }

    public void setCurDownloadSpeed(String curDownloadSpeed) {
        this.curDownloadSpeed = curDownloadSpeed;
    }

    public int getDownloadState() {
        return downloadState;
    }

    public void setDownloadState(int downloadState) {
        this.downloadState = downloadState;
    }

    public int getTotalByte() {
        return totalByte;
    }

    public void setTotalByte(int totalByte) {
        this.totalByte = totalByte;
    }

    public int getCurReadByte() {
        return curReadByte;
    }

    public void setCurReadByte(int curReadByte) {
        this.curReadByte = curReadByte;
    }

    public int getCurProgress() {
        return curProgress;
    }

    public void setCurProgress(int curProgress) {
        this.curProgress = curProgress;
    }

    public int getNotifiID() {
        return notifiID;
    }

    public void setNotifiID(int notifiID) {
        this.notifiID = notifiID;
    }
    
    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

//    public Notification getNotification() {
//        return notification;
//    }
//
//    public void setNotification(Notification notification) {
//        this.notification = notification;
//    }

    public Map<String, Object> getAdditionalArgsMap() {
        return additionalArgsMap;
    }

    public void setAdditionalArgsMap(Map<String, Object> additionalArgsMap) {
        this.additionalArgsMap = additionalArgsMap;
    }

}
