package com.vz.encrypt.demo.controller;

import com.vz.encrypt.demo.common.SysMessage;
import com.vz.encrypt.demo.dto.UserDto;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @author visy.wang
 * @description: 测试接口
 * @date 2023/7/25 9:42
 */
@RestController
@RequestMapping("/test")
public class TestController {
    @RequestMapping("/hello1")
    public SysMessage hello1(String name){
        return SysMessage.success(name);
    }

    @PostMapping("/hello2")
    public SysMessage hello2(@RequestBody UserDto userDto,
                             String name,
                             @RequestParam Integer age,
                             HttpServletRequest request){

        Enumeration<String> parameterNames = request.getParameterNames();
        System.out.println("-----------------------------------");
        while (parameterNames.hasMoreElements()){
            String key = parameterNames.nextElement();
            System.out.println(key+": "+ request.getParameter(key));
            System.out.println("-----------------------------------");
        }

        System.out.println("===================================");
        System.out.println("name="+ name);
        System.out.println("age="+ age);
        System.out.println("===================================");


        return SysMessage.success(userDto);
    }
}
