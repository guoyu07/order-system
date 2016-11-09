<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="cn.com.xbed.app.commons.util.Base64ImageUtil" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript"
	src="<%=request.getContextPath()%>/commons/js/jquery-1.8.0.min.js"></script>
	
	<script type="text/javascript"
	src="<%=request.getContextPath()%>/commons/js/jquery-1.8.0.min.js"></script>
<title>Insert title here</title>
<%
	String path = request.getContextPath();
	String strImg = Base64ImageUtil.getImageStr("d:/Gandum.jpg");
%>
<style type="text/css">
.m-div a {
	margin: 15px 0 15px 0;
}
</style>

</head>
<body>
	<div class="m-div">
	<hr>【修改房态,谨慎操作,该方法用于房态错误需要用SQL修复的时候】
		<form action="<%=path%>/test/sync" method="post">
			操作码: <input id="operateCode" value=''><br>
			roomId:<input id="roomId" value=''><br>
			日期(填写类似2015-12-02):<input id="date" value=''><br>
			<input type="button" value="朕要修改" onclick="modifyRoomState()">
			<span id="hint"></span>
		</form>
		
		<%
			List<Map<String,Object>> list = (List<Map<String,Object>>) request.getAttribute("operateCodeList");
			if (list != null) {
				out.print("操作代码 \t\t 注释 \t\t calendar_state \t\t room_state");
				out.print("<br>");
				for(Map<String,Object> map : list) {
					out.print(map.get("code") + "\t\t" + map.get("remark") + "\t\t" + map.get("calendar_state") + "\t\t" + map.get("room_state"));
					out.print("<br>");
				}
			}
		%>
 


	</div>
</body>
<script type="text/javascript">

var _context = "<%=path%>";
function modifyRoomState() {
	var operateCode = $('#operateCode').val();
	var roomId = $('#roomId').val();
	var date = $('#date').val();
	if (!operateCode || !roomId || !date) {
		alert("请填写正确的值");
		return;
	}
	if (!confirm("请核对是否填写正确?\r\noperateCode: " + operateCode + ",roomId: " + roomId + ",date: \"" + date + "\"")) {
		return;
	}
	var json_data = '{operateCode:'+$.trim(operateCode)+ ',roomId:' +$.trim(roomId)+ ',date:'+$.trim(date)+'}';
	var dataMap = 'json_data='+json_data;
	json_data = encodeURI(encodeURI(json_data));
	$.ajax({
		type : "POST",
		url : _context + "/test/roomstate",
		data : dataMap,
		success : function(jsonResult) {
			//document.write(jsonResult);
		}
	});
}
initOperCode();

</script>
</html>