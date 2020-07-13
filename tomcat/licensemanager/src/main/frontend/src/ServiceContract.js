import { FaBriefcase } from "react-icons/fa";
import { useAsync } from "react-async";
import Col from "react-bootstrap/Col";
import Container from "react-bootstrap/esm/Container";
import Form from "react-bootstrap/Form";
import InputGroup from "react-bootstrap/InputGroup";
import React from "react";
import Table from "react-bootstrap/Table";

const fetchContract = async ({ signal, id }) => {
  let resp = await fetch(
    `https://localhost:8443/licensemanager/rest/service-contracts/${id}`,
    {
      credentials: "include",
      method: "GET",
      signal,
    }
  );
  return {
    status: resp.status,
    serviceContract: resp.ok ? await resp.json() : null,
  };
};

const fetchServiceGroups = async ({ signal, id }) => {
  let resp = await fetch(
    `https://localhost:8443/licensemanager/rest/service-groups/by-contractor/${id}`,
    {
      credentials: "include",
      method: "GET",
      signal,
    }
  );
  return {
    status: resp.status,
    serviceGroups: resp.ok ? await resp.json() : null,
  };
};

function ServiceContract({ id }) {
  const {
    data: contractResult,
    isPending: isContractPending,
    error: contractError,
  } = useAsync({
    promiseFn: fetchContract,
    id,
  });
  const {
    data: groupsResult,
    isPending: isGroupsPending,
    error: groupsError,
  } = useAsync({
    promiseFn: fetchServiceGroups,
    id,
  });

  if (isGroupsPending) var groupsPendingContent = <p>Loading service groups</p>;
  if (groupsError)
    var groupsErrorContent = <p>Something went wrong: {groupsError.message}</p>;
  if (groupsResult) {
    if (groupsResult.status < 300) {
      var groupsResultContent = (
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
            {groupsResult.serviceGroups.map((group) => {
              return (
                <tr key={`${group.serviceContract.id}_${group.user.id}`}>
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
      groupsResultContent = (
        <p>Something went wrong! Response status: {groupsResult.status}</p>
      );
    }
  }

  if (isContractPending) return <p>Loading service contract...</p>;
  if (contractError)
    return <p>Something went wrong: {contractError.message}</p>;
  if (contractResult) {
    if (contractResult.status === 200) {
      return (
        <Form>
          <Form.Row>
            <Form.Group as={Col} md="3">
              <Form.Label>ID:</Form.Label>
              <Form.Control
                type="text"
                placeholder="service contract ID"
                readOnly
                value={contractResult.serviceContract.id}
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
                  value={contractResult.serviceContract.contractor.name}
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
                  new Date(contractResult.serviceContract.start)
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
                  new Date(contractResult.serviceContract.end)
                    .toISOString()
                    .split("T")[0]
                }
              />
            </Form.Group>
          </Form.Row>
          <Container md="12">
            <h3 className="header">Licenses in contract:</h3>
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
                {contractResult.serviceContract.licenses.map((license) => (
                  <tr key={license.id}>
                    <td>{license.productVariant.product}</td>
                    <td>{license.productVariant.version}</td>
                    <td>{license.expirationDate}</td>
                    <td>{license.count}</td>
                    <td>
                      <ul>
                        {license.ipMappings.map((ipMapping) => (
                          <li key={ipMapping.id} className="text-monospace">
                            {ipMapping.ipAddress}
                          </li>
                        ))}
                      </ul>
                    </td>
                    <td className="text-monospace">{license.key}</td>
                  </tr>
                ))}
              </tbody>
            </Table>
          </Container>
          <div>
            <h3 className="header">Users with access:</h3>
            {isGroupsPending ? (
              groupsPendingContent
            ) : groupsError ? (
              groupsErrorContent
            ) : groupsResult ? (
              groupsResultContent
            ) : (
              <div>placeholder</div>
            )}
          </div>
        </Form>
      );
    }

    return (
      <div>
        {id}
        <div>{JSON.stringify(contractResult)}</div>
      </div>
    );
  }
  return <div>placeholder</div>;
}

export default ServiceContract;
