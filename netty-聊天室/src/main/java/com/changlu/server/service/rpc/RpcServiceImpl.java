package com.changlu.server.service.rpc;

/**
 * @ClassName RpcSerivceImpl
 * @Author ChangLu
 * @Date 2022/1/16 22:03
 * @Description RpcService实现类
 */
public class RpcServiceImpl implements RpcService{

    @Override
    public String sayHello(String name) {
        int i = 1/0;  //测试抛出异常
        return "hello," + name + "!";
    }
}