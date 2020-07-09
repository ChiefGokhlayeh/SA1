import "./App.css";
import { Link, Redirect, Route, Switch, withRouter } from "react-router-dom";
import { Tab, Tabs, TabList, TabPanel } from "react-tabs";
import Dashboard from "./Dashboard";
import Login from "./Login";
import Logout from "./Logout";
import React, { Component } from "react";

function normalizeSearchParameters(params) {
  let newParams = new URLSearchParams();
  for (const it of params.getAll("sc")) {
    if (!newParams.getAll("sc").includes(it)) {
      newParams.append("sc", it);
    }
  }
  let sortedParams = new URLSearchParams();
  newParams
    .getAll("sc")
    .sort()
    .forEach((val) => sortedParams.append("sc", val));
  if (params.has("sel")) sortedParams.set("sel", params.get("sel"));
  return sortedParams;
}

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loginUser: null,
      selectedTab: 0,
    };
  }

  isLoggedIn() {
    return this.state.loginUser != null;
  }

  componentDidMount() {
    this.setState({ selectedTab: 0 });
    this.openQueriedTabs(
      normalizeSearchParameters(new URLSearchParams(this.props.location.search))
    );

    let querySelection = new URLSearchParams(this.props.location.search).get(
      "sel"
    );
    this.serviceContractTabs.forEach((tab) => {
      if (tab.id === querySelection) this.setState({ selectedTab: tab.index });
    });
  }

  openQueriedTabs(query) {
    this.serviceContractTabs = query.getAll("sc").map((sc, index) => {
      return { id: sc, index: index + 1 };
    });
  }

  render() {
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
                      oldLocation.state && oldLocation.state.referrer
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
                  <Tabs
                    selectedIndex={this.state.selectedTab}
                    onSelect={(index) => {
                      this.setState({ selectedTab: index });
                      let loc = this.props.location;
                      let params = new URLSearchParams(loc.search);
                      if (index > 0) params.set("sel", index);
                      else params.delete("sel");
                      loc.search = normalizeSearchParameters(params).toString();
                      this.props.history.replace(loc);
                    }}
                  >
                    <TabList tabIndex={0}>
                      <Tab>Dashboard</Tab>
                      {this.serviceContractTabs.map((tab) => {
                        return (
                          <Tab index={tab.index} key={tab.id}>
                            Service Contract #{tab.id}
                          </Tab>
                        );
                      })}
                    </TabList>
                    <TabPanel>
                      <Dashboard
                        {...props}
                        loginUser={this.state.loginUser}
                        onOpenServiceContract={(sc, query) => {
                          let normQuery = normalizeSearchParameters(query);
                          this.openQueriedTabs(
                            normalizeSearchParameters(normQuery)
                          );
                          console.log(normQuery.toString());

                          let loc = this.props.location;
                          loc.search = normQuery.toString();
                          this.props.history.replace(loc);
                          this.setState({
                            selectedTab: this.serviceContractTabs.find(
                              (scFromTabs) => scFromTabs.id === `${sc.id}`
                            ).index,
                          });
                        }}
                      />
                    </TabPanel>
                    {this.serviceContractTabs.map((tab) => {
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
