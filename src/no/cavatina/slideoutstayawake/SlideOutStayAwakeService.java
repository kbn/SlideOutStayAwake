/**
 * SlideOutStayAwakeService - Keep device from sleeping while hardware keyboard
 * is out.
 *
 * Written by Kristian Berge Nessa <kristian at cavatina dot no>
 * for Cavatina Software AS, placed in the public domain.
 */
package no.cavatina.slideoutstayawake;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.BatteryManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

public class SlideOutStayAwakeService extends Service {

	private static final String TAG = SlideOutStayAwakeService.class.getSimpleName();
	private static final String BCAST_CONFIGCHANGED = "android.intent.action.CONFIGURATION_CHANGED";
	private static final String POWER_ON = "android.intent.action.ACTION_POWER_CONNECTED";
	private static final String POWER_OFF = "android.intent.action.ACTION_POWER_DISCONNECTED";

	private static PowerManager.WakeLock wl;
	private static int wakelevel = PowerManager.SCREEN_DIM_WAKE_LOCK;
	private static int wakelocktype = PowerManager.SCREEN_DIM_WAKE_LOCK;
	private static boolean connected = false;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate()");
		try {
			setConnected(isConnected(getBaseContext()));
			IntentFilter filter = new IntentFilter();
			filter.addAction(BCAST_CONFIGCHANGED);
			filter.addAction(POWER_ON);
			filter.addAction(POWER_OFF);
			this.registerReceiver(mBroadcastReceiver, filter);
		}
		catch (Exception e) {
			Log.d(TAG, e.getMessage());
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			int lt = intent.getIntExtra("level", wakelevel);
			Log.d(TAG, "onStartCommand(" + lt + ")");
			setWakeLevel(lt);
		}
		else {
			Log.d(TAG, "onStartCommand(0, " + wakelevel + ")");
		}
		setWakeLock();
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy()");
		destroyWakeLock();
		this.unregisterReceiver(mBroadcastReceiver);
	}

	/// Make sure we have a WakeLock of the correct type.
	private void createWakeLock() {
		int wlt = wakelevel;
		if (wakelevel == Preferences.DIM_ON_BATTERY) {
			if (connected) wlt = PowerManager.FULL_WAKE_LOCK;
			else wlt = PowerManager.SCREEN_DIM_WAKE_LOCK;
		}
		// Recreate WakeLock if we need another type
		if (wlt != wakelocktype) {
			destroyWakeLock();
			wakelocktype = wlt;
		}
		if (wl == null) {
			Log.d(TAG, "createWakeLock(" + wakelocktype + ")");
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wl = pm.newWakeLock(wakelocktype, TAG);
		}
	}

	private void destroyWakeLock() {
		if (wl != null) {
			if (wl.isHeld()) wl.release();
			wl = null;
		}
	}

	private void setWakeLock(boolean lock) {
		try {
			createWakeLock();

			if (lock == true && !wl.isHeld()) {
				wl.acquire();
			}
			else if (lock == false && wl.isHeld()) {
				wl.release();
			}
		}
		catch (Exception e) {
			Log.d(TAG, e.getMessage());
		}
	}

	public void setWakeLock() {
		switch (getResources().getConfiguration().hardKeyboardHidden) {
		case Configuration.HARDKEYBOARDHIDDEN_NO:
			Log.d(TAG, "Keyboard out!");
			setWakeLock(true);
			break;
		case Configuration.HARDKEYBOARDHIDDEN_YES:
			Log.d(TAG, "Keyboard in!");
			setWakeLock(false);
			break;
		default:
			break;
		}
	}

	private void setWakeLevel(int wakelevel_) {
		wakelevel = wakelevel_;
	}

	public void setConnected(boolean connected_) {
		connected = connected_;
	}

    public static boolean isConnected(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
    }

    public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent myIntent) {
			if (myIntent.getAction().equals(BCAST_CONFIGCHANGED)) {
				Log.d(TAG, "received->" + BCAST_CONFIGCHANGED);
				setWakeLock();
			}
			else if (myIntent.getAction().equals(POWER_ON)) {
				Log.d(TAG, "received->" + myIntent.getAction());
				setConnected(true);
				setWakeLock();
			}
			else if (myIntent.getAction().equals(POWER_OFF)) {
				Log.d(TAG, "received->" + myIntent.getAction());
				setConnected(false);
				setWakeLock();
			}
		}
	};
}
