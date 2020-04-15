package com.xiaolee.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RPCClient {


    private Integer port;

    private String host;

    public RPCClient(String host , int port) {
        this.port = port;
        this.host = host;
    }


    public <T> T remote(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass },
                new InvocationHandler() {

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // TODO Auto-generated method stub
                        Socket socket = new Socket(host, port);
                        System.out.println(host);
                        try {
                            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

                            try {
                                output.writeUTF(interfaceClass.getName());
                                output.writeUTF(method.getName());
                                output.writeObject(method.getParameterTypes());
                                output.writeObject(args);

                                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

                                try {
                                    Object result = input.readObject();
                                    if (result instanceof Throwable) {
                                        throw (Throwable) result;
                                    }
                                    return result;
                                } finally {
                                    input.close();
                                }

                            } finally {
                                output.close();
                            }

                        } finally {
                            socket.close();
                        }
                    }
                });

    }

}
