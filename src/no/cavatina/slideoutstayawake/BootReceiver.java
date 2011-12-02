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

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent serviceIntent = new Intent(SlideOutStayAwakeService.class.getName());
		context.startService(serviceIntent);
	}
}
