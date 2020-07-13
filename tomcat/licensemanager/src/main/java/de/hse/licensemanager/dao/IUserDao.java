package de.hse.licensemanager.dao;

import java.util.List;

import de.hse.licensemanager.model.User;

public interface IUserDao {

    public List<User> getUsersByCompany(long id);

    public List<User> getUsers();

    public User getUser(final long id);

    public void delete(final long id);

    public void delete(final User user);

    public void modify(final long idToModify, final User other);

    public void refresh(final User user);

    public void save(final User user);
}
