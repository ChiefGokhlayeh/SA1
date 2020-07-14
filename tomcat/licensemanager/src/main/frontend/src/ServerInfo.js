import React from "react";

const ServerInfo = React.createContext({
  basename: "/licensemanager/web",
  restBaseUrl: `https://localhost:8443/licensemanager/rest`,
});

export default ServerInfo;
