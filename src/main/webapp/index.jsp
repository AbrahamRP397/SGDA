<%--
    Vista técnica: index.
    Responsabilidad: estructura la interfaz, enlaza recursos y expone datos preparados por los controladores.
    Autor: Dulce Janet Ríos Aguilar.
    Desde: 2026-08-24.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<%
  response.sendRedirect(request.getContextPath() + "/login");
%>
</body>
</html>
<%-- Punto de entrada del WAR; deriva la navegación al flujo configurado por la aplicación. --%>
