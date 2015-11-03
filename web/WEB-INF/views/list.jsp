<%--
  Created by IntelliJ IDEA.
  User: hubo
  Date: 2015/9/13
  Time: 16:51
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<html>
<head>
    <base href="<%=basePath%>">
    <title>聊天管理界面</title>
</head>
<body>
<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>客服编号</th>
        <th>客服名称</th>
        <th>操作</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${requestScope.csList}" var="cs">
        <tr>
            <td>${cs.id}</td>
            <td>${cs.code}</td>
            <td>${cs.name}</td>
            <td><a href="chat/rm/cs/${cs.id}">移除</a></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<hr>
<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>用户昵称</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${requestScope.userList}" var="u">
        <tr>
            <td>${u.id}</td>
            <td>${u.name}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<hr>
<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>用户昵称</th>
        <th>OpenId</th>
        <th>操作</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${requestScope.wxUserList}" var="u">
        <tr>
            <td>${u.id}</td>
            <td>${u.name}</td>
            <td>${u.sourceId}</td>
            <td><a href="chat/rm/u/${u.sourceId}">移除</a></td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<c:forEach items="${requestScope.chatRoomList}" var="r">
    <hr>
    <div style="border: 1px solid #333">
        聊天室信息:<br>客服id:${r.cs.id} name：${r.cs.name}
        <hr>
        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>用户昵称</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${r.userList}" var="u">
                <tr>
                    <td>${u.id}</td>
                    <td>${u.name}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</c:forEach>

</body>
</html>
