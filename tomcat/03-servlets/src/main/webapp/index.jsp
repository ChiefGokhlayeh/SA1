<%! int fontSize; %>
<%! int count; %>
<%! private de.hse.swa.FileCounter dao = new de.hse.swa.FileCounter(); %>
<%
 dao.getCount();
 %>
<html>

<head>
    <title>A JSP example </title>
</head>

<body>
    <p>
        Date &amp; time: <%= (new java.util.Date()).toString() %>
    </p>
    <h3>
        This site has been accessed <%= dao.getCount() %> times.
    </h3>
    <%for ( fontSize = 2; fontSize <= 4; fontSize++){ %>
    <font color="green" size="<%= fontSize %>">
        JSP Tutorial
    </font><br />
    <%}%>
 <% int count = dao.getCount();
 ++count;
 dao.save(count);
 %>
</body>

</html>
