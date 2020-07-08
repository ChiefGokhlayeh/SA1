import "./App.css";
import { withRouter } from "react-router-dom";
import React, { Component } from "react";

class Login extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loginName: "hanni",
      plainPassword: "test password 123",
    };
    if (props.loginUser != null) {
      this.state.loginName = props.loginUser.credentials.loginname;
    }
  }

  render() {
    return (
      <div>
        <form>
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
            type="button"
            id="login_submit"
            name="login_submit"
            value="Login"
            onClick={(event) => this.handleLoginClick(event)}
          />
        </form>
      </div>
    );
  }

  handleChange = (e) => {
    this.setState({ value: e.target.value });
  };

  handleLoginClick(_) {
    fetch("https://localhost:8443/licensemanager/rest/users/login", {
      credentials: "include",
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        loginname: this.state.loginName,
        password: this.state.plainPassword,
      }),
    })
      .then((res) => res.json())
      .then((data) => {
        console.log("Logged in as: " + JSON.stringify(data.user, null, 2));
        this.props.onLogin(data.user);
        this.props.history.push("/");
      })
      .catch(console.log);
  }
}

export default withRouter(Login);
