package com.xiaolee.service;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class RPCHandle {


    private Integer port;

    private Map<Class,Object> map = new HashMap<>(2);

    public RPCHandle(int port) {
        this.port = port;
        map.put(HelloWorldService.class,new HelloWorldServiceImpl());
    }


    public void handle() {
        ExecutorService thread = Executors.newSingleThreadExecutor();

        thread.execute(() -> {
            ServerSocket server = null;
            try {
                server = new ServerSocket(port);

                for (; ; ) {
                    final Socket socket = server.accept();
                    System.out.println("111");
                    try {
                        new Thread(() -> {
                            try {

                                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

                                try {
                                    String interfaceName = input.readUTF();
                                    System.out.println(interfaceName);
                                    String methodName = input.readUTF();
                                    System.out.println(methodName);
                                    Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
                                    Object[] arguments = (Object[]) input.readObject();
                                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

                                    try {
                                        Object service = map.get(Class.forName(interfaceName));
                                        Method method = service.getClass().getMethod(methodName, parameterTypes);
                                        Object result = method.invoke(service, arguments);
                                        output.writeObject(result);
                                    } catch (Throwable t) {
                                        output.writeObject(t);
                                    } finally {
                                        output.close();
                                    }
                                } finally {
                                    input.close();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                //socket.close();
                            }

                        }).start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            } catch (IOException e) {
                System.out.println("socket server 启动失败!");
                Thread.currentThread().interrupt();
            }


        });
    }

}
