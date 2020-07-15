import React from "react";

const ServerInfo = React.createContext({
  basename: "/licensemanager/web",
  restBaseUrl: `http://localhost:8080/licensemanager/rest`,
});

export default ServerInfo;
