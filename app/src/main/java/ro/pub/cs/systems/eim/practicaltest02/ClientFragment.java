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
    private EditText hourEditText, minuteEditText;
    private Button setButton, resetButton, pollButton;
    private TextView serverMessageTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state) {
        return inflater.inflate(R.layout.fragment_client, parent, false);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        hourEditText = (EditText)getActivity().findViewById(R.id.hour_edit_text);
        minuteEditText = (EditText)getActivity().findViewById(R.id.minute_edit_text);

        setButton = (Button)getActivity().findViewById(R.id.set_button);
        setButton.setOnClickListener(new SetButtonClickListener());
        resetButton = (Button)getActivity().findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new ResetButtonClickListener());
        pollButton = (Button)getActivity().findViewById(R.id.poll_button);
        pollButton.setOnClickListener(new PollButtonClickListener());

        serverMessageTextView = (TextView)getActivity().findViewById(R.id.server_message_text_view);
    }

    private void SendCommand(String command) {
        new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Socket socket = new Socket(Constants.SERVER_ADDRESS, Constants.SERVER_PORT);
                    PrintWriter printWriter = Utilities.getWriter(socket);
                    printWriter.println(command);
                    BufferedReader bfr = Utilities.getReader(socket);
                    String response = bfr.lines().collect(Collectors.joining());
                    publishProgress(response);
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onProgressUpdate(String... progress) {
                serverMessageTextView.append(progress[0] + "\n");
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    private class SetButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String hour = hourEditText.getText().toString();
            String minute = minuteEditText.getText().toString();
            SendCommand("set," + hour + "," + minute + "\n");
        }
    }

    private class ResetButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            SendCommand("reset");
        }
    }

    private class PollButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            SendCommand("poll");
        }
    }
}
