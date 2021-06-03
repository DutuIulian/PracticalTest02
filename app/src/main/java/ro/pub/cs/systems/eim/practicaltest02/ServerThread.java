package ro.pub.cs.systems.eim.practicaltest02;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ServerThread extends Thread {

    private boolean isRunning;
    private ServerSocket serverSocket;
    private TextView serverLogTextView;
    private Map<String, AlarmInfo> alarmInfoMap;

    public ServerThread(TextView serverLogTextView) {
        this.serverLogTextView = serverLogTextView;
    }

    public void startServer() {
        serverLogTextView.append("The server has started\n");
        isRunning = true;
        alarmInfoMap = new HashMap<>();
        start();
    }

    public void stopServer() {
        isRunning = false;
        try {
            if(serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(Constants.SERVER_PORT);
            while (isRunning) {
                Socket socket = serverSocket.accept();
                BufferedReader bfr = Utilities.getReader(socket);
                PrintWriter bfw = Utilities.getWriter(socket);

                String command = bfr.readLine();
                String clientAddress = socket.getInetAddress().getHostName();
                if(command.startsWith("set")) {
                    bfw.println(handleSetCommand(clientAddress, command));
                } else if(command.startsWith("reset")) {
                    bfw.println(handleResetCommand(clientAddress));
                } else if(command.startsWith("poll")) {
                    bfw.println(handlePollCommand(clientAddress));
                } else {
                    bfw.println("Wrong command!\n");
                }
                socket.close();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    String handleSetCommand(String clientAddress, String command) {
        int pos1 = command.indexOf(',');
        int pos2 = command.indexOf(',', pos1 + 1);

        String hour = command.substring(pos1 + 1, pos2);
        String minute = command.substring(pos2 + 1);
        alarmInfoMap.put(clientAddress, new AlarmInfo(Integer.parseInt(hour), Integer.parseInt(minute)));
        return "The alarm has been set for " + hour + ":" + minute + "\n";
    }

    String handleResetCommand(String clientAddress) {
        alarmInfoMap.remove(clientAddress);
        return "The alarm has been reset\n";
    }

    String handlePollCommand(String clientAddress) {
        AlarmInfo alarmInfo = alarmInfoMap.get(clientAddress);
        if(alarmInfo == null) {
            return "none\n";
        }

        if(isTimeExpired(alarmInfo)) {
            return "active\n";
        }

        return "inactive\n";
    }

    private boolean isTimeExpired(AlarmInfo alarmInfo) {
        if(alarmInfo.isTimeExpired()) {
            return true;
        }

        try {
            String address = "utcnist.colorado.edu";
            int port = 13;
            Socket socket = new Socket(address, port);
            BufferedReader bfr = Utilities.getReader(socket);
            String response = bfr.lines().collect(Collectors.joining());
            socket.close();

            if(response == null || response.isEmpty()) {
                Log.e("Server", "Empty response!");
                return false;
            }

            int posDate = response.indexOf(' ') + 1;
            int posTime = response.indexOf(' ', posDate) + 1;
            int hour = Integer.parseInt(response.substring(posTime, posTime + 2));
            int minute = Integer.parseInt(response.substring(posTime + 3, posTime + 5));

            if(hour > alarmInfo.getHour()) {
                alarmInfo.setTimeExpired(true);
                return true;
            }

            if(hour == alarmInfo.getHour() && minute >= alarmInfo.getMinute()) {
                alarmInfo.setTimeExpired(true);
                return true;
            }

            return false;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Server", "Error!");
            return false;
        }
    }
}
