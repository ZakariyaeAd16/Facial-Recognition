package com.gestionPres.gestionPres.Models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class TeacherDto {

    @NotEmpty(message = "The First Name is required")
    private String firstName;

    @NotEmpty(message = "The Last Name is required")
    private String lastName;
    @NotEmpty(message = "The Phone is required")
    @Size(min = 10, message = "Minimum Phone number is 10 numbers")
    private String phone;
    @NotEmpty(message = "The Class is required")
    private String departement;
    @NotEmpty(message = "The Matriculate is required")
    private String matriculate;
    private MultipartFile imageFile;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public String getMatriculate() {
        return matriculate;
    }

    public void setMatriculate(String matriculate) {
        this.matriculate = matriculate;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }
}
