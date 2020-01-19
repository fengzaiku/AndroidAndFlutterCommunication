package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
//import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import io.flutter.embedding.android.FlutterView;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.StringCodec;

public class FlutterAndroid extends AppCompatActivity {
    private FlutterEngine flutterEngine;
    private FlutterView   flutterView;
    TextView     textView;
    Button       sendButton;
    EditText     editTextInput;
    /**
     * 应用场景：以前两种都不一样，互相调用
     */
    public static final String ANDROID_AND_FLUTTER_CHANNEL = "android.and.flutter.chanel/plugin";

    BasicMessageChannel<String> basicMessageChannel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flutter_android);
        flutterEngine = new FlutterEngine(this);
        flutterEngine.getNavigationChannel().setInitialRoute("/three");
        flutterEngine.getDartExecutor().executeDartEntrypoint(DartExecutor.DartEntrypoint.createDefault());

        FrameLayout  frameLayout = findViewById(R.id.flutterPage1);
        textView = findViewById(R.id.textView4);
        sendButton = findViewById(R.id.button2);
        editTextInput = findViewById(R.id.editText1);
        flutterView = new FlutterView(FlutterAndroid.this);
        flutterView.attachToFlutterEngine(flutterEngine);
        frameLayout.addView(flutterView);

        //Initialize BasicMessageChannel
        basicMessageChannel = new BasicMessageChannel(flutterEngine.getDartExecutor().getBinaryMessenger(),ANDROID_AND_FLUTTER_CHANNEL, StringCodec.INSTANCE);


        String params = getIntent().getStringExtra("params");
        if (!TextUtils.isEmpty(params)) {
            textView.setText("flutter 传参到Android普通字符串:" + params);
        }
        sendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String text = editTextInput.getText().toString();
                sendBasichandleInfo(text);
            }
        });

        acceptanceBasichandleInfo();
    }

    public void sendBasichandleInfo(String message){
        basicMessageChannel.send("我是Native发送过来的数据"+ message, reply -> {
            textView.setText("发送数据:" + reply);
        });
    }

    private void acceptanceBasichandleInfo() {
        basicMessageChannel.setMessageHandler((message, reply) -> {
            textView.setText("接收到的数据:" + message);
            reply.reply("我是Native端返回的成功提示！");
        });
    }
}
