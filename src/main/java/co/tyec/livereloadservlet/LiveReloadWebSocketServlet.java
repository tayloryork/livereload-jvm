package co.tyec.livereloadservlet;


import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
@WebServlet(name ="Live Reload WebSocket Servlet",
        initParams = {
                @WebInitParam(name="useFileMappedBuffer", value="false"),
                @WebInitParam(name="dirAllowed", value="true")
        })
public class LiveReloadWebSocketServlet extends WebSocketServlet {

    @Override
    public void configure(WebSocketServletFactory webSocketServletFactory) {
        System.out.println("LiveReloadWebSocketServlet called!");
        webSocketServletFactory.register(LiveReloadWebSocket.class);
    }
}
