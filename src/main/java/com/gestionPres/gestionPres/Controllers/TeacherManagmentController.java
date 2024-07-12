package com.gestionPres.gestionPres.Controllers;

import com.gestionPres.gestionPres.Models.Student;
import com.gestionPres.gestionPres.Models.StudentDto;
import com.gestionPres.gestionPres.Models.Teacher;
import com.gestionPres.gestionPres.Models.TeacherDto;
import com.gestionPres.gestionPres.dao.StudentDao;
import com.gestionPres.gestionPres.dao.TeacherDao;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.unbescape.html.HtmlEscape.escapeHtml;

@Controller
@RequestMapping("/teacher")
public class TeacherManagmentController {
    @Autowired
    private TeacherDao dao;

    @GetMapping({"", "/"})
    public String showTeacherList(Model model) {
        List<Teacher> teachers = dao.findAll();
        model.addAttribute("teachers", teachers);
        return "Teacher/teacherManagment";
    }

    @GetMapping("/add")
    public String showCreatePage(Model model) {
        Integer lastId = dao.findLastTeacherId();
        Integer currentId = (lastId != null) ? lastId + 1 : 1;

        TeacherDto teacherDto = new TeacherDto();
        model.addAttribute("teacherDto", teacherDto);
        model.addAttribute("currentId", currentId);
        return "Teacher/addNewTeacher";
    }

    @PostMapping("/add")
    public String createTeacher(
            @Valid @ModelAttribute("teacherDto") TeacherDto teacherDto,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        if (teacherDto.getImageFile().isEmpty()) {
            result.addError(new FieldError("studentDto", "imageFile", "The image file is required"));
        }
        if (result.hasErrors()) {
            return "AdminSide/Student/addNewStudent";
        }

        MultipartFile image = teacherDto.getImageFile();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

        try {
            String uploadDir = "public/image/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, uploadPath.resolve(storageFileName), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            result.addError(new FieldError("teacherDto", "imageFile", "Failed to save image file: " + e.getMessage()));
            return "AdminSide/Student/addNewStudent";
        }

        Teacher teacher = new Teacher();
        teacher.setFirstName(teacherDto.getFirstName());
        teacher.setLastName(teacherDto.getLastName());

        teacher.setPhone(teacherDto.getPhone());
        teacher.setDepartement(teacherDto.getDepartement());
        teacher.setMatriculate(teacherDto.getMatriculate());

        teacher.setImageFileName(storageFileName);
        teacher.setCreatedAt(createdAt);

        dao.save(teacher);

        // Construire l'URL avec les paramètres de requête
        String flaskUrl = String.format(
                "http://localhost:5000/addprsn?id=%d&firstName=%s&lastName=%s",
                teacher.getId(), teacher.getFirstName(), teacher.getLastName()
        );

        redirectAttributes.addFlashAttribute("successMessage", "Teacher created successfully!");

        // Redirection vers l'URL Flask
        return "redirect:" + flaskUrl;
    }

    @GetMapping("/teacherDetails")
    public String showTeacherDetails(Model model, @RequestParam Long id) {
        try {
            Teacher teachers = dao.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid teacher ID: " + id));
            model.addAttribute("teachers", teachers);
            return "Teacher/teacherDetails"; // Remplacez par le nom de votre vue pour les détails de l'étudiant
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/teacher"; // Redirige vers la liste des étudiants en cas d'erreur
        }
    }

    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam Long id) {
        try {
            Teacher teacher = dao.findById(id).orElseThrow(() -> new RuntimeException("Teacher not found"));
            model.addAttribute("teacher", teacher);

            TeacherDto teacherDto = new TeacherDto();
            teacherDto.setFirstName(teacher.getFirstName());
            teacherDto.setLastName(teacher.getLastName());

            teacherDto.setPhone(teacher.getPhone());
            teacherDto.setDepartement(teacher.getDepartement());
            teacherDto.setMatriculate(teacher.getMatriculate());


            model.addAttribute("teacherDto", teacherDto);

        } catch (Exception ex) {
            System.out.println("Exception : " + ex.getMessage());
            return "redirect:/teacher";
        }
        return "Teacher/editTeacher";
    }

    @PostMapping("/edit")
    public String updateTeacher(
            Model model,
            @RequestParam Long id,
            @Valid @ModelAttribute TeacherDto teacherDto,
            BindingResult result
    ){
        try {
            Teacher teacher = dao.findById(id).get();
            model.addAttribute("student",teacher);
            if (result.hasErrors()) {
                return "AdminSide/Student/editStudent";
            }

            if (!teacherDto.getImageFile().isEmpty()){
                //delete old image
                String uploadDir = "public/image/";
                Path oldImagePath = Paths.get(uploadDir + teacher.getImageFileName());
                try {
                    Files.delete(oldImagePath);
                }catch (Exception ex) {
                    System.out.println("Exception : " + ex.getMessage());
                }

                //save new image file

                MultipartFile image = teacherDto.getImageFile();
                Date createdAt = new Date();
                String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                            StandardCopyOption.REPLACE_EXISTING);
                }

                teacher.setImageFileName(storageFileName);
            }

            teacher.setFirstName(teacherDto.getFirstName());
            teacher.setLastName(teacherDto.getLastName());

            teacher.setPhone(teacherDto.getPhone());
            teacher.setDepartement(teacherDto.getDepartement());
            teacher.setMatriculate(teacherDto.getMatriculate());


            dao.save(teacher);

        }catch (Exception ex) {
            System.out.println("Exception : " + ex.getMessage());
        }

        return "redirect:/teacher";
    }

    @GetMapping("/delete")
    public String deleteTeacher(
            @RequestParam Long id
    ){

        try {
            Teacher teacher = dao.findById(id).get();

            //delete product image
            Path imagePath = Paths.get("public/image/" + teacher.getImageFileName());
            try {
                Files.delete(imagePath);
            }catch (Exception ex) {
                System.out.println("Exception : " + ex.getMessage());
            }

            dao.delete(teacher);

        }catch (Exception ex) {
            System.out.println("Exception : " + ex.getMessage());
        }
        return "redirect:/teacher";
    }


}
