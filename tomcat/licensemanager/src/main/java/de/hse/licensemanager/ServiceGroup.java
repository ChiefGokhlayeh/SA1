package de.hse.licensemanager.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "t_service_group")
@IdClass(ServiceGroupId.class)
public class ServiceGroup {
    @Id
    @JoinColumn(name = "service_contract")
    private ServiceContract serviceContract;

    @Id
    @JoinColumn(name = "`user`")
    private User user;

    public ServiceContract getServiceContract() {
        return serviceContract;
    }

    public User getUser() {
        return user;
    }
}
