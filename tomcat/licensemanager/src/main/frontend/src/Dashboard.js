import { withRouter } from "react-router-dom";
import React, { Component } from "react";

class Dashboard extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loginUser: props.loginUser,
    };
  }

  render() {
    return (
      <div>
        <h1>Dashboard</h1>
        <h2>Welcome {this.state.loginUser.firstname}</h2>
      </div>
    );
  }
}

export default withRouter(Dashboard);
