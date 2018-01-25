package com.kuri.speechbluetoothandroidstudio;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class VoiceArmControlsActivity extends AppCompatActivity {

    TextInputLayout voiceLayerInput;
    SpeechRecognizer mSpeechRecognizer;
    ImageButton micButton;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket bluetoothSocket = null;
    private boolean isBtConnected = false;
    String bluetoothData;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //SPP UUID. Look for it
    Miscellaneous miscellaneousDoings = new Miscellaneous();


    //NOTES ON USING GOOGLE'S SPEECH RECOGNIZER:

    //Create a Speech Recognizer using SpeechRecognizer.createSpeechRecognizer(this);
    //Make an intent and pass in the necessary information regarding the language input
    //When the button/prompt is made, invoke startListening() and pass in the intent
    //In mSpeechRecognizer.setRecognitionListener(new RecognitionListener() -->
    //override the onResults(), get the result in an array list and display the first match



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_arm_controls);

        Intent obtainedIntent = getIntent();
        address = obtainedIntent.getStringExtra(ManualArmControlsActivity.MANUAL_EXTRA_ADDRESS);

        new ConnectBT().execute(); //needed to start the thread


        voiceLayerInput = (TextInputLayout) findViewById(R.id.voice_input_layout_arm);
        micButton = (ImageButton) findViewById(R.id.mic_button_2_arm);
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        /*

        EXTRA_LANGUAGE_MODEL
        Informs the recognizer which speech model to prefer when performing ACTION_RECOGNIZE_SPEECH.
        The recognizer uses this information to fine tune the results. This extra is required.
        Activities implementing ACTION_RECOGNIZE_SPEECH may interpret the values as they see fit


        LANGUAGE_MODEL_FREE_FORM
        Use a language model based on free-form speech recognition.
        This is a value to use for EXTRA_LANGUAGE_MODEL.



        EXTRA_LANGUAGE
        Optional IETF language tag (as defined by BCP 47), for example "en-US".
        This tag informs the recognizer to perform speech recognition in a language different
        than the one set in the getDefault().

         */


        final Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                voiceLayerInput.getEditText().setHint("Listening...");
            }
        });


        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {


                //get all matches
                ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                //display first match
                if (matches != null) {
                    voiceLayerInput.getEditText().setText(matches.get(0));
                    executeCommand(voiceLayerInput.getEditText().getText().toString());
                }

            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }


            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });


    }


    private void executeCommand(String voiceCommand){

        if(voiceCommand.toLowerCase().equals("rotate")){
            turnThree();

        } else if(voiceCommand.toLowerCase().equals("back")){
            turnFour();

        }

    }

    private void turnOne()
    {
        if (bluetoothSocket!=null)
        {
            try {
                bluetoothData = "1";
                bluetoothSocket.getOutputStream().write(bluetoothData.getBytes());

            } catch (IOException e) {
                miscellaneousDoings.displayErrorToast("Error", getApplicationContext());
            }
        }
    }

    private void turnTwo() {
        if (bluetoothSocket!=null)
        {
            try {
                bluetoothData = "2";
                bluetoothSocket.getOutputStream().write(bluetoothData.getBytes());

            } catch (IOException e) {
                miscellaneousDoings.displayErrorToast("Error", getApplicationContext());
            }
        }
    }


    private void turnThree() {

        if(bluetoothSocket!=null){

            try {
                bluetoothData = "3";
                bluetoothSocket.getOutputStream().write(bluetoothData.getBytes());

            } catch (IOException e) {
                miscellaneousDoings.displayErrorToast("Error", getApplicationContext());
            }
        }


    }


    private void turnFour() {

        if(bluetoothSocket!=null){

            try {
                bluetoothData = "4";
                bluetoothSocket.getOutputStream().write(bluetoothData.getBytes());

            } catch (IOException e) {
                miscellaneousDoings.displayErrorToast("Error", getApplicationContext());
            }
        }

    }






    //THIS IS THE MOST IMPORTANT SECTION FOR THE BLUETOOTH CODE
    //REFER THE ONLINE ANDROID MANUAL FOR MORE INFORMATION
    //MOST OF THE CODE STRUCTURE IS TAKEN FROM THE OPEN SOURCE CODE

    private class ConnectBT extends AsyncTask<Void, Void, Void> {  // UI thread
        //NOTE:
        //---------------------------
        //AsyncTask enables proper and easy use of the UI thread.
        //This class allows you to perform background operations and publish
        //results on the UI thread without having to manipulate threads and/or handlers.


        //Once created, a task is executed very simply:
        //new DownloadFilesTask().execute(url1, url2, url3);
        //The above invocation has been done in onCreate()



        //EXTRA NOTES:
        //----------------------------
        //onPreExecute():
        //invoked on the UI thread before the task is executed.
        //This step is normally used to setup the task,
        //for instance by showing a progress bar in the user interface.


        //doInBackground(Params...):
        //invoked on the background thread immediately after onPreExecute() finishes executing.
        //This step is used to perform background computation that can take a long time.
        //The parameters of the asynchronous task are passed to this step.
        //The result of the computation must be returned by this step and will be passed
        //back to the last step. This step can also use publishProgress(Progress...) to
        //publish one or more units of progress. These values are published on the UI thread,
        //in the onProgressUpdate(Progress...) step.


        //onProgressUpdate(Progress...):
        //invoked on the UI thread after a call to publishProgress(Progress...).
        //The timing of the execution is undefined. This method is used to display any
        //form of progress in the user interface while the
        //background computation is still executing. For instance, it can be used to animate a
        //progress bar or show logs in a text field.


        //onPostExecute(Result):
        //invoked on the UI thread after the background computation finishes.
        // The result of the background computation is passed to this step as a parameter.

        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(VoiceArmControlsActivity.this, "Connecting.......", "Please wait!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (bluetoothSocket == null || !isBtConnected) {
                    //check if bluetooth socket created and bluetooth should be connected if not
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice bluetoothDevice = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    bluetoothSocket.connect();//start connection
                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                miscellaneousDoings.displayErrorToast("Connection Failed. Is it a SPP Bluetooth? Try again.", getApplicationContext());
                finish();
            }
            else {
                miscellaneousDoings.displaySuccessToast("Connected", getApplicationContext());
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}
