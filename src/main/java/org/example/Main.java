package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import com.tsurugidb.tsubakuro.common.Session;
import com.tsurugidb.tsubakuro.common.SessionBuilder;
import com.tsurugidb.tsubakuro.exception.ServerException;
import com.tsurugidb.tsubakuro.sql.ResultSet;
import com.tsurugidb.tsubakuro.sql.SqlClient;
import com.tsurugidb.tsubakuro.sql.Transaction;
import org.json.JSONObject;


public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new Handler());
        server.setExecutor(null);
        System.out.println("server start! port=8000");
        server.start();
    }

    static class Handler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();

            switch (method) {
                case "GET":
                    handleGet(exchange);
                case "POST":
                    handlePost(exchange);
                case "PUT":
                    handlePut(exchange);
                case "DELETE":
                    handleDelete(exchange);
                default:
                    exchange.sendResponseHeaders(405, 0);
            }
        }

        private void handleGet(HttpExchange exchange) throws IOException {
            // WARN: Please Write Your Database Url
            String url = "";
            String response = "";
            try(Session session = SessionBuilder.connect(url).create();
                SqlClient sqlClient = SqlClient.attach(session);
                Transaction transaction = sqlClient.createTransaction().await()) {
                try (ResultSet resultSet = transaction.executeQuery("SELECT * FROM test").await();) {
                    response = resultSet.toString();
                }
            } catch (ServerException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private void handlePost(HttpExchange exchange) throws IOException {
            InputStream is = exchange.getRequestBody();
            String requestBodyString = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            // TODO: Tsurugi Java API Code(insert)

            String response = "POST OK";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private void handlePut(HttpExchange exchange) throws IOException {
            InputStream is = exchange.getRequestBody();
            String requestBodyString = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            // TODO: Tsurugi Java API Code(update)

            String response = "PUT OK";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private void handleDelete(HttpExchange exchange) throws IOException {
            InputStream is = exchange.getRequestBody();
            String requestBodyString = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            // TODO: Tsurugi Java API Code(insert)

            String response = "DELETE OK";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}