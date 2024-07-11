package com.gestionPres.gestionPres.Models;

public class PresenceDto {

    private Long accs_id;
    private String accs_prsn;
    private String prs_name;
    private String prs_skill;
    private String accs_added_time;

    public PresenceDto() {
    }

    public PresenceDto(Long accs_id, String accs_prsn, String prs_name, String prs_skill, String accs_added_time) {
        this.accs_id = accs_id;
        this.accs_prsn = accs_prsn;
        this.prs_name = prs_name;
        this.prs_skill = prs_skill;
        this.accs_added_time = accs_added_time;
    }

    public Long getAccs_id() {
        return accs_id;
    }

    public void setAccs_id(Long accs_id) {
        this.accs_id = accs_id;
    }

    public String getAccs_prsn() {
        return accs_prsn;
    }

    public void setAccs_prsn(String accs_prsn) {
        this.accs_prsn = accs_prsn;
    }

    public String getPrs_name() {
        return prs_name;
    }

    public void setPrs_name(String prs_name) {
        this.prs_name = prs_name;
    }

    public String getPrs_skill() {
        return prs_skill;
    }

    public void setPrs_skill(String prs_skill) {
        this.prs_skill = prs_skill;
    }

    public String getAccs_added_time() {
        return accs_added_time;
    }

    public void setAccs_added_time(String accs_added_time) {
        this.accs_added_time = accs_added_time;
    }
}
