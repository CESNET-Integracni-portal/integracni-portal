<%--
  User: pstrnad
  Date: 7/3/14
  Time: 10:39 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Integrační portál</title>
</head>
<body>
    <jsp:include page="header.jsp"/>

    <h1>Integrační portál</h1><a href="/register">Zaregistrovat se</a>

    <ul>
        <li><a href="/auth-only">Stránka dostupná pouze přihlášeným uživatelům</a></li>
        <li><a href="/admin-only">Stránka dostupná pouze administrátorům</a></li>
    </ul>
</body>
</html>
