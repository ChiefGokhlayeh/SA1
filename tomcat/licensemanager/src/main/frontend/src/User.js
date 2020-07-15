import {
  FaBriefcase,
  FaBuilding,
  FaEnvelope,
  FaUser,
  FaUsers,
} from "react-icons/fa";
import { useAsync, Async } from "react-async";
import { useParams } from "react-router";
import Badge from "react-bootstrap/Badge";
import Button from "react-bootstrap/Button";
import Col from "react-bootstrap/Col";
import Container from "react-bootstrap/Container";
import Form from "react-bootstrap/Form";
import InputGroup from "react-bootstrap/InputGroup";
import React, { useState, useEffect, useContext } from "react";
import Row from "react-bootstrap/Row";
import ServerInfo from "./ServerInfo";
import validator from "validator";

const fetchGroupTypes = async ({ signal, restBaseUrl }) => {
  const resp = await fetch(`${restBaseUrl}/users/group-types`, {
    signal,
    credentials: "include",
    method: "GET",
  });

  if (resp.ok) {
    return { success: true, groupTypes: await resp.json() };
  } else {
    return { success: false, status: resp.status };
  }
};

const fetchUser = async ({ signal, endpoint, restBaseUrl }) => {
  const resp = await fetch(`${restBaseUrl}/users/${endpoint}`, {
    signal,
    credentials: "include",
    method: "GET",
  });

  if (resp.ok) {
    return { success: true, user: await resp.json() };
  } else {
    return { success: false, status: resp.status };
  }
};

const pushUser = async ({ signal, endpoint, modifiedUser, restBaseUrl }) => {
  const resp = await fetch(`${restBaseUrl}/users/${endpoint}`, {
    signal,
    credentials: "include",
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(modifiedUser),
  });

  if (resp.ok) {
    return { success: true };
  } else {
    return { success: false, status: resp.status };
  }
};

