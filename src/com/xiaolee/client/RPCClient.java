package com.xiaolee.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;

public class RPCClient {


    private Integer port;

    private String host;

    public RPCClient(String host , int port) {
        this.port = port;
        this.host = host;
    }


    public <T> T remote(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass },
                (Object proxy, Method method, Object[] args)->{

                    Socket socket = new Socket(host, port);
                    System.out.println(host);
                    try(ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())) {

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
                        socket.close();
                    }
                });

    }

}
