import "./App.css";
import { useAsync } from "react-async";
import React, { useState } from "react";
import validator from "validator";

export var url = "/auth/login";

const queryLoginStatus = async ({ onLogin, signal }) => {
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
    console.debug("Already logged in as: " + JSON.stringify(user, null, 2));
    return { success: true, user: user, onLogin };
  } else {
    console.debug("Currently not logged in.");
    return { success: false, status: resp.status };
  }
};

function Login({ oldUser, oldLocation, onLogin }) {
  const [loginName, setLoginName] = useState(
    oldUser ? oldUser.credentials.loginName : "hanni"
  );
  const [loginPassword, setLoginPassword] = useState("test password 123");
  const { data: loginStatus, error, isPending } = useAsync({
    promiseFn: queryLoginStatus,
    onResolve: (loginStatus) => {
      if (loginStatus.success) {
        onLogin(loginStatus.user, oldLocation);
      }
    },
  });

  const disableLogin = () =>
    validator.isEmpty(loginName) || validator.isEmpty(loginPassword);

  if (isPending) return "Checking...";
  if (error) return `Something went wrong: ${error.message}`;
  if (loginStatus) {
    if (loginStatus.success) {
      return "Already logged in. Redirecting...";
    } else {
      return (
        <form onSubmit={(e) => handleSubmit(e)}>
          <label htmlFor="login_name">Username:</label>
          <input
            type="text"
            id="login_name"
            name="login_name"
            value={loginName}
            onChange={(e) => setLoginName(e.target.value)}
          />

          <label htmlFor="login_password">Password:</label>
          <input
            type="password"
            id="login_password"
            name="login_password"
            value={loginPassword}
            onChange={(e) => setLoginPassword(e.target.value)}
          />

          <input
            type="submit"
            id="login_submit"
            name="login_submit"
            value="Login"
            disabled={disableLogin()}
          />
        </form>
      );
    }
  }
  return "placeholder";

  async function handleSubmit(e) {
    e.preventDefault();

    let resp = await fetch(
      "https://localhost:8443/licensemanager/rest/auth/login",
      {
        credentials: "include",
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          loginname: loginName,
          password: loginPassword,
        }),
      }
    );
    if (resp.ok) {
      let data = await resp.json();

      if (data.success) {
        console.debug("Logged in as: " + JSON.stringify(data.user, null, 2));
        onLogin(data.user, oldLocation);
        return;
      }
    }
    console.debug("Login failed!", resp);
    alert("Incorrect user or password!");
  }
}

export default Login;
