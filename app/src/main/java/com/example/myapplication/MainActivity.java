package com.example.myapplication;
import com.example.myapplication.MainActivity;
import android.content.Intent;


import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import android.content.Context;
import android.net.wifi.WifiManager;

import java.net.InetAddress;
import java.net.UnknownHostException;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import android.util.Log;
import android.app.AlertDialog;
import android.content.DialogInterface;





public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";
    private static final String LOGIN_URL = "http://10.0.3.2";
    private EditText usernameEditText;
    private EditText passwordEditText;

    private WebView webView;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String ipAddress = getDeviceIpAddress(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("用户须知");
        builder.setMessage("\n" +
                "\n" +
                "    1.本软件用于自动连接广东工业大学校园网，方便用户快速访问网络资源。\n" +
                "\n" +
                "    2.本软件不会泄漏您的账号和密码，具有较高的安全性。用户的账号和密码仅会保存在手机本地，不会上传到任何服务器。\n" +
                "\n" +
                "    3.如果是第一次使用本软件，请确保您已连接到广东工业大学校园网的Wi-Fi网络。在此情况下，您需要输入您的用户名和密码，并确保输入的信息准确无误。\n" +
                "\n" +
                "    4.在输入用户名和密码后，点击登录按钮即可完成登录操作。之后的使用中，您只需要打开本软件，它将自动完成登录操作，无需再次输入用户名和密码。\n");


        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // 用户点击确定按钮后的操作
                dialog.dismiss(); // 关闭对话框
            }
        });




        AlertDialog dialog = builder.create();
        dialog.show();





        String filesDirPath = getFilesDir().getAbsolutePath();




        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                super.onPageFinished(view, url);
                fillLoginForm(username, password);
            }
        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        WebView backgroundWebView = findViewById(R.id.backgroundWebView);
        backgroundWebView.loadUrl("file:///android_asset/index.html");

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                } else {
                    // 保存用户信息
                    saveUserInfo(username, password);


                    // 提交登录请求
                    String url = "http://10.0.3.2:801/eportal/portal/login?callback=dr1003&login_method=1&user_account=,0,"+encodeValue(username) + "&user_password=" + encodeValue(password)+"&wlan_user_ip="+ ipAddress +"&wlan_user_ipv6=&wlan_user_mac=000000000000&wlan_ac_ip=10.0.3.58&wlan_ac_name=&jsVersion=&terminal_type=1&lang=zh-cn&lang=zh&v=";

                    Log.d("MainActivity", "URL: " + url);
                    webView.loadUrl(url);
                }
            }
        });



        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);


        String[] userInfo = loadUserInfo();
        if (userInfo != null) {
            String username = userInfo[0];
            String password = userInfo[1];

            // 已保存用户信息，直接提交登录请求
            String url = "http://10.0.3.2:801/eportal/portal/login?callback=dr1003&login_method=1&user_account=,0,"+encodeValue(username) + "&user_password=" + encodeValue(password)+"&wlan_user_ip="+ ipAddress +"&wlan_user_ipv6=&wlan_user_mac=000000000000&wlan_ac_ip=10.0.3.58&wlan_ac_name=&jsVersion=&terminal_type=1&lang=zh-cn&lang=zh&v=";

            Log.d("MainActivity", "URL: " + url);
            webView.loadUrl(url);
        } else {
            // 第一次打开软件，需要用户输入账号密码
            // TODO: 显示输入账号密码的界面，并保存用户信息
        }
    }

    public void submitUrl(View view) {
        // 在这里添加自动提交URL的代码
        String[] userInfo = loadUserInfo();
        String ipAddress = getDeviceIpAddress(this);
        if (userInfo != null) {
            String username = userInfo[0];
            String password = userInfo[1];

            // 已保存用户信息，直接提交登录请求
            String url = "http://10.0.3.2:801/eportal/portal/login?callback=dr1003&login_method=1&user_account=,0,"+encodeValue(username) + "&user_password=" + encodeValue(password)+"&wlan_user_ip="+ ipAddress +"&wlan_user_ipv6=&wlan_user_mac=000000000000&wlan_ac_ip=10.0.3.58&wlan_ac_name=&jsVersion=&terminal_type=1&lang=zh-cn&lang=zh&v=";

            Log.d("MainActivity", "URL: " + url);
            webView.loadUrl(url);
        }
    }

    private String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return value;
        }
    }
    private String getDeviceIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        // 将整数形式的IP地址转换为字符串形式
        String ipString = formatIpAddress(ipAddress);
        return ipString;
    }

    private String formatIpAddress(int ipAddress) {
        byte[] ipByteArray = intToByteArray(ipAddress);
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByAddress(ipByteArray);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "";
        }
        return inetAddress.getHostAddress();
    }

    private byte[] intToByteArray(int value) {
        return new byte[] {
                (byte) (value & 0xff),
                (byte) ((value >> 8) & 0xff),
                (byte) ((value >> 16) & 0xff),
                (byte) ((value >> 24) & 0xff)
        };
    }
    String FILENAME = "my_file.txt";
    private void saveUserInfo(String username, String password) {
        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            String userInfo = username + "," + password;
            fos.write(userInfo.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] loadUserInfo() {
        try {
            FileInputStream fis = openFileInput(FILENAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String userInfo = br.readLine();
            br.close();
            isr.close();
            fis.close();

            if (userInfo != null) {
                String[] userInfoArray = userInfo.split(",");
                String username = userInfoArray[0];
                String password = userInfoArray[1];

                Log.d("LoadUserInfo", "Username: " + username);
                Log.d("LoadUserInfo", "Password: " + password);

                return new String[]{username, password};
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    private void fillLoginForm(String username, String password) {
        String jsCode = "javascript:" +
                "document.getElementsByName('DDDDD')[0].value = '" + username + "';" +
                "document.getElementsByName('upass')[0].value = '" + password + "';" +
                "document.getElementsByName('0MKKey')[0].click();";
        webView.evaluateJavascript(jsCode, null);
    }
}