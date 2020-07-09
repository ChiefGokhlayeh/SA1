import "./App.css";
import { withRouter } from "react-router-dom";
import Async from "react-async";
import React, { Component } from "react";

const queryLoginStatus = async ({ signal }) => {
  let resp = await fetch(
    `https://localhost:8443/licensemanager/rest/users/me`,
    {
      signal,
      credentials: "include",
      method: "GET",
    }
  );

  if (resp.ok) {
    let user = await resp.json();
    console.log("Already logged in as: " + JSON.stringify(user, null, 2));
    return { success: true, user: user };
  } else {
    console.log("Currently not logged in.");
    return { success: false, status: resp.status };
  }
};

class Login extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loginName: "hanni",
      plainPassword: "test password 123",
    };
  }

  async componentDidMount() {
    if (this.props.loginUser != null) {
      this.setState({
        loginName: this.props.loginUser.credentials.loginname,
      });
    }

    this.loginStatusAbortController = new AbortController();
    this.loginStatusQuery = queryLoginStatus(
      this.loginStatusAbortController.signal
    );
    let loginStatus = await this.loginStatusQuery;
    if (loginStatus.success) {
      this.props.onLogin(loginStatus.user);
      this.props.history.push("/");
    } else this.forceUpdate();
  }

  componentWillUnmount() {
    if (this.loginStatusAbortController)
      this.loginStatusAbortController.abort();
  }

  render() {
    return (
      <div>
        <Async promise={this.loginStatusQuery}>
          {({ data, err, isPending }) => {
            if (isPending) return "Checking...";
            if (err) return `Something went wrong: ${err.message}`;
            if (data) {
              if (data.success) {
                return "Already logged in.";
              } else {
                return (
                  <form onSubmit={(e) => this.handleSubmit(e)}>
                    <label htmlFor="login_name">Username:</label>
                    <input
                      type="text"
                      id="login_name"
                      name="login_name"
                      value={this.state.loginName}
                      onChange={this.handleChange}
                    />

                    <label htmlFor="login_password">Password:</label>
                    <input
                      type="password"
                      id="login_password"
                      name="login_password"
                      value={this.state.plainPassword}
                      onChange={this.handleChange}
                    />

                    <input
                      type="submit"
                      id="login_submit"
                      name="login_submit"
                      value="Login"
                    />
                  </form>
                );
              }
            }
          }}
        </Async>
      </div>
    );
  }

  handleChange = (e) => {
    this.setState({ value: e.target.value });
  };

  async handleSubmit(e) {
    e.preventDefault();
    let resp = await fetch(
      "https://localhost:8443/licensemanager/rest/users/login",
      {
        credentials: "include",
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          loginname: this.state.loginName,
          password: this.state.plainPassword,
        }),
      }
    );
    let data = await resp.json();

    console.log("Logged in as: " + JSON.stringify(data.user, null, 2));
    this.props.onLogin(data.user);
    this.props.history.push("/");
  }
}

export default withRouter(Login);
