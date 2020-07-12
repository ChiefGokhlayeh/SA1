package de.hse.licensemanager.dao;

import java.util.List;

import de.hse.licensemanager.model.Company;
import de.hse.licensemanager.model.ServiceContract;

public interface IServiceContractDao {

    public List<ServiceContract> getServiceContracts();

    public List<ServiceContract> getServiceContractsByCompany(final Company company);

    public ServiceContract getServiceContract(final long id);

    public void delete(final long id);

    public void delete(final ServiceContract serviceContract);

    public void modify(final long idToModify, final ServiceContract other);

    public void refresh(final ServiceContract serviceContract);

    public void save(final ServiceContract serviceContract);
}
