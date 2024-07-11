package com.gestionPres.gestionPres.Controllers;

import com.gestionPres.gestionPres.Models.Presence;
import com.gestionPres.gestionPres.Models.PresenceDto;
import com.gestionPres.gestionPres.Models.Student;
import com.gestionPres.gestionPres.Service.PresenceService;
import com.gestionPres.gestionPres.dao.PresenceDao;
import com.gestionPres.gestionPres.dao.StudentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
@RequestMapping("/Presence")
public class PresenceController {


    @Autowired
    private PresenceService presenceService;


    @GetMapping({"", "/"})
    public String showPresences(Model model) {
        List<PresenceDto> presences = presenceService.getTodayPresences();
        model.addAttribute("presences", presences);
        return "AdminSide/Student/presenceHistorique";
    }


}
