import { FaBriefcase, FaMapMarker } from "react-icons/fa";
import { useParams } from "react-router";
import Async, { useAsync } from "react-async";
import Col from "react-bootstrap/Col";
import Container from "react-bootstrap/Container";
import Form from "react-bootstrap/Form";
import InputGroup from "react-bootstrap/InputGroup";
import ListGroup from "react-bootstrap/ListGroup";
import React, { useState, useEffect } from "react";
import Row from "react-bootstrap/Row";

const fetchCompany = async ({ signal, endpoint }) => {
  const resp = await fetch(
    `https://localhost:8443/licensemanager/rest/companies/${endpoint}`,
    { signal, credentials: "include", method: "GET" }
  );

  if (resp.ok) {
    return { success: true, company: await resp.json() };
  } else {
    return { success: false, status: resp.status };
  }
};

const fetchCompanyDepartments = async ({ signal, endpoint }) => {
  const resp = await fetch(
    `https://localhost:8443/licensemanager/rest/company-departments/${endpoint}`,
    { signal, credentials: "include", method: "GET" }
  );

  if (resp.ok) {
    return { success: true, companyDepartments: await resp.json() };
  } else {
    return { success: false, status: resp.status };
  }
};

const fetchUsers = async ({ signal, endpoint }) => {
  const resp = await fetch(
    `https://localhost:8443/licensemanager/rest/users/${endpoint}`,
    { signal, credentials: "include", method: "GET" }
  );

  if (resp.ok) {
    return { success: true, users: await resp.json() };
  } else {
    return { success: false, status: resp.status };
  }
};

function Company({ onCompanyChanged }) {
  const [company, setCompany] = useState(null);
  const [companyName, setCompanyName] = useState("");
  const [companyAddress, setCompanyAddress] = useState("");
  const [selectedCompanyDepartment, setSelectedCompanyDepartment] = useState(
    null
  );

  const { companyId } = useParams();

  const toCompanyEndpoint = (companyId) =>
    companyId ? Number(companyId) : "mine";

  const toCompanyDepartmentEndpoint = (companyId) =>
    `by-company/${Number(companyId)}`;

  const {
    data: companyData,
    isPending: isCompanyPending,
    error: companyError,
    reload: reloadCompany,
  } = useAsync({
    promiseFn: fetchCompany,
    endpoint: toCompanyEndpoint(companyId),
  });

  useEffect(() => {
    if (reloadCompany) reloadCompany();
  }, [companyId, reloadCompany]);

  useEffect(() => {
    if (companyData && companyData.success) {
      setCompany(companyData.company);
    }
  }, [companyData]);

  useEffect(() => {
    setCompanyName(company ? company.name : "");
    setCompanyAddress(company ? company.address : "");
  }, [company]);

  var companyForm = <>placeholder</>;
  if (isCompanyPending || !companyData) {
    companyForm = <p>Loading...</p>;
  } else if (companyError) {
    companyForm = <p>Something went wrong: {companyError.message}</p>;
  } else if (companyData.success) {
    companyForm = (
      <>
        <Form.Row>
          <Form.Group as={Col}>
            <Form.Label>Name:</Form.Label>
            <InputGroup>
              <InputGroup.Prepend>
                <InputGroup.Text>
                  <FaBriefcase />
                </InputGroup.Text>
              </InputGroup.Prepend>
              <Form.Control
                type="text"
                value={companyName}
                onChange={(e) => setCompanyName(e.target.value)}
                readOnly
              />
            </InputGroup>
          </Form.Group>
        </Form.Row>
        <Form.Row>
          <Form.Group as={Col}>
            <Form.Label>Address:</Form.Label>
            <InputGroup>
              <InputGroup.Prepend>
                <InputGroup.Text>
                  <FaMapMarker />
                </InputGroup.Text>
              </InputGroup.Prepend>
              <Form.Control
                as="textarea"
                rows="4"
                value={companyAddress}
                onChange={(e) => setCompanyAddress(e.target.value)}
                readOnly
              />
            </InputGroup>
          </Form.Group>
        </Form.Row>
      </>
    );
  } else {
    companyForm = <p>Something went wrong: {companyData.status}</p>;
  }

  return (
    <>
      <h2 className="header">Company</h2>
      <Container fluid>
        <Row>
          <Col>
            <Container fluid>
              <Row>
                <h3 className="header">Info</h3>
              </Row>
              <Row>
                <Col>
                  <Form>{companyForm}</Form>
                </Col>
              </Row>
              <Row>
                <h3 className="header">Departments</h3>
              </Row>
              <Row>
                <Col>
                  <ListGroup>
                    {company ? (
                      <Async
                        promiseFn={fetchCompanyDepartments}
                        endpoint={toCompanyDepartmentEndpoint(company.id)}
                      >
                        {({ data, isPending, error }) => {
                          if (isPending) return "Loading...";
                          if (error)
                            return `Something went wrong: ${error.message}`;
                          if (data) {
                            if (data.success) {
                              return data.companyDepartments.map(
                                (companyDepartment) => (
                                  <ListGroup.Item
                                    action
                                    active={
                                      selectedCompanyDepartment &&
                                      selectedCompanyDepartment.id ===
                                        companyDepartment.id
                                    }
                                    onClick={() =>
                                      setSelectedCompanyDepartment(
                                        companyDepartment
                                      )
                                    }
                                    key={companyDepartment.id}
                                  >
                                    {companyDepartment.name}
                                  </ListGroup.Item>
                                )
                              );
                            } else {
                              return `Something went wrong: ${data.status}`;
                            }
                          }
                          return <>placeholder</>;
                        }}
                      </Async>
                    ) : (
                      "Loading"
                    )}
                  </ListGroup>
                </Col>
              </Row>
            </Container>
          </Col>
          <Col>
            <h3 className="header">Users</h3>
            {selectedCompanyDepartment ? (
              <Async
                promiseFn={fetchUsers}
                endpoint={`by-company-department/${selectedCompanyDepartment.id}`}
              >
                {({ data, isPending, error }) => {
                  if (isPending) return "Loading...";
                  if (error) return `Something went wrong: ${error.message}`;
                  if (data) {
                    if (data.success) {
                      return (
                        <ListGroup>
                          {data.users.map((user) => (
                            <ListGroup.Item
                              action
                              key={user.id}
                              href={`/users/${user.id}`}
                            >
                              {user.firstname}
                            </ListGroup.Item>
                          ))}
                        </ListGroup>
                      );
                    } else {
                      return `Something went wrong: ${data.status}`;
                    }
                  }
                  return <>placeholder</>;
                }}
              </Async>
            ) : (
              <p className="text-center">
                Please select a department from list list on the left.
              </p>
            )}
          </Col>
        </Row>
      </Container>
    </>
  );
}

export default Company;
