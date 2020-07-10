package de.hse.licensemanager.dao;

import java.util.List;

import de.hse.licensemanager.model.Credentials;

public interface ICredentialsDao {

    public Credentials getCredentials(final long id);

    public Credentials getCredentialsByLoginname(final String login);

    public List<Credentials> getCredentials();

    public void delete(final Credentials credentials);

    public void delete(final long id);

    public void modify(final long idToModify, final Credentials other);

    public void refresh(final Credentials credentials);

    public void save(final Credentials credentials);
}
