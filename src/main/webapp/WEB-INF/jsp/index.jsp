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

	function chgPwd() {
		var mobile = $('#mobile2').val();
		var password = $('#password2').val();
		var newPassword = $('#newPassword').val();
		var dataMap = 'json_data={"mobile":"'+mobile+'","password":"'+password+'","newPassword":"'+newPassword+'"}';
		$.ajax({
			type : "POST",
			url : _context + "/user/chgpwd",
			data : dataMap,
			success : function(jsonResult) {
				//document.write(jsonResult);
			}
		});
	}
	
	function queryRoomGoods() {
		var roomId = $('#roomId').val();
		var dataMap = 'json_data={"roomId":'+roomId+'}';
		$.ajax({
			type : "POST",
			url : _context + "/goods/list",
			data : dataMap,
			success : function(jsonResult) {
				//document.write(jsonResult);
			}
		});
	}
	
	function advice() {
		var userId = $('#userId').val();
		var title = $('#title').val();
		var content = $('#content').val();
		var dataMap = 'json_data={"userId":' + userId + ',"title":"' + encodeURI(encodeURI(title)) + '",content:"' + encodeURI(encodeURI(content)) + '"}';
		$.ajax({
			type : "POST",
			url : _context + "/user/addadvice",
			data : dataMap,
			success : function(jsonResult) {
				//document.write(jsonResult);
			}
		});
	}
	
	function report() {
		var goodsId = $('#report_goodsId').val();
		var userId = $('#report_userId').val();
		var roomId = $('#report_roomId').val();
		var reportType = $('#report_reportType').val();
		var reportDesc = $('#report_reportDesc').val();
		var report_orderId = $('#report_orderId').val();
		var dataMap = 'json_data={goodsId:' + goodsId + ',userId:' + userId + ',roomId:'+roomId+',orderId:'+report_orderId+',reportType:' + reportType + ',reportDesc:"' + encodeURI(encodeURI(reportDesc)) +'"}';
		$.ajax({
			type : "POST",
			url : _context + "/goods/reportgoods",
			data : dataMap,
			success : function(jsonResult) {
				//document.write(jsonResult);
			}
		});
	}
	
	function chgBusiSts() {
		var userId = $('#chgbusists_userId').val();
		var newBusiSts = $('#chgbusists_busiSts').val();
		var dataMap = 'json_data={userId:' + userId + ',newBusiSts:' + newBusiSts +'}';
		$.ajax({
			type : "POST",
			url : _context + "/user/chgbusists",
			data : dataMap,
			success : function(jsonResult) {
				//document.write(jsonResult);
			}
		});
	}
	
	function queryBalance() {
		var userId = $('#account_userId').val();
		var dataMap = 'json_data={userId:' + userId +'}';
		$.ajax({
			type : "POST",
			url : _context + "/account/balance",
			data : dataMap,
			success : function(jsonResult) {
				//document.write(jsonResult);
			}
		});
	}
	
	
	
	function queryChain() {
		var userId = $('#chain_userId').val();
		var dataMap = 'json_data={userId:' + userId +'}';
		$.ajax({
			type : "POST",
			url : _context + "/chain/query",
			data : dataMap,
			success : function(jsonResult) {
				//document.write(jsonResult);
			}
		});
	}
	
	function finishcheck() {
		var userId = $('#b_userId').val();
		var roomId = $('#b_roomId').val();
		var dataMap = 'json_data={userId:' + userId + ',roomId:' + roomId + '}';
		$.ajax({
			type : "POST",
			url : _context + "/order/finishcheck",
			data : dataMap,
			success : function(jsonResult) {
				//document.write(jsonResult);
			}
		});
	}
	
	function setCleanRoom() {
		var userId = $('#b_userId').val();
		var roomId = $('#b_roomId').val();
		var dataMap = 'json_data={userId:' + userId + ',roomId:' + roomId + '}';
		$.ajax({
			type : "POST",
			url : _context + "/order/setroomcleaned",
			data : dataMap,
			success : function(jsonResult) {
				//document.write(jsonResult);
			}
		});
	}
	
	function syncOne() {
		var json_data = $('#json_data').val();
		var dataMap = 'json_data='+json_data;
		$.ajax({
			type : "POST",
			url : _context + "/ws/syncdirty",
			data : dataMap,
			success : function(jsonResult) {
				//document.write(jsonResult);
			}
		});
	}
	
	function syncMulti() {
		var json_data = $('#json_data_textarea').val();
		var dataMap = 'json_data='+json_data;
		$.ajax({
			type : "POST",
			url : _context + "/ws/syncmultidirty",
			data : dataMap,
			success : function(jsonResult) {
				//document.write(jsonResult);
			}
		});
	}
	
	function queryStep() {
		var stepRoomId = $('#step_roomId').val();
		var dataMap = 'json_data={roomId:' + stepRoomId + '}';
		
		$.ajax({
			type : "POST",
			url : _context + "/room/cleanstep",
			data : dataMap,
			success : function(jsonResult) {
				//document.write(jsonResult);
			}
		});
	}
	
	function queryBull() {
		var bull_chainId = $('#bull_chainId').val();
		var dataMap = 'json_data={chainId:' + bull_chainId + '}';
		
		$.ajax({
			type : "POST",
			url : _context + "/bull/list",
			data : dataMap,
			success : function(jsonResult) {
				//document.write(jsonResult);
			}
		});
	}
	
	function acceptOrReject() {
		var accept_userId = $('#accept_userId').val();
		var accept_orderId = $('#accept_orderId').val();
		var accept_operType = $('#accept_operType').val();
		var dataMap = 'json_data={userId:' + accept_userId + ',orderId:'+accept_orderId + ',operType:'+accept_operType + '}';
		
		$.ajax({
			type : "POST",
			url : _context + "/order/acceptreject",
			data : dataMap,
			success : function(jsonResult) {
				//document.write(jsonResult);
			}
		});
	}
	
	function queryUserOrderState() {
		var accept_userId = $('#cc_userId').val();
		var dataMap = 'json_data={userId:' + accept_userId +  '}';
		
		$.ajax({
			type : "POST",
			url : _context + "/order/qrysts",
			data : dataMap,
			success : function(jsonResult) {
				//document.write(jsonResult);
			}
		});
	}
	
	function queryByGoodsSetId() {
		var cc_goodsSetId = $('#cc_goodsSetId').val();
		var dataMap = 'json_data={goodsSetId:' + cc_goodsSetId +  '}';
		
		$.ajax({
			type : "POST",
			url : _context + "/goods/roomgoods",
			data : dataMap,
			success : function(jsonResult) {
				//document.write(jsonResult);
			}
		});
	}
	
	function queryByGoodsIds() {
		var cc_goodsIds = $('#cc_goodsIds').val();
		var dataMap = 'json_data={goodsIds:"' + cc_goodsIds +  '"}';
		
		$.ajax({
			type : "POST",
			url : _context + "/goods/goodsids",
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
	<hr>
	<b>查询svchain的接口</b>
	<form>
		用户ID<input value="1"  id="chain_userId"><input type="button" value="查询" onclick="queryChain()">
	</form>
	
	<hr>
	<b>查询用户订单状态接口</b>
	<form>
		用户ID<input id="cc_userId" value='1'><br>
		<input type="button" value="调用" onclick="queryUserOrderState()">
	</form>
	
	<hr>
	<b>接受或拒绝接口</b>
	<form>
		accept_userId<input id="accept_userId" value='1'><br>
		accept_orderId<input id="accept_orderId" value='1'><br>
		accept_operType<input id="accept_operType" value='1'>
		<input type="button" value="调用" onclick="acceptOrReject()">
	</form>
	
	
	<hr>
	<b>★通过ids查询物品(可以验证正确性)</b>
	<form>
		goodsIds<input id="cc_goodsIds" value='1:2:个,2:3:个,3:3:个,4:2:个,5:2:个,6:1:条,7:1:个,8:1:个,9:1:个,10:1:个,11:1:个,12:1:个,13:1:个,14:1:个,15:1:套,16:1:个,17:2:个,18:1:个,19:1:个,20:2:个,21:1:张'><br>
		<input type="button" value="调用" onclick="queryByGoodsIds()">
	</form>
	
	
	<hr>
	<b>★通过goodsSetId查询物品</b>
	<form>
		goodsSetId<input id="cc_goodsSetId" value='1'><br>
		<input type="button" value="调用" onclick="queryByGoodsSetId()">
	</form>
	
	
	
	
	
	
	
	<hr>
	<form action="<%=request.getContextPath()%>/websocket.jsp" method="post">
		测试web socket
		<input name="url" value="ws://localhost:8081/xbedservice/match/websocket">
		<input name="userId" value="2">
		<input type="submit" value="测试web socket" >
	</form>
	
	
	<hr>
	<form>
		<b>公告查询接口</b>
		区域ID<input id="bull_chainId" value='1'>
		<input type="button" value="调用" onclick="queryBull()">
	</form>
	
	
	<hr>
	<form>
		<b>和Bonly的接口:他调我</b>同步多个脏房数据
		<textarea rows="10" cols="40" id="json_data_textarea">{
    "PageAbleListOfRoom": {
        "Space": "www.mingyansoft.com",
        "Local": "PageAbleListOfRoom"
    },
    "PageCount": 1,
    "PageNo": 1,
    "PageSize": 10,
    "RowCount": 2,
    "DataSet": {
        "DataSet": {
            "Space": "www.mingyansoft.com",
            "Local": "DataSet"
        },
        "Room": [
            {
                "Room": {
                    "Space": "www.mingyansoft.com",
                    "Local": "Room"
                },
                "CheckInState": "CheckOut",
                "HouseKeepState": "EnableSaleRoom",
                "ELockNo": "",
                "CheckInFolioID": "7788",
                "HouseKeepFolioID": "0",
                "Remark": "",
                "BuildingID": "0",
                "RoomID": "5",
                "Floor": "2",
                "ChainID": "1",
                "RoomNo": "8206",
                "RoomTypeID": "1",
                "MasterRoomID": "0",
                "Flag": "1"
            },
            {
                "Room": {
                    "Space": "www.mingyansoft.com",
                    "Local": "Room"
                },
                "CheckInState": "CheckOut",
                "HouseKeepState": "EnableSaleRoom",
                "ELockNo": "",
                "CheckInFolioID": "9933",
                "HouseKeepFolioID": "0",
                "Remark": "",
                "BuildingID": "0",
                "RoomID": "4",
                "Floor": "2",
                "ChainID": "1",
                "RoomNo": "8205",
                "RoomTypeID": "1",
                "MasterRoomID": "0",
                "Flag": "1"
            }
        ]
    }
}</textarea>
		<input type="button" value="调用" onclick="syncMulti()">
	</form>
	
	<hr>
	<form>
		<b>和Bonly的接口:他调我</b>同步单个脏房数据
		<input id="json_data" value='{"roomId":"1","chainId":"1","buildingId":"1","roomNo":"1001","roomTypeId":"1","roomTypeName":"古房","roomTypeCode":"103","longitude":"34.331","latitude":"53.343","area":"110","buildingAddr":"尚层国际","roomAddr":"3009","address":"广州天河区天河北路179号尚层国际大厦3009","roomFolioId":"9911"}'>
		<input type="button" value="调用" onclick="syncOne()">
	</form>
	
	<hr>
	<form>
		<b>和Bonly的接口:我调他(还没通)</b>通知退款
		<input id="b_userId" value="1">
		<input id="b_roomId" value="1">
		<input type="button" value="调用" onclick="finishcheck()">
	</form>
	<hr>
	<form>
		<b>和Bonly的接口:我调他</b>改成清洁房，调用区管接口
		<input id="a_userId" value="1">
		<input id="a_roomId" value="1">
		<input type="button" value="调用" onclick="setCleanRoom()">
	</form>
	
	<hr>
	<b>查询清洁步骤: </b>
	<form>
		房间ID<input value="1"  id="step_roomId"><input type="button" value="查询" onclick="queryStep()">
	</form>
	
	
	
	
	
	<hr>
	<b>查询帐户剩余积分等统计数据</b>
	<form>
		用户ID<input value="1"  id="account_userId">
		<input type="button" value="查询统计" onclick="queryBalance()">
	</form>
	
	
	
	<hr>
	<b>更改用户接单状态</b>
	<form>
		用户ID<input value="1"  id="chgbusists_userId">
		新状态(1-可接单 2-不可接单)<input value="1"  id="chgbusists_busiSts"><input type="button" value="修改接单状态" onclick="chgBusiSts()">
	</form>
	
	
	<hr>
		<b>阿姨反馈物品缺失或损坏情况接口</b>
		<form>
			<table>
				<tr>
					<td>物品ID</td>
					<td><input value="1"  id="report_goodsId"></td>
				</tr>
				<tr>
					<td>用户ID</td>
					<td><input value="1"  id="report_userId"></td>
				</tr>
				<tr>
					<td>房间ID</td>
					<td><input value="1"  id="report_roomId"></td>
				</tr>
				<tr>
					<td>反馈类型1-数量不够 2-损坏</td>
					<td><input value="2" id="report_reportType"></td>
				</tr>
				<tr>
					<td>反馈内容</td>
					<td><input value="哪里哪里有问题" id="report_reportDesc"></td>
				</tr>
				<tr>
					<td>订单ID</td>
					<td><input value="333" id="report_orderId"></td>
				</tr>
				<tr>
					<td colspan="2"><input type="button" value="提交反馈" onclick="report()"></td>
				</tr>
			</table>
		</form>
		
		<hr>
		<b>阿姨提建议接口</b>
		<form>
			<table>
				<tr>
					<td>用户ID</td>
					<td><input value="1" name="userId" id="userId"></td>
				</tr>
				<tr>
					<td>建议标题</td>
					<td><input value="这个标题是没设计的" name="title" id="title"></td>
				</tr>
				<tr>
					<td>建议内容</td>
					<td><input value="我建议我建议要这样这样子" name="content" id="content"></td>
				</tr>
				<tr>
					<td colspan="2"><input type="button" value="提交意见" onclick="advice()"></td>
				</tr>
			</table>
		</form>
		
		
		<hr>
		<b>查询房间物品列表</b>
		房间ID:<input value="9527" id="roomId"><input type="button" value="查询房间拥有的物品列表" onclick="queryRoomGoods()">
		
		<hr>
		<b>测试修改密码</b>
		<form>
			<table>
				<tr>
					<td>用户名</td>
					<td><input value="13042014177" name="mobile" id="mobile2"></td>
				</tr>
				<tr>
					<td>密码</td>
					<td><input value="123456" name="password" id="password2"></td>
				</tr>
				<tr>
					<td>新密码</td>
					<td><input value="123456" name="newPassword" id="newPassword"></td>
				</tr>
				<tr>
					<td colspan="2"><input type="button" value="修改密码" onclick="chgPwd()"></td>
				</tr>
			</table>
		</form>


		<hr>
		<b>闪电服务测试登录接口</b>
		<form>
			<table>
				<tr>
					<td>用户名</td>
					<td><input value="13431093332" name="mobile" id="mobile1"></td>
				</tr>
				<tr>
					<td>密码</td>
					<td><input value="123456" name="password" id="password1"></td>
				</tr>
				<tr>
					<td colspan="2"><input type="button" value="登录" onclick="login()"></td>
				</tr>
			</table>
		</form>




	</div>
</body>
</html>