<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML>
<html>
<head>
<base href="<%=basePath%>">
<title>My WebSocket</title>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/commons/js/jquery-1.8.0.min.js"></script>
</head>

<body>
	<br>
	<div id="message">
		<hr>
		<form>
			模拟扫描匹配进程(配置好数据后点此运行匹配，若无匹配说明数据造得不对) <input type="button" value="调用"
				onclick="testMatch()">
		</form>
	</div>
</body>
<%
	String url = request.getParameter("url");
	String userId = request.getParameter("userId");
%>
<script type="text/javascript">
	var _context = "<%=request.getContextPath()%>";
	function testMatch() {
		var dataMap = '';

		$.ajax({
			type : "POST",
			url : _context + "/test/match",
			data : dataMap,
			success : function(jsonResult) {
				//document.write(jsonResult);
			}
		});
	}

	var websocket = null;
	var url = "<%=url%>";
	var userId = "<%=userId%>";
	//判断当前浏览器是否支持WebSocket
	if ('WebSocket' in window) {
		websocket = new WebSocket(url);
	} else {
		alert('Not support websocket');
	}
	//连接成功建立的回调方法
	websocket.onopen = function(event) {
		var data = "{isNewCreated:1,userId:" + userId + "}";
		websocket.send(data);// isNewCreated通知服务端将session放进ConcurrentHashMap
		setMessageInnerHTML("open");
	}

	//连接发生错误的回调方法
	websocket.onerror = function() {
		setMessageInnerHTML("error");
	};

	//接收到消息的回调方法
	websocket.onmessage = function() {
		setMessageInnerHTML(event.data);
		//websocket.close();
	}

	//连接关闭的回调方法
	websocket.onclose = function() {
		setMessageInnerHTML("close");
	}

	//监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
	window.onbeforeunload = function() {
		websocket.close();
	}

	//将消息显示在网页上
	function setMessageInnerHTML(innerHTML) {
		document.getElementById('message').innerHTML += innerHTML + '<br/>';
	}

	//关闭连接
	function closeWebSocket() {
		websocket.close();
	}

	//发送消息
	function send() {
		var message = document.getElementById('text').value;
		websocket.send(message);
	}
</script>
</html>