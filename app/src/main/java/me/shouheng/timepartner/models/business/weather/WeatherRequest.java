package me.shouheng.timepartner.models.business.weather;

public class WeatherRequest {

    private static String city;

    private static String json = "json";  //不将其设置为json的话，返回的是xml格式的数据

    //static String apiKey = "iGs8rFvzh1e8c7C9DjXT5toK";
    private static String apiKey = "UqMLtUyom3ktq82lliv1fXVH5zRkPYUv";

    private static String SHA1 = "BE:2C:0E:D7:A2:FF:C5:23:D1:0A:5D:89:A7:2E:A5:EE:BD:48:12:C0";

    private static String packageName = "me.shouheng.timepartner";

    //http://api.map.baidu.com/telematics/v3/weather？location=南昌&output=json&ak=你的API Key&mcode=你的数字签名SHA1;com.example.administrator.jsontest（包名）
	// http://api.map.baidu.com/telematics/v3/weather？location=南昌&output=json&ak=UqMLtUyom3ktq82lliv1fXVH5zRkPYUv&mcode=BE:2C:0E:D7:A2:FF:C5:23:D1:0A:5D:89:A7:2E:A5:EE:BD:48:12:C0;me.shouheng.timepartner

    public static String getData(){
        return "http://api.map.baidu.com/telematics/v3/weather?" +
                "location=" + city +
                "&output=" + json +
                "&ak=" + apiKey +
                "&mcode=" + SHA1 +
                ";" + packageName;
    }

    public static String getCity() {
        return city;
    }

    public static void setCity(String city) {
        WeatherRequest.city = city;
    }

    public static String getJson() {
        return json;
    }

    public static void setJson(String json) {
        WeatherRequest.json = json;
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static void setApiKey(String apiKey) {
        WeatherRequest.apiKey = apiKey;
    }

    public static String getSHA1() {
        return SHA1;
    }

    public static void setSHA1(String SHA1) {
        WeatherRequest.SHA1 = SHA1;
    }

    public static String getPackageName() {
        return packageName;
    }

    public static void setPackageName(String packageName) {
        WeatherRequest.packageName = packageName;
    }
}
