package com.mshop.paymentservice.service.impl;

import com.mshop.paymentservice.service.IpService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
public class IpServiceImpl implements IpService {
    private static final String LOCALHOST_IPV4 = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
    private static final String UNKNOWN = "unknown";
    @Override
    public String getIpAddress(HttpServletRequest request) {
        String clientIpAddress = request.getHeader("X-Forwarded-For");
        if (!StringUtils.hasLength(clientIpAddress) || clientIpAddress.equals(UNKNOWN)) {
            clientIpAddress = request.getHeader("Proxy-Client-IP");
        }

        if (!StringUtils.hasLength(clientIpAddress) || clientIpAddress.equals(UNKNOWN)) {
            clientIpAddress = request.getHeader("WL-Proxy-Client-IP");
        }

        if (!StringUtils.hasLength(clientIpAddress) || clientIpAddress.equals(UNKNOWN)) {
            clientIpAddress = request.getRemoteAddr();
            if (LOCALHOST_IPV4.equals(clientIpAddress) || LOCALHOST_IPV6.equals(clientIpAddress)) {
                try {
                    InetAddress inetAddress = InetAddress.getLocalHost();
                    clientIpAddress = inetAddress.getHostAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }

        if (StringUtils.hasLength(clientIpAddress)
                && clientIpAddress.length() > 15
                && clientIpAddress.indexOf(",") > 0
        ) {
            clientIpAddress = clientIpAddress.substring(0, clientIpAddress.indexOf(","));
        }
        return clientIpAddress;
    }
}
