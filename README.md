Overview
========

A java servlet usefull for dev ONLY :

* serve static file
* notify change to client via LiveReload protocol (over socket)

Usages
======

The default port is not 35729 (like define in the LiveReload protocol). Instead, it uses whatever port your personal webapp uses.
If you change the port then you could not use the [Browsers Extension](http://feedback.livereload.com/knowledgebase/articles/86242-how-do-i-install-and-use-the-browser-extensions-) for LiveReload, but it should works if you insert a [JavaScript snippet](http://go.livereload.com/mobile) in your pages.

Java Webapp Integration
----------------

This is an example WebApp web.xml that configures LiveReload to watch /src/main/webapp for changes.
    <web-app xmlns="http://java.sun.com/xml/ns/javaee" version="3.0">

        <display-name>Your Web Application</display-name>
        <description>Your Apps description</description>

        <servlet>
            <servlet-name>LiveReloadJSServlet</servlet-name>
            <servlet-class>co.tyec.livereloadservlet.LiveReloadJSServlet</servlet-class>
        </servlet>
        <servlet>
            <servlet-name>LiveReloadWebSocketServlet</servlet-name>
            <servlet-class>co.tyec.livereloadservlet.LiveReloadWebSocketServlet</servlet-class>
            <init-param>
                <param-name>watchDir</param-name>
                <param-value>src/main/webapp/</param-value>
            </init-param>
        </servlet>

        <servlet-mapping>
            <servlet-name>LiveReloadJSServlet</servlet-name>
            <url-pattern>/livereload.js</url-pattern>
        </servlet-mapping>
        <servlet-mapping>
            <servlet-name>LiveReloadWebSocketServlet</servlet-name>
            <url-pattern>/livereload</url-pattern>
        </servlet-mapping>
    </web-app>

Links
=====

* [Browsers Extension](http://feedback.livereload.com/knowledgebase/articles/86242-how-do-i-install-and-use-the-browser-extensions-)
* [livereload-js](https://github.com/livereload/livereload-js) the client side
* [LiveReload Protocol](http://feedback.livereload.com/knowledgebase/articles/86174-livereload-protocol)

Alternatives
============

* [LiveReload-JVM](https://github.com/davidB/livereload-jvm/) This project was start with the LiveReload-JVM as the base. It used an embedded standalone jetty webserver instead of integrating with your own webserver.
* [LiveReload 2/3](http://livereload.com/) the main tool (Mac & Windows only) include GUI
* [guard-livereload](https://github.com/guard/guard-livereload) a LiveReload server-side for Guard (Ruby)
* [grunt-reload](https://github.com/webxl/grunt-reload) a LiveReload server-side for Grunt (javascript/nodejs)
* [LivePage](https://chrome.google.com/webstore/detail/livepage/pilnojpmdoofaelbinaeodfpjheijkbh) an other way to "auto-reload"
* without LiveReload : `cd web/root/path && python -m http.server 8000` (python)

License
=======

* the project is under [unlicense](http://unlicense.org/)
* the project (source and binaries) include [livereload.js], livereload.js is under MIT. livereload.js HAS BEEN SLIGHTLY MODIFIED