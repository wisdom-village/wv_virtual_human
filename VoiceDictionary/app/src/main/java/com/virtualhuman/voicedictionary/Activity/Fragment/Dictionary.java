package com.virtualhuman.voicedictionary.Activity.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.virtualhuman.voicedictionary.Helper.InternetConnectionHandling;
import com.virtualhuman.voicedictionary.R;

/**
 * Dictionary Fragment
 * Created by minthiri
 */
public class Dictionary extends Fragment {

    public static ImageView micIcon;
    public static TextView micHintText;
    public static TextView answerText;

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

}
