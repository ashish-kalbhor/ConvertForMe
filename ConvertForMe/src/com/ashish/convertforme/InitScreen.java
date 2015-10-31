package com.ashish.convertforme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * 
 * InitScreen is the Splash Screen that will be shown for this app.
 * This screen should run only once, after installation.
 * This screen contains information required to use the app.
 * 
 * @author Ashish Kalbhor
 *
 */
public class InitScreen extends Activity
{
	private static int SPLASH_TIMEOUT = 3000;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_init);
		
		new Handler().postDelayed(new Runnable() 
		{
			@Override
			public void run() 
			{
				Intent intent = new Intent(InitScreen.this, Convert.class);
				startActivity(intent);
				finish();
			}
		}, SPLASH_TIMEOUT);
		
	}
}
