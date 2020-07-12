import { withRouter } from "react-router-dom";
import Async from "react-async";
import React, { Component } from "react";

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
      <div>
        <h1>Dashboard</h1>
        <h2>
          Welcome {this.state.loginUser.firstname}, here are your licenses:
        </h2>
        <Async promiseFn={loadServiceContracts}>
          {({ data, err, isPending }) => {
            if (isPending) return "Loading...";
            if (err) return `Something went wrong: ${err.message}`;
            if (data) {
              return (
                <table>
                  <thead>
                    <tr>
                      <th>Product</th>
                      <th>Valid From</th>
                      <th>Valid To</th>
                      <th>Count</th>
                      <th>Mapping</th>
                      <th>Service Contract</th>
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
                          <td>{license.count}</td>
                          <td>
                            <ul>
                              {license.ipMappings.map((ipMapping) => (
                                <li key={ipMapping.id}>
                                  {ipMapping.ipAddress}
                                </li>
                              ))}
                            </ul>
                          </td>
                          <td>
                            <button
                              onClick={() => {
                                if (this.props.onOpenServiceContract)
                                  this.props.onOpenServiceContract(
                                    serviceGroup.serviceContract
                                  );
                              }}
                            >
                              Edit #{serviceGroup.serviceContract.id}
                            </button>
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              );
            }
          }}
        </Async>
      </div>
    );
  }
}

export default withRouter(Dashboard);
