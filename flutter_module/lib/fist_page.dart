//import 'package:flutter/material.dart';
//
//class FirstViewPage extends StatelessWidget {
//
//}
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
class FirstViewPage extends StatefulWidget {
  @override
  _FirstViewPageState createState() => _FirstViewPageState();
}

class _FirstViewPageState extends State<FirstViewPage> {
  String _batteryLevel = 'Battery level: unknown.';
  static const MethodChannel methodChannel = MethodChannel("flutter.to.android/battery");
  Future<void> _getBatteryLevel() async {
    String batteryLevel;
    try {
//      final int result = await methodChannel.invokeMethod('flutterToAndroid');
      final int result = await methodChannel.invokeMethod('getBatteryLevel');
      batteryLevel = 'Battery level: $result%.';
    } on PlatformException catch (e) {
      batteryLevel = 'Failed to get battery level.';
    }
    setState(() {
      _batteryLevel = batteryLevel;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("First page"),
      ),
      body: Column(
        children: <Widget>[
          Text(_batteryLevel),
          RaisedButton(onPressed: _getBatteryLevel, child: Text("获取电量"),)
        ],
      ),
    );
  }
}
