//: com.yulikexuan.ssl.app.controllers.RestAssuredController.java


package com.yulikexuan.ssl.app.controllers;


import com.yulikexuan.ssl.app.model.OddDataDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/api/restassured")
public class RestAssuredController {

    @GetMapping("/anonymous")
    public int[] anonymousJsonRoot() {
        return new int[] {1, 2, 3};
    }

    @GetMapping("/odd")
    public OddDataDto oddData() {
        return new OddDataDto();
    }

}///:~