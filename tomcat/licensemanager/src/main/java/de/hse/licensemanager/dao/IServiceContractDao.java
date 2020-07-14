package de.hse.licensemanager.dao;

import java.util.List;

import de.hse.licensemanager.model.Company;
import de.hse.licensemanager.model.ServiceContract;
import de.hse.licensemanager.model.User;

public interface IServiceContractDao {

    public List<ServiceContract> getServiceContracts();

    public List<ServiceContract> getServiceContractsByContractor(final Company contractor);

    public List<ServiceContract> getServiceContractsByContractor(final long id);

    public List<ServiceContract> getServiceContractsByUser(final long id);

    public List<ServiceContract> getServiceContractsByUser(final User user);

    public ServiceContract getServiceContract(final long id);

    public void delete(final long id);

    public void delete(final ServiceContract serviceContract);

    public void modify(final long idToModify, final ServiceContract other);

    public void refresh(final ServiceContract serviceContract);

    public void save(final ServiceContract serviceContract);
}
