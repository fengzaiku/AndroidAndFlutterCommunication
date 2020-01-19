import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class SecondViewPage extends StatelessWidget {
  static const MethodChannel methodChannel = MethodChannel("flutter.to.android/battery");

  Future<void> _flutterToAndroid() async {
    Map<String, String> map = { "flutter": "这是一条来自flutter的参数" };
    await methodChannel.invokeMethod('flutterToAndroid',map);
  }
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("Second page"),
      ),
      body: Column(
        children: <Widget>[
          RaisedButton(onPressed: _flutterToAndroid, child: Text("去原生页面带参数"),)
        ],
      ),
    );
  }
}
