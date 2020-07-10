package de.hse.licensemanager.dao;

import java.util.List;

import de.hse.licensemanager.model.Company;

public interface ICompanyDao {

    public Company getCompany(final long id);

    public List<Company> getCompanies();

    public void delete(final Company company);

    public void delete(final long id);

    public void modify(final long idToModify, final Company other);

    public void refresh(final Company company);

    public void save(final Company company);
}
