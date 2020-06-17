package de.hse.licensemanager.model;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "t_service_group")
@IdClass(ServiceGroupId.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@query_id")
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

    public void setServiceContract(final ServiceContract serviceContract) {
        this.serviceContract = serviceContract;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceContract, user);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other)
            return true;

        if (other == null || other.getClass() != this.getClass())
            return false;

        final ServiceGroup otherServiceGroup = (ServiceGroup) other;
        return Objects.equals(this.serviceContract, otherServiceGroup.serviceContract)
                && Objects.equals(this.user, otherServiceGroup.user);
    }
}
