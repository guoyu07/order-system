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
	
	function applyCleanService() {
		var apply_jsonData = $('#apply_jsonData').val();
		var dataMap = 'json_data='+apply_jsonData;
		$.ajax({
			type : "POST",
			url : _context + "/app/sv/clean",
			data : dataMap,
			success : function(jsonResult) {
				//document.write(jsonResult);
			}
		});
	}

	function report() {
		var goodsId = $('#report_goodsId').val();
		var roomId = $('#report_roomId').val();
		var reportType = $('#report_reportType').val();
		var reportDesc = $('#report_reportDesc').val();
		var orderId = $('#report_orderId').val();
		var dataMap = 'json_data=[{goodsId:' + goodsId + ',roomId:'+roomId+',reportType:' +
				reportType + ',orderId:'+orderId +',reportDesc:"' + encodeURI(encodeURI(reportDesc)) +'"}]';
		$.ajax({
			type : "POST",
			url : _context + "/man/addreport",
			data : dataMap,
			success : function(jsonResult) {
				//document.write(jsonResult);
			}
		});
	}
	
	function adjust() {
		var adjust_orderId = $('#adjust_orderId').val();
		var adjust_newMoney = $('#adjust_newMoney').val();
		var adjust_operRemark = $('#adjust_operRemark').val();
		var dataMap = 'json_data={orderId:' + adjust_orderId + ',newMoney:'+adjust_newMoney+',operRemark:"' +
		adjust_operRemark+'"}';
		$.ajax({
			type : "POST",
			url : _context + "/man/adjustrem",
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
	<hr>初始化xb_checkin.chkin_price/night_count
		<form action="<%=path%>/test/initnightcount" method="post">
			<input name="json_data" value=''>
			<input type="submit" value="按钮">
		</form>
		
		
	<hr>【早餐】查询门店列表
		<form action="<%=path%>/app/bf/get_hotel" method="post">
			<input name="json_data" value='{roomId:1}'>
			<input type="submit" value="按钮">
		</form>
	<hr>【早餐】查询入住的用户的信息
		<form action="<%=path%>/app/bf/get_user" method="post">
			<input name="json_data" value='{"lodgerId":"1536","checkinId":"13341"}'>
			<input type="submit" value="按钮">
		</form>
	
	
	
	<hr>
		<b>调整金额</b>
		<form>
			<table>
				<tr>
					<td>订单ID</td>
					<td><input value="139"  id="adjust_orderId"></td>
				</tr>
				<tr>
					<td>newMoney</td>
					<td><input value="1"  id="adjust_newMoney"></td>
				</tr>
				<tr>
					<td>operRemark</td>
					<td><input value="做得太差要减你薪水" id="adjust_operRemark"></td>
				</tr>
				<tr>
					<td colspan="2"><input type="button" value="调整金额" onclick="adjust()"></td>
				</tr>
			</table>
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
		<a href="man/qryorders">查询清洁单</a>
	
	
		<hr>
		<b>申请清洁服务</b>
		<form>
			json_data<input id="apply_jsonData" value='{mebId:23423,roomInfo:[{roomNo:"333",chainId:222},{roomNo:"332",chainId:221}],openId:"这是openId",mobile:"13435558987"}'><br> 
			<input type="button" value="调用" onclick="applyCleanService()">
		</form>
			
		<hr>
		<b>客户增加建议接口</b>
		<form>
			建议标题<input id="adv_subject" value='建议标题'><br> 建议内容<input
				id="adv_content" value='建议内容'><br> 联系方式<input
				id="contact" value='13431095554'><br> <input
				type="button" value="调用" onclick="addCustAdvice()">
		</form>





	</div>
</body>
</html>