package com.mshop.paymentservice.service;

import javax.servlet.http.HttpServletRequest;

public interface IpService {
    String getIpAddress(HttpServletRequest request);
}
