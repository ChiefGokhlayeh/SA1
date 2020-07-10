package de.hse.licensemanager.dao;

import java.util.List;

import de.hse.licensemanager.model.IpMapping;

public interface IIpMappingDao {

    public IpMapping getIpMapping(final long id);

    public List<IpMapping> getIpMappings();

    public void delete(final IpMapping ipMapping);

    public void delete(final long id);

    public void modify(final long idToModify, final IpMapping other);

    public void refresh(final IpMapping ipMapping);

    public void save(final IpMapping ipMapping);
}
