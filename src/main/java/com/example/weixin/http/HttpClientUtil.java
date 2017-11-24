package com.example.weixin.http;

import com.example.weixin.util.StringUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * Created by C on 2017/11/17.
 */
public class HttpClientUtil {

    private static HttpClient client = null;
    static {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(128);
        cm.setDefaultMaxPerRoute(128);
        client = HttpClients.custom().setConnectionManager(cm)
                .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false)).build();
    }

    /**
     * 发送一个 Post 请求, 使用指定的字符集编码.
     *
     * @param url
     * @param body
     *            RequestBody
     * @return ResponseBody, 使用指定的字符集编码.
     *
     * @throws ConnectTimeoutException
     *             建立链接超时异常
     * @throws SocketTimeoutException
     *             响应超时
     * @throws Exception
     */
    public static HttpResult post(String url, String body) throws GeneralSecurityException, IOException {
        return post(url, null, body, null, null, 6 * 1000, 10 * 1000);
    }

    /**
     * 发送一个 Post 请求, 使用指定的字符集编码.
     *
     * @param url
     * @param headers
     * @param body
     *            RequestBody
     * @param mimeType
     *            例如 application/xml, 默认: text/plain
     * @param charset
     *            编码
     * @param connTimeout
     *            建立链接超时时间,毫秒.
     * @param readTimeout
     *            响应超时时间,毫秒.
     * @return ResponseBody, 使用指定的字符集编码.
     *
     * @throws ConnectTimeoutException
     *             建立链接超时异常
     * @throws SocketTimeoutException
     *             响应超时
     * @throws Exception
     */
    public static HttpResult post(String url, Map<String, String> headers, String body, String mimeType,
                                  String charset, Integer connTimeout, Integer readTimeout) throws GeneralSecurityException, IOException {
        HttpClient client = null;
        HttpPost post = new HttpPost(url);
        String result = "";
        int code;
        if (StringUtils.isBlank(mimeType)) {
            mimeType = "application/json";
        }
        if (StringUtils.isBlank(charset)) {
            charset = "utf-8";
        }
        try {
            if (StringUtils.isNotBlank(body)) {

                HttpEntity entity = new StringEntity(body, ContentType.create(mimeType, charset));
                post.setEntity(entity);
            }
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    post.addHeader(entry.getKey(), entry.getValue());
                }
            }
            // 设置参数
            RequestConfig.Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }
            post.setConfig(customReqConf.build());

            HttpResponse res;
            if (url.startsWith("https")) {
                // 执行 Https 请求.
                client = createSSLInsecureClient();
                res = client.execute(post);
            } else {
                // 执行 Http 请求.
                client = HttpClientUtil.client;
                res = client.execute(post);
            }
            if (null != res.getEntity()) {
                result = IOUtils.toString(res.getEntity().getContent(), charset);
            }
            code = res.getStatusLine().getStatusCode();
            if (code / 100 == 2) {
                return new HttpResult(code, result, false);
            } else {
                return new HttpResult(code, result, true);
            }
        } finally {
            post.releaseConnection();
            if (url.startsWith("https") && client != null && client instanceof CloseableHttpClient) {
                ((CloseableHttpClient) client).close();
            }
        }
    }

    /**
     * 发送一个 GET 请求
     *
     * @param url
     * @return
     * @throws Exception
     */
    public static HttpResult get(String url) throws GeneralSecurityException, IOException {
        return get(url, "utf-8", null, null);
    }

    /**
     * 发送一个 GET 请求
     *
     * @param url
     * @param charset
     * @return
     * @throws Exception
     */
    public static HttpResult get(String url, String charset) throws GeneralSecurityException, IOException {
        return get(url, charset, null, null);
    }

    /**
     * 发送一个 GET 请求
     *
     * @param url
     * @param charset
     * @param connTimeout
     *            建立链接超时时间,毫秒.
     * @param readTimeout
     *            响应超时时间,毫秒.
     * @return
     * @throws IOException
     * @throws Exception
     */
    public static HttpResult get(String url, String charset, Integer connTimeout, Integer readTimeout)
            throws GeneralSecurityException, IOException {
        HttpClient client = null;

        HttpGet get = new HttpGet(url);
        String result = "";
        try {
            // 设置参数
            RequestConfig.Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }
            get.setConfig(customReqConf.build());

            HttpResponse res = null;

            if (url.startsWith("https")) {
                // 执行 Https 请求.
                client = createSSLInsecureClient();
                res = client.execute(get);
            } else {
                // 执行 Http 请求.
                client = HttpClientUtil.client;
                res = client.execute(get);
            }
            result = IOUtils.toString(res.getEntity().getContent(), charset);
            int statusCode = res.getStatusLine().getStatusCode();
            if (statusCode / 100 == 2) {
                return new HttpResult(statusCode, result, false);
            } else {
                return new HttpResult(statusCode, result, true);
            }
        } finally {
            get.releaseConnection();
            if (url.startsWith("https") && client != null && client instanceof CloseableHttpClient) {
                ((CloseableHttpClient) client).close();
            }
        }
    }

    /**
     * 创建SSL连接
     *
     * @return
     * @throws GeneralSecurityException
     */
    public static CloseableHttpClient createSSLInsecureClient() throws GeneralSecurityException {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new HostnameVerifier() {

                @Override
                public boolean verify(String paramString, SSLSession paramSSLSession) {

                    return true;
                }
            });
            return HttpClients.custom().setSSLSocketFactory(sslsf)
                    .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false)).build();
        } catch (GeneralSecurityException e) {
            throw e;
        }
    }
}
