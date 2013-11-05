package livereloadwar;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by tyork on 11/2/13.
 */
@WebServlet(name = "Live Reload WebSocket Servlet", urlPatterns = "/livereload.js",
        initParams = {
                @WebInitParam(name="useFileMappedBuffer", value="false"),
                @WebInitParam(name="dirAllowed", value="false")
        })
public class LiveReloadJSServlet extends HttpServlet{

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletConfig config = getServletConfig();
        String watchDir = config.getInitParameter("watchDir");

        String requestPath = request.getRequestURI();
        if (requestPath != null){
            if(requestPath.endsWith("/livereload") || requestPath.endsWith("/livereload/")) {


            } else if(requestPath.endsWith("livereload.js") ){
                InputStream jsStream = LiveReloadJSServlet.class.getResourceAsStream("/livereload.js");
                response.setContentType("application/javascript");
                OutputStream output = response.getOutputStream();
                byte[] buffer = new byte[8192];

                for (int length = 0; (length = jsStream.read(buffer)) > 0;) {
                    output.write(buffer, 0, length);
                }

            }
        }

    }
}
