package com.virtualhuman.voicedictionary.Activity.Fragment;

import android.content.Intent;
import android.app.SearchManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.virtualhuman.voicedictionary.Helper.InternetConnectionHandling;
import com.virtualhuman.voicedictionary.R;

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
    private final int REQ_CODE_SPEECH_INPUT = 100;

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
}
