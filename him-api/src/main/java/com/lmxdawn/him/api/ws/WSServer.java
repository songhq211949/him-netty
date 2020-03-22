package com.lmxdawn.him.api.ws;

import com.alibaba.fastjson.JSON;
import com.lmxdawn.him.api.vo.req.WSMessageReqVO;
import com.lmxdawn.him.api.vo.req.WSBaseReqVO;
import com.lmxdawn.him.api.vo.req.WSUserReqVO;
import com.lmxdawn.him.common.protobuf.WSBaseResProtoOuterClass;
import com.lmxdawn.him.common.protobuf.WSMessageResProtoOuterClass;
import com.lmxdawn.him.common.protobuf.WSUserResProtoOuterClass;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.util.Date;

@Component
@Slf4j
public class WSServer {

    @Value("${ws.port}")
    private int wsPort;

    //NioEventLoopGroup 相当于时间循环组  这个组里面包括多个NioEventLoop
    //NioEventLoop包含1个selector 和1个事件循环线程
    //每个boss NioEventLoop循环执行的任务包含3步：
    //第1步：轮询accept事件；
    //第2步：处理io任务，即accept事件，与client建立连接，生成NioSocketChannel，并将NioSocketChannel注册到某个worker NioEventLoop的selector上；
    //第3步：处理任务队列中的任务，runAllTasks。任务队列中的任务包括用户调用eventloop.execute或schedule执行的任务，或者其它线程提交到该eventloop的任务
    //每个worker NioEventLoop循环执行的任务包含3步：
    //第1步：轮询read、write事件；
    //第2步：处理io任务，即read、write事件，在NioSocketChannel可读、可写事件发生时进行处理；
    //第3步：处理任务队列中的任务，runAllTasks
    private EventLoopGroup boss = new NioEventLoopGroup();
    private EventLoopGroup work = new NioEventLoopGroup();

    /**
     * 启动 ws server
     *
     * @return
     * @throws InterruptedException
     */
    @PostConstruct
    public void start() throws InterruptedException {

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(boss, work)
                .channel(NioServerSocketChannel.class)//服务端的父通道
                .localAddress(new InetSocketAddress(wsPort))//监听端口
                //保持长连接
                .childOption(ChannelOption.SO_KEEPALIVE, true)//子通道保持长连接
                .childHandler(new WSServerInitializer());//子通道的

        ChannelFuture future = bootstrap.bind().sync();
        if (future.isSuccess()) {
            log.info("启动 ws server 成功");
        }
    }

    /**
     * 销毁
     */
    @PreDestroy
    public void destroy() {
        boss.shutdownGracefully().syncUninterruptibly();
        work.shutdownGracefully().syncUninterruptibly();
        log.info("关闭 ws server 成功");
    }

    /**
     * 发送 Google Protocol 编码消息
     * @param fromUid 发送给谁
     * @param wsBaseReqVO 消息
     * @return
     */
    public Boolean sendMsg(Long fromUid, WSBaseReqVO wsBaseReqVO) {
        log.info("发送消息给用户，该用户id为：" + fromUid + "发送的消息是"+ JSON.toJSONString(wsBaseReqVO));
        Channel channel = WSSocketHolder.get(fromUid);

        if (null == channel) {
            log.info("用户ID[" + fromUid + "]不在线！");
            return false;
        }
        WSMessageReqVO wsMessageReqVO = wsBaseReqVO.getMessage();
        WSMessageResProtoOuterClass.WSMessageResProto wsMessageResProto = WSMessageResProtoOuterClass.WSMessageResProto.newBuilder()
                .setReceiveId(wsMessageReqVO.getReceiveId())
                .setMsgType(wsMessageReqVO.getMsgType())
                .setMsgContent(wsMessageReqVO.getMsgContent())
                .build();
    
        WSUserReqVO wsUserReqVO = wsBaseReqVO.getUser();
        WSUserResProtoOuterClass.WSUserResProto wsUserResProto = WSUserResProtoOuterClass.WSUserResProto.newBuilder()
                .setUid(wsUserReqVO.getUid())
                .setName(wsUserReqVO.getName())
                .setAvatar(wsUserReqVO.getAvatar())
                .setRemark(wsUserReqVO.getRemark())
                .build();
    
        WSBaseResProtoOuterClass.WSBaseResProto wsBaseResProto = WSBaseResProtoOuterClass.WSBaseResProto.newBuilder()
                .setType(wsBaseReqVO.getType())
                .setMessage(wsMessageResProto)
                .setUser(wsUserResProto)
                .setCreateTime(new Date().toString())
                .build();

        channel.writeAndFlush(wsBaseResProto);
        return true;
    }

}
