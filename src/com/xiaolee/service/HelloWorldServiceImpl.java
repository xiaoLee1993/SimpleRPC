package com.xiaolee.service;

public class HelloWorldServiceImpl implements HelloWorldService{

    @Override
    public String print(String text) {
        StringBuilder sb = new StringBuilder();
        sb.append("service: ") ;
        sb.append(text);
        return sb.toString();
    }
}
