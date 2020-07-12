import { FaEnvelope, FaBriefcase, FaUser, FaUsers } from "react-icons/fa";
import { useAsync, Async } from "react-async";
import Badge from "react-bootstrap/Badge";
import Button from "react-bootstrap/Button";
import Col from "react-bootstrap/Col";
import Container from "react-bootstrap/Container";
import Form from "react-bootstrap/Form";
import InputGroup from "react-bootstrap/InputGroup";
import React, { useState } from "react";
import Row from "react-bootstrap/Row";
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

const fetchGroupTypes = async ({ signal }) => {
  const resp = await fetch(
    `https://localhost:8443/licensemanager/rest/users/group-types`,
    { signal, credentials: "include", method: "GET" }
  );

  if (resp.ok) {
    return { success: true, groupTypes: await resp.json() };
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
    <>
      <div>
        <h2 className="header">
          <Container>
            <Row>
              <Col></Col>
              <Col>Credentials</Col>
              <Col>
                <Badge variant={user.active ? "primary" : "secondary"}>
                  {user.active ? "Active" : "Inactive"}
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
                  value={user ? user.credentials.loginname : null}
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
                <Async promiseFn={fetchGroupTypes}>
                  {({ data, isPending, error }) => {
                    if (isPending) return "Loading...";
                    if (error) return `Something went wrong: ${error.message}`;
                    if (data) {
                      if (data.success)
                        return (
                          <>
                            <Form.Control
                              as="select"
                              name="group"
                              disabled
                              value={user ? user.group : null}
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
          </Form.Row>
          <Form.Row>
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
