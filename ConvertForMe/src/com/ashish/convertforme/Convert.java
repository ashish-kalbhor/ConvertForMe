package com.ashish.convertforme;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Ashish Kalbhor
 * 
 * Convert class is responsible for loading the User choice
 * of conversion unit, retrieve the input value and output
 * the converted value.
 * Off-line Voice Recognition feature is available.
 * Convert class implements OnGestureListener to add a swipe
 * gesture for clearing the screen input and output.
 *
 */
public class Convert extends Activity implements OnGestureListener
{
	private static final String F_C = "°F -> °C";
	private static final String C_F = "°C -> °F";
	private static final String LBS_KGS = "Lbs -> Kgs";
	private static final String KGS_LBS = "Kgs -> Lbs";
	// Screen widgets
	private Spinner conversions;
	private Button convertButton;
	private ImageButton speakButton;
	private EditText givenVal;
	private TextView convertedVal;
	private GestureDetector gDetector; 

	// Variables
	private String typeOfConversion;
	private double givenValue;
	private double convertedValue;
	private String unit;
	
	private static final int REQUEST_CODE = 1234;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_convert);
		
		conversions = (Spinner)findViewById(R.id.spinner1);
		givenVal = (EditText)findViewById(R.id.editText1);
		convertButton = (Button)findViewById(R.id.button1);
		convertedVal = (TextView)findViewById(R.id.textView1);
		speakButton  = (ImageButton)findViewById(R.id.imageButton1);
		gDetector = new GestureDetector(this);
		
		// Disable button if no recognition service is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0)
        {
            speakButton.setEnabled(false);
        }
		
        speakButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) 
			{
				speakButtonClicked(view);
			}
		});
        
		convertButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				try {
					typeOfConversion = String.valueOf(conversions.getSelectedItem());
					givenValue = Double.parseDouble(givenVal.getText().toString());
					if(typeOfConversion.equalsIgnoreCase(KGS_LBS))
					{
						convertedValue = toLbs(givenValue);
						unit = "Lbs";
					}
					else if(typeOfConversion.equalsIgnoreCase(LBS_KGS))
					{
						convertedValue = toKgs(givenValue);
						unit = "Kgs";
					}
					else if(typeOfConversion.equalsIgnoreCase(C_F))
					{
						convertedValue = toFahrenheit(givenValue);
						unit = "°F";
					}
					else if(typeOfConversion.equalsIgnoreCase(F_C))
					{
						convertedValue = toCelsius(givenValue);
						unit = "°C";
					}

					convertedVal.setEnabled(true);
					DecimalFormat twoDigitFormat = new DecimalFormat("#.##");
					convertedVal.setText(
							String.valueOf(twoDigitFormat.format(convertedValue)) + " " + unit);
				} catch (NumberFormatException e) {
					Toast.makeText(getApplicationContext(), "Please check the input data !", Toast.LENGTH_LONG).show();
				}				
			}			
		});
		
		conversions.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				givenVal.setText("");
				convertedVal.setText("");
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		conversions.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
			@Override
			public void onItemSelected(AdapterView<?> aview, View view, int pos, long id) 
			{
				String item = aview.getItemAtPosition(pos).toString();
				View root = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
				switch (item) 
				{
					case KGS_LBS:
						root.setBackgroundResource(R.color.CornflowerBlue);
						break;
					case LBS_KGS:
						root.setBackgroundResource(R.color.Aquamarine);
						break;
					case F_C:
						root.setBackgroundResource(R.color.DarkCyan);
						break;
					case C_F:
						root.setBackgroundResource(R.color.DarkSlateBlue);
						break;
					default:
						break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) 
			{
				// Do nothing				
			}
		});
		
		
		
	}

	/**
	 * Convert given degree celsius into degree fahrenheit
	 * @param celsius
	 * @return fahrenheit
	 */
	protected double toFahrenheit(double celsius)
	{
		return (celsius * (9/5) + 32.0);
	}
	
	/**
	 * Convert given degree fahrenheit into degree celsius
	 * @param fahrenheit
	 * @return celsius
	 */
	protected double toCelsius(double fahrenheit)
	{
		return (fahrenheit - 32) * 5 / 9;
	}
	
	/**
	 * Convert given Kgs into Lbs.
	 * @param kgs
	 * @return Lbs
	 */
	protected double toLbs(double kgs)
	{
		return (kgs * 2.2);
	}
	
	/**
	 * Convert given Lbs into Kgs.
	 * @param Lbs
	 * @return Kgs
	 */
	protected double toKgs(double Lbs)
	{
		return (Lbs * 0.45);
	}
	
	/**
	 * Starts listening to the voice input.
	 * @param view
	 */
	public void speakButtonClicked(View view)
	{
	    startVoiceRecognitionActivity();
	}

	/**
	 * Fire an intent to start the voice recognition activity.
	 */
	private void startVoiceRecognitionActivity()
	{
	    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
	            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Listening..");
	    startActivityForResult(intent, REQUEST_CODE);
	}

	/**
	 * Handle the results from the voice recognition activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
	    if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
	    {
	        // Populate the wordsList with the String values the recognition engine thought it heard
	        ArrayList<String> matches = data.getStringArrayListExtra(
	                RecognizerIntent.EXTRA_RESULTS);
	        givenVal.setText(matches.get(0));
	    }
	    super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	/**
	 * Recognize the swipe gesture.
	 * Swipe movement from up to down cleans the input and output texts.
	 */
	@Override
	public boolean onFling(MotionEvent start, MotionEvent finish, float velocityX, float velocityY) 
	{
			if (start.getRawY() < finish.getRawY()) 
			{
	            // Clear text
				givenVal.setText("");
				convertedVal.setText("");
			} else {
				// Clear text
			}
			return true;
	}

	@Override
	public void onLongPress(MotionEvent e) 
	{
		// do nothing
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) 
	{
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e)
	{
		// do nothing
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) 
	{
		return false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return gDetector.onTouchEvent(event);
	}
}
