package com.hotel.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * 调用大模型（DeepSeek，OpenAI 兼容接口）。
 * 密钥、地址、模型都从环境变量读，不写死在代码里，避免密钥泄露。
 */
public class AiClient {

    private static String key() {
        return env("AI_API_KEY", "");
    }

    private static String baseUrl() {
        return env("AI_BASE_URL", "https://api.deepseek.com");
    }

    private static String model() {
        return env("AI_MODEL", "deepseek-chat");
    }

    private static String env(String k, String def) {
        String v = System.getenv(k);
        return (v == null || v.trim().isEmpty()) ? def : v.trim();
    }

    /** 是否已经配置了密钥 */
    public static boolean configured() {
        return !key().isEmpty();
    }

    /** 发一次对话，返回大模型回复的正文 */
    public static String chat(String systemPrompt, String userPrompt) throws IOException {
        String apiKey = key();
        if (apiKey.isEmpty()) {
            throw new IOException("未配置大模型密钥 AI_API_KEY");
        }
        URL url = new URL(baseUrl().replaceAll("/+$", "") + "/chat/completions");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(60000);
        conn.setDoOutput(true);

        JsonObject body = new JsonObject();
        body.addProperty("model", model());
        JsonArray messages = new JsonArray();
        messages.add(msg("system", systemPrompt));
        messages.add(msg("user", userPrompt));
        body.add("messages", messages);
        body.addProperty("temperature", 0.7);
        body.addProperty("stream", false);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.toString().getBytes(StandardCharsets.UTF_8));
        }

        int code = conn.getResponseCode();
        InputStream is = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();
        String resp = readAll(is);
        if (code < 200 || code >= 300) {
            throw new IOException("大模型接口返回 " + code + "：" + resp);
        }
        JsonObject json = JsonParser.parseString(resp).getAsJsonObject();
        return json.getAsJsonArray("choices").get(0).getAsJsonObject()
                .getAsJsonObject("message").get("content").getAsString().trim();
    }

    private static JsonObject msg(String role, String content) {
        JsonObject o = new JsonObject();
        o.addProperty("role", role);
        o.addProperty("content", content);
        return o;
    }

    private static String readAll(InputStream is) throws IOException {
        if (is == null) {
            return "";
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int n;
        while ((n = is.read(buf)) != -1) {
            bos.write(buf, 0, n);
        }
        return bos.toString("UTF-8");
    }
}
