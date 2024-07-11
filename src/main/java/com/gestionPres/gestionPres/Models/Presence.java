package com.gestionPres.gestionPres.Models;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "presence_historique")
public class Presence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accs_id")
    private Integer accsId;

    @Column(name = "accs_date", nullable = false)
    private LocalDate accsDate;

    @Column(name = "accs_prsn", nullable = false, length = 3)
    private String accsPrsn;

    @Column(name = "accs_added", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime accsAdded;

    public Integer getAccsId() {
        return accsId;
    }

    public void setAccsId(Integer accsId) {
        this.accsId = accsId;
    }

    public LocalDate getAccsDate() {
        return accsDate;
    }

    public void setAccsDate(LocalDate accsDate) {
        this.accsDate = accsDate;
    }

    public String getAccsPrsn() {
        return accsPrsn;
    }

    public void setAccsPrsn(String accsPrsn) {
        this.accsPrsn = accsPrsn;
    }

    public LocalDateTime getAccsAdded() {
        return accsAdded;
    }

    public void setAccsAdded(LocalDateTime accsAdded) {
        this.accsAdded = accsAdded;
    }
}
