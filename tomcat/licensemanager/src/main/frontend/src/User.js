import { useAsync } from "react-async";
import React, { useState, useEffect } from "react";

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

  useEffect(() => {
    const userDetails = {
      firstname: firstname,
      lastname: lastname,
      email: email,
    };

    if (onUserDetailsChanged) onUserDetailsChanged(userDetails);
  }, [firstname, lastname, email, onUserDetailsChanged]);

  return (
    <div>
      <div>
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
          <input type="submit" value="Change Password" />
        </form>
      </div>
      <div>
        <h2>User Details</h2>
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
      </div>
    </div>
  );
}

export default User;
