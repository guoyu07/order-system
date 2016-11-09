<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript"
	src="<%=request.getContextPath()%>/commons/js/jquery-1.8.0.min.js"></script>
<title>Insert title here</title>
<%
	String path = request.getContextPath();
%>
<style type="text/css">

.m-div a {
	margin: 15px 0 15px 0;
}
</style>
<script type="text/javascript">
	var _context = "<%=path%>";
	function login() {
		var mobile = $('#mobile1').val();
		var password = $('#password1').val();
		var dataMap = 'json_data={"mobile":"' + mobile + '","password":"'
				+ password + '"}';
		$.ajax({
			type : "POST",
			url : _context + "/user/login",
			data : dataMap,
			success : function(jsonResult) {
				//document.write(jsonResult);
			}
		});
	}
	function func() {
		var aa_orderId = $('#aa_orderId').val();
		var aa_orderNo = $('#aa_orderNo').val();
		var aa_checkinId = $('#aa_checkinId').val();
		var dataMap = 'json_data={"orderId":"' + aa_orderId + '","orderNo":"'
				+ orderNo + + '","checkinId":"' + aa_checkinId + '"}';
		$.ajax({
			type : "POST",
			url : _context + "/test/debugsuperman",
			data : dataMap,
			success : function(jsonResult) {
				//document.write(jsonResult);
			}
		});
	}
</script>
</head>
<body>
	<div class="m-div">
		<hr>帮助DEBUG,查询某个房间某个日期到某个日期之间有什么订单
		<form action="<%=path%>/test/queryorderbyperiod" method="post">
			<input name="json_data" width="60" value='{start:"2016-01-07",end:"2016-01-08",roomId:1}'>
			<input type="submit" value="按钮">
		</form>
		
		
		<hr>帮助debug
		<form action="<%=path%>/test/queryorderbyperiod" method="post">
			orderId:<input name="aa_orderId" width="60" value=''>
			orderNo:<input name="aa_orderNo" width="60" value=''>
			checkinId:<input name="aa_checkinId" width="60" value=''>
			<input type="button" onclick="func()" value="按钮">
		</form>

	</div>
</body>
</html>