package server;

import com.sun.net.httpserver.*;
import domain.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

public class WebServer {
    private final EstoqueDatabase db = new EstoqueDatabase();
    private final int port;

    public WebServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // REST APIs
        server.createContext("/api/dashboard", exchange -> sendJsonResponse(exchange, db.getDashboardStatsJson()));

        server.createContext("/api/produtos", exchange -> {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                StringBuilder json = new StringBuilder("[");
                int i = 0;
                Collection<Produto> list = db.getProdutos();
                for (Produto p : list) {
                    json.append(p.toJson());
                    if (++i < list.size()) json.append(",");
                }
                json.append("]");
                sendJsonResponse(exchange, json.toString());
            } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                // Parse simples para novos produtos
                String id = "PRD-" + (db.getProdutos().size() + 1);
                String nome = extractJsonField(body, "nome");
                String cat = extractJsonField(body, "categoria");
                double preco = Double.parseDouble(extractJsonField(body, "preco"));
                int min = Integer.parseInt(extractJsonField(body, "estoqueMinimo"));

                Produto newP = new Produto(id, nome, cat, preco, min);
                db.addProduto(newP);
                sendJsonResponse(exchange, newP.toJson());
            }
        });

        server.createContext("/api/produtos/movimentar", exchange -> {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                String id = extractJsonField(body, "id");
                String tipo = extractJsonField(body, "tipo");
                int quant = Integer.parseInt(extractJsonField(body, "quantidade"));

                Produto p = db.getProduto(id);
                if (p != null) {
                    if ("ENTRADA".equalsIgnoreCase(tipo)) {
                        p.adicionarEstoque(quant);
                    } else if ("SAIDA".equalsIgnoreCase(tipo)) {
                        p.removerEstoque(quant);
                    }
                    sendJsonResponse(exchange, p.toJson());
                } else {
                    sendResponse(exchange, 404, "{\"error\":\"Produto nao encontrado\"}", "application/json");
                }
            }
        });

        server.createContext("/api/funcionarios", exchange -> {
            StringBuilder json = new StringBuilder("[");
            int i = 0;
            Collection<Funcionario> list = db.getFuncionarios();
            for (Funcionario f : list) {
                json.append(f.toJson());
                if (++i < list.size()) json.append(",");
            }
            json.append("]");
            sendJsonResponse(exchange, json.toString());
        });

        // Static Web UI Files
        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/") || path.equals("/index.html")) {
                serveStaticFile(exchange, "/web/index.html", "text/html");
            } else if (path.equals("/style.css")) {
                serveStaticFile(exchange, "/web/style.css", "text/css");
            } else if (path.equals("/app.js")) {
                serveStaticFile(exchange, "/web/app.js", "text/javascript");
            } else {
                sendResponse(exchange, 404, "404 Not Found", "text/plain");
            }
        });

        server.setExecutor(null);
        System.out.println("==========================================================");
        System.out.println(" 🚀 SISTEMA GALPÃO ENTERPRISE RODANDO NA PORTA " + port);
        System.out.println(" 🌐 Acesse a interface web em: http://localhost:" + port);
        System.out.println("==========================================================");
        server.start();
    }

    private void sendJsonResponse(HttpExchange exchange, String json) throws IOException {
        sendResponse(exchange, 200, json, "application/json; charset=utf-8");
    }

    private void sendResponse(HttpExchange exchange, int status, String response, String contentType) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(status, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private void serveStaticFile(HttpExchange exchange, String resourcePath, String contentType) throws IOException {
        InputStream is = getClass().getResourceAsStream(resourcePath);
        if (is == null) {
            sendResponse(exchange, 404, "File not found", "text/plain");
            return;
        }
        byte[] bytes = is.readAllBytes();
        exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=utf-8");
        exchange.sendResponseHeaders(200, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private String extractJsonField(String json, String field) {
        String key = "\"" + field + "\":";
        int start = json.indexOf(key);
        if (start == -1) return "";
        start += key.length();
        while (start < json.length() && (json.charAt(start) == ' ' || json.charAt(start) == '"')) start++;
        int end = start;
        while (end < json.length() && json.charAt(end) != '"' && json.charAt(end) != ',' && json.charAt(end) != '}') end++;
        return json.substring(start, end).trim();
    }

    public static void main(String[] args) {
        try {
            int port = 8080;
            new WebServer(port).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}