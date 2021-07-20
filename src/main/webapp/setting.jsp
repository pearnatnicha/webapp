<%@ page import="java.util.Date" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<body>
<h2>This is where the content of setting page is</h2>
<h3>
    Time now is: <%
        Date date = new Date();
        out.println(date);
    %>
</h3>
</body>
</html>