import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Scanner;

public class HTTPRequest {
    //The Client

    String fileName;
    String filetype;

    //readInputStream
    HTTPRequest getHTTPRequest(Socket socket) throws IOException {
        InputStream input = socket.getInputStream();

        Scanner scanner = new Scanner(input); //reads input from server
        String cmd = scanner.next();
        fileName = scanner.next();
        String protocol = scanner.next();

        try {
            filetype = getFiletype(fileName);
        } catch (FileNotFoundException e) {
            e.getMessage();
        }

        scanner.nextLine();
        return this;
    }

    public String getFiletype(String filename) throws FileNotFoundException {
        if (filename.endsWith(".css")) {
            return "text/css";
        } else if (filename.endsWith(".html")) {
            return "text/html";
        } else if (filename.endsWith(".jpeg")){
            return "img/jpeg";
        } else if (filename.endsWith(".png")){
            return "img/png";
        } else {
            throw new FileNotFoundException("400 Bad Access");
        }
    }
}
