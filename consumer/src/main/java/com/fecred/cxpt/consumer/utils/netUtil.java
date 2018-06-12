package com.fecred.cxpt.consumer.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class netUtil {

    public static final String getLocalIp() {
        String ipString = "";

        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();

            InetAddress ip = null;

            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = allNetInterfaces.nextElement();
                Enumeration<InetAddress> address = networkInterface.getInetAddresses();

                while (address.hasMoreElements()) {
                    ip = address.nextElement();

                    if (ip != null && ip instanceof Inet4Address && !ip.equals("127.0.0.1")) {
                        return ip.getHostAddress();
                    }
                }
            }

            return ipString;
        } catch (Exception e) {}

        return ipString;
    }

}
