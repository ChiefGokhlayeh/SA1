import { withRouter } from "react-router-dom";
import Async from "react-async";
import React, { Component } from "react";
import Table from "react-bootstrap/Table";
import Button from "react-bootstrap/Button";

const loadServiceContracts = async ({ signal }) => {
  const res = await fetch(
    `https://localhost:8443/licensemanager/rest/service-groups/mine/`,
    { signal, credentials: "include", method: "GET" }
  );
  if (!res.ok) throw new Error(res.statusText);
  return res.json();
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
        <Async promiseFn={loadServiceContracts}>
          {({ data, err, isPending }) => {
            if (isPending) return "Loading...";
            if (err) return `Something went wrong: ${err.message}`;
            if (data) {
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
                    {data.map((serviceGroup) =>
                      serviceGroup.serviceContract.licenses.map((license) => (
                        <tr key={license.id}>
                          <td>
                            {license.productVariant.product}{" "}
                            {license.productVariant.version}
                          </td>
                          <td>
                            {new Date(
                              serviceGroup.serviceContract.start
                            ).toLocaleString()}
                          </td>
                          <td>
                            {earlier(
                              new Date(serviceGroup.serviceContract.end),
                              new Date(license.expirationDate)
                            )}
                          </td>
                          <td className="text-center">{license.count}</td>
                          <td>
                            <ul>
                              {license.ipMappings.map((ipMapping) => (
                                <li key={ipMapping.id}>
                                  {ipMapping.ipAddress}
                                </li>
                              ))}
                            </ul>
                          </td>
                          <td className="text-center">
                            <Button
                              onClick={() => {
                                if (this.props.onOpenServiceContract)
                                  this.props.onOpenServiceContract(
                                    serviceGroup.serviceContract
                                  );
                              }}
                            >
                              {this.state.loginUser &&
                              (this.state.loginUser.group === "SYSTEM_ADMIN" ||
                                this.state.loginUser.group === "COMPANY_ADMIN")
                                ? "Edit"
                                : "View"}{" "}
                              #{serviceGroup.serviceContract.id}
                            </Button>
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </Table>
              );
            }
          }}
        </Async>
      </>
    );
  }
}

export default withRouter(Dashboard);
