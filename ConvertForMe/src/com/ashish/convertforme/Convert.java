package com.ashish.convertforme;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
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
 * Convert class is responsible for loading the User choice
 * of conversion unit, retrieve the input value and output
 * the converted value.
 * Off-line Voice Recognition feature is available.
 * Convert class implements <i>OnGestureListener</i> to add a swipe
 * gesture for clearing the screen input and output.
 *
 * @author Ashish Kalbhor
 * 
 */
public class Convert extends Activity implements OnGestureListener, OnInitListener
{
	private static final String F_C = "°F -> °C";
	private static final String C_F = "°C -> °F";
	private static final String LBS_KGS = "Lbs -> Kgs";
	private static final String KGS_LBS = "Kgs -> Lbs";
	private static final String GRAM_OZ = "grams -> Oz";
	private static final String OZ_GRAM = "Oz -> grams";
	private static final String INCH_CMS = "Inch -> cm";
	private static final String CMS_INCH = "cm -> Inch";
	private static final String MET_YARD = "meters -> yards";
	private static final String YARD_MET = "yards -> meters";
	private static final String LTR_PINT = "ltr -> pint";
	private static final String PINT_LTR = "pint -> ltr";
	private static final String LTR_QUART = "ltr -> quart";
	private static final String QUART_LTR = "quart -> ltr";
	// Screen widgets
	private Spinner conversions;
	private Button convertButton;
	private ImageButton speakButton;
	private EditText givenVal;
	private TextView convertedVal;
	private GestureDetector gDetector; 
	private TextToSpeech speech;

	// Variables
	private String typeOfConversion;
	private double givenValue;
	private double convertedValue;
	private String unit;
	private boolean canSpeak = false;
	
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
        
        // Initialize text to speech converter.
        speech = new TextToSpeech(this, this);
		
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
					else if(typeOfConversion.equalsIgnoreCase(GRAM_OZ))
					{
						convertedValue = toOzs(givenValue);
						unit = "Oz";
					}
					else if(typeOfConversion.equalsIgnoreCase(OZ_GRAM))
					{
						convertedValue = toGms(givenValue);
						unit = "gms";
					}
					else if(typeOfConversion.equalsIgnoreCase(CMS_INCH))
					{
						convertedValue = toInches(givenValue);
						unit = "inches";
					}
					else if(typeOfConversion.equalsIgnoreCase(INCH_CMS))
					{
						convertedValue = toCms(givenValue);
						unit = "cms";
					}
					else if(typeOfConversion.equalsIgnoreCase(MET_YARD))
					{
						convertedValue = toYards(givenValue);
						unit = "yards";
					}
					else if(typeOfConversion.equalsIgnoreCase(YARD_MET))
					{
						convertedValue = toMeter(givenValue);
						unit = "meters";
					}
					else if(typeOfConversion.equalsIgnoreCase(LTR_PINT))
					{
						convertedValue = toPints(givenValue);
						unit = "pints";
					}
					else if(typeOfConversion.equalsIgnoreCase(PINT_LTR))
					{
						convertedValue = toPLiters(givenValue);
						unit = "liters";
					}
					else if(typeOfConversion.equalsIgnoreCase(LTR_QUART))
					{
						convertedValue = toQuarts(givenValue);
						unit = "quarts";
					}
					else if(typeOfConversion.equalsIgnoreCase(QUART_LTR))
					{
						convertedValue = toQLiters(givenValue);
						unit = "liters";
					}

					convertedVal.setEnabled(true);
					DecimalFormat twoDigitFormat = new DecimalFormat("#.##");
					String result = String.valueOf(twoDigitFormat.format(convertedValue));
					convertedVal.setText(result + " " + unit);
					
					
					if(canSpeak)
					{
						speech.speak("Answer is " + result + " " + unit, TextToSpeech.QUEUE_FLUSH, null);
					}
					
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
					case LBS_KGS:
					case GRAM_OZ:
					case OZ_GRAM:
						root.setBackgroundResource(R.color.CornflowerBlue);
						break;
					case F_C:
					case C_F:
						root.setBackgroundResource(R.color.Aquamarine);
						break;
					case INCH_CMS:
					case CMS_INCH:
					case MET_YARD:
					case YARD_MET:
						root.setBackgroundResource(R.color.DarkSlateBlue);
						break;
					case LTR_PINT:
					case PINT_LTR:
					case LTR_QUART:
					case QUART_LTR:
						root.setBackgroundResource(R.color.Azure);
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
	
	@Override
	protected void onPause() 
	{
		if(speech !=null)
		{
			speech.stop();
			speech.shutdown();
	    }
	    super.onPause();
	}
	
	@Override
	protected void onDestroy() 
	{
		if(speech != null)
		{
			speech.stop();
			speech.shutdown();
		}
		super.onDestroy();
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
	 * Convert given Gms into Ozs.
	 * @param gms
	 * @return Ozs
	 */
	protected double toOzs(double gms)
	{
		return (gms * 0.035274);
	}
	
	/**
	 * Convert given Ozs into Gms.
	 * @param Ozs
	 * @return Gms
	 */
	protected double toGms(double Ozs)
	{
		return (Ozs * 28.3495);
	}
	
	/**
	 * Convert given inches into Cms.
	 * @param inches
	 * @return Cms
	 */
	protected double toCms(double inches)
	{
		return (inches * 2.54);
	}
	
	/**
	 * Convert given cms into Inches.
	 * @param cms
	 * @return Inches
	 */
	protected double toInches(double cms)
	{
		return (cms * 0.393701);
	}
	
	/**
	 * Convert given mtr into Yards.
	 * @param mtr
	 * @return Yards
	 */
	protected double toYards(double mtr)
	{
		return (mtr * 1.09361);
	}
	
	/**
	 * Convert given yards into Meter.
	 * @param yards
	 * @return Meter
	 */
	protected double toMeter(double yards)
	{
		return (yards * 0.9144);
	}
	
	/**
	 * Convert given liters into Pints.
	 * @param liters
	 * @return Pints
	 */
	protected double toPints(double liters)
	{
		return (liters * 2.11338);
	}
	
	/**
	 * Convert given Pints into liters.
	 * @param Pints
	 * @return liters
	 */
	protected double toPLiters(double pints)
	{
		return (pints * 0.473176);
	}
	
	/**
	 * Convert given liters into Quarts.
	 * @param liters
	 * @return Quarts
	 */
	protected double toQuarts(double liters)
	{
		return (liters * 1.05669);
	}
	
	/**
	 * Convert given quarts into liters.
	 * @param quarts
	 * @return liters
	 */
	protected double toQLiters(double quarts)
	{
		return (quarts * 0.946353);
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

	@Override
	public void onInit(int status) 
	{
		if(status  != TextToSpeech.ERROR)
		{
			speech.setLanguage(Locale.getDefault());
			canSpeak = true;
		}
		else
		{
			Toast.makeText(getApplicationContext(), "TTS Init Failed", Toast.LENGTH_SHORT).show();
		}
	}
}
