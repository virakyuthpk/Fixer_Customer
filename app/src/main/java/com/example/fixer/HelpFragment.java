package com.example.fixer;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.VideoView;

public class HelpFragment extends Fragment {

    EditText editText_Name;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_help, container, false);
        MediaController mc = new MediaController(getActivity());

        editText_Name = rootView.findViewById(R.id.edText_Name);

        VideoView view = (VideoView) rootView.findViewById(R.id.videos);
        String path = "android.resource://" + getActivity().getPackageName() + "/" + R.raw.help;
        view.setVideoURI(Uri.parse(path));
        view.setMediaController(mc);
        view.start();
        return rootView;
    }

}