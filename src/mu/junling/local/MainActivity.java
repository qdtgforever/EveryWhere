package mu.junling.local;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	private LocationClient mLocationClient;
	private SensorManager sm;
	private Sensor aSensor;  
    private Sensor mSensor;
    private float[] accelerometerValues = new float[3];  
    private float[] magneticFieldValues = new float[3]; 
    private TextView mDirction;
    private TextView mCoordinate_name;
    private TextView mCoordinate;
    private Button mLocationButton;
    private Button mMapButton;
    //private Button mShareMapButton;
    
    
    private RotateAnimation mRotateAnimation;
    //private float mCurrent=0;
    //private float mNext;
    private float mDataHead=0;
    private float mDataMid=0;
    private float mDataLast=0;
    private float mData=0;
   
    
    
    
    
    private mu.junling.view.CompassView mCompassView;


    private Handler mHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(msg.what==1){
				   mRotateAnimation.setDuration(500);
				   mRotateAnimation.setFillAfter(true);
	           	   mRotateAnimation.setRepeatCount(0);
	           	   mCompassView.startAnimation(mRotateAnimation);
			}
			super.handleMessage(msg);
			
		}
    	
    };

	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 
		setContentView(R.layout.activity_main);
	
		
		mLocationClient = new LocationClient(getApplicationContext());
		
		
		mDirction=(TextView)findViewById(R.id.dirction);
		mCoordinate_name=(TextView)findViewById(R.id.Coordinate_name);
		mCoordinate=(TextView)findViewById(R.id.Coordinate);
		
		mLocationButton=(Button)findViewById(R.id.button_location);
		mMapButton=(Button)findViewById(R.id.button_map);
		//mShareMapButton=(Button)findViewById(R.id.share_map);
		
		mCompassView=(mu.junling.view.CompassView)findViewById(R.id.compass_of_myposition);
		
		sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		aSensor=(Sensor)sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensor=(Sensor)sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		if(aSensor==null){
			Toast.makeText(getApplicationContext(), "手机不包含加速度传感器，暂不能使用指南针功能", Toast.LENGTH_LONG).show();
		}
		if(mSensor==null){
			Toast.makeText(getApplicationContext(), "手机不包含方向传感器，暂不能使用指南针功能", Toast.LENGTH_LONG).show();
		}
		
		init();
		InitLocation();
		calculateOrientation(); 
		
	
		
	}
	
	
	private void init() {
		// TODO Auto-generated method stub
		sm.registerListener(myListener, aSensor, SensorManager.SENSOR_DELAY_NORMAL);  
		sm.registerListener(myListener, mSensor,SensorManager.SENSOR_DELAY_NORMAL); 
		
		mLocationClient.registerLocationListener(new BDLocationListener(){

			@Override
			public void onReceiveLocation(BDLocation Location) {
				// TODO Auto-generated method stub
				Log.v(TAG, "mBDLocationListener");
				mCoordinate_name.setText(Location.getAddrStr());
				mCoordinate.setText("纬度："+Location.getLatitude()+" 经度："+Location.getLongitude());
		/*		MyLocationData locData = new MyLocationData.Builder()
				.accuracy(Location.getRadius())
				// 此处设置开发者获取到的方向信息，顺时针0-360
				.direction(100).latitude(Location.getLatitude())
				.longitude(Location.getLongitude()).build();
		mBaiduMap.setMyLocationData(locData);
		if (isFirstLoc) {
			isFirstLoc = false;
			LatLng ll = new LatLng(Location.getLatitude(),
					Location.getLongitude());
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
		}*/
			}
			
		});
		
		mLocationButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
					 mLocationClient.start();
					 mLocationClient.requestLocation();
			
			}
		});
		mMapButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent mIntent=new Intent(getApplicationContext(), MyMap.class); 
				startActivity(mIntent);
			}
		});
/*		mShareMapButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent mIntent=new Intent(getApplicationContext(), MyMap.class); 
				startActivity(mIntent);
			}
		});*/
		
		
		
	}

