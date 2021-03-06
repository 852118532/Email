package com.example.jack.email;

/*import android.app.Activity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;*/
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ReceiveEmail extends Activity{

    private ListView listView;
    private ArrayAdapter<String> arr_adapter;
    private SimpleAdapter simpleAdapter;
    static  int id=0;
    static Thread thread;
    private static Socket sc;
    private static String POP3Server = "pop.163.com";
    private static String USERNAME ="";
    private static String PASSWORD ="";
    private static int PORT = 110;
    int count;
    String[] arr_data;
    static DataInputStream input;
    static DataOutputStream out;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receivemail);
        listView= (ListView) findViewById(R.id.listView);
        USERNAME =LoginActivity.user+"@163.com";
        PASSWORD = LoginActivity.pwd;
        receive();
        while(thread.isAlive());
        //3、视图(ListView)加载适配器
        listView.setAdapter(arr_adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReceiveEmail.id=count-position;
                Intent intent=new Intent(ReceiveEmail.this,Content.class);
                startActivity(intent);
            }
        });
    }

    private void receive(){
        thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sc = new Socket(POP3Server,PORT);
                    input = new DataInputStream(sc.getInputStream());
                    out = new DataOutputStream(sc.getOutputStream());
                    Log.i("logcat", input.readLine());
                    out.writeBytes("user " + USERNAME + "\r\n");
                    Log.i("logcat", input.readLine());
                    out.writeBytes("pass " + PASSWORD + "\r\n");
                    Log.i("logcat", input.readLine());
                    out.writeBytes("stat" + "\r\n");
                    String temp[] = input.readLine().split(" ");
                    count = Integer.parseInt(temp[1]);

                    //1、新建一个适配器
                    //ArrayAdapter(上下文，当前ListView加载的每一个列表项所对应的布局文件，数据源)
                    //2、适配器加载数据源
                    String[] arr_data=new String[count];
                    for(int i=0;i<arr_data.length;i++){
                        arr_data[i]="第"+(arr_data.length-i)+"封信";
                    }
                    arr_adapter=new ArrayAdapter<String>(ReceiveEmail.this,android.R.layout.simple_list_item_1,arr_data);
                }catch (Exception e){
                    Log.i("logcat",e.toString());
                }
            }
        });
        thread.start();
    }
}
