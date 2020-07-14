import Async from "react-async";
import Button from "react-bootstrap/Button";
import React, { useContext } from "react";
import ServerInfo from "./ServerInfo";
import Table from "react-bootstrap/Table";

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

const fetchIpMappings = async ({ signal, id, restBaseUrl }) => {
  const resp = await fetch(`${restBaseUrl}/ip-mappings/by-license/${id}`, {
    signal,
    credentials: "include",
    method: "GET",
  });

  if (resp.ok) {
    return { success: true, ipMappings: await resp.json() };
  } else {
    return { success: false, status: resp.status };
  }
};

function Dashboard(props) {
  const serverInfo = useContext(ServerInfo);

  const earlier = (a, b) => (a < b ? a.toLocaleString() : b.toLocaleString());

  return (
    <>
      <h1 className="header">Dashboard</h1>
      <h2 className="header">
        Welcome {props.loginUser ? props.loginUser.firstname : "???"}, here are
        your licenses:
      </h2>
      <Async promiseFn={fetchLicenses}>
        {({ data, error, isPending }) => {
          if (isPending) return "Loading...";
          if (error) return `Something went wrong: ${error.message}`;
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
                          <Async
                            promiseFn={fetchIpMappings}
                            id={license.id}
                            restBaseUrl={serverInfo.restBaseUrl}
                          >
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
                              if (props.onOpenServiceContract)
                                props.onOpenServiceContract(
                                  license.serviceContract
                                );
                            }}
                          >
                            {props.loginUser &&
                            (props.loginUser.group === "SYSTEM_ADMIN" ||
                              props.loginUser.group === "COMPANY_ADMIN")
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

export default Dashboard;
