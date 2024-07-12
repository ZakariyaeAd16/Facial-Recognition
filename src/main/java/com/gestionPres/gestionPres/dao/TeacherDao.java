package com.gestionPres.gestionPres.dao;

import com.gestionPres.gestionPres.Models.Student;
import com.gestionPres.gestionPres.Models.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TeacherDao extends JpaRepository<Teacher,Long> {
    @Query("SELECT MAX(t.id) FROM Teacher t")
    Integer findLastTeacherId();
}
