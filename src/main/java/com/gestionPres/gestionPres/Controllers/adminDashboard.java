package com.gestionPres.gestionPres.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class adminDashboard {

    @GetMapping({"", "/"})
    public String showAdminDashboard(){
        return "AdminSide/Student/index";
    }
}
