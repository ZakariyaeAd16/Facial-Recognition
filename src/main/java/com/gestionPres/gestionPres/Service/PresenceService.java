package com.gestionPres.gestionPres.Service;

import com.gestionPres.gestionPres.Models.PresenceDto;
import com.gestionPres.gestionPres.dao.PresenceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PresenceService {
    @Autowired
    private PresenceDao presenceDao;

    public List<PresenceDto> getTodayPresences() {
        List<Object[]> results = presenceDao.findTodayPresences();
        return results.stream().map(result -> new PresenceDto(
                ((Number) result[0]).longValue(),  // Changé à longValue() pour obtenir un Long
                (String) result[1],                // accs_prsn
                (String) result[2],                // prs_name
                (String) result[3],                // prs_skill
                (String) result[4]                 // accs_added_time
        )).collect(Collectors.toList());
    }
}
