package com.ashish.convertforme;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Convert extends Activity 
{
	// Screen widgets
	private Spinner conversions;
	private Button convertButton;
	private ImageButton speakButton;
	private EditText givenVal;
	private TextView convertedVal;

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
					if(typeOfConversion.equalsIgnoreCase("Kgs -> Lbs"))
					{
						convertedValue = toLbs(givenValue);
						unit = "Lbs";
					}
					else if(typeOfConversion.equalsIgnoreCase("Lbs -> Kgs"))
					{
						convertedValue = toKgs(givenValue);
						unit = "Kgs";
					}
					else if(typeOfConversion.equalsIgnoreCase("�C -> �F"))
					{
						convertedValue = toFahrenheit(givenValue);
						unit = "�F";
					}
					else if(typeOfConversion.equalsIgnoreCase("�F -> �C"))
					{
						convertedValue = toCelsius(givenValue);
						unit = "�C";
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
		
	}

	protected double toFahrenheit(double celsius)
	{
		return (celsius * (9/5) + 32.0);
	}
	
	protected double toCelsius(double fahrenheit)
	{
		return (fahrenheit - 32) * 5 / 9;
	}
	
	protected double toLbs(double kgs)
	{
		return (kgs * 2.2);
	}
	
	protected double toKgs(double Lbs)
	{
		return (Lbs * 0.45);
	}
	
	public void speakButtonClicked(View v)
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
	    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Listening your input..");
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
}
