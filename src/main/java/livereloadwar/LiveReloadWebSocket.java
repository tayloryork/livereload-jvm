package livereloadwar;


import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

@WebSocket
public class LiveReloadWebSocket {
    final ConcurrentLinkedQueue<LiveReloadWebSocket> _broadcast = new ConcurrentLinkedQueue<LiveReloadWebSocket>();
    final LiveReloadProtocol _protocol = new LiveReloadProtocol();
    protected Session _session;
    Watcher _watcher = null;

    private final boolean debug = false;

    @OnWebSocketConnect
    public void doWebSocketConnect(Session session) {

        if(debug) {
            System.out.println("LiveReloadWebSocket: doWebSocketConnect called!");
        }
        _session = session;
        _broadcast.add(this);
        try {
            _watcher = new Watcher((new File("./")).toPath());
            _watcher.listener = this;
            _watcher.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnWebSocketClose
    public void onClose(int code, String message) {
        if(debug){
            System.out.println("LiveReloadWebSocket: onClose called!");
        }
        _broadcast.remove(this);
        try {
            _watcher.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        if(debug){
            System.out.println("LiveReloadWebSocket: onMessage called!");
        }
        try {
            if (_protocol.isHello(message)) {
                session.getRemote().sendString(_protocol.hello());
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    public void notifyChange(String path) throws Exception {
        String msg = _protocol.reload(path);
        for (LiveReloadWebSocket ws : _broadcast) {
            try {
               ws._session.getRemote().sendString(msg);
            } catch (IOException e) {
                _broadcast.remove(ws);
                e.printStackTrace();
            }
        }
    }

}
