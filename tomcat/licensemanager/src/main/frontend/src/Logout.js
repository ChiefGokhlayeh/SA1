import { Link } from "react-router-dom";
import { useAsync } from "react-async";
import React from "react";
import { url as loginUrl } from "./Login";

export var url = "/auth/logout";

const logout = async ({ signal }) => {
  return await fetch(
    "https://localhost:8443/licensemanager/rest/users/logout",
    {
      credentials: "include",
      method: "GET",
      signal,
    }
  );
};

function Logout(onLogout) {
  const { error, isPending } = useAsync({
    promiseFn: logout,
    onResolve: (response) => {
      if (response.ok && onLogout) onLogout.onLogout();
    },
  });

  if (isPending) return <p>Logging out...</p>;
  if (error) return <p>Something went wrong: {error.message}</p>;
  return (
    <p>
      You are logged out. Please <Link to={loginUrl}>login</Link> again to
      access the service.
    </p>
  );
}

export default Logout;
