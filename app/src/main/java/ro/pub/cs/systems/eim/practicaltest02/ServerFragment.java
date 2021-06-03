package ro.pub.cs.systems.eim.practicaltest02;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ServerFragment extends Fragment {
    private ServerThread serverThread;
    private TextView serverLogTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state) {
        return inflater.inflate(R.layout.fragment_server, parent, false);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        serverLogTextView = (TextView)getActivity().findViewById(R.id.server_log_text_view);
        serverThread = new ServerThread(serverLogTextView);
        serverThread.startServer();
    }

    @Override
    public void onDestroy() {
        if (serverThread != null) {
            serverThread.stopServer();
        }
        super.onDestroy();
    }
}
