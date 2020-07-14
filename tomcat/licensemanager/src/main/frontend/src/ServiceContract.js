import { FaBriefcase } from "react-icons/fa";
import { Async } from "react-async";
import Col from "react-bootstrap/Col";
import Container from "react-bootstrap/esm/Container";
import Form from "react-bootstrap/Form";
import InputGroup from "react-bootstrap/InputGroup";
import React from "react";
import Table from "react-bootstrap/Table";

const fetchServiceContract = async ({ signal, id }) => {
  const resp = await fetch(
    `https://localhost:8443/licensemanager/rest/service-contracts/${id}`,
    {
      credentials: "include",
      method: "GET",
      signal,
    }
  );

  if (resp.ok) {
    return { success: true, serviceContract: await resp.json() };
  } else {
    return { success: false, status: resp.status };
  }
};

const fetchLicenses = async ({ signal, id }) => {
  const resp = await fetch(
    `https://localhost:8443/licensemanager/rest/licenses/by-service-contract/${id}`,
    {
      credentials: "include",
      method: "GET",
      signal,
    }
  );

  if (resp.ok) {
    return { success: true, licenses: await resp.json() };
  } else {
    return { success: false, status: resp.status };
  }
};

const fetchIpMappings = async ({ signal, id }) => {
  const resp = await fetch(
    `https://localhost:8443/licensemanager/rest/ip-mappings/by-license/${id}`,
    { signal, credentials: "include", method: "GET" }
  );

  if (resp.ok) {
    return { success: true, ipMappings: await resp.json() };
  } else {
    return { success: false, status: resp.status };
  }
};

const fetchServiceGroups = async ({ signal, id }) => {
  const resp = await fetch(
    `https://localhost:8443/licensemanager/rest/service-groups/by-service-contract/${id}`,
    {
      credentials: "include",
      method: "GET",
      signal,
    }
  );

  if (resp.ok) {
    return { success: true, serviceGroups: await resp.json() };
  } else {
    return { success: false, status: resp.status };
  }
};

function ServiceContract({ id }) {
  return (
    <>
      <Async promiseFn={fetchServiceContract} id={id}>
        {({ data, isPending, error }) => {
          if (isPending) return "Loading...";
          if (error) return `Something went wrong: ${error.message}`;
          if (data) {
            if (data.success) {
              return (
                <Form>
                  <Form.Row>
                    <Form.Group as={Col} md="3">
                      <Form.Label>ID:</Form.Label>
                      <Form.Control
                        type="text"
                        placeholder="service contract ID"
                        readOnly
                        value={data.serviceContract.id}
                      />
                    </Form.Group>
                    <Form.Group as={Col} md="9">
                      <Form.Label>Contractor:</Form.Label>
                      <InputGroup>
                        <InputGroup.Prepend>
                          <InputGroup.Text>
                            <FaBriefcase />
                          </InputGroup.Text>
                        </InputGroup.Prepend>
                        <Form.Control
                          type="text"
                          placeholder="name of company"
                          readOnly
                          value={data.serviceContract.contractor.name}
                        />
                      </InputGroup>
                    </Form.Group>
                  </Form.Row>
                  <Form.Row>
                    <Form.Group as={Col} md="6">
                      <Form.Label>Contractual period from:</Form.Label>
                      <Form.Control
                        type="date"
                        placeholder="start of contract"
                        readOnly
                        value={
                          new Date(data.serviceContract.start)
                            .toISOString()
                            .split("T")[0]
                        }
                      />
                    </Form.Group>
                    <Form.Group as={Col} md="6">
                      <Form.Label>To:</Form.Label>
                      <Form.Control
                        type="date"
                        placeholder="end of contract"
                        readOnly
                        value={
                          new Date(data.serviceContract.end)
                            .toISOString()
                            .split("T")[0]
                        }
                      />
                    </Form.Group>
                  </Form.Row>
                </Form>
              );
            } else {
              return `Something went wrong: ${data.status}`;
            }
          }
        }}
      </Async>
      <Container md="12">
        <h3 className="header">Licenses in contract:</h3>
        <Async promiseFn={fetchLicenses} id={id}>
          {({ data, isPending, error }) => {
            if (isPending) return "Loading...";
            if (error) return `Something went wrong: ${error.message}`;
            if (data) {
              if (data.success) {
                return (
                  <Table md="12" hover="true">
                    <thead>
                      <tr>
                        <th scope="col">Product</th>
                        <th scope="col">Variant</th>
                        <th scope="col">Expiration Date</th>
                        <th scope="col">Count</th>
                        <th scope="col">Mapping</th>
                        <th scope="col">Key</th>
                      </tr>
                    </thead>
                    <tbody>
                      {data.licenses.map((license) => (
                        <tr key={license.id}>
                          <td>{license.productVariant.product}</td>
                          <td>{license.productVariant.version}</td>
                          <td>
                            {new Date(license.expirationDate).toLocaleString()}
                          </td>
                          <td>{license.count}</td>
                          <td>
                            <Async promiseFn={fetchIpMappings} id={license.id}>
                              {({ data, isPending, error }) => {
                                if (isPending) return "Loading...";
                                if (error)
                                  return `Something went wrong: ${error.message}`;
                                if (data) {
                                  if (data.success) {
                                    return (
                                      <ul>
                                        {data.ipMappings.map((ipMapping) => (
                                          <li
                                            key={ipMapping.id}
                                            className="text-monospace"
                                          >
                                            {ipMapping.ipAddress}
                                          </li>
                                        ))}
                                      </ul>
                                    );
                                  } else {
                                    return `Something went wrong: ${data.status}`;
                                  }
                                }
                              }}
                            </Async>
                          </td>
                          <td className="text-monospace">{license.key}</td>
                        </tr>
                      ))}
                    </tbody>
                  </Table>
                );
              } else {
                return `Something went wrong: ${data.status}`;
              }
            }
          }}
        </Async>
      </Container>
      <Container md="12">
        <h3 className="header">Users with access:</h3>
        <Async promiseFn={fetchServiceGroups} id={id}>
          {({ data, isPending, error }) => {
            if (isPending) return "Loading...";
            if (error) return `Something went wrong: ${error.message}`;
            if (data) {
              if (data.success) {
                return (
                  <Table hover="true" md="12">
                    <thead>
                      <tr>
                        <th scope="col">Username</th>
                        <th scope="col">Firstname</th>
                        <th scope="col">Lastname</th>
                        <th scope="col">Company</th>
                        <th scope="col">Department</th>
                      </tr>
                    </thead>
                    <tbody>
                      {data.serviceGroups.map((group) => {
                        return (
                          <tr
                            key={`${group.serviceContract.id}_${group.user.id}`}
                          >
                            <td>{group.user.credentials.loginname}</td>
                            <td>{group.user.firstname}</td>
                            <td>{group.user.lastname}</td>
                            <td>{group.user.companyDepartment.company.name}</td>
                            <td>{group.user.companyDepartment.name}</td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </Table>
                );
              } else {
                return `Something went wrong: ${data.status}`;
              }
            }
          }}
        </Async>
      </Container>
    </>
  );
}

export default ServiceContract;
