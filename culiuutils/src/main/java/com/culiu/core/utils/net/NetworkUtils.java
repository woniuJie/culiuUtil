package com.culiu.core.utils.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.culiu.core.utils.debug.DebugLog;
import com.culiu.core.utils.string.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;

/**
 * @author adison
 * @describe 网络通用工具类.
 * @date: 2014-10-23 上午10:48:19 <br/>
 */
public class NetworkUtils {

    private NetworkUtils() {
    }

    /**
     * 获取ConnectivityManager
     */
    public static ConnectivityManager getConnManager(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    // 在WLAN设置界面
    // 1，显示连接已保存，但标题栏没有，即没有实质连接上，输出为：not connect， available

    // 2，显示连接已保存，标题栏也有已连接上的图标， 输出为：connect， available

    // 3，选择不保存后 输出为：not connect， available

    // 4，选择连接，在正在获取IP地址时 输出为：not connect， not available

    // 5，连接上后 输出为：connect， available

    /**
     * 判断网络连接是否有效（此时可传输数据）。
     *
     * @param context
     * @return boolean 不管wifi，还是mobile net，只有当前在连接状态（可有效传输数据）才返回true,反之false。
     */
    public static boolean isConnected(Context context) {
        try {
            NetworkInfo net = getConnManager(context).getActiveNetworkInfo();
            return net != null && net.isConnected();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断有无网络正在连接中（查找网络、校验、获取IP等）。
     *
     * @param context
     * @return boolean 不管wifi，还是mobile net，只有当前在连接状态（可有效传输数据）才返回true,反之false。
     */
    public static boolean isConnectedOrConnecting(Context context) {
        NetworkInfo[] nets = getConnManager(context).getAllNetworkInfo();
        if (nets != null) {
            for (NetworkInfo net : nets) {
                if (net.isConnectedOrConnecting()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取当前网络连接类型（字符串形式）
     *
     * @param context
     * @return
     */
    public static String getNetworkTypeSimple(Context context) {
        NetType netType = NetworkUtils.getConnectedType(context, false);
        switch (netType) {
            case NET_WIFI:
                return "wifi";
            case NET_MOBILE:
                return "mobile";
            case NET_OTHER:
                return "others";
            case NET_NONE:
                return "none";
            default:
                return "unknow";
        }
    }

    /**
     * 获取当前网络连接类型（字符串形式）
     *
     * @param context
     * @return
     */
    public static String getNetworkTypeDetail(Context context) {
        NetType netType = NetworkUtils.getConnectedType(context, true);
        switch (netType) {
            case NET_WIFI:
                return "wifi";
            case NET_MOBILE_2G:
                return "2g";
            case NET_MOBILE_3G:
                return "3g";
            case NET_MOBILE_4G:
                return "4g";
            case NET_OTHER:
                return "others";
            case NET_NONE:
                return "none";
            default:
                return "unknow";
        }
    }

    /**
     * 获取当前网络连接类型
     *
     * @param context
     * @return
     */
    public static NetType getConnectedType(Context context, boolean detail) {
        try {
            NetworkInfo net = getConnManager(context).getActiveNetworkInfo();
            if (net != null) {
                switch (net.getType()) {
                    case ConnectivityManager.TYPE_WIFI:
                        return NetType.NET_WIFI;
                    case ConnectivityManager.TYPE_MOBILE:
                        if (!detail) {
                            return NetType.NET_MOBILE;
                        } else {
                            return getMobileConnectedType(context);
                        }
                    default:
                        return NetType.NET_OTHER;
                }
            }
            return NetType.NET_NONE;
        } catch (Exception e) {
            return NetType.NET_OTHER;
        }
    }

    /***************************
     * 获取手机2G、3G、4G网络状况
     ***************************/
    private static final int NETWORK_TYPE_UNAVAILABLE = -1;
    // private static final int NETWORK_TYPE_MOBILE = -100;
    private static final int NETWORK_TYPE_WIFI = -101;

    private static final int NETWORK_CLASS_WIFI = -101;
    private static final int NETWORK_CLASS_UNAVAILABLE = -1;
    /**
     * Unknown network class.
     */
    private static final int NETWORK_CLASS_UNKNOWN = 0;
    /**
     * Class of broadly defined "2G" networks.
     */
    private static final int NETWORK_CLASS_2_G = 1;
    /**
     * Class of broadly defined "3G" networks.
     */
    private static final int NETWORK_CLASS_3_G = 2;
    /**
     * Class of broadly defined "4G" networks.
     */
    private static final int NETWORK_CLASS_4_G = 3;

    // 适配低版本手机
    /**
     * Network type is unknown
     */
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    /**
     * Current network is GPRS
     */
    public static final int NETWORK_TYPE_GPRS = 1;
    /**
     * Current network is EDGE
     */
    public static final int NETWORK_TYPE_EDGE = 2;
    /**
     * Current network is UMTS
     */
    public static final int NETWORK_TYPE_UMTS = 3;
    /**
     * Current network is CDMA: Either IS95A or IS95B
     */
    public static final int NETWORK_TYPE_CDMA = 4;
    /**
     * Current network is EVDO revision 0
     */
    public static final int NETWORK_TYPE_EVDO_0 = 5;
    /**
     * Current network is EVDO revision A
     */
    public static final int NETWORK_TYPE_EVDO_A = 6;
    /**
     * Current network is 1xRTT
     */
    public static final int NETWORK_TYPE_1xRTT = 7;
    /**
     * Current network is HSDPA
     */
    public static final int NETWORK_TYPE_HSDPA = 8;
    /**
     * Current network is HSUPA
     */
    public static final int NETWORK_TYPE_HSUPA = 9;
    /**
     * Current network is HSPA
     */
    public static final int NETWORK_TYPE_HSPA = 10;
    /**
     * Current network is iDen
     */
    public static final int NETWORK_TYPE_IDEN = 11;
    /**
     * Current network is EVDO revision B
     */
    public static final int NETWORK_TYPE_EVDO_B = 12;
    /**
     * Current network is LTE
     */
    public static final int NETWORK_TYPE_LTE = 13;
    /**
     * Current network is eHRPD
     */
    public static final int NETWORK_TYPE_EHRPD = 14;
    /**
     * Current network is HSPA+
     */
    public static final int NETWORK_TYPE_HSPAP = 15;

    private static NetType getMobileConnectedType(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case NETWORK_TYPE_UNAVAILABLE:
                return NetType.NET_NONE;
            case NETWORK_TYPE_WIFI:
                return NetType.NET_WIFI;
            case NETWORK_TYPE_GPRS:
            case NETWORK_TYPE_EDGE:
            case NETWORK_TYPE_CDMA:
            case NETWORK_TYPE_1xRTT:
            case NETWORK_TYPE_IDEN:
                return NetType.NET_MOBILE_2G;
            case NETWORK_TYPE_UMTS:
            case NETWORK_TYPE_EVDO_0:
            case NETWORK_TYPE_EVDO_A:
            case NETWORK_TYPE_HSDPA:
            case NETWORK_TYPE_HSUPA:
            case NETWORK_TYPE_HSPA:
            case NETWORK_TYPE_EVDO_B:
            case NETWORK_TYPE_EHRPD:
            case NETWORK_TYPE_HSPAP:
                return NetType.NET_MOBILE_3G;
            case NETWORK_TYPE_LTE:
                return NetType.NET_MOBILE_4G;
            default:
                return NetType.NET_OTHER;
        }
    }
    /*************************** 获取手机2G、3G、4G网络状况 ***************************/

    /**
     * 是否存在有效的WIFI连接
     */
    public static boolean isWifiConnected(Context context) {
        NetworkInfo net = getConnManager(context).getActiveNetworkInfo();
        return net != null && net.getType() == ConnectivityManager.TYPE_WIFI && net.isConnected();
    }

    /**
     * 是否存在有效的移动连接
     *
     * @param context
     * @return boolean
     */
    public static boolean isMobileConnected(Context context) {
        NetworkInfo net = getConnManager(context).getActiveNetworkInfo();
        return net != null && net.getType() == ConnectivityManager.TYPE_MOBILE && net.isConnected();
    }

    /**
     * 检测网络是否为可用状态
     */
    public static boolean isAvailable(Context context) {
        return isWifiAvailable(context) || (isMobileAvailable(context) && isMobileEnabled(context));
    }

    /**
     * 判断是否有可用状态的Wifi，以下情况返回false： 1. 设备wifi开关关掉; 2. 已经打开飞行模式； 3. 设备所在区域没有信号覆盖； 4. 设备在漫游区域，且关闭了网络漫游。
     *
     * @param context
     * @return boolean wifi为可用状态（不一定成功连接，即Connected）即返回ture
     */
    public static boolean isWifiAvailable(Context context) {
        NetworkInfo[] nets = getConnManager(context).getAllNetworkInfo();
        if (nets != null) {
            for (NetworkInfo net : nets) {
                if (net.getType() == ConnectivityManager.TYPE_WIFI) {
                    return net.isAvailable();
                }
            }
        }
        return false;
    }

    /**
     * 判断有无可用状态的移动网络，注意关掉设备移动网络直接不影响此函数。 也就是即使关掉移动网络，那么移动网络也可能是可用的(彩信等服务)，即返回true。 以下情况它是不可用的，将返回false： 1. 设备打开飞行模式； 2.
     * 设备所在区域没有信号覆盖； 3. 设备在漫游区域，且关闭了网络漫游。
     *
     * @param context
     * @return boolean
     */
    public static boolean isMobileAvailable(Context context) {
        NetworkInfo[] nets = getConnManager(context).getAllNetworkInfo();
        if (nets != null) {
            for (NetworkInfo net : nets) {
                if (net.getType() == ConnectivityManager.TYPE_MOBILE) {
                    return net.isAvailable();
                }
            }
        }
        return false;
    }

    /**
     * 设备是否打开移动网络开关
     *
     * @param context
     * @return boolean 打开移动网络返回true，反之false
     */
    public static boolean isMobileEnabled(Context context) {
        try {
            Method getMobileDataEnabledMethod = ConnectivityManager.class.getDeclaredMethod("getMobileDataEnabled");
            getMobileDataEnabledMethod.setAccessible(true);
            return (Boolean) getMobileDataEnabledMethod.invoke(getConnManager(context));
        } catch (Exception e) {
            DebugLog.i(e.getMessage());
        }
        // 反射失败，默认开启
        return true;
    }

    /**
     * 返回Wifi是否启用
     *
     * @param context 上下文
     * @return Wifi网络可用则返回true，否则返回false
     */
    public static boolean isWIFIActivate(Context context) {
        return ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).isWifiEnabled();
    }

    /**
     * 修改Wifi状态
     *
     * @param context 上下文
     * @param status  true为开启Wifi，false为关闭Wifi
     */
    public static void changeWIFIStatus(Context context, boolean status) {
        ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).setWifiEnabled(status);
    }

    /**
     * 打印当前各种网络状态
     *
     * @param context
     * @return boolean
     */
    public static boolean printNetworkInfo(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo in = connectivity.getActiveNetworkInfo();
            DebugLog.i("-------------$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$-------------");
            DebugLog.i("getActiveNetworkInfo: " + in);
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    // if (info[i].getType() == ConnectivityManager.TYPE_WIFI) {
                    DebugLog.i("NetworkInfo[" + i + "]isAvailable : " + info[i].isAvailable());
                    DebugLog.i("NetworkInfo[" + i + "]isConnected : " + info[i].isConnected());
                    DebugLog.i("NetworkInfo[" + i + "]isConnectedOrConnecting : " + info[i].isConnectedOrConnecting());
                    DebugLog.i("NetworkInfo[" + i + "]: " + info[i]);
                    // }
                }
                DebugLog.i("\n");
            } else {
                DebugLog.i("getAllNetworkInfo is null");
            }
        }
        return false;
    }

    /**
     * getIpAddress:获取本机网络IP. <br/>
     *
     * @param context
     * @return
     * @author adison
     */
    public static String getIpAddress(Context context) {
        try {
            NetType type = NetworkUtils.getConnectedType(context, false);
            if (type != null) {
                if (NetType.NET_MOBILE == type) {
                    return getMobileIPAddress();
                } else if (NetType.NET_WIFI == type) {
                    return getWifiIPAddress(context);
                }
            }
        } catch (Exception e) {
            DebugLog.e("getIp-->Error::", e);
        }
        return "127.0.0.1";
    }

    /**
     * getMobileIPAddress:获取手机网络ip地址. <br/>
     *
     * @return
     * @author adison
     */
    public static String getMobileIPAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        // if (!inetAddress.isLoopbackAddress() && inetAddress
                        // instanceof Inet6Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
            DebugLog.e("getIp-->Error::", e);

        }
        return "127.0.0.1";
    }

    /*
     * 获取wifi IP地址
     */
    public static String getWifiIPAddress(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            // 获取32位整型IP地址
            int ipAddress = wifiInfo.getIpAddress();
            // 返回整型地址转换成“*.*.*.*”地址
            return String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff),
                    (ipAddress >> 24 & 0xff));
        } catch (Exception e) {

            DebugLog.e("getIp-->Error::", e);

        }
        return "127.0.0.1";
    }

    /**
     * 获取网络运营商
     *
     * @return
     */
    public static String getProvider(Context context) {
        String provider = "未知";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String IMSI = telephonyManager.getSubscriberId();
            if (IMSI == null) {
                if (TelephonyManager.SIM_STATE_READY == telephonyManager
                        .getSimState()) {
                    String operator = telephonyManager.getSimOperator();
                    if (operator != null) {
                        if (operator.equals("46000")
                                || operator.equals("46002")
                                || operator.equals("46007")) {
                            provider = "中国移动";
                        } else if (operator.equals("46001")) {
                            provider = "中国联通";
                        } else if (operator.equals("46003")) {
                            provider = "中国电信";
                        }
                    }
                }
            } else {
                if (IMSI.startsWith("46000") || IMSI.startsWith("46002")
                        || IMSI.startsWith("46007")) {
                    provider = "中国移动";
                } else if (IMSI.startsWith("46001")) {
                    provider = "中国联通";
                } else if (IMSI.startsWith("46003")) {
                    provider = "中国电信";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return provider;
    }

    /**
     * 获取wifi rssi
     *
     * @param context
     * @return
     */
    public static String getWifiRssi(Context context) {
        int asu = 85;
        try {
            final NetworkInfo network = ((ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE))
                    .getActiveNetworkInfo();
            if (network != null && network.isAvailable()
                    && network.isConnected()) {
                int type = network.getType();
                if (type == ConnectivityManager.TYPE_WIFI) {
                    WifiManager wifiManager = (WifiManager) context
                            .getSystemService(Context.WIFI_SERVICE);

                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    if (wifiInfo != null) {
                        asu = wifiInfo.getRssi();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return asu + "dBm";
    }

    /**
     * 获取wifissid
     *
     * @param context
     * @return
     */
    public static String getWifiSsid(Context context) {
        String ssid = "";
        try {
            final NetworkInfo network = ((ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE))
                    .getActiveNetworkInfo();
            if (network != null && network.isAvailable()
                    && network.isConnected()) {
                int type = network.getType();
                if (type == ConnectivityManager.TYPE_WIFI) {
                    WifiManager wifiManager = (WifiManager) context
                            .getSystemService(Context.WIFI_SERVICE);

                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    if (wifiInfo != null) {
                        ssid = wifiInfo.getSSID();
                        if (ssid == null) {
                            ssid = "";
                        }
                        ssid = ssid.replaceAll("\"", "");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ssid;
    }

    /**
     * 检查sim卡状态
     *
     * @return
     */
    public static boolean checkSimState(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getSimState() == TelephonyManager.SIM_STATE_ABSENT
                || tm.getSimState() == TelephonyManager.SIM_STATE_UNKNOWN) {
            return false;
        }
        return true;
    }

    /**
     * 从Url中获取Host字段
     *
     * @param url
     * @return Host字段
     */
    public static String getHostFromUrl(String url) {
        try {
            return new java.net.URL(url).getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getHost(String urlStr) {
        if (TextUtils.isEmpty(urlStr))
            return "";
        Uri uri = Uri.parse(urlStr);
        if (uri != null)
            return uri.getHost();
        return "";
    }

    /**
     * 通过淘宝的api，获取公网ip
     * <p>
     * <p>
     * {
     * "code": 0,
     * "data": {
     * "country": "中国",
     * "country_id": "CN",
     * "area": "华北",
     * "area_id": "100000",
     * "region": "北京市",
     * "region_id": "110000",
     * "city": "北京市",
     * "city_id": "110100",
     * "county": "朝阳区",
     * "county_id": "110105",
     * "isp": "鹏博士",
     * "isp_id": "1000143",
     * "ip": "219.238.5.66"
     * }
     * }
     *
     * @return
     */
    public static String[] getPublicNetworkInfo() {
        URL infoUrl = null;
        InputStream inStream = null;
        try {
            // http://1212.ip138.com/ic.asp ip138接口
            infoUrl = new URL("http://ip.taobao.com/service/getIpInfo.php?ip=myip");
            URLConnection connection = infoUrl.openConnection();
            connection.setRequestProperty("User-Agent", StringUtils.generateRandom(32));
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
                StringBuilder responseBuilder = new StringBuilder("");
                String lineText = null;
                while ((lineText = reader.readLine()) != null) {
                    responseBuilder.append(lineText + "\n");
                }
                inStream.close();
                JSONObject jsonResponseObj = null;
                try {
                    jsonResponseObj = new JSONObject(responseBuilder.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (jsonResponseObj == null)
                    return new String[3];
                String publicIp = "";
                String location = "";
                String isp = "";
                if (jsonResponseObj != null) {
                    int code = jsonResponseObj.getInt("code");
                    if (code == 0) {
                        JSONObject data = jsonResponseObj.getJSONObject("data");
                        if (data != null) {
                            publicIp = data.getString("ip");
                            location = data.getString("country") + " "
                                    + data.getString("region") + " "
                                    + data.getString("city") + " "
                                    + data.getString("county") + "/"
                                    + data.getString("country_id") + " "
                                    + data.getString("region_id") + " "
                                    + data.getString("city_id") + " "
                                    + data.getString("county_id");
                            isp = data.getString("isp") + "/" + data.getString("isp_id");
                        }
                    }
                }
                // 组装数据
                String[] info = new String[3];
                info[0] = publicIp;
                info[1] = location;
                info[2] = isp;
                return info;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[3];
    }

}
