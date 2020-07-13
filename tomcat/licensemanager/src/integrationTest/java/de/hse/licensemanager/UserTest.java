package de.hse.licensemanager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import org.junit.Before;
import org.junit.Test;

import de.hse.licensemanager.dao.CompanyDao;
import de.hse.licensemanager.dao.CompanyDepartmentDao;
import de.hse.licensemanager.dao.UserDao;
import de.hse.licensemanager.model.Company;
import de.hse.licensemanager.model.CompanyDepartment;
import de.hse.licensemanager.model.Credentials;
import de.hse.licensemanager.model.User;
import de.hse.licensemanager.model.User.Group;

public class UserTest {
    private Client client;
    private String restURI;

    private static final String BY_COMPANY_DEPARTMENT_ENDPOINT = "/by-company-department";
    private static final String BY_COMPANY_ENDPOINT = "/by-company";
    private static final String COUNT_ENDPOINT = "/count";
    private static final String GROUP_TYPES_ENDPOINT = "/group-types";
    private static final String ME_ENDPOINT = "/me";

    @Before
    public void setUp() {
        UnitTestSupport.initDatabase();

        client = ClientBuilder.newClient();
        restURI = IntegrationTestSupport.getRestURI() + "/users";
    }

    @Test
    public void testGetCount() {
        final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                UnitTestSupport.CREDENTIALS_LOGINNAME_HANNELORE, UnitTestSupport.CREDENTIALS_PASSWORD_PLAIN_HANNELORE);

