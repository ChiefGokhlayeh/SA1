import "./App.css";
import "react-tabs/style/react-tabs.css";
import { LinkContainer } from "react-router-bootstrap";
import { Redirect, Route, Switch, useHistory } from "react-router-dom";
import { Tab, Tabs, TabList, TabPanel } from "react-tabs";
import Container from "react-bootstrap/Container";
import Dashboard from "./Dashboard";
import Login, { url as loginUrl } from "./Login";
import Logout, { url as logoutUrl } from "./Logout";
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import React, { useState, useEffect } from "react";
import ServiceContract from "./ServiceContract";
import User from "./User";
import Users from "./Users";

const useStateWithLocalStorage = (sessionStorageKey, initialValue = null) => {
  let storageValue = sessionStorage.getItem(sessionStorageKey);
  const [value, setValue] = useState(
    storageValue ? JSON.parse(storageValue) : initialValue
  );

  useEffect(() => {
    sessionStorage.setItem(sessionStorageKey, JSON.stringify(value));
  }, [value, sessionStorageKey]);

  return [value, setValue];
};

function App() {
  let history = useHistory();
  const [loginUser, setLoginUser] = useStateWithLocalStorage("login-user");
  const [openTabs, setOpenTabs] = useStateWithLocalStorage(
    "open-service-contract-tabs",
    []
  );
  const [selectedTab, setSelectedTab] = useStateWithLocalStorage(
    "selected-service-contract-tabs",
    0
  );
  useEffect(() => {
    if (!openTabs.find((openTab) => openTab.index === selectedTab))
      setSelectedTab(0);
  }, [openTabs, selectedTab, setSelectedTab]);

  const hideNavLicenses = loginUser ? false : true;
  const hideNavProfile = loginUser ? false : true;
  const hideNavMyCompany = loginUser ? false : true;
  const hideNavCompanies =
    loginUser && loginUser.group === "SYSTEM_ADMIN" ? false : true;
  const hideNavUsers =
    loginUser &&
    (loginUser.group === "COMPANY_ADMIN" || loginUser.group === "SYSTEM_ADMIN")
      ? false
      : true;

  return (
    <Container className="p-3">
      <Container fluid>
        <Navbar className="shadow p-3 mb-5 bg-white rounded">
          <LinkContainer to="/">
            <Navbar.Brand>License Manager</Navbar.Brand>
          </LinkContainer>
          <Navbar.Toggle aria-controls="basic-navbar-nav" />
          <Navbar.Collapse id="basic-navbar-nav">
            <Nav className="mr-auto">
              <LinkContainer to="/" hidden={hideNavLicenses}>
                <Nav.Link>Licenses</Nav.Link>
              </LinkContainer>
              <LinkContainer to="/users/myself" hidden={hideNavProfile}>
                <Nav.Link>Profile</Nav.Link>
              </LinkContainer>
              <LinkContainer to="/companies/mine" hidden={hideNavMyCompany}>
                <Nav.Link>Company</Nav.Link>
              </LinkContainer>
              <LinkContainer to="/companies" hidden={hideNavCompanies}>
                <Nav.Link>Companies</Nav.Link>
              </LinkContainer>
              <LinkContainer to="/users" hidden={hideNavUsers}>
                <Nav.Link>Users</Nav.Link>
              </LinkContainer>
              {loginUser ? (
                <LinkContainer to={logoutUrl} replace>
                  <Nav.Link>Logout</Nav.Link>
                </LinkContainer>
              ) : (
                <LinkContainer to={loginUrl} replace>
                  <Nav.Link>Login</Nav.Link>
                </LinkContainer>
              )}
            </Nav>
          </Navbar.Collapse>
        </Navbar>
        <Switch>
          <Route
            path={loginUrl}
            exact={true}
            render={() => (
              <Login
                oldUser={loginUser}
                oldLocation={history.location}
                onLogin={(loginUser, oldLocation) => {
                  console.debug("Setting new loginUser state");
                  setLoginUser(loginUser);
                  history.replace(
                    oldLocation.state && oldLocation.state.referrer
                      ? oldLocation.state.referrer
                      : "/"
                  );
                }}
              />
            )}
          />
          <Route
            path={logoutUrl}
            exact={true}
            render={(props) => (
              <Logout
                {...props}
                onLogout={() => {
                  console.debug("Invalidating loginUser");
                  setLoginUser(null);
                  setSelectedTab(0);
                  setOpenTabs([]);
                  sessionStorage.clear();
                }}
              />
            )}
          />
          <Route
            path={"/users/myself"}
            exact={true}
            render={(props) => (
              <User
                {...props}
                user={loginUser}
                onUserCredentialsChanged={({ success }) =>
                  success
                    ? alert("Password changed!")
                    : alert(
                        "Changing password failed! Make sure your old password is correct."
                      )
                }
                onUserDetailsChanged={({ success, user }) => {
                  if (success) {
                    setLoginUser(user);
                    alert("User details changed!");
                  } else alert("Failed to update user data!");
                }}
              />
            )}
          />
          <Route
            path={"/users/:userId"}
            exact={true}
            render={(props) => (
              <User
                {...props}
                onUserCredentialsChanged={({ success }) =>
                  success
                    ? alert("Password changed!")
                    : alert(
                        "Changing password failed! Make sure your old password is correct."
                      )
                }
                onUserDetailsChanged={({ success, user }) => {
                  if (success) {
                    setLoginUser(user);
                    alert("User details changed!");
                  } else alert("Failed to update user data!");
                }}
              />
            )}
          />
          <Route
            path={"/users"}
            exact={true}
            render={(props) => (
              <Users
                {...props}
                onUserCreated={({ success, user }) => {
                  if (success) alert("User created!");
                  else alert("Failed to create user!");
                }}
              />
            )}
          />
          <Route
            path="/"
            exact={true}
            render={(props) =>
              loginUser != null ? (
                <Tabs
                  selectedIndex={selectedTab}
                  onSelect={(index) => {
                    if (
                      openTabs.find((tab) => tab.index === index) ||
                      index === 0
                    ) {
                      setSelectedTab(index);
                    }
                  }}
                >
                  <TabList>
                    <Tab index={0}>Dashboard</Tab>
                    {openTabs.map((tab) => {
                      return (
                        <Tab index={tab.index} key={tab.id}>
                          <span className="align-middle">
                            Service Contract #{tab.id}
                          </span>
                          <span
                            className="pl-2 close"
                            onClick={() => {
                              let newOpenTabs = openTabs.filter(
                                (t) => tab !== t
                              );
                              setOpenTabs(newOpenTabs);
                              setSelectedTab(0);
                            }}
                          >
                            <span>&times;</span>
                          </span>
                        </Tab>
                      );
                    })}
                  </TabList>
                  <TabPanel>
                    <Dashboard
                      {...props}
                      loginUser={loginUser}
                      onOpenServiceContract={(sc) => {
                        let alreadyOpenTab = openTabs.find(
                          (openTab) => openTab.id === sc.id
                        );
                        let index = 0;
                        if (alreadyOpenTab) {
                          index = alreadyOpenTab.index;
                        } else {
                          index = openTabs.length + 1;
                          let newOpenTabs = openTabs.slice();
                          newOpenTabs.push({
                            id: sc.id,
                            index: index,
                          });
                          setOpenTabs(newOpenTabs);
                        }
                        setSelectedTab(index);
                      }}
                    />
                  </TabPanel>
                  {openTabs.map((tab) => {
                    return (
                      <TabPanel index={tab.index} key={tab.id}>
                        <ServiceContract id={tab.id} />
                      </TabPanel>
                    );
                  })}
                </Tabs>
              ) : (
                <Redirect
                  to={{
                    pathname: loginUrl,
                    state: { referrer: history.location },
                  }}
                />
              )
            }
          />
          <Route>
            <h1>Nobody here but us chickens</h1>
          </Route>
        </Switch>
      </Container>
    </Container>
  );
}

export default App;
