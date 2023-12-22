package com.mshop.userservice.external;

import com.sudo248.domain.common.Constants;
import com.mshop.userservice.controller.dto.ghn.address.GHNDistrictDto;
import com.mshop.userservice.controller.dto.ghn.address.GHNProvinceDto;
import com.mshop.userservice.controller.dto.ghn.address.GHNResponse;
import com.mshop.userservice.controller.dto.ghn.address.GHNWardDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "GHN", url = "https://dev-online-gateway.ghn.vn/shiip/public-api")
@Service
public interface GHNService {
    @GetMapping(value = "/master-data/province", headers = "token="+ Constants.GHN_TOKEN)
    GHNResponse<List<GHNProvinceDto>> getGHNProvince();

    @GetMapping(value = "/master-data/district", headers = "token="+ Constants.GHN_TOKEN)
    GHNResponse<List<GHNDistrictDto>> getGHNDistrict(@RequestParam("province_id") int provinceId);

    @GetMapping(value = "/master-data/ward", headers = "token="+ Constants.GHN_TOKEN)
    GHNResponse<List<GHNWardDto>> getGHNWard(@RequestParam("district_id") int districtId);
}
