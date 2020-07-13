package de.hse.licensemanager.dao;

import java.util.List;

import de.hse.licensemanager.model.CompanyDepartment;

public interface ICompanyDepartmentDao {

    public List<CompanyDepartment> getCompanyDepartmentsByCompany(final long id);

    public CompanyDepartment getCompanyDepartment(final long id);

    public List<CompanyDepartment> getCompanyDepartments();

    public void delete(final CompanyDepartment department);

    public void delete(final long id);

    public void modify(final long idToModify, final CompanyDepartment other);

    public void refresh(final CompanyDepartment department);

    public void save(final CompanyDepartment department);
}
