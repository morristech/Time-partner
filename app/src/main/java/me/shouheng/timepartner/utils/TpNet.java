package me.shouheng.timepartner.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TpNet {
    private static final String TAG = "TPNet__";
    private static HttpClient httpClient = new DefaultHttpClient();

    /**
     * 发送请求
     * @param url 地址
     * @param rawParams 地址参数 http://xxx?user=user_name&pass=password
     * @throws Exception 异常 */
    public static void postRequest(final String url, final Map<String ,String> rawParams, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 创建HttpPost对象。
                    final HttpPost post = new HttpPost(url);
                    // 如果传递参数个数比较多的话可以对传递的参数进行封装
                    List<NameValuePair> params = new ArrayList<>();
                    for(String key : rawParams.keySet()) {
                        // 封装请求参数
                        params.add(new BasicNameValuePair(key , rawParams.get(key)));
                    }
                    // 设置请求参数
                    post.setEntity(new UrlEncodedFormEntity(params, "gbk"));
                    // 发送POST请求
                    HttpResponse httpResponse = httpClient.execute(post);
                    // 如果服务器成功地返回响应
                    if (httpResponse.getStatusLine().getStatusCode() == 200){
                        // 获取服务器响应字符串
                        if (listener != null){
                            listener.onFinish(EntityUtils.toString(httpResponse.getEntity()));
                        }
                    }
                } catch (IOException e) {
                    if (listener != null){
                        listener.onError(e);
                    }
                }
            }
        }).start();
    }

    /**
     * 基于URL的回调方法
     * @param address 链接地址
     * @param listener 回调接口*/
    public static void sendUrlRequest(final String address, final HttpCallbackListener listener){
        // 定义一个线程用来从网络
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    // 从字符串中获取URL对象
                    URL url = new URL(address);
                    // 获取用于打开输入流的HttpURLConnection对象
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    // 获取输入流
                    InputStream in = connection.getInputStream();
                    BufferedReader bf = new BufferedReader(new InputStreamReader(in));
                    StringBuilder sb = new StringBuilder();
                    // 从流中读取数据
                    String line;
                    while ((line = bf.readLine()) != null){
                        sb.append(line);
                    }
                    // 发送数据给回调函数
                    if (listener != null){
                        listener.onFinish(sb.toString());
                    }
                } catch (IOException e) {
                    if (listener != null){
                        listener.onError(e);
                    }
                    e.printStackTrace();
                } finally {
                    // 断开连接
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    /**
     * 基于HttpClient的回调
     * @param address 链接的URL地址
     * @param listener 回调接口*/
    public static void sendClientRequest(final String address, final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
            }
        }).start();
    }

    public interface HttpCallbackListener {
        void onFinish(String response);
        void onError(Exception e);
    }

    /**
     * 网络状态是否可用
     * @param context 上下文
     * @return 网络状态*/
    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return ((networkInfo != null) && (networkInfo.isAvailable()));
    }
}
