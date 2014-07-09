<%--
  User: pstrnad
  Date: 7/8/14
  Time: 10:08 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <title>Integrační portál - Login</title>
</head>
<body onload='document.loginForm.username.focus();'>
    <h1>Login</h1>
    <c:url var="loginUrl" value="/login-process"/>
    <form:form commandName="loginForm" method="post" action="${loginUrl}">
        <form:errors path="username"/>
        <table>
            <tr>
                <td><form:label path="username" >Uživatelské jméno:</form:label></td>
                <td><form:input path="username"/></td>
            </tr>
            <tr>
                <td><form:label path="password">Heslo:</form:label></td>
                <td><form:password path="password"/></td>
            </tr>
            <tr>
                <td colspan="2">
                    <input type="submit" value="Přihlásit se"/>
                </td>
            </tr>
        </table>
    </form:form>

    <a href="/register">Zaregistrovat se</a>
</body>
</html>
