# simpleDownloadServer
Simple Java server that exposes a folder's contents for download, initially set up as a TF2 map download server

#### Basic setup

1. Open/Import project in Eclipse
2. Open the project's properties
3. Under "Project Facets", make sure "Dynamic Web Module" and "Java" are checked. Close properties.
4. You *might* need to go to Build Path and configure the jre and Apache Tomcat versions if you don't have jdk1.8 and Tomcat v8.0 set up.
5. In the "Servers" view, create a new server.
6. Right-click the server and select "Add and Remove..."
7. Select your project on the left side and click "Add >", then "Finish".
8. Double-click the server to open its config editor.
9. Set the ports. The HTTP/1.1 port is the one we need to remember. I like using port 666 for it (it's Doom's official multiplayer server port).
10. At the bottom of the server config editor, select the "Modules" tab.
11. Select your simpleDownloadServer module and click "Edit"
12. Change the path to something like "/maps" (it won't accept "/" or "" for some reason). Save.
13. Open `web.xml` and set the two `<init-param>` values:
  - `rootFolder` should be the folder you want to expose for download. For a TF2 server, this should be the server's "maps" folder, e.g. `C:\tf2-server\server\tf\maps\`.
  - `allowedFileNameRegex` is the RegExp String used to determine which file names are allowed. `([^\s]+(\.(?i)(bsp|nav|ztmp|bz2))$)` allows TF2 map, bot-nav, temp, and compressed map files. `[^\s]+` allows any non-empty, non-whitespace filename, ie just about anything.
13. Build your project, and start your server.
14. Enter a url on your server that would access a file in the configured folder. If all goes correctly, your browser will download the requested file.

#### TF2 Map File Server

To use it as a map file server alongside a TF2 game server:

1. Edit your `server.cfg` file.
2. Set `sv_allowdownload 1` and add an entry for `sv_downloadurl`
3. Get your server's ip address. If you're not sure how, this works for LAN:
  1. Run `cmd`
  2. Type `ipconfig`.
  3. Under "Wireless LAN" or "Ethernet adapter", find `IPv4 Address` - this is the ip you want for LAN.
4. Combine "http://"s plus your ip address, plus your server's port number, plus the module's path. E.g. `http://123.46.78.90:666/maps`
5. Put that url as the value for `sv_downloadurl`
6. Start up your file server and your TF2 server.
7. When a player needs to download a map, it should try to get it through the file server, which will serve up the files much faster than the game server.
