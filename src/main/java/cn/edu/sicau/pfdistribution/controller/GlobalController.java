package cn.edu.sicau.pfdistribution.controller;

import cn.edu.sicau.pfdistribution.dao.betterSave.GlobalDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GlobalController {
    @Autowired
    private GlobalDao globalDao;

    @GetMapping("/create_global")
    public void createGlobal() {
        globalDao.createGlobal();
    }
}
