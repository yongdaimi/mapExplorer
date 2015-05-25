package com.yuneec.android.mapexplorer.maps;

import android.content.SharedPreferences;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.yuneec.android.mapexplorer.entity.LatLongInfo;
import com.yuneec.android.mapexplorer.settings.MyApplication;

public class BaiduMapManager {
	
	private static BaiduMapManager baiduMapManager = null;
	private static MapView mapView;
	private static BaiduMap baiduMap;
	public static BaiduMap getBaiduMap() {
		return baiduMap;
	}

	private MapStatus mapStatus;
	private MapStatusUpdate mapStatusUpdate;

	public BDLocationListener myLocationListener = new MyLocationListener();// 定位的回调接口
	private LatLng currentLatLng;//当前坐标
	private double currentLatitude;
	private double currentlongitude;
	private BaiduMapManager() {
		super();
	}
	public static BaiduMapManager getInstance(MapView mapView) {
		if (baiduMapManager == null) {
			baiduMapManager = new BaiduMapManager();
			BaiduMapManager.mapView = mapView;
			baiduMap=mapView.getMap();
			mapView.showZoomControls(false);// 不显示缩放控件
		}
		return baiduMapManager;
	}
	

	public LatLng getmCurrentLatLng() {
		return currentLatLng;
	}

	public double getmCurrentatitude() {
		return currentLatitude;
	}

	public double getmCurrentlongitude() {
		return currentlongitude;
	}
	

	/**
	 * 定位
	 * @param locationClient 定位的核心类
	 */
	public void goToMyLocation(LocationClient locationClient) {			
		// 设置是否允许定位图层,只有先允许定位图层后设置定位数据才会生效
		baiduMap.setMyLocationEnabled(true);
		baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
		setLocationClient(locationClient);
		updateBaiduMap(currentLatLng);
	}
	

	/**
	 * 设置LocationClient数据
	 * @param LocationClient
	 */
	public void setLocationClient(LocationClient locationClient) {
		// 使用LocationClientOption类.
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(3000);// 设置发起定位请求的间隔时间为3000ms
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
		option.setOpenGps(true);
		locationClient.setLocOption(option);
		locationClient.registerLocationListener(myLocationListener);
		locationClient.start();
	}

	/**
	 * 刷新地图 
	 * @param latLng 
	 */
	public void updateBaiduMap(LatLng latLng) {
		// 获得地图的当前状态的信息
		mapStatus = new MapStatus.Builder().zoom(18).target(latLng).build();
		// 设置地图将要变成的状态
		mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
		baiduMap.animateMapStatus(mapStatusUpdate);// 设置地图的变化
		
	}
	
	

	public class MyLocationListener implements BDLocationListener {
	

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null || mapView == null)
				return;			
			
			currentLatitude = location.getLatitude();
			currentlongitude = location.getLongitude();
			currentLatLng = new LatLng(location.getLatitude(),
					location.getLongitude());			
			
			SharedPreferences.Editor sharedata = MyApplication.getInstance().getContext().getSharedPreferences("data", 0).edit();   
			sharedata.putString("oldLat",String.valueOf(currentLatitude));   
			sharedata.putString("oldLng",String.valueOf(currentlongitude));   
			sharedata.commit();
			
			// 构造定位数据
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					.latitude(currentLatitude)
					.longitude(currentlongitude)
					.build();
			baiduMap.setMyLocationData(locData);// 设置定位数据
		}
	}
	

}
