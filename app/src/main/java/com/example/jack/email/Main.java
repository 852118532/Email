package com.example.jack.email;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Main extends Activity{

    private Button B_Send;
    private Button B_Recive;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layoutmain);
        B_Send=(Button)findViewById(R.id.button_send);
        B_Recive=(Button)findViewById(R.id.button_recive);
        B_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Main.this,SendMail.class);
                startActivity(intent);
            }
        });
        B_Recive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(Main.this,ReceiveEmail.class);
                startActivity(intent1);
            }
        });
    }
}
