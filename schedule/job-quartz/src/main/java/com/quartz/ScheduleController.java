package com.quartz;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@Api(value = "Quartz管理", description = "Quartz管理")
@RestController
public class ScheduleController {

    @ApiOperation(value="Quartz管理")
    @RequestMapping(value = "/getInfo", method = RequestMethod.GET)
    public String getInfo(@RequestParam("id") String id,
                          HttpServletRequest request){


        return "success";
    }



}
