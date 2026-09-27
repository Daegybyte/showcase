import jdk.jfr.ContentType;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HTTPResponse {
    //Server
    int code;
    byte[] byteStream;
    String contentType;
    String status;


    public HTTPResponse populateHttpResponse(HTTPRequest httpRequest) throws IOException{
        File file = new File("./Resources/" + httpRequest.fileName);
        if(file.exists()){
            byteStream = Files.readAllBytes(Paths.get("./Resources/",httpRequest.fileName));
            code = 200;
            status = " OK";
        } else if(!file.exists()){
            byteStream = Files.readAllBytes(Paths.get("./Resources/","404.html"));
            code = 404;
            status = " Not Found";
        }
        else{
            //TODO FIX bad filetype
            //Issue in getFiletype line 35?
            byteStream = Files.readAllBytes(Paths.get("./Resources/","badFileRequest.html"));
            code = 400;
            status = " Bad Request";
        }

        contentType = httpRequest.filetype;
        return this;
    }

    //response code
    public void sendToUser(HTTPResponse httpResponse, Socket socket) throws IOException {
        OutputStream myOutputStream = socket.getOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
        PrintWriter pw = new PrintWriter(myOutputStream, true);
        pw.println("HTTP/1.1 "+ code + status);
        pw.println("Content-type: " + contentType);

        bufferedOutputStream.write(byteStream);
        bufferedOutputStream.flush();
        pw.flush();
        socket.close();
    }
}
