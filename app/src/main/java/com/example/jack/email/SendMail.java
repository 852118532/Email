package com.example.jack.email;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
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

public class SendMail extends Activity{

    static OutputStream out=null;
    static BufferedReader reader=null;
    static private String FROM="mail from:";//用户名
    static private String TO="rcpt to:";//收件人.
    static private String SUBJECT="";
    static private String from="";
    String line="";
    Socket socket;


    private Button B_mysend;
    private Button B_back;
    private EditText E1;     //收件人地址
    private EditText E2;     //主题
    private EditText E3;     //正文

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendmail);

        out=LoginActivity.out;
        reader=LoginActivity.reader;
        socket=LoginActivity.socket;

        B_mysend=(Button)findViewById(R.id.button_mysend);
        B_back=(Button)findViewById(R.id.button_back);
        E1=(EditText)findViewById(R.id.editText_1);
        E2=(EditText)findViewById(R.id.editText_2);
        E3=(EditText)findViewById(R.id.editText_3);

        B_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2=new Intent(SendMail.this,Main.class);
                startActivity(intent2);
            }
        });

        B_mysend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initial();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        send();
                    }}).start();
            }
        });

    }
    private boolean send(){
        try {
            reLogin();
            out.write(FROM.getBytes("utf-8"));//发件人
            line=reader.readLine();
            Log.i("logcat", "from:" + line);
            if(!line.contains("250")){
                from="";
                Looper.prepare();
                Toast.makeText(SendMail.this, "发送失败，发件人错误", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            out.write(TO.getBytes("utf-8"));//收件人
            line=reader.readLine();
            Log.i("logcat", "to:" + line);
            if(!line.contains("250")){
                from="";
                Looper.prepare();
                Toast.makeText(SendMail.this, "发送失败，收件人错误", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            out.write("data\r\n".getBytes("utf-8"));
            line=reader.readLine();
            Log.i("logcat", line);
            if(!line.contains("354")){
                from="";
                Looper.prepare();
                Toast.makeText(SendMail.this, "发送失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
                return false;
            }
            out.write(("From:" + from+ "\r\n"
                    + "To:"+(E1).getText().toString()+"\r\n"
                    + "Subject:"+SUBJECT).getBytes("UTF-8"));

            out.write(("\r\t" + E3.getText().toString()).getBytes("UTF-8"));

            out.write("\r\n.\r\n".getBytes("UTF-8"));
            line=reader.readLine();
            Log.i("logcat", line);
            if(line.contains("250")){
                from="";
                Looper.prepare();
                Toast.makeText(SendMail.this, "发送成功", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }else{
                from="";
                Looper.prepare();
                Toast.makeText(SendMail.this, "发送失败，垃圾邮件", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
            out.write("QUIT\r\n".getBytes("UTF-8"));//退出登录
            line=reader.readLine();
            out.close();
            reader.close();
            socket.close();
            return true;
        }catch (Exception e) {
            from="";
            Looper.prepare();
            Toast.makeText(SendMail.this, e.toString(), Toast.LENGTH_SHORT).show();
            Looper.loop();
            return false;
        }
    }
    private void reLogin(){
        try{
            socket=new Socket();
            socket.connect(new InetSocketAddress("smtp.163.com", 25), 3000);//链接服务器
            reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out=socket.getOutputStream();
            reader.readLine();
            out.write("helo 163.com\r\n".getBytes("utf-8"));//发送问候消息
            reader.readLine();
            out.write("auth login\r\n".getBytes("utf-8"));//请求登录
            reader.readLine();
            out.write(LoginActivity.NAME_BASE64.getBytes("utf-8"));//发送账户
            reader.readLine();
            out.write(LoginActivity.PASSWORD_BASE64.getBytes("utf-8"));//发送密码
            reader.readLine();
        }catch (Exception e){
            from="";
            Looper.prepare();
            Toast.makeText(SendMail.this, "发送失败", Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
    }
    private void initial(){
        from=((EditText)findViewById(R.id.editText_2)).getText().toString();
        if(!from.contains(LoginActivity.user)){
            from+="<"+LoginActivity.user+"@163.com>";
        }
        FROM="mail from:<"+LoginActivity.user+"@163.com>\r\n";
        TO="rcpt to:<"+(E1).getText()+">\r\n";    //e3是E1，收件人
        SUBJECT=(E2).getText().toString()+"\r\n\r\n";    //e4是E2，主题
    }
}
