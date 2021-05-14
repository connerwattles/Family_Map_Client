package Tasks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.familymapclient.DataCache;
import com.example.familymapclient.R;
import com.example.familymapclient.ServerProxy;

import java.net.MalformedURLException;
import java.net.URL;

import Request.RegisterRequest;
import Result.LoginResult;
import Result.RegisterResult;

public class RegisterTask implements Runnable {
    private static final String SUCCESS_KEY = "success key";
    private static final String TOKEN_KEY = "token key";

    private RegisterRequest registerRequest;
    private Handler messageHandler;
    private String serverHost;
    private String serverPort;

    public RegisterTask(Handler message, RegisterRequest request, String host, String port) {
        this.messageHandler = message;
        this.registerRequest = request;
        this.serverHost = host;
        this.serverPort = port;
    }

    @Override
    public void run() {
        String urlString = "http://" + serverHost + ":" + serverPort + "/user/register";
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            RegisterResult result = new RegisterResult();
            result.setSuccess(false);
            result.setMessage("Invalid Host/Port");
            sendMessage(result.getSuccess(), null);
        }

        ServerProxy proxy = new ServerProxy();

        RegisterResult result = proxy.register(url, this.registerRequest);

        DataCache dataCache = DataCache.getInstance();

        dataCache.setCurrUsername(result.getUsername());
        dataCache.setCurrPersonID(result.getPersonID());
        dataCache.setCurrAuthToken(result.getAuthToken());

        sendMessage(result.getSuccess(), result.getAuthToken());
    }

    private void sendMessage(boolean success, String token) {
        Message message = Message.obtain();

        Bundle messageBundle = new Bundle();
        messageBundle.putBoolean(SUCCESS_KEY, success);
        messageBundle.putString(TOKEN_KEY, token);
        message.setData(messageBundle);

        messageHandler.sendMessage(message);
    }
}
