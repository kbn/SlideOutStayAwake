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
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

public class SlideOutStayAwakeService extends Service {

	private static final String TAG = SlideOutStayAwakeService.class.getSimpleName();
	private static final String BCAST_CONFIGCHANGED = "android.intent.action.CONFIGURATION_CHANGED";

	private static PowerManager.WakeLock wl;
	private static int wakelevel = PowerManager.SCREEN_DIM_WAKE_LOCK;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate()");

		IntentFilter filter = new IntentFilter();
		filter.addAction(BCAST_CONFIGCHANGED);
		this.registerReceiver(mBroadcastReceiver, filter);

		setWakeLock();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int lt = intent.getIntExtra("level", wakelevel);
		Log.d(TAG, "onStartCommand(" + lt + ")");
		setWakeLevel(lt);
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

	private void setWakeLevel(int wakelevel_) {
		if (wakelevel_ != wakelevel) {
			wakelevel = wakelevel_;
			// Recreate WakeLock if we need another type
			destroyWakeLock();
		}
	}

	private void createWakeLock() {
		if (wl == null) {
			Log.d(TAG, "createWakeLock(" + wakelevel + ")");
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wl = pm.newWakeLock(wakelevel, TAG);
		}
	}

	private void destroyWakeLock() {
		if (wl != null) {
			if (wl.isHeld()) wl.release();
			wl = null;
		}
	}

	private void setWakeLock(boolean lock) {
		createWakeLock();

		if (lock == true && !wl.isHeld()) {
			wl.acquire();
		}
		else if (lock == false && wl.isHeld()) {
			wl.release();
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

	public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent myIntent) {
			if (myIntent.getAction().equals(BCAST_CONFIGCHANGED)) {
				Log.d(TAG, "received->" + BCAST_CONFIGCHANGED);
				setWakeLock();
			}
		}
	};
}
