package cn.dijia478.xianhouseprice.service.impl;

import cn.dijia478.xianhouseprice.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 价格服务层
 *
 * @author dijia478
 * @date 2020-8-31 17:15
 */
@Service
public class PriceServiceImpl implements PriceService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 2000L, multiplier = 2))
    public String getHouseInfo(String ipPort, String url, int page) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("page", String.valueOf(page));
        body.add("size", "15");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, httpHeaders);
        return restTemplate.postForObject(ipPort + url, entity, String.class);
    }

    @Override
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 2000L, multiplier = 2))
    public String getHousePrice(String ipPort, String url, String id, int page) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("page", String.valueOf(page));
        body.add("size", "15");
        body.add("id", id);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, httpHeaders);
        return restTemplate.postForObject(ipPort + url, entity, String.class);
    }

}
