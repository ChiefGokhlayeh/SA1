package de.hse.licensemanager.dao;

import java.util.List;

import de.hse.licensemanager.model.IpMapping;
import de.hse.licensemanager.model.License;

public interface IIpMappingDao {

    public List<IpMapping> getIpMappingsByLicense(final long id);

    public List<IpMapping> getIpMappingsByLicense(final License license);

    public IpMapping getIpMapping(final long id);

    public List<IpMapping> getIpMappings();

    public void delete(final IpMapping ipMapping);

    public void delete(final long id);

    public void modify(final long idToModify, final IpMapping other);

    public void refresh(final IpMapping ipMapping);

    public void save(final IpMapping ipMapping);
}
