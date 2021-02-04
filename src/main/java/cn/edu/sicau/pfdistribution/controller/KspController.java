package cn.edu.sicau.pfdistribution.controller;

import cn.edu.sicau.pfdistribution.Utils.DataBaseLoading;
import cn.edu.sicau.pfdistribution.Utils.ResultMsg;
import cn.edu.sicau.pfdistribution.Utils.ResultMsg2;
import cn.edu.sicau.pfdistribution.dao.Impl.OracleQueryStationByNameOrIdImpl;
import cn.edu.sicau.pfdistribution.entity.*;
import cn.edu.sicau.pfdistribution.Utils.ResultStatusCode;
import cn.edu.sicau.pfdistribution.entity.correct.TripPlanVo;
import cn.edu.sicau.pfdistribution.service.Web.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/modularSWJTU")
public class KspController {

    @Autowired
    private KspService kspService;
    @Autowired
    OracleQueryStationByNameOrIdImpl oracleQueryStationByNameOrId;
    @Autowired
    private RoadPlanningService roadPlanningService;
    private org.slf4j.Logger logger = LoggerFactory.getLogger(KspController.class);

    /**
     * @author zhouzhiyuan
     */
    @RequestMapping(value = "/tripPlan.do", produces = "application/json;charset=utf-8")
    @CrossOrigin
    public String intactFindKsp(@RequestBody SWJTU_DTO swjtu_dto) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> stationNameToId = DataBaseLoading.stationNameToId;
        swjtu_dto.setStartStation(stationNameToId.get(swjtu_dto.getStartStation()));
        swjtu_dto.setEndStation(stationNameToId.get(swjtu_dto.getEndStation()));
        List<PathSearch> NetResult = roadPlanningService.kspResult(swjtu_dto);
        ResultMsgKsp resultMsgKsp = new ResultMsgKsp(ResultStatusCode.OK.getErrcode(), ResultStatusCode.OK.getErrmsg(), NetResult);
        return getTry(resultMsgKsp, mapper);
    }

    @RequestMapping(value = "/queryByStation.do", produces = "application/json;charset=utf-8")
    @CrossOrigin
    public String getQueryInfo(@RequestBody QueryStationByNameOrID queryStationByNameOrID) {
        List<KspQueryResult> odAndTime = oracleQueryStationByNameOrId.findAll(queryStationByNameOrID);
        ObjectMapper mapper = new ObjectMapper();
        if (odAndTime.size() == 0) {
            ResultMsg2 result = new ResultMsg2(ResultStatusCode.SYSTEM_ERR.getErrcode(), ResultStatusCode.SYSTEM_ERR.getErrmsg(), odAndTime);
            return getTry(result, mapper);
        } else {
            ResultMsg2 result = new ResultMsg2(ResultStatusCode.OK.getErrcode(), ResultStatusCode.OK.getErrmsg(), odAndTime);
            return getTry(result, mapper);
        }
    }

    public String getTry(Object result, ObjectMapper mapper) {
        String resultMsg;
        try {
            resultMsg = mapper.writeValueAsString(result);
            logger.info("返回请求数据{}", resultMsg);
        } catch (IOException e) {
            resultMsg = e.getMessage();
            logger.error("请求出错，错误原因{}", resultMsg);
        }
        return resultMsg;
    }

    @RequestMapping(value = "/querySectionsCrowd.do", produces = "application/json;charset=utf-8")
    @CrossOrigin
    public String GetVolumeRatio(@RequestBody GetSectionCrowdNumInitialParameter sectionId) {
        List<CrowdNumResult> data = kspService.getSectionCrowdNumBySectionId(sectionId);
        ObjectMapper mapper = new ObjectMapper();
        if (data.size() == 0) {
            ReturnResult resultForm = new ReturnResult(ResultStatusCode.SYSTEM_ERR.getErrcode(), ResultStatusCode.SYSTEM_ERR.getErrmsg(), data);
            return getTry(resultForm, mapper);
        } else {
            ReturnResult resultForm = new ReturnResult(ResultStatusCode.OK.getErrcode(), ResultStatusCode.OK.getErrmsg(), data);
            return getTry(resultForm, mapper);
        }
    }
    @PostMapping("/modularSWJTU/tripPlan.do2")
    @ResponseBody
    public ResultMsg tripPlan(TripPlanVo tripPlanVo){
        return null;
    }
}
