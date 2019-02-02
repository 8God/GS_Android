package com.gaoshou.android.entity;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.gaoshou.android.R;

public class BaiduLocationEntity {
	/**唯一实例*/
	private static BaiduLocationEntity mInstance;
	/**获取定位工具*/
	private LocationClient mLocationClient;
	/**地址搜索工具*/
	private GeoCoder mGeoCoder;
	
	private LocationClientOption mlocationClientOption;
	
	private Context context;
	
	private MyLocationListener mLocationListener;
	
	private BaiduLocationEntity(Context context){
		this.context = context;
		
		initOption();
		// 初始化LocationClient
		mLocationClient = new LocationClient(context.getApplicationContext(),mlocationClientOption);
	}
	
	public static BaiduLocationEntity getInstance(Context context){
		if(mInstance==null){
			mInstance = new BaiduLocationEntity(context.getApplicationContext());
		}
		return mInstance;
	}

	private void initOption() {
		mlocationClientOption = new LocationClientOption();
		mlocationClientOption.setLocationMode(LocationMode.Hight_Accuracy);
		mlocationClientOption.setOpenGps(true);
		mlocationClientOption.setAddrType("all");
		mlocationClientOption.setCoorType("bd09ll");
		mlocationClientOption.setScanSpan(5000);
		mlocationClientOption.setIsNeedAddress(true);
		mlocationClientOption.setNeedDeviceDirect(false);
	}
	
	public void startLocate(){
		mLocationClient.registerLocationListener(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				if (location == null) {
					Log.i("testYJ", "location==null");
					Toast.makeText(context, context.getString(R.string.locate_fail), Toast.LENGTH_SHORT).show();
					return;
				}
				// locType代表定位结果，61代表GPS定位结果,66代表离线定位结果,161代表网络定位结果
				int locType = location.getLocType();
				if (locType == 61 || locType == 66 || locType == 161) {
					if(mLocationListener!=null){
						mLocationListener.onReceiveLocation(location,locType);
					}
				}
			}
		});
		// 启动Client 服务
		mLocationClient.start();
		mLocationClient.requestOfflineLocation();
		mLocationClient.requestLocation();
	}
	
	public void stopLocate(){
		if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.stop();
		}
	}
	
	public void setLocationListener(MyLocationListener mLocationListener){
		this.mLocationListener = mLocationListener;
	}
	
	//定位回调接口
	public interface MyLocationListener{
		public void onReceiveLocation(BDLocation location, int locType);
	}
}
