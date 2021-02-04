package cn.edu.sicau.pfdistribution.Utils;

import cn.edu.sicau.pfdistribution.entity.jiaoda.RequestCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class SendCommand {
    @Value("${url.distribution}")
    public String url;
    public String sendPost(RequestCommand requestCommand) {
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
//  请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//  封装参数，千万不要替换为Map与HashMap，否则参数无法传递
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
//  也支持中文
        params.add("dateDt", requestCommand.getDateDt());
        params.add("startTime", requestCommand.getStartTime());
        params.add("endTime", requestCommand.getEndTime());
        params.add("timeInterval", String.valueOf(requestCommand.getTimeInterval()));
        params.add("command", requestCommand.getCommand());
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(params, headers);
//  执行HTTP请求
        ResponseEntity<String> response = client.exchange(url, HttpMethod.POST, requestEntity, String.class);
//  输出结果
        return response.getBody();
    }
}
