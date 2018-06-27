package com.virtualhuman.voicedictionary.Activity.Fragment;

import android.content.Intent;
import android.app.SearchManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.os.AsyncTask;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.virtualhuman.voicedictionary.Helper.InternetConnectionHandling;
import com.virtualhuman.voicedictionary.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Dictionary Fragment
 * Created by minthiri
 */
public class Dictionary extends Fragment {

    public static ImageView micIcon;
    public static TextView micHintText;
    public static TextView answerText;
    ProgressBar progressBar;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private static final String TAG = "searchApp";
    static String result = null;
    Integer responseCode = null;
    String responseMessage = "";

    public Dictionary() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.fragment_dictionary, container, false);
        micIcon=(ImageView) view.findViewById(R.id.micImage);
        micHintText=(TextView) view.findViewById(R.id.micHintText);
        answerText=(TextView) view.findViewById(R.id.answerText);
        progressBar = (ProgressBar) view.findViewById(R.id.pb_loading_indicator);

        micIcon.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Toast.makeText(getContext(), "Click the button", Toast.LENGTH_SHORT).show();
                //TODO: to write code to read the voice
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.ACTION_WEB_SEARCH, RecognizerIntent.EXTRA_WEB_SEARCH_ONLY);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi speak something");
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
                } else {
                    Toast.makeText(getContext(), "Your Device Doesn't Support Speech Input", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // internet connection checking
        if (InternetConnectionHandling.haveNetworkConnection(getContext())) {
            //TODO: to send data to server
        } else{
            Toast.makeText(getContext(), R.string.connectionNotExistError, Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == getActivity().RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    answerText.setText(result.get(0));
                    if (!result.isEmpty()) {
                        if (result.get(0).contains("search")) {
                            String searchQuery = result.get(0);
                            searchQuery = searchQuery.replace("search", "");
                            Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
                            search.putExtra(SearchManager.QUERY, searchQuery);
                            startActivity(search);
                        } else if (result.get(0).contains("show")) {
                            String showQuery = result.get(0);
                            // looking for
                            showQuery = showQuery.replace("show", "");
                            String searchStringNoSpaces = showQuery.replace(" ", "+");

                            //API key
                            String key="AIzaSyCd88YocMmsgCW4RRQE99zt4CcDB3hf84s";

                            //Search Engine ID
                            String cx = "010854282352050238959:tk2utnommbm";

                            String urlString = "https://www.googleapis.com/customsearch/v1?q=" + searchStringNoSpaces + "&key=" + key + "&cx=" + cx + "&alt=json";
                            URL url = null;
                            try {
                                url = new URL(urlString);
                            } catch (MalformedURLException e) {
                                Log.e(TAG, "ERROR converting String to URL " + e.toString());
                            }
                            Log.d(TAG, "Url = "+  urlString);

                            // start AsyncTask
                            GoogleSearchAsyncTask searchTask = new GoogleSearchAsyncTask();
                            searchTask.execute(url);
                        }
                    }
                    break;
                } //Result code for various error.
                else if(resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){
                    showToastMessage("Audio Error");
                } else if(resultCode == RecognizerIntent.RESULT_CLIENT_ERROR){
                    showToastMessage("Client Error");
                } else if(resultCode == RecognizerIntent.RESULT_NETWORK_ERROR){
                    showToastMessage("Network Error");
                } else if(resultCode == RecognizerIntent.RESULT_NO_MATCH){
                    showToastMessage("No Match");
                }else if(resultCode == RecognizerIntent.RESULT_SERVER_ERROR){
                    showToastMessage("Server Error");
                }
               super.onActivityResult(requestCode, resultCode, data);
            }
        }

    }

    void showToastMessage(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private class GoogleSearchAsyncTask extends AsyncTask<URL, Integer, String>{

        protected void onPreExecute(){
            Log.d(TAG, "AsyncTask - onPreExecute");
            // show progressbar
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... urls) {

            URL url = urls[0];
            Log.d(TAG, "AsyncTask - doInBackground, url=" + url);

            // Http connection
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                Log.e(TAG, "Http connection ERROR " + e.toString());
            }

            try {
                responseCode = conn.getResponseCode();
                responseMessage = conn.getResponseMessage();
            } catch (IOException e) {
                Log.e(TAG, "Http getting response code ERROR " + e.toString());
            }

            Log.d(TAG, "Http response code =" + responseCode + " message=" + responseMessage);

            try {
                if(responseCode == 200) {
                    // response OK
                    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = rd.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    rd.close();
                    conn.disconnect();
                    result = sb.toString();
                    Log.d(TAG, "result=" + result);

                    return result;
                }else{
                    // response problem
                    String errorMsg = "Http ERROR response " + responseMessage + "\n" + "Make sure to replace in code your own Google API key and Search Engine ID";
                    Log.e(TAG, errorMsg);
                    result = errorMsg;
                    return  result;

                }
            } catch (IOException e) {
                Log.e(TAG, "Http Response ERROR " + e.toString());
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            Log.d(TAG, "AsyncTask - onProgressUpdate, progress=" + progress);

        }

        protected void onPostExecute(String result) {

            Log.d(TAG, "AsyncTask - onPostExecute, result=" + result);

            // hide progressbar
            progressBar.setVisibility(View.GONE);

            // make TextView scrollable
            answerText.setMovementMethod(new ScrollingMovementMethod());
            // show result
            answerText.setText(result);
        }
    }
}
