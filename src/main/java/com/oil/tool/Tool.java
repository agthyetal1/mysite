package com.oil.tool;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Tool {

    private static Logger logger = LoggerFactory.getLogger(Tool.class);

    public static String ipToLocation(String ip) {
        if("127.0.0.1".equals(ip)) {
            return "保留地址";
        }
        String key = "eecc6c08fb8abe8e63b4df352942bec3";
        String result = httpWithGet("http://apis.juhe.cn/ip/ip2addr?ip="+ip+"&dtype=&key=" + key);
        logger.info(result);
        JsonObject json = new JsonParser().parse(result).getAsJsonObject();
        json.get("resultcode").getAsString();
        if("200".equals(json.get("resultcode").getAsString())) {
            String area = json.getAsJsonObject("result").get("area").getAsString();
            return area;
        }
        return "";
    }



    public static String httpWithGet(String url) {
        Map<String, String> queries = new HashMap<String, String>();
        int SocketTimeout = 30000; //30秒
        int ConnectTimeout = 30000; //30
        Boolean SetTimeOut = true;

        String responseBody = "";
        // CloseableHttpClient httpClient=HttpClients.createDefault();
        // 支持https
        CloseableHttpClient httpClient = getHttpClient();

        StringBuilder sb = new StringBuilder(url);

        if (queries != null && queries.keySet().size() > 0) {
            boolean firstFlag = true;
            Iterator iterator = queries.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry<String, String>) iterator.next();
                if (firstFlag) {
                    sb.append("?" + (String) entry.getKey() + "=" + (String) entry.getValue());
                    firstFlag = false;
                } else {
                    sb.append("&" + (String) entry.getKey() + "=" + (String) entry.getValue());
                }
            }
        }
        HttpGet httpGet = new HttpGet(sb.toString());
        if (SetTimeOut) {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(SocketTimeout)
                    .setConnectTimeout(ConnectTimeout).build(); //设置请求和传输超时时间
            httpGet.setConfig(requestConfig);
        }
        try {
            logger.info("Executing request " + httpGet.getRequestLine());
            // 请求数据
            CloseableHttpResponse response = httpClient.execute(httpGet);
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                responseBody = EntityUtils.toString(entity, "utf-8");
                // EntityUtils.consume(entity);
            } else {
                logger.warn("http return status error:" + status);
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try{
                httpClient.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return responseBody;
    }

    private static CloseableHttpClient getHttpClient() {
        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create();
        ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();
        registryBuilder.register("http", plainSF);
        // 指定信任密钥存储对象和连接套接字工厂
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            // 信任任何链接
            TrustStrategy anyTrustStrategy = new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            };
            SSLContext sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(trustStore, anyTrustStrategy).build();
            LayeredConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            registryBuilder.register("https", sslSF);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        Registry<ConnectionSocketFactory> registry = registryBuilder.build();
        // 设置连接管理器
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);
        // connManager.setDefaultConnectionConfig(connConfig);
        // connManager.setDefaultSocketConfig(socketConfig);
        //构建客户端
        return HttpClientBuilder.create().setConnectionManager(connManager).build();
    }

    // 从request中解析出user-agent
    public static String getUserAgent(HttpServletRequest request) {
        String ua = request.getHeader("user-agent");
        return ua;
    }

    // 获取真实ip
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        logger.info("x-forwarded-for = {}", ip);
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            logger.info("Proxy-Client-IP = {}", ip);
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            logger.info("WL-Proxy-Client-IP = {}", ip);
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            logger.info("RemoteAddr-IP = {}", ip);
        }
        if(!Strings.isNullOrEmpty(ip)) {
            ip = ip.split(",")[0];
        }
        return ip;
    }

}
