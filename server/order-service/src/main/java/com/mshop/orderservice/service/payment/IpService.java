package com.mshop.orderservice.service.payment;

import javax.servlet.http.HttpServletRequest;

public interface IpService {
    String getIpAddress(HttpServletRequest request);
}
