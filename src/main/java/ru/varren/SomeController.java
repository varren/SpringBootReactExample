package ru.varren;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SomeController {

    // this controller shows that you can override any url you would like to
    @RequestMapping("/map")
    public String testController(){
        return "Works fine";
    }
}
