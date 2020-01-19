package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.android.FlutterView;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.embedding.engine.loader.FlutterLoader;
import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.EventChannel.StreamHandler;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.StringCodec;
import io.flutter.plugins.GeneratedPluginRegistrant;
import io.flutter.view.FlutterMain;


public class MainActivity extends AppCompatActivity {
//public class MainActivity extends FlutterActivity {
    private FlutterEngine flutterEngine;
    private FlutterView   flutterView;
    /**
     * 从flutter这边传递数据到Android
     */
    public static final String FLUTTER_TO_ANDROID_CHANNEL = "flutter.to.android/battery";
    /**
     * 从Android这边传递数据到flutter
     */
    public static final String ANDROID_TO_FLUTTER_CHANNEL = "android.to.flutter/plugin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        flutterEngine = new FlutterEngine(this);
//        跳转到指定页面 否则跳转到首页
//        flutterEngine.getNavigationChannel().setInitialRoute("/second");
        flutterEngine.getDartExecutor().executeDartEntrypoint(DartExecutor.DartEntrypoint.createDefault());

        FlutterEngineCache.getInstance().put("my_engine_id", flutterEngine);
        GeneratedPluginRegistrant.registerWith(flutterEngine);
        FlutterFromAndroid();
        androidFromFlutter();
    }

    public void dianji(View view){
        flutterView = new FlutterView(MainActivity.this);
        FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        layout.leftMargin = 0;
        layout.topMargin = 0;
        flutterView.attachToFlutterEngine(flutterEngine);
        addContentView(flutterView, layout);
    }

    public void jumpCurrentInitialRoutePage(View view){
        startActivity(FlutterActivity.withNewEngine().initialRoute("/first").build(this));
    }

    public void jumpCurrentPage(View view){
        Intent intent = FlutterActivity
                .withCachedEngine("my_engine_id")
                .build(this);
        startActivity(intent);
    }

    private BroadcastReceiver createChargingStateChangeReceiver(final EventChannel.EventSink events) {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

                if (status == BatteryManager.BATTERY_STATUS_UNKNOWN) {
                    events.error("UNAVAILABLE", "Charging status unavailable", null);
                } else {
                    boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                            status == BatteryManager.BATTERY_STATUS_FULL;
                    events.success(isCharging ? "charging" : "discharging");
                }
            }
        };
    }

    private void FlutterFromAndroid(){
        new EventChannel(flutterEngine.getDartExecutor(), ANDROID_TO_FLUTTER_CHANNEL).setStreamHandler(
                new StreamHandler (){
                    private BroadcastReceiver chargingStateChangeReceiver;
                    @Override
                    public void onListen(Object arguments, EventChannel.EventSink events) {
                        chargingStateChangeReceiver = createChargingStateChangeReceiver(events);
                        registerReceiver(
                                chargingStateChangeReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
//                        events.success("你好，我来自原生的Android");
                    }

                    @Override
                    public void onCancel(Object arguments) {
                        unregisterReceiver(chargingStateChangeReceiver);
                        chargingStateChangeReceiver = null;
                    }
                }
        );
    }

    private void androidFromFlutter(){
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), FLUTTER_TO_ANDROID_CHANNEL)
                .setMethodCallHandler((call, result) -> {
                    if(call.method.equals("flutterToAndroid")){
                        Object text = call.argument("flutter");
                        if (text instanceof String){
                            //带参数跳转到指定Activity
                            Intent intent = new Intent(MainActivity.this, FlutterAndroid.class);
                            intent.putExtra("params", (String) text);
                            startActivity(intent);
                        }
//                        result.success(100);
                    }
                    if(call.method.equals("getBatteryLevel")){
                        int batteryLevel = getBatteryLevel();
                        result.success(batteryLevel);
                    }
                });
    }


    private int getBatteryLevel() {
        int batteryLevel = -1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
            batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        } else {
            Intent intent = new ContextWrapper(getApplicationContext()).
                    registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            batteryLevel = (intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100) /
                    intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        }

        return batteryLevel;
    }
}
