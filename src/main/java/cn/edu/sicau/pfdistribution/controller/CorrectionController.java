package cn.edu.sicau.pfdistribution.controller;

import cn.edu.sicau.pfdistribution.entity.jiaoda.RequestCommand;
import cn.edu.sicau.pfdistribution.service.jiaodaTest.CorrectionDataAllocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * @Author LiYongPing
 * @Date 2021-02-02
 * @LastUpdate 2021-02-02
 */
@Slf4j
@Controller
public class CorrectionController {
    @Autowired
    private CorrectionDataAllocation correctionDataAllocation;
    @PostMapping("/correctData")
    @ResponseBody
    public void correctData(String fromTable,String targetTable,RequestCommand requestCommand){
        correctionDataAllocation.CorrectDataTest(fromTable,targetTable,requestCommand);
    }
}
