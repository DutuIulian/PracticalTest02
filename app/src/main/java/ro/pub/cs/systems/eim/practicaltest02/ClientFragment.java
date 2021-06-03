package ro.pub.cs.systems.eim.practicaltest02;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.stream.Collectors;

public class ClientFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state) {
        return inflater.inflate(R.layout.fragment_client, parent, false);
    }
}
