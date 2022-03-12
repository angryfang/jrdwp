package com.example.server.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * origin demo:https://www.cnblogs.com/xuwenjin/p/12664650.html
 * 调试服务，接受websocket请求
 */
@Slf4j
@ServerEndpoint(value = "/debug/{hostport}")
@Component
public class DebugWebSocket {

    /**
     * 记录当前在线连接数
     */
    private static AtomicInteger onlineCount = new AtomicInteger(0);
    private static ConcurrentHashMap<String, DebugWebSocket> webSocketMap = new ConcurrentHashMap<>();
    private String hostAndPort;
    private Session session;
    private TCPClient tcpClient;

    private void closeTcpClient(String hostAndPort) {
        if(webSocketMap.containsKey(hostAndPort)) {
            try {
                webSocketMap.get(hostAndPort).tcpClient.close();
                Thread.sleep(200L);
            }catch (Exception ex){
                // do nothing
            }
        }
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session,@PathParam("hostport") String hostAndPort) {
        onlineCount.incrementAndGet(); // 在线数加1
        log.info("有新连接加入：{}，当前在线人数为：{}, hostAndPort:{}", session.getId(), onlineCount.get(), hostAndPort);
        String[] arr = hostAndPort.split("_");
        this.hostAndPort = hostAndPort;
        this.session = session;
        closeTcpClient(hostAndPort);
        this.tcpClient = new TCPClient(arr[0], Integer.valueOf(arr[1])) {
            @Override
            protected void onDataReceive(byte[] bytes, int size) {
                printlog(hostAndPort, bytes, size);
                ByteBuffer byteBuffer = ByteBuffer.wrap(bytes, 0, size);
                sendMessage(byteBuffer, session);
            }
        };
        tcpClient.connect();
        webSocketMap.put(hostAndPort, this);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        onlineCount.decrementAndGet(); // 在线数减1
        closeTcpClient(hostAndPort);
        webSocketMap.remove(hostAndPort);
        log.info("有一连接关闭：{}，当前在线人数为：{}, hostAndPort:{}", session.getId(), onlineCount.get(), hostAndPort);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param bytes 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(byte[] bytes, Session session) {
        printlog("ws", bytes, bytes.length);
        if(tcpClient.isConnected()) {
            tcpClient.send(bytes);
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误", error);
        try {
            session.close();
        } catch (IOException e) {
            // do nothing
        }
        closeTcpClient(hostAndPort);
        webSocketMap.remove(hostAndPort);
    }

    private void printlog(String from, byte[] bytes, int size) {
//        log.info("收到[{}]的消息byte:{}", from, bytes);
        try {
            String message = new String(bytes, 0, size, "utf-8");
//            log.info("收到[{}]的消息:{}", from, message);
        } catch (Exception ex) {
            // do nothing
        }
    }

    /**
     * 服务端发送消息给客户端
     */
    private void sendMessage(ByteBuffer data, Session toSession) {
        try {
            toSession.getBasicRemote().sendBinary(data);
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败：{}", e);
        }
    }
}
