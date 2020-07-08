import { Link } from "react-router-dom";
import React, { Component } from "react";

class Logout extends Component {
  constructor(props) {
    super(props);
    if (props.onLogout) props.onLogout();
  }

  render() {
    return (
      <div>
        You are logged out. Please <Link to="/login">login</Link> again to
        access the service.
      </div>
    );
  }
}

export default Logout;
