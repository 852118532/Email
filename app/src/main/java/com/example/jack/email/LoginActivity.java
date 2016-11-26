package com.example.jack.email;

import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import Decoder.BASE64Encoder;

import static android.R.attr.tag;


public class LoginActivity extends AppCompatActivity {

    public EditText username;
    public EditText password;
    private Button B_Login;
    private Button B_AC;
    static OutputStream out=null;
    static BufferedReader reader=null;
    static Socket socket;
    static String user=null;
    static String pwd=null;
    static  String NAME_BASE64="";//用户名 base64编码
    static  String PASSWORD_BASE64="";//密码base64编码
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username=(EditText)findViewById(R.id.editText_username);
        password=(EditText)findViewById(R.id.editText2);
        B_Login=(Button)findViewById(R.id.button_Login);
        B_AC=(Button) findViewById(R.id.button_ALLCLEAN);
        B_AC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username.setText("");
                password.setText("");
            }
        });
        B_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socket=new Socket();
                encode();
                new Thread(new Runnable() {
                    public void run() {
                        if (!login()) {
                            return;
                        }
                    }
                }).start();
            }
        });
    }
    private boolean login(){
        try{
            String line=null;
            if(!socket.isConnected()){
                socket.connect(new InetSocketAddress("smtp.163.com", 25), 3000);//链接服务器
                reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out=socket.getOutputStream();
                line=reader.readLine();
                Log.i("logcat", line);
                if(!line.contains("220")){
                    Looper.prepare();
                    Toast.makeText(LoginActivity.this, "登录失败，未连接服务器", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    return false;
                }
            }
            out.write("helo 163.com\r\n".getBytes("utf-8"));//发送问候消息
            line=reader.readLine();
            Log.i("logcat", line);
            if(!line.contains("250")){
                Looper.prepare();
                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                Looper.loop();                    return false;
            }

            out.write("auth login\r\n".getBytes("utf-8"));//请求登录
            line=reader.readLine();
            Log.i("logcat", line);
            if(!line.contains("334")){
                Looper.prepare();
                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
                return false;
            }
            out.write(NAME_BASE64.getBytes("utf-8"));//发送账户
            line=reader.readLine();
            Log.i("logcat", line);
            if(!line.contains("334")){
                Looper.prepare();
                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                Looper.loop();                return false;
            }
            out.write(PASSWORD_BASE64.getBytes("utf-8"));//发送密码
            line=reader.readLine();
            Log.i("logcat", line);
            if(!line.contains("successful")){
                Looper.prepare();
                Toast.makeText(LoginActivity.this, "登录失败，账户或密码错误", Toast.LENGTH_SHORT).show();
                Looper.loop();
                return false;
            }else{
                Intent intent = new Intent(LoginActivity.this, Main.class);
                startActivity(intent);
                Looper.prepare();
                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                Looper.loop();
                out.write("QUIT\r\n".getBytes("UTF-8"));//退出登录
                line=reader.readLine();
                out.close();
                reader.close();
                socket.close();
            }
        }catch (Exception e){
//            Looper.prepare();
//            Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
//            Looper.loop();
            Log.i("logcat", e.toString());
            return false;
        }

        return true;
    }
    private void encode(){
        user=username.getText().toString();
        pwd=password.getText().toString();
        BASE64Encoder decode=new BASE64Encoder();
        try {
            NAME_BASE64= decode.encode(user.getBytes()).toString()+"\r\n";
            PASSWORD_BASE64=decode.encode(pwd.getBytes()).toString()+"\r\n";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
