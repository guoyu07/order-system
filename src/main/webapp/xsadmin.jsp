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
<h5>2016-03-04 11:30:54</h5>
	<div class="m-div">
		<table>
			<tr>
				<td>app1.jsp</td>
				<td><a href="<%=path%>/test/forward/app1">按钮</a></td>
			</tr>
			<tr>
				<td>test.jsp</td>
				<td><a href="<%=path%>/test/forward/test">测试</a></td>
			</tr>
			<%-- <tr>
				<td>app.jsp</td>
				<td><a href="<%=path%>/test/forward/app">按钮</a></td>
			</tr>
			<tr>
				<td>index.jsp</td>
				<td><a href="<%=path%>/test/forward/index">按钮</a></td>
			</tr>
			<tr>
				<td>websocket.jsp</td>
				<td><a href="<%=path%>/test/forward/websocket">按钮</a></td>
			</tr>
			<tr>
				<td>room_state_tool.jsp</td>
				<td><a href="<%=path%>/test/initopercode">按钮</a></td>
			</tr> --%>
			<tr>
				<td>management.jsp</td>
				<td><a href="<%=path%>/test/forward/management">按钮</a></td>
			</tr>
			<%-- <tr>
				<td>transdata.jsp</td>
				<td><a href="<%=path%>/test/forward/transdata">转换xb_calendar数据到xb_calendar_daily</a></td>
			</tr> --%>
		</table>
	</div>
</body>
</html>