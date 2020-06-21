package de.hse.licensemanager.dao;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.PrepareTests;
import de.hse.licensemanager.model.Credentials;

public class CredentialsDaoTest {

    @Before
    public void setupBeforeTest() {
        PrepareTests.initDatabase();
    }

    @Test
    public void testInstanceAllocated() {
        assertThat(CredentialsDao.getInstance(), notNullValue());
    }

    @Test
    public void testInstanceSingleton() {
        final CredentialsDao a = CredentialsDao.getInstance();
        final CredentialsDao b = CredentialsDao.getInstance();
        assertThat(a, sameInstance(b));
    }

    @Test
    public void testFindCredentialsById() {
        final Credentials mustermann = CredentialsDao.getInstance()
                .getCredentials(PrepareTests.CREDENTIALS_ID_MUSTERMANN);
        assertThat(mustermann, notNullValue());
    }

    @Test
    public void testFindCredentialsByLoginname() {
        final Credentials mustermann = CredentialsDao.getInstance()
                .getCredentialsByLoginname(PrepareTests.CREDENTIALS_LOGINNAME_MUSTERMANN);
        assertThat(mustermann, notNullValue());
    }

    @Test
    public void testFoundCredentialsDataPopulated() {
        final Credentials hannelore = CredentialsDao.getInstance()
                .getCredentials(PrepareTests.CREDENTIALS_ID_HANNELORE);

        assertThat(hannelore.getLoginname(), equalTo(PrepareTests.CREDENTIALS_LOGINNAME_HANNELORE));
        assertThat(hannelore.getPasswordHash(), not(nullValue()));
    }

    @Test
    public void testFoundCredentialsNestedDataPopulated() {
        final Credentials hannelore = CredentialsDao.getInstance()
                .getCredentials(PrepareTests.CREDENTIALS_ID_HANNELORE);

        assertThat(hannelore.getUser().getCompany().getAddress(), equalTo(PrepareTests.COMPANY_ADDRESS_NOTABROTHEL));
        assertThat(hannelore.getUser().getCompanyDepartment().getName(),
                equalTo(PrepareTests.COMPANY_DEPARTMENT_NAME_ACCOUNTING));
    }

    @Test
    public void testQueryAllCredentials() {
        final List<Credentials> credentials = CredentialsDao.getInstance().getCredentials();

        assertThat(credentials, not(empty()));
        assertThat(credentials, hasSize(3));
        assertThat(credentials.stream().map((u) -> u.getId()).collect(Collectors.toList()),
                containsInAnyOrder(PrepareTests.CREDENTIALS_ID_HANNELORE, PrepareTests.CREDENTIALS_ID_MUSTERMANN,
                        PrepareTests.CREDENTIALS_ID_DELETEME));
    }

    @Test
    public void testSaveSimple() {
        final Credentials credentials = new Credentials();
        credentials.setLoginname("max_mu");
        credentials.setPasswordHash(new byte[60]);

        CredentialsDao.getInstance().save(credentials);

        final List<Credentials> allCredentials = CredentialsDao.getInstance().getCredentials();

        assertThat(credentials.getId(),
                is(in(allCredentials.stream().map((c) -> c.getId()).collect(Collectors.toList()))));
    }

    @Test
    public void testDelete() {
        final Credentials credentials = CredentialsDao.getInstance()
                .getCredentials(PrepareTests.CREDENTIALS_ID_DELETEME);

        assertThat(credentials, notNullValue());
        assertThat(credentials, in(CredentialsDao.getInstance().getCredentials()));

        CredentialsDao.getInstance().delete(credentials.getId());

        assertThat(credentials, not(in(CredentialsDao.getInstance().getCredentials())));
    }
}
