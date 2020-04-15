package com.xiaolee.client;

import com.xiaolee.service.HelloWorldService;

public class Client {
    public static void main(String[] args) {
        RPCClient client = new RPCClient("127.0.0.1",9999);
        HelloWorldService service = client.remote(HelloWorldService.class);
        String result = service.print("test RPC");
        System.out.println(result);

    }
}
