package de.hse.licensemanager.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;

@Embeddable
public class ServiceGroupId implements Serializable {
    private static final long serialVersionUID = 6038550433903536523L;

    private final long serviceContract;

    private final long user;

    public ServiceGroupId(final long serviceContract, final long user) {
        this.serviceContract = serviceContract;
        this.user = user;
    }

    public long getServiceContract() {
        return serviceContract;
    }

    public long getUser() {
        return user;
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

        final ServiceGroupId otherServiceGroupId = (ServiceGroupId) other;
        return Objects.equals(otherServiceGroupId.serviceContract, serviceContract)
                && Objects.equals(otherServiceGroupId.user, user);
    }
}
