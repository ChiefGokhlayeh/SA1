package de.hse.licensemanager.dao;

import java.util.List;

import de.hse.licensemanager.model.License;
import de.hse.licensemanager.model.ServiceContract;
import de.hse.licensemanager.model.User;

public interface ILicenseDao {

    public License getLicense(final long id);

    public List<License> getLicenses();

    public List<License> getLicensesByServiceContract(final long id);

    public List<License> getLicensesByServiceContract(final ServiceContract serviceContract);

    public List<License> getLicensesByUser(final long id);

    public List<License> getLicensesByUser(final User user);

    public void delete(final License license);

    public void delete(final long id);

    public void modify(final long idToModify, final License other);

    public void refresh(final License license);

    public void save(final License license);
}
