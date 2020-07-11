import React from "react";
import { useAsync } from "react-async";

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
        <table>
          <thead>
            <tr>
              <th>Username</th>
              <th>Firstname</th>
              <th>Lastname</th>
              <th>Company</th>
              <th>Department</th>
            </tr>
          </thead>
          <tbody>
            {groupsResult.serviceGroups.map((group) => {
              return (
                <tr key={`${group.serviceContract.id}_${group.user.id}`}>
                  <td>{group.user.credentials.loginname}</td>
                  <td>{group.user.firstname}</td>
                  <td>{group.user.lastname}</td>
                  <td>coming soon</td>
                  <td>coming soon</td>
                </tr>
              );
            })}
          </tbody>
        </table>
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
        <div>
          <label htmlFor="id">ID:</label>
          <input
            id="id"
            type="number"
            readOnly
            value={contractResult.serviceContract.id}
          />
          <label htmlFor="contractor">Contractor:</label>
          <input
            id="contractor"
            type="text"
            readOnly
            value={contractResult.serviceContract.contractor.name}
          />
          <label htmlFor="from">Contractual period from</label>
          <input
            id="from"
            type="date"
            readOnly
            value={
              new Date(contractResult.serviceContract.start)
                .toISOString()
                .split("T")[0]
            }
          />
          <label htmlFor="to">to</label>
          <input
            id="to"
            type="date"
            readOnly
            value={
              new Date(contractResult.serviceContract.end)
                .toISOString()
                .split("T")[0]
            }
          />
          <div>
            Licenses in contract:
            <table>
              <thead>
                <tr>
                  <th>Product</th>
                  <th>Variant</th>
                  <th>Expiration Date</th>
                  <th>Count</th>
                  <th>Mapping</th>
                  <th>Key</th>
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
                          <li key={ipMapping.id}>{ipMapping.ipAddress}</li>
                        ))}
                      </ul>
                    </td>
                    <td>{license.key}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <div>
            Users with access:
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
        </div>
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
