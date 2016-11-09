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
	function addCustAdvice() {
		var adv_subject = $('#adv_subject').val();
		var adv_content = $('#adv_content').val();
		var contact = $('#contact').val();
		var dataMap = 'json_data={"advSubject":"' + adv_subject + '","advContent":"'
				+ adv_content + '",contact:"' + contact  + '"}';
		$.ajax({
			type : "POST",
			url : _context + "/app/adv/add",
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
		<hr>增加一条日志
		<form action="<%=path%>/test/testLog" method="get">
			<input name="json_data" value='{logLevel:"info",msg:"logmsg"}'>
			<input type="submit" value="按钮">
		</form>
		
		<hr>增加一条日志2
		<form action="<%=path%>/test/testReqFromJsp" method="post">
			<input name="json_data" value='{logLevel:"info",msg:"logmsg"}'>
			<input type="submit" value="按钮">
		</form>
	
	</div>
</body>
</html>