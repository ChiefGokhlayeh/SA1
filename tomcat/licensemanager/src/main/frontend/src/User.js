import { useAsync } from "react-async";
import React, { useState } from "react";
import validator from "validator";

const fetchCompany = async ({ signal }) => {
  const resp = await fetch(
    `https://localhost:8443/licensemanager/rest/companies/mine`,
    { signal, credentials: "include", method: "GET" }
  );

  if (resp.ok) {
    return { success: true, company: await resp.json() };
  } else {
    return { success: false, status: resp.status };
  }
};

function User({ user, onUserCredentialsChanged, onUserDetailsChanged }) {
  const {
    data: companyData,
    isPending: isCompanyPending,
    error: companyError,
  } = useAsync({ promiseFn: fetchCompany });

  const [firstname, setFirstname] = useState(user ? user.firstname : "");
  const [lastname, setLastname] = useState(user ? user.lastname : "");
  const [email, setEmail] = useState(user ? user.email : "");
  const [oldPassword, setOldPassword] = useState("test password 123");
  const [newPassword, setNewPassword] = useState("123");
  const [repeatPassword, setRepeatPassword] = useState("123");

  const disableChangePassword = () =>
    validator.isEmpty(oldPassword) ||
    validator.isEmpty(newPassword) ||
    !validator.equals(newPassword, repeatPassword);

  const disableChangeUserDetails = () =>
    validator.isEmpty(firstname) ||
    validator.isEmpty(lastname) ||
    !validator.isEmail(email) ||
    (validator.equals(firstname, user.firstname) &&
      validator.equals(lastname, user.lastname) &&
      validator.equals(email, user.email));

  return (
    <div>
      <div>
        <label htmlFor="active">Active:</label>
        <input id="active" type="checkbox" readOnly checked={user.active} />
        <h2>Credentials</h2>
        <label htmlFor="username">Username:</label>
        <input
          id="username"
          type="text"
          readOnly
          value={user.credentials.loginname}
        />
        <form
          onSubmit={(e) => {
            e.preventDefault();
            async function submit() {
              if (
                oldPassword !== "" &&
                newPassword !== "" &&
                repeatPassword !== "" &&
                newPassword === repeatPassword
              ) {
                const resp = await fetch(
                  `https://localhost:8443/licensemanager/rest/auth/change`,
                  {
                    credentials: "include",
                    method: "PUT",
                    headers: {
                      "Content-Type": "application/json",
                    },
                    body: JSON.stringify({
                      loginname: user.credentials.loginname,
                      password: oldPassword,
                      newPassword: newPassword,
                    }),
                  }
                );
                if (resp.ok) {
                  setOldPassword("");
                  setNewPassword("");
                  setRepeatPassword("");
                  if (onUserCredentialsChanged)
                    onUserCredentialsChanged({ success: true });
                } else {
                  if (onUserCredentialsChanged)
                    onUserCredentialsChanged({ success: false });
                }
              }
            }
            submit();
          }}
        >
          <label htmlFor="old_password">Old Password:</label>
          <input
            id="old_password"
            type="password"
            value={oldPassword}
            onChange={(e) => setOldPassword(e.target.value)}
          />
          <label htmlFor="new_password">New Password:</label>
          <input
            id="new_password"
            type="password"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
          />
          <label htmlFor="repeat_password">Repeat Password:</label>
          <input
            id="repeat_password"
            type="password"
            value={repeatPassword}
            onChange={(e) => setRepeatPassword(e.target.value)}
          />
          <input
            type="submit"
            value="Change Password"
            disabled={disableChangePassword()}
          />
        </form>
      </div>
      <div>
        <h2>User Details</h2>
        <form
          onSubmit={(e) => {
            e.preventDefault();

            async function submit() {
              let userDetails = {
                firstname: firstname,
                lastname: lastname,
                email: email,
                active: user.active,
              };

              let resp = await fetch(
                "https://localhost:8443/licensemanager/rest/users/me",
                {
                  credentials: "include",
                  method: "PUT",
                  headers: {
                    "Content-Type": "application/json",
                  },
                  body: JSON.stringify(userDetails),
                }
              );
              if (resp.ok && onUserDetailsChanged) {
                resp = await fetch(
                  "https://localhost:8443/licensemanager/rest/users/me",
                  {
                    credentials: "include",
                    method: "GET",
                  }
                );
                if (resp.ok) {
                  onUserDetailsChanged({
                    success: true,
                    user: await resp.json(),
                  });
                } else {
                  onUserDetailsChanged({
                    success: true,
                    user: await resp.json(),
                  });
                }
              } else {
                onUserDetailsChanged({ success: false });
              }
            }
            submit();
          }}
        >
          <label htmlFor="firstname">Firstname:</label>
          <input
            id="firstname"
            type="text"
            value={firstname}
            onChange={(e) => setFirstname(e.target.value)}
          />
          <label htmlFor="lastname">Lastname:</label>
          <input
            id="lastname"
            type="text"
            value={lastname}
            onChange={(e) => setLastname(e.target.value)}
          />
          <label htmlFor="email">E-Mail:</label>
          <input
            id="email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <label htmlFor="company">Company:</label>
          <input
            id="company"
            type="text"
            readOnly
            value={
              isCompanyPending
                ? "Loading..."
                : companyError
                ? `Something went wrong: ${companyError.message}`
                : companyData && companyData.success
                ? companyData.company.name
                : `Something went wrong: ${companyData.status}`
            }
          />
          <input
            type="submit"
            value="Change User"
            disabled={disableChangeUserDetails()}
          />
        </form>
      </div>
    </div>
  );
}

export default User;
