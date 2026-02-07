<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<style>
	.error-message {
		background-color: #ffcccc;
		border: 1px solid #ff0000;
		color: #cc0000;
		padding: 10px;
		margin: 10px 0;
		border-radius: 5px;
	}
	.success-message {
		background-color: #ccffcc;
		border: 1px solid #00ff00;
		color: #006600;
		padding: 10px;
		margin: 10px 0;
		border-radius: 5px;
	}
</style>
</head>
<body>

<!-- Mostrar mensaje de error si existe -->
<c:if test="${not empty errorMessage}">
	<div class="error-message">
		<strong>Error en la importación:</strong> ${errorMessage}
	</div>
</c:if>

<!-- Mostrar mensaje de éxito si existe -->
<c:if test="${not empty successMessage}">
	<div class="success-message">
		<strong>Éxito:</strong> ${successMessage}
	</div>
</c:if>

Clientes: <br /><br />

<c:forEach items="${taxpayers}" var="Client">
	
	Nombre: ${Client.name}<br />
	<c:forEach items="${Client.address}" var="add">
		Direccion: ${add.name}  ${add.number} <br />
	</c:forEach>
	<c:forEach items="${Client.subsidiary}" var="sucursal">
		Sucursal: ${sucursal.name} <br />
	</c:forEach>
	<c:forEach items="${Client.payBookInstance}" var="payBookInstance">
		Instancia de Libro: ${payBookInstance} <br />
		<c:forEach items="${payBookInstance.payBookDetails}" var="payBookDetails">
			Detalle de Instancia: ${payBookDetails} <br /> 
		</c:forEach>
		 
	</c:forEach>
	<br />
	<br />

	
</c:forEach>

<form action="test" >
	<input type="submit"/>
	

</form>
<form action="importBook">
<input type="submit" value="Cargar Info Mensual" />
</form>


</body>
</html>