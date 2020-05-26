package de.hse.licensemanager;

import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.persistence.Column;

@Entity
@Table(name = "t_system_group")
public class SystemGroup {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    @Column(name = "displayname")
    private String displayName;

    public long getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }
}
