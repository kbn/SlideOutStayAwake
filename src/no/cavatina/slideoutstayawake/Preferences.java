/**
 * Preferences - Setup the SlideOutStayAwake service.
 *
 * Written by Kristian Berge Nessa <kristian at cavatina dot no>
 * for Cavatina Software AS, placed in the public domain.
 */
package no.cavatina.slideoutstayawake;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;

public class Preferences extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		if (isEnabled())
			startService();
	}

	@Override
	public void onResume() {
		super.onResume();
		PreferenceManager.getDefaultSharedPreferences(this)
			.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		PreferenceManager.getDefaultSharedPreferences(this)
			.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
		if (key.equals("enabled") || key.equals("level")) {
			if (isEnabled())
				startService();
			else
				stopService();
		}
	}

	public boolean isEnabled() {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		return p.getBoolean("enabled", true);
	}

	public int getWakeLevel() {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		String level = p.getString("level", "dim");
		if (level.equalsIgnoreCase("full"))
			return PowerManager.FULL_WAKE_LOCK;
		if (level.equalsIgnoreCase("dim_on_battery"))
			return SlideOutStayAwakeService.DIM_ON_BATTERY;
		else
			return PowerManager.SCREEN_DIM_WAKE_LOCK;
	}
	
	public void startService() {
		Intent intent = new Intent(SlideOutStayAwakeService.class.getName());
		intent.putExtra("level", getWakeLevel());
		startService(intent);
	}

	public void stopService() {
		stopService(new Intent(SlideOutStayAwakeService.class.getName()));		
	}
	
	public void onClickStart(View view) {
		startService();
	}

	public void onClickStop(View view) {
		stopService();
	}

}
