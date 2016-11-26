package com.example.jack.email;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Content extends Activity{

    private static DataInputStream input;
    private static DataOutputStream out;
    private TextView tv;
    private int id=ReceiveEmail.id;
    private Thread thread;
    String string;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_layout);
        input=ReceiveEmail.input;
        out=ReceiveEmail.out;
        tv= (TextView) findViewById(R.id.textView4);
        content();
        while(thread.isAlive());
        tv.setText(string);
    }
    private void content(){
        thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    string="";
                    out.writeBytes("retr " + id + "\r\n");
                    //System.out.println("以下为第" + i + "封邮件的内容");
                    // int j=1;
                    while (true) {
                        String reply = input.readLine();
                        // System.out.println((j++)+reply);
                        string+=reply;
                        if (reply.toLowerCase().equals(".")) {
                            break;
                        }
                    }
                }catch (Exception e){
                    Log.i("logcat",e.toString());
                }
            }
        });
        thread.start();
    }
}
