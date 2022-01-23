package com.example.demo.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author nuc
 * @date 前端通过此URL与后端建立连接
 */
@ServerEndpoint("/webSocket/{uid}")
@Component
public class WebSocketServer {

    public static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);

    // 当前连击数量
    private static final AtomicInteger onlineNum = new AtomicInteger(0);

    // concurrent包的线程安全Set，用来存放每个客户端对应的WebSocketServer对象。
    private static ConcurrentHashMap<String,Session> sessions = new ConcurrentHashMap<>();

    // 双向删除数据结构！！！为将来但对单发消息做处理
    private static ConcurrentHashMap<String,String> km = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam(value = "uid") String uid){
        sessions.put(session.getId(),session);
        km.put(uid,session.getId());
        onlineNum.incrementAndGet();
        log.info("{}加入了团队，当前人数:{}",uid,onlineNum);
    }

    @OnClose
    public void onClose(Session session){
        sessions.remove(session.getId());
        int cnt = onlineNum.decrementAndGet();
        log.info("一个连接退出，当前连接人数：{}",cnt);
    }

    @OnError
    public void onError(Session session, Throwable throwable){
        log.error("异常发生",throwable.getMessage());
        throwable.printStackTrace();
    }

    /**
     * 收到客户端消息后调用的方法
     * 发送消息,发自己
     */
    public void sendMessage(Session session, String message) throws IOException {
        if (session != null){
            synchronized (session){
                session.getBasicRemote().sendText(message);
            }
        }
    }

    /**
     * 群发消息,不包括自己
     */
    @OnMessage
    public void putAllInfo(String messsage,Session session) throws IOException {
        for (Map.Entry<String, Session> sessionEntry : sessions.entrySet()) {
            Session s = sessionEntry.getValue();
            if (s.isOpen() && !s.getId().equals(session.getId())){
                sendMessage(s,messsage);
            }
        }
    }

    public void putAllInfo(String messsage) throws IOException {
        for (Map.Entry<String, Session> sessionEntry : sessions.entrySet()) {
            Session s = sessionEntry.getValue();
            if (s.isOpen()){
                sendMessage(s,messsage);
            }
        }
    }
}
