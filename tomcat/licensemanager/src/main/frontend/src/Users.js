import { Async } from "react-async";
import { FaSearch, FaEdit } from "react-icons/fa";
import { LinkContainer } from "react-router-bootstrap";
import { useHistory } from "react-router";
import Badge from "react-bootstrap/Badge";
import Button from "react-bootstrap/Button";
import Form from "react-bootstrap/Form";
import Fuse from "fuse.js";
import InputGroup from "react-bootstrap/InputGroup";
import React, { useState, useEffect, useContext } from "react";
import ServerInfo from "./ServerInfo";
import Table from "react-bootstrap/Table";

const fetchUsers = async ({ signal, restBaseUrl }) => {
  const resp = await fetch(`${restBaseUrl}/users/`, {
    signal,
    credentials: "include",
    method: "GET",
  });

  if (resp.ok) {
    return { success: true, users: await resp.json() };
  } else {
    return { success: false, status: resp.status };
  }
};

function Users() {
  const serverInfo = useContext(ServerInfo);

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
    <>
      <Async promiseFn={fetchUsers} restBaseUrl={serverInfo.restBaseUrl}>
        {({ data, isPending, error }) => {
          if (isPending) return "Loading...";
          if (error) return `Something went wrong: ${error.message}`;
          if (data) {
            if (data.success) {
              const fuse = new Fuse(data.users, fuseOptions);
              return (
                <>
                  <Form>
                    <Form.Group>
                      <InputGroup className="mb-3">
                        <InputGroup.Prepend>
                          <InputGroup.Text id="basic-addon1">
                            <FaSearch />
                          </InputGroup.Text>
                        </InputGroup.Prepend>
                        <Form.Control
                          type="search"
                          placeholder="search for specific user"
                          value={searchPattern}
                          onChange={(e) => setSearchPattern(e.target.value)}
                        />
                      </InputGroup>
                    </Form.Group>
                  </Form>
                  <Table hover="true">
                    <thead>
                      <tr>
                        <th>Username</th>
                        <th>Firstname</th>
                        <th>Lastname</th>
                        <th>Company</th>
                        <th>Department</th>
                        <th>Active</th>
                        <th>Edit</th>
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
                        <tr key={user.id} className="cursor: pointer">
                          <td>{user.credentials.loginname}</td>
                          <td>{user.firstname}</td>
                          <td>{user.lastname}</td>
                          <td>{user.companyDepartment.company.name}</td>
                          <td>{user.companyDepartment.name}</td>
                          <td className="text-center align-middle">
                            <h4>
                              <Badge
                                pill
                                variant={user.active ? "primary" : "secondary"}
                              >
                                {user.active ? "Active" : "Inactive"}
                              </Badge>
                            </h4>
                          </td>
                          <td className="text-center">
                            <LinkContainer to={`/users/${user.id}`}>
                              <Button size="sm">
                                <FaEdit />
                              </Button>
                            </LinkContainer>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </Table>
                </>
              );
            } else return <p>Something went wrong: {data.status}</p>;
          }
        }}
      </Async>
    </>
  );
}

export default Users;
