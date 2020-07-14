import { withRouter } from "react-router-dom";
import Async from "react-async";
import React, { Component } from "react";
import Table from "react-bootstrap/Table";
import Button from "react-bootstrap/Button";

const fetchLicenses = async ({ signal }) => {
  const resp = await fetch(
    "https://localhost:8443/licensemanager/rest/licenses/mine/",
    { signal, credentials: "include", method: "GET" }
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

class Dashboard extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loginUser: props.loginUser,
    };
  }

  render() {
    const earlier = (a, b) => (a < b ? a.toLocaleString() : b.toLocaleString());

    return (
      <>
        <h1 className="header">Dashboard</h1>
        <h2 className="header">
          Welcome {this.state.loginUser.firstname}, here are your licenses:
        </h2>
        <Async promiseFn={fetchLicenses}>
          {({ data, err, isPending }) => {
            if (isPending) return "Loading...";
            if (err) return `Something went wrong: ${err.message}`;
            if (data) {
              if (data.success) {
                return (
                  <Table hover={true}>
                    <thead>
                      <tr>
                        <th scope="col">Product</th>
                        <th scope="col">Valid From</th>
                        <th scope="col">Valid To</th>
                        <th scope="col">Count</th>
                        <th scope="col">Mapping</th>
                        <th scope="col">Service Contract</th>
                      </tr>
                    </thead>
                    <tbody>
                      {data.licenses.map((license) => (
                        <tr key={license.id}>
                          <td>
                            {license.productVariant.product}{" "}
                            {license.productVariant.version}
                          </td>
                          <td>
                            {new Date(
                              license.serviceContract.start
                            ).toLocaleString()}
                          </td>
                          <td>
                            {earlier(
                              new Date(license.serviceContract.end),
                              new Date(license.expirationDate)
                            )}
                          </td>
                          <td className="text-center">{license.count}</td>
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
                                          <li key={ipMapping.id}>
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
                          <td className="text-center">
                            <Button
                              onClick={() => {
                                if (this.props.onOpenServiceContract)
                                  this.props.onOpenServiceContract(
                                    license.serviceContract
                                  );
                              }}
                            >
                              {this.state.loginUser &&
                              (this.state.loginUser.group === "SYSTEM_ADMIN" ||
                                this.state.loginUser.group === "COMPANY_ADMIN")
                                ? "Edit"
                                : "View"}{" "}
                              #{license.serviceContract.id}
                            </Button>
                          </td>
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
      </>
    );
  }
}

export default withRouter(Dashboard);