/*	BDLocationListener mBDLocationListener=new BDLocationListener() {
		
		@Override
		public void onReceiveLocation(BDLocation Location) {
			// TODO Auto-generated method stub
			Log.v(TAG, "mBDLocationListener");
			mCoordinate_name.setText(Location.getCity());
			mCoordinate.setText("纬度："+Location.getLatitude()+"经度："+Location.getAltitude());
			
			
		}
	};*/
	SensorEventListener myListener=new SensorEventListener() {
		
		@Override
		public void onSensorChanged(SensorEvent sensorEvent) {
			// TODO Auto-generated method stub
			  if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)  
				      magneticFieldValues = sensorEvent.values;  
			  if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)  
				          accelerometerValues = sensorEvent.values;  
				      calculateOrientation();  

		}
		
		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
	};

	private void calculateOrientation() {
		
			           float[] values = new float[3];  
			           float[] R = new float[9];  
			          SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
			          Log.i(TAG, values[0]+"val"); 
			          if(R[1]==0&&R[4]==0&&R[5]==0){
			        	  return;
			          }
			          
			           SensorManager.getOrientation(R, values);  
			   
			           // 要经过一次数据格式的转换，转换为度  
			           values[0] = (float) Math.toDegrees(values[0]);  
			           Log.i(TAG, values[0]+"val");  
			          // mTextView.setText(""+values[0]);
			           //values[1] = (float) Math.toDegrees(values[1]);  
			           //values[2] = (float) Math.toDegrees(values[2]);
			          // mRotateAnimation= RotateAnimation(mCurrent, (float)values[0],Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
			          // mNext = -values[0];
			           mDataLast=mDataMid;
			           mDataMid=mDataHead;
			           mDataHead=-values[0];
			           mData=(mDataLast+mDataHead+mDataMid)/3;
			           //mRotateAnimation=new RotateAnimation(mCurrent,mNext,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f); 
			           //mRotateAnimation=new RotateAnimation(mDataHead,mData,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f); 
			           if(values[0] >= -5 && values[0] < 5){  
			             // Log.i(TAG, "正北"); 
			              mDirction.setText("手机指向：正北");
			           }  
			           else if(values[0] >= 5 && values[0] < 85){  
			              // Log.i(TAG, "东北");  
			               mDirction.setText("手机指向：东北");
			           }  
			           else if(values[0] >= 85 && values[0] <=95){  
			               //Log.i(TAG, "正东");  
			               mDirction.setText("手机指向：正东");
			           }  
			           else if(values[0] >= 95 && values[0] <175){  
			               //Log.i(TAG, "东南"); 
			               mDirction.setText("手机指向：东南");
			           }  
			           else if((values[0] >= 175 && values[0] <= 180) || (values[0]) >= -180 && values[0] < -175){  
			               //Log.i(TAG, "正南");
			               mDirction.setText("手机指向：正南");
			           }  
			           else if(values[0] >= -175 && values[0] <-95){  
			              // Log.i(TAG, "西南");  
			               mDirction.setText("手机指向：西南");
			           }  
			           else if(values[0] >= -95 && values[0] < -85){  
			               //Log.i(TAG, "正西");  
			               mDirction.setText("手机指向：正西");
			           }  
			           else if(values[0] >= -85 && values[0] <-5){  
			               //Log.i(TAG, "西北");  
			               mDirction.setText("手机指向：西北");
			           } 
			           if(mDataHead-mData>-3&&mDataHead-mData<3){
			        	   mRotateAnimation=new RotateAnimation(mDataHead,mData,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f); 
			        	   mHandler.sendEmptyMessage(1);
			           }
			          
			          // mCurrent=-values[0];
			          
			         }

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		sm.unregisterListener(myListener);
		//mMapView.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sm.registerListener(myListener, aSensor, SensorManager.SENSOR_DELAY_NORMAL);  
		sm.registerListener(myListener, mSensor,SensorManager.SENSOR_DELAY_NORMAL); 
		//mMapView.onResume();
	}  
	

	
	private void InitLocation(){
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		//option.setCoorType("all");
		option.setLocationMode(com.baidu.location.LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
		//option.setCoorType("gcj02");//返回的定位结果是百度经纬度，默认值gcj02
		option.setCoorType("bd09ll");
		
		option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 退出时销毁定位
		mLocationClient.stop();
		// 关闭定位图层
		
		//mMapView.onDestroy(); 
	}
		
	
/*	 public  class  MyLocationListener  implements  BDLocationListener{

		@Override
		public void onReceiveLocation(BDLocation Location) {
			// TODO Auto-generated method stub
			Log.v(TAG, "mBDLocationListener");
			mCoordinate_name.setText(Location.getCity());
			mCoordinate.setText("纬度："+Location.getLatitude()+"经度："+Location.getAltitude());
		}
		 
	 }*/
		
	}








