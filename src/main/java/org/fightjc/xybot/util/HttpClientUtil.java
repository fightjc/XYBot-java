package org.fightjc.xybot.util;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.fightjc.xybot.po.HttpClientResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
    public static final boolean VERBOSE = false;

    public static final String ENCODING = "UTF-8";
    public static final int CONNECT_TIMEOUT = 2000; // 连接超时(毫秒)
    public static final int SOCKET_TIMEOUT = 30000; // 读取超时(毫秒)

    public static HttpClientResult doGet(String url, Map<String, String> headers, Map<String, String> params) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        URIBuilder uriBuilder = new URIBuilder(url);
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                uriBuilder.setParameter(entry.getKey(), entry.getValue());
            }
        }

        HttpGet httpGet = new HttpGet(uriBuilder.build());
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
        httpGet.setConfig(requestConfig);

        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
        }

        try {
            return getHttpClientResult(httpClient, httpGet);
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
        }
    }

    public static HttpClientResult doPost(String url, Map<String, String> headers, Map<String, String> params) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
        httpPost.setConfig(requestConfig);

        httpPost.setHeader("Connection", "keep-alive");
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }

        if (params != null) {
            List<NameValuePair> pairList = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                pairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, ENCODING));
        }

        try {
            return getHttpClientResult(httpClient, httpPost);
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
        }
    }

    private static HttpClientResult getHttpClientResult(CloseableHttpClient httpClient, HttpRequestBase httpMethod) throws IOException {
        HttpClientResult result;

        logger.info("网络请求" + httpMethod.getMethod() + ": " + httpMethod.getURI());

        CloseableHttpResponse httpResponse = httpClient.execute(httpMethod);
        if (httpResponse != null && httpResponse.getStatusLine() != null) {
            String content = "";
            if (httpResponse.getEntity() != null) {
                content = EntityUtils.toString(httpResponse.getEntity(), ENCODING);
            }
            int code = httpResponse.getStatusLine().getStatusCode();

            result = new HttpClientResult(code, content);
        } else {
            result = new HttpClientResult(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

        if (httpResponse != null) {
            httpResponse.close();
        }

        if (VERBOSE) {
            logger.info("网络请求结果: " + result.code + ", " + result.content);
        }

        return result;
    }

}
