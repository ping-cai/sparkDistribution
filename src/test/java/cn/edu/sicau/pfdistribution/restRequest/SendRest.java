package cn.edu.sicau.pfdistribution.restRequest;

import cn.edu.sicau.pfdistribution.entity.jiaoda.RequestCommand;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;


public class SendRest {
    public static void main(String[] args) throws IOException {
        String url = "http://10.11.22.1:12123/dopost";
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
//  请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//  封装参数，千万不要替换为Map与HashMap，否则参数无法传递
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
//  也支持中文
        params.add("dateDt", "2018-09-01 00:00:00");
        params.add("startTime", "2018-09-01 07:00:00");
        params.add("endTime", "2018-09-01 24:00:00");
        params.add("timeInterval", "60");
        params.add("command", "dynamic");
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(params, headers);
//  执行HTTP请求
        ResponseEntity<String> response = client.exchange(url, HttpMethod.POST, requestEntity, String.class);
//  输出结果
        System.out.println(response.getBody());
    }
}
