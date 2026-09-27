# HTTP Server

A basic HTTP server built from scratch in Java using raw sockets, without a web framework.

## Overview

This project handles incoming HTTP requests directly at the socket level. It parses the request, figures out what file is being asked for, reads that file from disk, and sends back an appropriate HTTP response, including the correct status code and content type.

## Features

- Listens for incoming connections on a TCP socket
- Parses HTTP request lines to extract the requested file and method
- Detects content type based on file extension (HTML, CSS, JPEG, PNG)
- Reads the requested file and returns it as the response body
- Returns proper HTTP status codes: 200 for success, 404 for missing files, 400 for bad requests
- Handles multiple client connections at the same time using multithreading

## Architecture

- `Server` accepts incoming connections and hands each one off to its own thread, so the server can accept new connections without waiting for previous ones to finish
- `HTTPRequest` reads the raw request from the socket and figures out what file and file type are being requested
- `HTTPResponse` looks up the requested file, builds the appropriate status code and content type, and writes the response back to the client

## Getting Started

### Prerequisites

- Java 11 or later

### Running the Server

Compile and run:

```bash
javac Server.java HTTPRequest.java HTTPResponse.java
java Server
```

The server listens on port 8080 by default.

### Testing It

With the server running, open a browser and go to:

```
http://localhost:8080/yourfile.html
```

Or use curl:

```bash
curl http://localhost:8080/yourfile.html
```

Files are served from the `Resources` folder, so make sure the file you're requesting exists there.

## Known Limitations

- Only handles GET-style requests for static files, no routing or dynamic content
- No HTTPS support
- Minimal error handling beyond the basic status codes
- Uses a new thread per connection rather than a thread pool, so a very high number of simultaneous connections could create a large number of threads

## Notes

This project was built to understand how HTTP works at a lower level, without relying on a framework to handle requests and responses.
