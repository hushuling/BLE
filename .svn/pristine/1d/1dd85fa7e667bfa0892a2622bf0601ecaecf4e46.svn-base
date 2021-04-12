package com.xiekang.bluetooths.bluetooths.oxgen;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.xiekang.bluetooths.utlis.ContextProvider;

public class BLEManager {
	
	private static final String TAG = "BLEManager";
    private BluetoothLeService mBluetoothLeService;
    public static BLEHelper mBleHelper;  
    private Context mContext;
    public static final String ACTION_FIND_DEVICE = "find_device";
    public static final String ACTION_SEARCH_TIME_OUT = "search_timeout";
    public static final String ACTION_START_SCAN = "start_scan";
	private boolean isBind;

	public BLEManager() {
		mContext = ContextProvider.get().getContext();
	}

	private String address;
	// Code to manage Service lifecycle.
	private final ServiceConnection mServiceConnection = new ServiceConnection() {



		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();

			mBleHelper = new BLEHelper(mBluetoothLeService);
			
			// Automatically connects to the device upon successful start-up
			// initialization.
			mBluetoothLeService.connect(address);
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
			mBleHelper = null;
		}
	};
	
	
	public void closeService(){
		if (isBind)mContext.unbindService(mServiceConnection);
		mBleHelper=null;
		isBind=false;
		if (mBluetoothLeService!=null)mBluetoothLeService.close();
		Log.d(TAG, "-- closeService --");
	}
	
	/**
	 * 断开连接
	 */
	public void disconnect() {
		if (mBluetoothLeService != null) {
			mBluetoothLeService.disconnect();
		}
	}
		
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        mContext.sendBroadcast(intent);
    }
	
	/**
	 * 自定义过滤器
	 * custom intentFilter
	 */
	public static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		//---
		intentFilter.addAction(BluetoothLeService.ACTION_SPO2_DATA_AVAILABLE);
		intentFilter.addAction(BluetoothLeService.ACTION_CHARACTER_NOTIFICATION);
		intentFilter.addAction(ACTION_FIND_DEVICE);
		intentFilter.addAction(ACTION_SEARCH_TIME_OUT);
		intentFilter.addAction(ACTION_START_SCAN);
		return intentFilter;
	}

	public void connect(String address) {
		// start BluetoothLeService
		this.address=address;
		Intent gattServiceIntent = new Intent(mContext, BluetoothLeService.class);
		isBind = mContext.bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
	}
}
