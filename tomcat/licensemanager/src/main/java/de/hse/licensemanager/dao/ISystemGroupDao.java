package de.hse.licensemanager.dao;

import java.util.List;

import de.hse.licensemanager.model.SystemGroup;

public interface ISystemGroupDao {

    public SystemGroup getSystemGroup(final long id);

    public List<SystemGroup> getSystemGroups();

    public void delete(final SystemGroup systemGroup);

    public void delete(final long id);

    public void modify(final long idToModify, final SystemGroup other);

    public void refresh(final SystemGroup systemGroup);

    public void save(final SystemGroup systemGroup);
}
