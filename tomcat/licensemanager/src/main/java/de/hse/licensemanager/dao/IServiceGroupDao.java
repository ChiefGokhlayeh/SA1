package de.hse.licensemanager.dao;

import java.util.List;

import de.hse.licensemanager.model.ServiceContract;
import de.hse.licensemanager.model.ServiceGroup;
import de.hse.licensemanager.model.ServiceGroupId;
import de.hse.licensemanager.model.User;

public interface IServiceGroupDao {

    public List<ServiceGroup> getServiceGroupsByServiceContract(final long serviceContractId);

    public List<ServiceGroup> getServiceGroupsByServiceContract(final ServiceContract serviceContract);

    public List<ServiceGroup> getServiceGroupsByUser(final long userId);

    public List<ServiceGroup> getServiceGroupsByUser(final User user);

    public List<ServiceGroup> getServiceGroups();

    public ServiceGroup getServiceGroup(final long serviceContractId, final long userId);

    public ServiceGroup getServiceGroup(final ServiceGroupId id);

    public void delete(final long serviceContractId, final long userId);

    public void delete(final ServiceGroup serviceGroup);

    public void delete(final ServiceGroupId id);

    public void modify(final long serviceContractId, final long userId, final ServiceGroup other);

    public void modify(final ServiceGroupId idToModify, final ServiceGroup other);

    public void refresh(final ServiceGroup serviceGroup);

    public void save(final ServiceGroup serviceGroup);
}
