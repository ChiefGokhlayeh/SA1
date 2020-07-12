import React, { useState, useEffect } from "react";
import { Async } from "react-async";
import Fuse from "fuse.js";
import { useHistory } from "react-router";

const fetchUsers = async ({ signal }) => {
  const resp = await fetch(
    `https://localhost:8443/licensemanager/rest/users/`,
    { signal, credentials: "include", method: "GET" }
  );

  if (resp.ok) {
    return { success: true, users: await resp.json() };
  } else {
    return { success: false, status: resp.status };
  }
};

function Users() {
  const fuseOptions = {
    keys: ["id", "credentials.loginname", "firstname", "lastname"],
  };
  const history = useHistory();
  const [searchPattern, setSearchPattern] = useState(
    new URLSearchParams(history.location.search).get("search") || ""
  );

  useEffect(() => {
    let loc = history.location;
    let params = new URLSearchParams(loc.search);
    searchPattern
      ? params.set("search", searchPattern)
      : params.delete("search");
    loc.search = params.toString();
    history.push(loc);
  }, [searchPattern, history]);

  return (
    <div>
      <Async promiseFn={fetchUsers}>
        {({ data, isPending, error }) => {
          if (isPending) return "Loading...";
          if (error) return `Something went wrong: ${error.message}`;
          if (data) {
            if (data.success) {
              const fuse = new Fuse(data.users, fuseOptions);
              return (
                <div>
                  <label htmlFor="search">Search:</label>
                  <input
                    id="search"
                    type="search"
                    value={searchPattern}
                    onChange={(e) => setSearchPattern(e.target.value)}
                  />
                  <table>
                    <thead>
                      <tr>
                        <th>Username</th>
                        <th>Firstname</th>
                        <th>Lastname</th>
                        <th>Company</th>
                        <th>Active</th>
                      </tr>
                    </thead>
                    <tbody>
                      {(searchPattern === ""
                        ? data.users
                        : fuse
                            .search(searchPattern)
                            .sort((a, b) => a.score - b.score)
                            .map((result) => result.item)
                      ).map((user) => (
                        <tr key={user.id}>
                          <td>{user.credentials.loginname}</td>
                          <td>{user.firstname}</td>
                          <td>{user.lastname}</td>
                          <td></td>
                          <td>
                            <input
                              type="checkbox"
                              checked={user.active}
                              onChange={(e) => null}
                            />
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              );
            } else return <p>Something went wrong: {data.status}</p>;
          }
        }}
      </Async>
    </div>
  );
}

export default Users;
