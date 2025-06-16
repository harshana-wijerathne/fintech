<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>JSP - Hello World</title>
</head>
<body>
<%
    if(session.getAttribute("username")==null){
        response.sendRedirect("pages/login.jsp");
    }
%>
<%@include file="pages/header.jsp"%>
<h1><%= "Hello World!" %>
</h1>
<br/>
<a href="api/customers">Hello Servlet</a>
</body>
</html>