function User(props) {
  const serverInfo = useContext(ServerInfo);

  const [user, setUser] = useState(null);
  const [firstname, setFirstname] = useState(user ? user.firstname : "");
  const [lastname, setLastname] = useState(user ? user.lastname : "");
  const [email, setEmail] = useState(user ? user.email : "");
  const [oldPassword, setOldPassword] = useState("test password 123");
  const [newPassword, setNewPassword] = useState("123");
  const [repeatPassword, setRepeatPassword] = useState("123");

  const { userId } = useParams();

  const disableChangePassword = () =>
    validator.isEmpty(oldPassword) ||
    validator.isEmpty(newPassword) ||
    !validator.equals(newPassword, repeatPassword);

  const disableChangeUserDetails = () =>
    validator.isEmpty(firstname) ||
    validator.isEmpty(lastname) ||
    !validator.isEmail(email) ||
    ((user ? validator.equals(firstname, user.firstname) : true) &&
      (user ? validator.equals(lastname, user.lastname) : true) &&
      (user ? validator.equals(email, user.email) : true));

  const toUserEndpoint = (userId) => (userId ? Number(userId) : "me");

  const { data: userData, reload: reloadUser } = useAsync({
    promiseFn: fetchUser,
    endpoint: toUserEndpoint(userId),
    restBaseUrl: serverInfo.restBaseUrl,
  });

  useEffect(() => {
    if (reloadUser) reloadUser();
  }, [userId, reloadUser]);

  useEffect(() => {
    if (userData && userData.success) {
      setUser(userData.user);
    }
  }, [userData]);

  useEffect(() => {
    if (user) {
      setFirstname(user.firstname);
      setLastname(user.lastname);
      setEmail(user.email);
    }
  }, [user]);

  return (
    <>
      <div>
        <h2 className="header">
          <Container>
            <Row>
              <Col></Col>
              <Col>Credentials</Col>
              <Col>
                <Badge
                  pill
                  variant={user && user.active ? "primary" : "secondary"}
                >
                  {user && user.active ? "Active" : "Inactive"}
                </Badge>
              </Col>
            </Row>
          </Container>
        </h2>
        <Form
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
                  `${serverInfo.restBaseUrl}/auth/change`,
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
                  if (props.onUserCredentialsChanged)
                    props.onUserCredentialsChanged({ success: true });
                } else {
                  if (props.onUserCredentialsChanged)
                    props.onUserCredentialsChanged({ success: false });
                }
              }
            }
            submit();
          }}
        >
          <Form.Row>
            <Form.Group as={Col}>
              <Form.Label>Username</Form.Label>
              <InputGroup>
                <InputGroup.Prepend>
                  <InputGroup.Text>
                    <FaUser />
                  </InputGroup.Text>
                </InputGroup.Prepend>
                <Form.Control
                  type="text"
                  placeholder="enter username"
                  readOnly
                  value={user ? user.credentials.loginname : ""}
                ></Form.Control>
              </InputGroup>
            </Form.Group>
            <Form.Group as={Col}>
              <Form.Label>Group:</Form.Label>
              <InputGroup>
                <InputGroup.Prepend>
                  <InputGroup.Text>
                    <FaUsers />
                  </InputGroup.Text>
                </InputGroup.Prepend>
                <Async
                  promiseFn={fetchGroupTypes}
                  restBaseUrl={serverInfo.restBaseUrl}
                >
                  {({ data, isPending, error }) => {
                    if (isPending) return "Loading...";
                    if (error) return `Something went wrong: ${error.message}`;
                    if (data) {
                      if (data.success)
                        return (
                          <>
                            <Form.Control
                              as="select"
                              placeholder="select group"
                              disabled
                              value={user ? user.group : ""}
                            >
                              {data.groupTypes.map((groupType) => (
                                <option key={groupType}>{groupType}</option>
                              ))}
                            </Form.Control>
                          </>
                        );
                      else return <p>Something went wrong: {data.status}</p>;
                    }
                  }}
                </Async>
              </InputGroup>
            </Form.Group>
          </Form.Row>
          <Form.Row>
            <Form.Group as={Col}>
              <Form.Label>Old Password:</Form.Label>
              <Form.Control
                type="password"
                placeholder="enter old password"
                value={oldPassword}
                onChange={(e) => setOldPassword(e.target.value)}
              ></Form.Control>
            </Form.Group>
            <Form.Group as={Col}>
              <Form.Label>New Password:</Form.Label>
              <Form.Control
                type="password"
                placeholder="enter new password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
              ></Form.Control>
            </Form.Group>
            <Form.Group as={Col}>
              <Form.Label>Repeat Password:</Form.Label>
              <Form.Control
                type="password"
                placeholder="retype password"
                value={repeatPassword}
                onChange={(e) => setRepeatPassword(e.target.value)}
              ></Form.Control>
            </Form.Group>
          </Form.Row>
          <Button
            variant="primary"
            type="submit"
            value="Change Password"
            disabled={disableChangePassword()}
          >
            Login
          </Button>
        </Form>
      </div>
      <div>
        <h2 className="header">User Details</h2>
        <Form
          onSubmit={(e) => {
            e.preventDefault();

            async function submit() {
              let modUser = {
                firstname: firstname,
                lastname: lastname,
                email: email,
                active: user.active,
              };
              let signal = new AbortController().signal;

              let result = await pushUser({
                signal,
                endpoint: toUserEndpoint(userId),
                modifiedUser: modUser,
              });
              if (result.success) {
                result = await fetchUser({
                  signal,
                  endpoint: toUserEndpoint(userId),
                });
                if (result.success) setUser(result.user);
                if (props.onUserDetailsChanged)
                  props.onUserDetailsChanged(result);
              } else if (props.onUserDetailsChanged) {
                props.onUserDetailsChanged({ success: false });
              }
            }
            submit();
          }}
        >
          <Form.Row>
            <Form.Group as={Col}>
              <Form.Label>Firstname:</Form.Label>
              <Form.Control
                type="text"
                placeholder="enter firstname"
                value={firstname}
                onChange={(e) => setFirstname(e.target.value)}
              />
            </Form.Group>
            <Form.Group as={Col}>
              <Form.Label>Lastname:</Form.Label>
              <Form.Control
                type="text"
                placeholder="enter lastname"
                value={lastname}
                onChange={(e) => setLastname(e.target.value)}
              />
            </Form.Group>
            <Form.Group as={Col}>
              <Form.Label>E-Mail:</Form.Label>
              <InputGroup>
                <InputGroup.Prepend>
                  <InputGroup.Text>
                    <FaEnvelope />
                  </InputGroup.Text>
                </InputGroup.Prepend>
                <Form.Control
                  type="email"
                  placeholder="enter e-mail"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />
              </InputGroup>
            </Form.Group>
          </Form.Row>
          <Form.Row>
            <Form.Group as={Col}>
              <Form.Label>Company:</Form.Label>
              <InputGroup>
                <InputGroup.Prepend>
                  <InputGroup.Text>
                    <FaBriefcase />
                  </InputGroup.Text>
                </InputGroup.Prepend>
                <Form.Control
                  type="text"
                  readOnly
                  value={user ? user.companyDepartment.company.name : ""}
                />
              </InputGroup>
            </Form.Group>
            <Form.Group as={Col}>
              <Form.Label>Department:</Form.Label>
              <InputGroup>
                <InputGroup.Prepend>
                  <InputGroup.Text>
                    <FaBuilding />
                  </InputGroup.Text>
                </InputGroup.Prepend>
                <Form.Control
                  type="text"
                  readOnly
                  value={user ? user.companyDepartment.name : ""}
                />
              </InputGroup>
            </Form.Group>
          </Form.Row>
          <Button type="submit" disabled={disableChangeUserDetails()}>
            Change User
          </Button>
        </Form>
      </div>
    </>
  );
}

export default User;
