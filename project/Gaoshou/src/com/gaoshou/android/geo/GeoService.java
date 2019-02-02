package com.gaoshou.android.geo;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

public class GeoService {

    public interface IGeoServiceToRenderListener {
        public void toRender(Address address);
    }

    private Context notApplicationContext;

    private IGeoServiceToRenderListener geoServiceToRenderListener;

    private LocationClient locationClient;
    private BDLocationListener myLocationListener = new MyLocationListener();

    public void setGeoServiceToRenderListener(IGeoServiceToRenderListener geoServiceToRenderListener) {
        this.geoServiceToRenderListener = geoServiceToRenderListener;
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            loadAddress(location);
        }

//        @Override
//        public void onReceivePoi(BDLocation arg0) {
//            // TODO Auto-generated method stub
//
//        }
    }

    private void loadAddress(BDLocation location) {
        Address address = null;
        if (null != location) {

            switch (location.getLocType()) {
                case 161:
                    address = new Address();
                    address.setProvince(location.getProvince());
                    address.setCity(location.getCity());
                    address.setDistrict(location.getDistrict());
                    address.setStreet(location.getStreet());
                    address.setStreetNumber(location.getStreetNumber());
                    address.setAltitude(location.getAltitude());
                    address.setLatitude(location.getLatitude());
                    address.setLongitude(location.getLongitude());
                    break;

                default:
                    break;
            }

        } // if (null != location)

        stop();

        if (null != geoServiceToRenderListener) {
            geoServiceToRenderListener.toRender(address);
        } // if (null != geoServiceToRenderListener)
    }

    public GeoService(Context notApplicationContext) {
        this.notApplicationContext = notApplicationContext;
        if (null != this.notApplicationContext) {
            this.locationClient = new LocationClient(this.notApplicationContext);
            LocationClientOption option = new LocationClientOption();
            option.setLocationMode(LocationMode.Battery_Saving);//设置定位模式
            option.setCoorType("bd09ll");//返回的定位结果是百度经纬度，默认值gcj02
            option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
            option.setIsNeedAddress(true);//返回的定位结果包含地址信息
            option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
            option.setTimeOut(10000);
            this.locationClient.setLocOption(option);
            this.locationClient.registerLocationListener(myLocationListener);

        } // if (null != this.notApplicationContext)

    }

    public void execute() {
        start();
    }

    private void start() {
        if (null != locationClient && !locationClient.isStarted()) {
            locationClient.start();
        } else {
            loadAddress(null);
        }
    }

    private void stop() {
        if (null != locationClient) {
            locationClient.unRegisterLocationListener(myLocationListener);

            if (locationClient.isStarted()) {
                locationClient.stop();
            } // if (locationClient.isStarted())
        } // if (null != locationClient)
    }

}
