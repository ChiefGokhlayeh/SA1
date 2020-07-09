import "./App.css";
import { Link, Redirect, Route, Switch, withRouter } from "react-router-dom";
import { Tab, Tabs, TabList, TabPanel } from "react-tabs";
import Dashboard from "./Dashboard";
import Login from "./Login";
import Logout from "./Logout";
import React, { Component } from "react";

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loginUser: null,
    };
  }

  isLoggedIn() {
    return this.state.loginUser != null;
  }

  render() {
    let query = new URLSearchParams(this.props.location.search);

    let serviceContractTabs = query.getAll("sc").map((sc, index) => {
      return { id: sc, index: index + 1 };
    });

    let selectedTab = { id: -1, index: 0 };
    let querySelection = query.get("sel");
    serviceContractTabs.forEach((tab) => {
      if (tab.id === querySelection) selectedTab = tab;
    });

    return (
      <div>
        <div>
          <nav>
            <ul>
              <li>
                <Link to="/">Licenses</Link>
              </li>
              <li>
                <Link to="/users/me">Profile</Link>
              </li>
              <li>
                {this.isLoggedIn() ? (
                  <Link to="/logout">Logout</Link>
                ) : (
                  <Link to="/login">Login</Link>
                )}
              </li>
            </ul>
          </nav>
        </div>
        <div>
          <Switch>
            <Route
              path="/login"
              render={(props) => (
                <Login
                  {...props}
                  oldUser={this.state.loginUser}
                  oldLocation={this.props.location}
                  onLogin={(loginUser, oldLocation) => {
                    console.debug("Setting new loginUser state");
                    this.setState({
                      loginUser: loginUser,
                    });
                    this.props.history.push(
                      oldLocation.state.referrer
                        ? oldLocation.state.referrer
                        : "/"
                    );
                  }}
                />
              )}
            />
            <Route
              path="/logout"
              render={(props) => (
                <Logout
                  {...props}
                  onLogout={() => {
                    console.debug("Invalidating loginUser");
                    this.setState({ loginUser: null });
                  }}
                />
              )}
            />
            <Route
              path="/"
              render={(props) =>
                this.isLoggedIn() ? (
                  <Tabs defaultIndex={selectedTab.index}>
                    <TabList tabIndex={0}>
                      <Tab>Dashboard</Tab>
                      {serviceContractTabs.map((tab) => {
                        return (
                          <Tab index={tab.index} key={tab.id}>
                            Service Contract #{tab.id}
                          </Tab>
                        );
                      })}
                    </TabList>
                    <TabPanel>
                      <Dashboard {...props} loginUser={this.state.loginUser} />
                    </TabPanel>
                    {serviceContractTabs.map((tab) => {
                      return (
                        <TabPanel index={tab.index} key={tab.id}>
                          {tab.id}
                        </TabPanel>
                      );
                    })}
                  </Tabs>
                ) : (
                  <Redirect
                    to={{
                      pathname: "/login",
                      state: { referrer: this.props.location },
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
}

export default withRouter(App);