        final Invocation.Builder b = client.target(restURI + COUNT_ENDPOINT).request(MediaType.TEXT_PLAIN);
        cookies.forEach(b::cookie);
        final Response response = b.buildGet().invoke();

        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(Integer.parseInt(response.readEntity(String.class)), is(UserDao.getInstance().getUsers().size()));
    }

    @Test
    public void testGetGroupTypes() {
        final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                UnitTestSupport.CREDENTIALS_LOGINNAME_HANNELORE, UnitTestSupport.CREDENTIALS_PASSWORD_PLAIN_HANNELORE);

        final Invocation.Builder b = client.target(restURI + GROUP_TYPES_ENDPOINT).request(MediaType.APPLICATION_JSON);
        cookies.forEach(b::cookie);
        final Response response = b.buildGet().invoke();

        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(response.readEntity(new GenericType<List<Group>>() {
        }), everyItem(in(Group.values())));
    }

    @Test
    public void testGetMe() {
        final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                UnitTestSupport.CREDENTIALS_LOGINNAME_HANNELORE, UnitTestSupport.CREDENTIALS_PASSWORD_PLAIN_HANNELORE);

        final Invocation.Builder b = client.target(restURI + ME_ENDPOINT).request(MediaType.APPLICATION_JSON);
        cookies.forEach(b::cookie);
        final Response response = b.buildGet().invoke();

        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(response.readEntity(User.class),
                is(equalTo(UserDao.getInstance().getUser(UnitTestSupport.USER_ID_HANNELORE))));
    }

    @Test
    public void testGetMeNotLoggedIn() {
        final Invocation.Builder b = client.target(restURI + ME_ENDPOINT).request(MediaType.APPLICATION_JSON);
        final Response response = b.buildGet().invoke();

        assertThat(response.getStatus(), is(Response.Status.UNAUTHORIZED.getStatusCode()));
    }

    @Test
    public void testPutMe() {
        final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                UnitTestSupport.CREDENTIALS_LOGINNAME_HANNELORE, UnitTestSupport.CREDENTIALS_PASSWORD_PLAIN_HANNELORE);

        final User originalUser = UserDao.getInstance().getUser(UnitTestSupport.USER_ID_HANNELORE);
        final User modifiedUser = new User(originalUser.getFirstname(), "Schweizer", "greta.schweizer@email.com", null,
                null, null);

        final Invocation.Builder b = client.target(restURI + ME_ENDPOINT).request(MediaType.APPLICATION_JSON);
        cookies.forEach(b::cookie);
        final Response response = b.buildPut(Entity.json(modifiedUser)).invoke();

        assertThat(response.getStatus(), is(Response.Status.CREATED.getStatusCode()));
    }

    @Test
    public void testGetUsersByCompany() {
        final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                UnitTestSupport.CREDENTIALS_LOGINNAME_MUSTERMANN,
                UnitTestSupport.CREDENTIALS_PASSWORD_PLAIN_MUSTERMANN);

        final Company company = CompanyDao.getInstance().getCompany(UnitTestSupport.COMPANY_ID_LICENSEMANAGER);
        final List<User> expectedUsers = company.getDepartments().stream().flatMap((dep) -> dep.getUsers().stream())
                .collect(Collectors.toList());

        final Invocation.Builder b = client
                .target(restURI + String.format("%s/%d", BY_COMPANY_ENDPOINT, company.getId()))
                .request(MediaType.APPLICATION_JSON);
        cookies.forEach(b::cookie);
        final Response response = b.buildGet().invoke();

        final List<User> users = response.readEntity(new GenericType<List<User>>() {
        });

        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertThat(users, not(empty()));
        assertThat(users, hasSize(expectedUsers.size()));
        assertThat(users, everyItem(in(expectedUsers)));
    }

    @Test
    public void testGetUsersByCompanyDepartment() {
        final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                UnitTestSupport.CREDENTIALS_LOGINNAME_MUSTERMANN,
                UnitTestSupport.CREDENTIALS_PASSWORD_PLAIN_MUSTERMANN);

        final CompanyDepartment company = CompanyDepartmentDao.getInstance()
                .getCompanyDepartment(UnitTestSupport.COMPANY_DEPARTMENT_ID_IT);
        final List<User> expectedUsers = company.getUsers();

        final Invocation.Builder b = client
                .target(restURI + String.format("%s/%d", BY_COMPANY_DEPARTMENT_ENDPOINT, company.getId()))
                .request(MediaType.APPLICATION_JSON);
        cookies.forEach(b::cookie);
        final Response response = b.buildGet().invoke();

        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        final List<User> users = response.readEntity(new GenericType<List<User>>() {
        });
        assertThat(users, not(empty()));
        assertThat(users, hasSize(expectedUsers.size()));
        assertThat(users, everyItem(in(expectedUsers)));
    }

    @Test
    public void testCreateNewUser() {
        final Collection<NewCookie> cookies = IntegrationTestSupport.login(client,
                UnitTestSupport.CREDENTIALS_LOGINNAME_MUSTERMANN,
                UnitTestSupport.CREDENTIALS_PASSWORD_PLAIN_MUSTERMANN);

        final User newUser = new User("Greg", "Long", "greg.long@email.com",
                CompanyDepartmentDao.getInstance().getCompanyDepartment(UnitTestSupport.COMPANY_DEPARTMENT_ID_IT),
                Group.USER, new Credentials("greg", "greg123") {
                    @JsonProperty(access = Access.READ_WRITE)
                    private byte passwordHash[];

                    @JsonProperty(access = Access.READ_WRITE)
                    private byte[] passwordSalt;

                    @JsonProperty(access = Access.READ_WRITE)
                    private int passwordIterations;
                });

        final Invocation.Builder b = client.target(restURI).request(MediaType.APPLICATION_JSON);
        cookies.forEach(b::cookie);
        final Response response = b.buildPost(Entity.json(newUser)).invoke();

        assertThat(response.getStatus(), is(Response.Status.CREATED.getStatusCode()));

        final User createdUser = response.readEntity(User.class);

        assertThat(createdUser.getCredentials().getLoginname(), equalTo(newUser.getCredentials().getLoginname()));
        assertThat(createdUser.getCredentials().getPasswordHash(), nullValue());
        assertThat(createdUser.getCredentials().getPasswordIterations(), equalTo(0));
        assertThat(createdUser.getCredentials().getPasswordSalt(), nullValue());
        assertThat(newUser.getFirstname(), equalTo(createdUser.getFirstname()));
        assertThat(newUser.getLastname(), equalTo(createdUser.getLastname()));
        assertThat(newUser.getEmail(), equalTo(createdUser.getEmail()));
        assertThat(newUser.getGroup(), equalTo(createdUser.getGroup()));
        assertThat(newUser.getCompany(), equalTo(createdUser.getCompany()));
        assertThat(createdUser, in(UserDao.getInstance().getUsers()));
    }
}
