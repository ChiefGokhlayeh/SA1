import "./App.css";
import {
  BrowserRouter as Router,
  Link,
  Redirect,
  Route,
  Switch,
} from "react-router-dom";
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
    return (
      <Router basename="/licensemanager">
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
                  loginUser={this.state.loginUser}
                  onLogin={(loginUser) => {
                    this.setState({
                      loginUser: loginUser,
                    });
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
                    this.setState({ loginUser: null });
                    fetch(
                      "https://localhost:8443/licensemanager/rest/users/logout",
                      {
                        credentials: "include",
                        method: "GET",
                      }
                    ).catch(console.warn);
                  }}
                />
              )}
            />
            <Route
              path="/"
              exact={true}
              render={(props) =>
                !this.isLoggedIn() ? (
                  <Redirect to="/login" />
                ) : (
                  <Dashboard {...props} loginUser={this.state.loginUser} />
                )
              }
            />
            <Route>
              <h1>Nobody here but us chickens</h1>
            </Route>
          </Switch>
        </div>
      </Router>
    );
  }
}

export default App;
