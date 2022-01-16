package com.changlu.server.service.rpc;

/**
 * @ClassName RpcService
 * @Author ChangLu
 * @Date 2022/1/16 22:02
 * @Description service接口
 */
public interface RpcService {

    /**
     *
     * @param name 用户名
     * @return hello，用户名！
     */
    String sayHello(String name);

}