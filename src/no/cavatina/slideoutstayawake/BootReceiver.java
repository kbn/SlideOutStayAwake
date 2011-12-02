/**
 * BootReceiver - Start SlideOutStayAwake service at boot.
 *
 * Written by Kristian Berge Nessa <kristian at cavatina dot no>
 * for Cavatina Software AS, placed in the public domain.
 */
package no.cavatina.slideoutstayawake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
		boolean start = p.getBoolean("boot", true);
		if (start == true) {
			Intent serviceIntent = new Intent(SlideOutStayAwakeService.class.getName());
			context.startService(serviceIntent);
		}
	}
}
