package com.gestionPres.gestionPres.dao;

import com.gestionPres.gestionPres.Models.Student;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudentDao extends JpaRepository<Student,Long> {

    @Query("SELECT MAX(s.id) FROM Student s")
    Integer findLastStudentId();
}
