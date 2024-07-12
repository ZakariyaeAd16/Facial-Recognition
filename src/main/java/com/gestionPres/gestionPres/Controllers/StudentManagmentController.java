package com.gestionPres.gestionPres.Controllers;

import com.gestionPres.gestionPres.Models.Student;
import com.gestionPres.gestionPres.Models.StudentDto;
import com.gestionPres.gestionPres.dao.StudentDao;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

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


@Controller
@RequestMapping("/student")
public class StudentManagmentController {

    @Autowired
    private StudentDao dao;



    @GetMapping({"", "/"})
    public String showStudenttList(Model model) {
        List<Student> students = dao.findAll();
        model.addAttribute("students", students);
        return "AdminSide/Student/studentManagment";
    }

    @GetMapping("/export-excel")
    public ResponseEntity<ByteArrayResource> exportStudentsToExcel() throws IOException {
        List<Student> students = dao.findAll();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(out)) {

            writer.println("<html>");
            writer.println("<head>");
            writer.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
            writer.println("</head>");
            writer.println("<body>");
            writer.println("<table>");

            // En-tête
            writer.println("<tr>");
            writer.println("<th>ID</th>");
            writer.println("<th>Prénom</th>");
            writer.println("<th>Nom</th>");
            writer.println("<th>Email</th>");
            writer.println("<th>Téléphone</th>");
            writer.println("<th>Filière</th>");
            writer.println("<th>Matricule</th>");
            writer.println("<th>Date de naissance</th>");
            writer.println("<th>Date de création</th>");

            writer.println("</tr>");

            // Données
            for (Student student : students) {
                writer.println("<tr>");
                writer.println("<td>" + student.getId() + "</td>");
                writer.println("<td>" + escapeHtml(student.getFirstName()) + "</td>");
                writer.println("<td>" + escapeHtml(student.getLastName()) + "</td>");
                writer.println("<td>" + escapeHtml(student.getEmail()) + "</td>");
                writer.println("<td>" + escapeHtml(student.getPhone()) + "</td>");
                writer.println("<td>" + escapeHtml(student.getFiliere()) + "</td>");
                writer.println("<td>" + escapeHtml(student.getMatriculate()) + "</td>");
                writer.println("<td>" + (student.getBirthday() != null ? dateFormat.format(student.getBirthday()) : "") + "</td>");
                writer.println("<td>" + (student.getCreatedAt() != null ? dateFormat.format(student.getCreatedAt()) : "") + "</td>");

                writer.println("</tr>");
            }

            writer.println("</table>");
            writer.println("</body>");
            writer.println("</html>");

            writer.flush();

            ByteArrayResource resource = new ByteArrayResource(out.toByteArray());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students.xls")
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
        }
    }

    private String escapeHtml(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }


    @GetMapping("/add")
    public String showCreatePage(Model model) {
        Integer lastId = dao.findLastStudentId();
        Integer currentId = (lastId != null) ? lastId + 1 : 1;

        StudentDto studentDto = new StudentDto();
        model.addAttribute("studentDto", studentDto);
        model.addAttribute("currentId", currentId);
        return "AdminSide/Student/addNewStudent";
    }

    @PostMapping("/add")
    public String createStudent(
            @Valid @ModelAttribute("studentDto") StudentDto studentDto,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        if (studentDto.getImageFile().isEmpty()) {
            result.addError(new FieldError("studentDto", "imageFile", "The image file is required"));
        }
        if (result.hasErrors()) {
            return "AdminSide/Student/addNewStudent";
        }

        MultipartFile image = studentDto.getImageFile();
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
            result.addError(new FieldError("studentDto", "imageFile", "Failed to save image file: " + e.getMessage()));
            return "AdminSide/Student/addNewStudent";
        }

        Student student = new Student();
        student.setFirstName(studentDto.getFirstName());
        student.setLastName(studentDto.getLastName());
        student.setEmail(studentDto.getEmail());
        student.setPhone(studentDto.getPhone());
        student.setFiliere(studentDto.getFiliere());
        student.setMatriculate(studentDto.getMatriculate());
        student.setBirthday(studentDto.getBirthday());
        student.setImageFileName(storageFileName);
        student.setCreatedAt(createdAt);

        dao.save(student);

        // Construire l'URL avec les paramètres de requête
        String flaskUrl = String.format(
                "http://localhost:5000/addprsn?id=%d&firstName=%s&lastName=%s",
                student.getId(), student.getFirstName(), student.getLastName()
        );

        redirectAttributes.addFlashAttribute("successMessage", "Student created successfully!");

        // Redirection vers l'URL Flask
        return "redirect:" + flaskUrl;
    }


    @GetMapping("/studentDetails")
    public String showStudentDetails(Model model, @RequestParam Long id) {
        try {
            Student students = dao.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid student ID: " + id));
            model.addAttribute("students", students);
            return "AdminSide/Student/studentDetails"; // Remplacez par le nom de votre vue pour les détails de l'étudiant
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/student"; // Redirige vers la liste des étudiants en cas d'erreur
        }
    }

    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam Long id) {
        try {
            Student student = dao.findById(id).orElseThrow(() -> new RuntimeException("Student not found"));
            model.addAttribute("student", student);

            StudentDto studentDto = new StudentDto();
            studentDto.setFirstName(student.getFirstName());
            studentDto.setLastName(student.getLastName());
            studentDto.setEmail(student.getEmail());
            studentDto.setPhone(student.getPhone());
            studentDto.setFiliere(student.getFiliere());
            studentDto.setMatriculate(student.getMatriculate());
            studentDto.setBirthday(student.getBirthday());

            model.addAttribute("studentDto", studentDto);

        } catch (Exception ex) {
            System.out.println("Exception : " + ex.getMessage());
            return "redirect:/student";
        }
        return "AdminSide/Student/editStudent";
    }

    @PostMapping("/edit")
    public String updateStudent(
            Model model,
            @RequestParam Long id,
            @Valid @ModelAttribute StudentDto studentDto,
            BindingResult result
    ){
        try {
            Student student = dao.findById(id).get();
            model.addAttribute("student",student);
            if (result.hasErrors()) {
                return "AdminSide/Student/editStudent";
            }

            if (!studentDto.getImageFile().isEmpty()){
                //delete old image
                String uploadDir = "public/image/";
                Path oldImagePath = Paths.get(uploadDir + student.getImageFileName());
                try {
                    Files.delete(oldImagePath);
                }catch (Exception ex) {
                    System.out.println("Exception : " + ex.getMessage());
                }

                //save new image file

                MultipartFile image = studentDto.getImageFile();
                Date createdAt = new Date();
                String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                            StandardCopyOption.REPLACE_EXISTING);
                }

                student.setImageFileName(storageFileName);
            }

            student.setFirstName(studentDto.getFirstName());
            student.setLastName(studentDto.getLastName());
            student.setEmail(studentDto.getEmail());
            student.setPhone(studentDto.getPhone());
            student.setFiliere(studentDto.getFiliere());
            student.setMatriculate(studentDto.getMatriculate());
            student.setBirthday(studentDto.getBirthday());

            dao.save(student);

        }catch (Exception ex) {
            System.out.println("Exception : " + ex.getMessage());
        }

        return "redirect:/student";
    }

    @GetMapping("/delete")
    public String deleteStudent(
            @RequestParam Long id
    ){

        try {
            Student student = dao.findById(id).get();

            //delete product image
            Path imagePath = Paths.get("public/image/" + student.getImageFileName());
            try {
                Files.delete(imagePath);
            }catch (Exception ex) {
                System.out.println("Exception : " + ex.getMessage());
            }

            dao.delete(student);

        }catch (Exception ex) {
            System.out.println("Exception : " + ex.getMessage());
        }
        return "redirect:/student";
    }

}
