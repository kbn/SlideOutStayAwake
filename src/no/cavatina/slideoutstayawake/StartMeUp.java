/**
 * StartMeUp - Activity to start SlideOutStayAwake service.
 *
 * Written by Kristian Berge Nessa <kristian at cavatina dot no>
 * for Cavatina Software AS, placed in the public domain.
 */
package no.cavatina.slideoutstayawake;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class StartMeUp extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		startService(new Intent(SlideOutStayAwakeService.class.getName()));
		
		Toast.makeText(getBaseContext(),
			       "Started SlideOutStayAwakeService",
			       Toast.LENGTH_LONG).show();
		finish();
	}
}
