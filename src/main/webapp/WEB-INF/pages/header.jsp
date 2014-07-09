<%--
  User: pstrnad
  Date: 7/8/14
  Time: 3:53 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div id="topbar">
    <security:authorize access="isAuthenticated()">
        <security:authentication property="principal" var="user"/>
        Logged in as ${user.username}. <a href="<c:url value="/j_spring_security_logout"/>">Logout</a>
    </security:authorize>
    <security:authorize access="isAnonymous()">
        <a href="/login">Login</a>
    </security:authorize>
</div>