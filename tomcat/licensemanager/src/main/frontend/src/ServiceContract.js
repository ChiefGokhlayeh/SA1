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

function ServiceContract({ id }) {
  const { data, isPending, error } = useAsync({ promiseFn: fetchContract, id });

  if (isPending) return <p>Loading data...</p>;
  if (error) return <p>Something went wrong: {error.message}</p>;
  if (data) {
    if (data.status === 200) {
      return (
        <div>
          <label htmlFor="id">ID:</label>
          <input
            id="id"
            type="number"
            readOnly
            value={data.serviceContract.id}
          />
          <label htmlFor="contractor">Contractor:</label>
          <input
            id="contractor"
            type="text"
            readOnly
            value={data.serviceContract.contractor.name}
          />
          <label htmlFor="from">Contractual period from</label>
          <input
            id="from"
            type="date"
            readOnly
            value={
              new Date(data.serviceContract.start).toISOString().split("T")[0]
            }
          />
          <label htmlFor="to">to</label>
          <input
            id="to"
            type="date"
            readOnly
            value={
              new Date(data.serviceContract.end).toISOString().split("T")[0]
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
                {data.serviceContract.licenses.map((license) => (
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
              <tbody></tbody>
            </table>
          </div>
        </div>
      );
    }

    return (
      <div>
        {id}
        <div>{JSON.stringify(data)}</div>
      </div>
    );
  }
  return <div>placeholder</div>;
}

export default ServiceContract;
