<%--
  User: pstrnad
  Date: 7/8/14
  Time: 1:07 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
    <title>Integrační portál - Registrace</title>
    <meta charset="UTF-8">
    <meta http-equiv="content-type" content="text/html;charset=utf-8">
</head>
<body>
    <h1>Registrace</h1>

    <form:form commandName="registerForm" method="post" action="/register">
        <table>
            <tr>
                <td><form:label path="username">Uživatelské jméno:</form:label></td>
                <td><form:input path="username"/> <form:errors path="username"/></td>
            </tr>
            <tr>
                <td><form:label path="password">Heslo:</form:label></td>
                <td><form:password path="password"/> <form:errors path="password"/></td>
            </tr>
            <tr>
                <td><form:label path="passwordRepeat">Heslo znovu:</form:label></td>
                <td><form:password path="passwordRepeat"/></td>
            </tr>
            <tr>
                <td colspan="2">
                    <input type="submit" value="Zaregistrovat"/>
                </td>
            </tr>
        </table>
    </form:form>
</body>
</html>
