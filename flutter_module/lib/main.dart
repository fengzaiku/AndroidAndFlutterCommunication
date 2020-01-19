import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_module/fist_page.dart';
import 'package:flutter_module/second_page.dart';
import 'package:flutter_module/three_page.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(window.defaultRouteName,title: 'Flutter Demo Home Page'),
      routes: {
        '/first': (BuildContext context) => FirstViewPage(),
        '/second': (BuildContext context) => SecondViewPage(),
        '/three': (BuildContext context) => ThreeViewPage(),
      },
    );
  }
}

class MyHomePage extends StatefulWidget {
  String path;
  MyHomePage(path,{Key key, this.title}) : super(key: key);

  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const EventChannel eventChannel = EventChannel("android.to.flutter/plugin");
  String _chargingStatus = 'Battery status: unknown.';
  @override
  void initState() {
    super.initState();
    eventChannel.receiveBroadcastStream().listen(_onEventData);
  }

  void _onEventData(Object event){
    setState(() {
      _chargingStatus = "Battery status: ${event.toString() } charging.";
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              'path is ${widget.path}',
              style: Theme.of(context).textTheme.display1,
            ),
            Text(
              '$_chargingStatus',
              style: Theme.of(context).textTheme.display1,
            ),
            RaisedButton(
              child: Text("跳转到first page"),
              onPressed: (){
                Navigator.push(context, MaterialPageRoute(builder: (BuildContext context) => FirstViewPage()));
              },
            ),
            RaisedButton(
              child: Text("跳转到second page"),
              onPressed: () {
                Navigator.pushReplacementNamed(context, '/second');
              },
            ),
          ],
        ),
      )
    );
  }
}
