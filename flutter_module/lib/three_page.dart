import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class ThreeViewPage extends StatefulWidget {
  @override
  _ThreeViewPageState createState() => _ThreeViewPageState();
}

class _ThreeViewPageState extends State<ThreeViewPage> {
  static const BasicMessageChannel<String> _basicMessageChannel = BasicMessageChannel("android.and.flutter.chanel/plugin", StringCodec());
  TextEditingController _controller;
  String basicMessageChannelMessage = "此时还是空的";
  String showMessage = "";

  @override
  void initState() {
    super.initState();
    _controller = TextEditingController();
    handleMessageChannel();
  }

  @override
  void dispose() {
    // TODO: implement dispose
    super.dispose();
    _controller.dispose();
  }

  // 接收信息
  void handleMessageChannel(){
    _basicMessageChannel.setMessageHandler((String message) => Future<String>((){
      setState(() {
        basicMessageChannelMessage = message;
      });
      return "收到Native的消息：接受成功";
    }));
  }

//  发送信息
  Future<void> handleMessageChannelSend() async{
    String response = await _basicMessageChannel.send(_controller.text ?? "Flutter 端发送过去的数据");
    setState(() {
      showMessage = response;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("这里是flutter three 页面"),
      ),
      body: Column(
        children: <Widget>[
          Text(basicMessageChannelMessage),
          TextField(
              controller:_controller,
              decoration: const InputDecoration(
                hintText: '请输入要发送给Native端的数据',
                contentPadding: const EdgeInsets.all(10.0),
              ),
          ),
          RaisedButton(onPressed: (){
            handleMessageChannelSend();
          }, child: Text("向原生页面发送参数"),),
          Text(showMessage),
        ],
      ),
    );
  }
}
