<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>

<script type="text/javascript"> 
$( document ).ready(function() {
	
	function verCargas(){
		console.log("cargas");
	}
	
	function changeDisplay(id){
		$("#"+id).show();
	};

	console.log("test");
	
	$(document).on("click", "#client", function(){
		console.log(this.title);
		changeDisplay("client_dir");
		changeDisplay("client_suc");
		//$("#client_dir").show();
	});
	
	$(document).on("click", "#client_dir", function(){
		console.log("client_dir");
		
	});
	
	//$(document).on("click", "#button_charge", function(){
		//console.log("cargas");
		//window.location.href = "/charges?id="+this.title; 
	//});
	
});

</script>

</head>
<body>



<form method="post" action="/charges">
	<input type="submit" name="name" value="williams" />
	<input type="text" name="name" value="williams" />
	<input type="hidden" name="invisible" value="williams2" />
</form>

<table border="1">
	<tbody>
		<tr><td><h2>CLIENTES</h2></td></tr>
		<c:forEach items="${taxpayers}" var="Client">
		<form method="post" action="/charges" th:action="@{/charges}">
		<tr  title="${Client.rut}">
			<td id="client">		
					<h3>Nombre: ${Client.name}</h3>
			</td>
			
				<td><h3>Rut: ${Client.rut}</h3></td>
				<td><input type="button" value="templates"></td>
				<td><input type="submit" id="button_charge" title="${Client.id}" value="cargas" ></td>
				<td><input type="hidden" name="id" value="${Client.id}" /></td>
				<td><input type="button" value="empleados" ></td>
		</tr>
		<tr id="client_dir" style="display:none">
			<td></td>
			<c:forEach items="${Client.address}" var="add">
			<td><h4>Direccion: ${add.name}  ${add.number}</h4></td>
			</c:forEach>
		</tr>
		<tr id="client_suc" style="display:none">
			<td></td>
			<c:forEach items="${Client.subsidiary}" var="sucursal">
			<td><h4>Sucursal: ${sucursal.name} <br />
			</h4></td>
			</c:forEach>
		</tr>
		</form>
		</c:forEach>

	</tbody>
</table>

<!--				<c:forEach items="${Client.subsidiary}" var="sucursal">
						Sucursal: ${sucursal.name} <br />
					</c:forEach>
					Template: ${Client.template}<br />
					<br />
					<br />
  -->
	

<br /><br />



<!-- <form action="test" >
	<input type="submit"/>
	

</form>
<form action="importBook">
<input type="submit" value="Cargar Info Mensual" />
</form>
 -->

</body>
</html>