package cn.edu.sicau.pfdistribution.controller;

import cn.edu.sicau.pfdistribution.service.kspdistribution.GetParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class GetParameterController {
    @Autowired
    GetParameters getParameters;

    @RequestMapping("/getParameter")
    public void TreeParameters(Double A, Double B, Double Coefficient, HttpServletResponse response){
        /**
         * @param A;
         * @param B;
         * @param Coefficient;
         * 修改分配系数和2个校正系数
         */
        try {
            getParameters.setA(A);
            getParameters.setB(B);
            getParameters.setCoefficient(Coefficient);
            response.setCharacterEncoding("utf-8");
            response.getWriter().println("提交成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
