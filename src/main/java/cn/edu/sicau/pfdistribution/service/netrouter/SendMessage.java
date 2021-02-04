package cn.edu.sicau.pfdistribution.service.netrouter;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


public class SendMessage {
    /**
     * @param url       发送地址
     * @param startTime 发送开始时间
     * @param endTime   发送结束时间
     * @param tableName 发送的数据表的名字
     */
    public static void sendPost(String url, String startTime, String endTime, String tableName) {
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
//  请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//  封装参数，千万不要替换为Map与HashMap，否则参数无法传递
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
//  也支持中文
        params.add("startTime", startTime);
        params.add("endTime", endTime);
        params.add("tableName", tableName);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(params, headers);
//  执行HTTP请求
        client.exchange(url, HttpMethod.POST, requestEntity, String.class);
    }

}
