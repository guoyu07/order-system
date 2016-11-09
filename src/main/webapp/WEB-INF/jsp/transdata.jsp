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
</script>
</head>
<body>
	<div class="m-div">
		<hr>转换xb_calendar数据到xb_calendar_daily【此操作需要几秒到十几秒的操作时间】
		<form action="<%=path%>/test/transdata" method="post">
			<input type="submit" value="谨慎操作">
		</form>
	</div>
</body>
</html>