import "./App.css";
import { Link, Redirect, Route, Switch, useHistory } from "react-router-dom";
import { Tab, Tabs, TabList, TabPanel } from "react-tabs";
import Dashboard from "./Dashboard";
import Login, { url as loginUrl } from "./Login";
import Logout, { url as logoutUrl } from "./Logout";
import React, { useState, useEffect } from "react";
import ServiceContract from "./ServiceContract";
import User from "./User";

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
    <div>
      <div>
        <nav>
          <ul>
            <Link to="/" hidden={hideNavLicenses}>
              <li>Licenses</li>
            </Link>
            <Link to="/users/myself" hidden={hideNavProfile}>
              <li>Profile</li>
            </Link>
            <Link to="/companies/mine" hidden={hideNavMyCompany}>
              <li>My Company</li>
            </Link>
            <Link to="/companies" hidden={hideNavCompanies}>
              <li>Companies</li>
            </Link>
            <Link to="/users" hidden={hideNavUsers}>
              <li>Users</li>
            </Link>
            {loginUser ? (
              <Link to={logoutUrl} replace>
                <li>Logout</li>
              </Link>
            ) : (
              <Link to={loginUrl} replace>
                <li>Login</li>
              </Link>
            )}
          </ul>
        </nav>
      </div>
      <div>
        <Switch>
          <Route
            path={loginUrl}
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
            path={`/users/myself`}
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
                          Service Contract #{tab.id}
                          <button
                            onClick={() => {
                              let newOpenTabs = openTabs.filter(
                                (t) => tab !== t
                              );
                              setOpenTabs(newOpenTabs);
                              setSelectedTab(0);
                            }}
                          >
                            x
                          </button>
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
      </div>
    </div>
  );
}

export default App;
