package com.gestionPres.gestionPres.dao;

import com.gestionPres.gestionPres.Models.Presence;
import com.gestionPres.gestionPres.Models.PresenceDto;
import com.gestionPres.gestionPres.Models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PresenceDao extends JpaRepository<Presence, Integer> {
    @Query(value = "SELECT a.accs_id, a.accs_prsn, b.prs_name, b.prs_skill, DATE_FORMAT(a.accs_added, '%Y-%m-%d %H:%i:%s') as accs_added_time " +
            "FROM presence_historique a " +
            "LEFT JOIN prs_mstr b ON a.accs_prsn = b.prs_nbr " +
            "WHERE DATE(a.accs_added) = CURDATE() " +
            "ORDER BY a.accs_id DESC", nativeQuery = true)
    List<Object[]> findTodayPresences();
}
