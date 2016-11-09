<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="cn.com.xbed.app.commons.util.Base64ImageUtil" %>
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

	String strImg = Base64ImageUtil.getImageStr("d:/none.jpg");
%>
<style type="text/css">
.m-div a {
	margin: 15px 0 15px 0;
}
</style>
<script type="text/javascript">
function ffff() {
	var dataMap = 'json_data={如果是中文通过AJAX发起,而不是HTML的标签,会怎么样2}';
	$.ajax({
		type : "POST",
		url : _context + "/test/testReqFromJsp",
		data : dataMap,
		success : function(jsonResult) {
			alert(jsonResult);
		}
	});
}

</script>
</head>
<body>
	<hr>查询活动订单
		<form action="<%=path%>/app/ordmgnt/queryotachannelorder" method="post">
			<input name="json_data" width="60" value='{}'>
			<input type="submit" value="按钮">
		</form>
		

	<hr>修改日历价格
		<form action="<%=path%>/app/roommgnt/editcalprice" method="post">
			<input name="json_data" width="60" value='{account:"xbedadmin",password:"bae4cbdad9b98ab7100dd8e5c617ef65",data:[{roomId:1,date:"2015-09-21",newPrice:38800},{roomId:1,date:"2015-09-22",newPrice:28800}]}'>
			<input type="submit" value="按钮">
		</form>
		
		
<hr>查订单的价格
<form action="<%=path%>/app/ordmgnt/qryprice" method="post">
	<input name="json_data" value='{roomId:64,beginDate:"2016-02-22",endDate:"2016-02-23",lodgerId:1072,cardCode:23}'>
	<input type="submit" value="按钮">
</form>



<hr>模拟业主过来的房间的审核
	<form action="<%=path%>/test/trans2LocalRoom" method="post">
		<input name="json_data" width="60" value='{"tmpRoomId":17}'>
		<input type="submit" value="按钮">
	</form>
	
	
<hr>查询接口(通过房间id查房间信息和门店信息)
	<form action="<%=path%>/common/qryroominfo" method="post">
		<input name="json_data" width="60" value='{"roomId":668}'>
		<input type="submit" value="按钮">
	</form>
	
	<hr>办理入住加载用户数据
	<form action="<%=path%>/app/ordmgnt/qrycheckiner" method="post">
		<input name="json_data" width="60" value='{"checkInId":367}'>
		<input type="submit" value="按钮">
	</form>
		 
		 
		 
<hr>查询房间详情
		<form action="<%=path%>/app/ordmgnt/roomdetail" method="post">
			<input name="json_data" width="60" value='{roomId:62}'>
			<input type="submit" value="查询房间详情">
		</form>
<hr>OMS查询订单状态2
		<form action="<%=path%>/app/ordmgnt/queryorders" method="post">
			<input name="json_data" value='{thisPage:1,pageSize:20}'>
			<input type="submit" value="按钮2">
		</form>
		
<hr>查询未入住的单(OMS用)
		<form action="<%=path%>/app/ordmgnt/qrynotchkinord" method="post">
			<input name="json_data" value='{thisPage:1,pageSize:20}'>
			<input type="submit" value="按钮">
		</form>
		
<hr>查询可换房的接口
		<form action="<%=path%>/app/ordmgnt/orderinfocheckininfo" method="post">
			<input name="json_data" width="60" value='{orderNo:"2016020211531168301",qryType:1}'>
			<input type="submit" value="按钮">
		</form>
		

		
	<hr>查询能换房的订单(已支付未入住,已支付未入住)
		<form action="<%=path%>/app/ordmgnt/canchgroomorderlist" method="post">
			<input name="json_data" width="60" value="{thisPage:1,pageSize:20}">
			<input type="submit" value="按钮">
		</form>
	
	
<hr>查询换房目标roomList
		<form action="<%=path%>/app/ordmgnt/destroomlist" method="post">
			<input name="json_data" width="60" value="{checkinId:2815}">
			<input type="submit" value="按钮">
		</form>
<hr>【我的订单】
		<form action="<%=path%>/app/ordmgnt/querylodgerorders" method="post">
			<input name="json_data" value=' {lodgerId:2, orderType:"self2"}'>
			<input type="submit" value="按钮">
		</form>
		
		
	<hr>查开门密码
		<form action="<%=path%>/app/ordmgnt/doorpwd" method="post">
			<input name="json_data" width="60" value='{account:"xbedadmin",password:"bae4cbdad9b98ab7100dd8e5c617ef65",checkinId:293}'>
			<input type="submit" value="按钮">
		</form>
		
	
		
查询停用单
<form action="<%=path%>/app/ordmgnt/querystopsheet" method="post">
	<input name="json_data" width="60" value='{thisPage:1,pageSize:50}'>
	<input type="submit" value="测试">
</form>


<hr>查询携程订单
		<form action="<%=path%>/app/ordmgnt/queryctriporder" method="post">
			<input name="json_data" width="60" value=''>
			<input type="submit" value="按钮">
		</form>
		
		
<hr>查询活动订单
		<form action="<%=path%>/app/ordmgnt/querypromoorder" method="post">
			<input name="json_data" width="60" value=''>
			<input type="submit" value="按钮">
		</form>
		
	<hr>查询导航图
		<form action="<%=path%>/app/roommgnt/qrynavipic" method="post">
			<input name="json_data" value='{roomId:1}'>
			<input type="submit" value="按钮">
		</form>



<hr>查询某个房间的图片
		<form action="<%=path%>/app/roommgnt/qryroompic" method="post">
			<input id="json_data" name="json_data" width="60" value='{roomId:1}'>
			<input type="submit" value="按钮" >
		</form>
		
		
		
<hr>新增房间信息
<form action="<%=path%>/app/roommgnt/operroom" method="post">
	<input id="adfasdfasdf" name="json_data" width="60" value='{operType:1,roomId:23234,roomInfo:{roomName:"房间名",chainId:123,roomTypeName:"类型名称",roomTypeId:23,title:"房间个性标题",descri:"房间亮点描述",province:"省份中文",city:"城市",district:"区",addr:"详细地址",houseType:"x室x厅x厨x卫x阳台",area:"面积",lodgerCount:"可住人数",bedCount:"床位数",tag:"房间特色标签",price:468},faciInfo:[{operType:1,facilityId:123},{operType:1,facilityId:456},{operType:1,facilityId:789}],additionInfo:{traffic:"交通路线",guide:"入住指南"}}'>
	<input type="button" value="新增房间信息" onclick="addRoom()">
</form>
		
		
<hr>查询设施
<form action="<%=path%>/app/roommgnt/queryfacility" method="post">
	<input id="json_data" name="json_data" width="60" value=''>
	<input type="submit" value="按钮" >
</form>
		
		
<hr>查询所有区域中心（分店）
		<form action="<%=path%>/app/roommgnt/querychain" method="post">
			<input id="json_data" name="json_data" width="60" value=''>
			<input type="submit" value="按钮" >
		</form>
		
<hr>查询日历
		<form action="<%=path%>/app/roommgnt/calendar" method="post">
			<input name="json_data" width="60" value="{roomId:8,howManyMon:3}">
			<input type="submit" value="查询日历">
		</form>
		
		
<hr>查询房间
<form action="<%=path%>/app/roommgnt/query" method="post">
	<input name="json_data" width="60" value="{roomId:668}">
	<input type="submit" value="查询房间">
</form>


	<hr>预计退房数
		<form action="<%=path%>/ljh/precheckout" method="post">
			<input name="json_data" value='{date:"2016-01-28"}'>
			<input type="submit" value="按钮">
		</form>
		
<hr>查询所有房间
<form action="<%=path%>/app/roommgnt/list" method="post">
	<input name="json_data" width="60" value="{qryPreCheckout:1}">
	<input type="submit" value="查询所有房间">
</form>
		
		
<hr>续住
	<form action="<%=path%>/app/ordmgnt/overstay" method="post">
		<input name="json_data" value='{    "checkinId": 2777,    "source": 0,    "roomId": 68,    "overstayEndDay": "2016-01-30",    "actualPrice": 100,    "lodgerId": 296,    "invoiceInfo": {        "invoiceTitle": "广州搜床网络科技有限公司",        "mailName": "王永锋",        "contactNo": "13062014179",        "mailAddr": "广州市荔湾区芳村信义路24号七喜创意园3栋103",        "postCode": "510640"    },    "payType": 1}'>
		<input type="submit" value="点击">
	</form>
	
	
<hr>检查是否可以续住
<form action="<%=path%>/app/ordmgnt/checkoverstay" method="post">
	<input id="ttt" name="json_data" width="60" value='{checkinId:2777,lodgerId:296}'>
	<input type="submit" value="点击">
</form>


<hr>查发票信息
<form action="<%=path%>/app/ordmgnt/queryinvoice" method="post">
	<input id="ttt" name="json_data" width="60" value='{orderId:2772}'>
	<input type="submit" value="点击">
</form>
		
		
<hr>查可以预定的房间
		<form action="<%=path%>/app/ordmgnt/queryrooms" method="post">
			<input id="ttt" width="60" value='{province:"广东省",city:"广州市",district:"海珠区",time:{start:"2015-11-05",end:"2015-11-07"},chainId:3}'>
<!-- 			<input id="ttt" width="60" value='{chainId:1}'>
 -->			<input type="button" value="查可以预定的房间" onclick="queryrooms()">
		</form>
		
<form action="<%=path%>/test/testReqFromJsp">
	<input type="checkbox" name="hobby" value="basketball">basketball
	<input type="checkbox" name="hobby" value="football">football
	<input type="checkbox" name="hobby" value="table tenis">table tenis
	<input type="submit" value="点击">
</form>



<hr>测试
<form action="<%=path%>/test/testReqFromJsp" method="post">
	<!-- <input type="button" value="点击" onclick="ffff()"> -->
	<input name="json_data" width="60" value=''>
	<input type="submit" value="点击">
</form>

<hr>支付成功
	<form action="<%=path%>/app/ordmgnt/paysucc" method="post">
		<input id="book_room_json_data" name="json_data" value='{orderNo:"2016012114331769001",platformSn:"1263833801201601211433181072"}'>
		<input type="submit" value="点击">
	</form>

<hr>预定房间
	<form action="<%=path%>/app/ordmgnt/book" method="post">
		<input id="book_room_json_data" name="json_data" value='{source:0,    "roomId": 668,    "beginDate": "2016-01-25",    "endDate": "2016-01-26",    "actualPrice": 2200,    "lodgerId": 2,    "checkInData": [        {            "checkInName": "王永锋",            "checkInMobile": "13042014179"        },        {            "checkInName": "周会",            "checkInMobile": "13042014171"        }    ],    "invoiceInfo": {        "invoiceTitle": "广州搜床网络科技有限公司",        "mailName": "王永锋",        "contactNo": "13062014179",        "mailAddr": "广州市荔湾区芳村信义路24号七喜创意园3栋103",        "postCode": "510640"    },    "payType": 1}'>
		<input type="button" value="按钮" onclick="bookRoom()">
	</form>
		


<hr>业主APP新增订单()
<form action="<%=path%>/owner/neworder" method="post">
	<input name="json_data" width="60" value='{"bookName":"fooocker","bookMobile":"13926415757","begin":"2016-02-18","end":"2016-02-19","roomId":"1"}'>
	<input type="submit" value="查询">
</form>


<hr>查询房间当前房态
<form action="<%=path%>/app/roommgnt/querycurroomstat" method="post">
	<input id="book_room_json_data" name="json_data" value='{roomId:1}'>
	<input type="submit" value="查询">
</form>
	
<hr>某个位置附近多少米内的房间
<form action="<%=path%>/app/roommgnt/around" method="post">
	<input id="book_room_json_data" name="json_data" value='{location:"113.22842,23.0968746",radius:500}'>
	<input type="submit" value="查询">
</form>




<hr>查询身份证信息
		<form action="<%=path%>/app/lodger/idcardinfo" method="post">
			<input id="json_data" name="json_data" width="60" value='{checkinId:289}'>
			<input type="submit" value="按钮" >
		</form>
		
	<hr>lodger登录  微信
		<form action="<%=path%>/app/lodger/login" method="post">
			<input id="json_data" name="json_data" width="60" value='{"mobile":"13042014179","password":"123456",from:1,openId:"openIdKKKKBBccc"}'>
			<input type="submit" value="按钮" >
		</form>
		

<hr>查用户信息
		<form action="<%=path%>/app/lodger/lodgerinfo" method="post">
			<input name="json_data" width="60" value=''>
			<input type="submit" value="按钮">
		</form>
		
<hr>取消订单(OMS用)
		<form action="<%=path%>/app/ordmgnt/omscancelorder" method="post">
			<input name="json_data" value='{orderId:3485}'>
			<input type="submit" value="按钮">
		</form>
		
		



<hr>取消订单(微信端用)
		<form action="<%=path%>/app/ordmgnt/cancelorder" method="post">
			<input name="json_data" value='{orderId:3505}'>
			<input type="submit" value="按钮">
		</form>
		
		
-------------------------------------------华丽分割线2------------------------------------------------------------------------<br>
预退房提醒,每天早上10点钟运行,点击一次模拟一次
<form action="<%=path%>/test/testprecheckouthint" method="post">
	<input name="json_data" width="60" value=''>
	<input type="submit" value="测试">
<br>
</form>
-------------------------------------------华丽分割线------------------------------------------------------------------------


<hr>【换房接口】
		<form action="<%=path%>/app/ordmgnt/chgrooms" method="post">
			<input name="json_data" width="60" value='{account:"xbedadmin",password:"bae4cbdad9b98ab7100dd8e5c617ef65",chgOperList:[{newRoomId:668,oldCheckinId:2263}]}'>
			<input type="submit" value="按钮">
		</form>
		

		

		

		
		

		

		
		
<hr>业主app发送短信
		<form action="<%=path%>/owner/sendsms" method="post">
			<input name="json_data" value='{mobile:"13042014179",content:"test"}'>
			<input type="submit" value="按钮">
		</form>
		
		
		


业主新增
<form action="<%=path%>/owner/newowner" method="post">
	<input name="json_data" value='{name:"test",mobile:"13042014179"}'>
	<input type="submit" value="按钮">
</form>
		
		


		
		
		
		
新建停用单
<form action="<%=path%>/app/ordmgnt/addstopsheet" method="post">
	<input name="json_data" width="60" value='{ roomId:1,stopBegin:"2015-12-25",stopEnd:"2015-12-26",contactName:"wangnima",contactMobile:"1388888888",memo:"becacuse wwww",userId:122,userAcct:"zhongsm"}'>
	<input type="submit" value="测试">
</form>

取消停用单
<form action="<%=path%>/app/ordmgnt/cancelstopsheet" method="post">
	<input name="json_data" width="60" value='{stopId:3,userId:123,userAcct:"zhongsm"}'>
	<input type="submit" value="测试">
</form>


-------------------------------------------华丽分割线------------------------------------------------------------------------
扫描自动退房进程，点击则运行一次
<form action="<%=path%>/test/autocheckout" method="post">
	<input name="json_data" width="60" value=''>
	<input type="submit" value="测试">
</form>
扫描即将超时的订单，点击则运行一次
<form action="<%=path%>/test/scanwarning" method="post">
	<input name="json_data" width="60" value=''>
	<input type="submit" value="测试">
</form>
扫描超时取消的订单，点击则运行一次
<form action="<%=path%>/test/scancancel" method="post">
	<input name="json_data" width="60" value=''>
	<input type="submit" value="测试">
</form>
扫描i_from_qhh表，即去呼呼同步给我们的报文
<form action="<%=path%>/test/testsyncqhh" method="post">
	<input name="json_data" width="60" value=''>
	<input type="submit" value="测试">
</form>
-------------------------------------------华丽分割线------------------------------------------------------------------------


		
<hr>xmonitor查询下一任用户
		<form action="<%=path%>/common/qrynextlodger" method="post">
			<input name="json_data" width="60" value='{roomId:63, date:"2015-12-18"}'>
			<input type="submit" value="查询">
		</form>
		
<hr>xmonitor当前任
<form action="<%=path%>/common/qrynowlodger" method="post">
	<input name="json_data" width="60" value='{roomId:65}'>
	<input type="submit" value="查询">
</form>
		
<hr>xmonitor查询用户信息
		<form action="<%=path%>/common/qrylodger" method="post">
			<input name="json_data" width="60" value='{lodgerId:2}'>
			<input type="submit" value="查询">
		</form>
		

	<div class="m-div">
		<hr>上传身份证【正式】
		<form action="<%=path%>/app/lodger/upload" method="post">
			<input type="button" value="按钮" onclick="uploadIdCard()">
		</form>
	<hr>图片上传
		<form action="<%=path%>/app/roommgnt/upload" method="post" enctype="multipart/form-data" id="uploadpicform">
			房间号<input type="text" name="json_data" value='{operType:1,roomId:1,isCover:0,tag:"客房"}'><br>
			图片上传<input type="file" name="file_data">
			<input type="submit" value="上传">
		</form>
		
	<hr>OMS发送短信
		<form action="<%=path%>/app/valid/sendsms" method="post">
			<input name="json_data" value='{mobile:"13042014179",content:"test"}'>
			<input type="submit" value="按钮">
		</form>
	<hr>查询可以checkin的列表
		<form action="<%=path%>/app/ordmgnt/querycheckin" method="post">
			<input id="json_data" name="json_data" width="60" value='{lodgerId:2}'>
			<input type="submit" value="按钮" >
		</form>
	

		
	<hr>【模拟去哪儿同步】
		<form action="<%=path%>/test/sync" method="post">
			<input name="json_data" value='D:/moniquhh/a_test_unpay.txt'>
			<input type="submit" value="按钮">
		</form>
		
	<hr>【从去哪儿报文获得有用的结构】
		<form action="<%=path%>/test/getusefulsyncentity" method="post">
			<input name="json_data" value='D:/moniquhh/h_direct_sync_checkin.txt'>
			<input type="submit" value="按钮">
		</form>
		
		
	
		
		
		
	
		

		
		
		<hr>新建房态控制单
		<form action="<%=path%>/app/ordmgnt/addroomctrlsheet" method="post">
<!-- 			<input name="json_data" value='{ctrlType:1,chainId:1,roomId:64,checkinTime:"2015-12-13",checkoutTime:"2015-12-14",openMan:"王永锋",openManMobile:"13042014179",remark:"remark",account:"xbedadmin",password:"bae4cbdad9b98ab7100dd8e5c617ef65"}'> -->
			<input name="json_data" value='{"ctrlType":"1","chainId":"1","roomId":"40","checkinTime":"2015-11-17","checkoutTime":"2015-11-18","openMan":"13560448557","openManMobile":"13560448557","remark":"测试","account":"xbedadmin","password":"bae4cbdad9b98ab7100dd8e5c617ef65"}'>
			<input type="submit" value="按钮">
		</form>
		
	
	
	
	<hr>查询房态控制单
		<form action="<%=path%>/app/ordmgnt/roomctrlsheet" method="post">
			<input id="json_data" name="json_data" width="60" value='{thisPage:1,pageSize:50}'>
			<input type="submit" value="按钮" >
		</form>
	

		
		
	<hr>测试
		<form action="<%=path%>/test/test" method="post">
			<input name="json_data" value=''>
			<input type="submit" value="按钮">
		</form>
		
	<hr>退订房间
		<form action="<%=path%>/app/ordmgnt/checkout" method="post">
			<input name="json_data" value='{checkinId:300,roomId:2,mobile:"13042014179",password:"123456"}'>
			<input type="submit" value="按钮">
		</form>
		
		
	
		
	
		
	
		
		

		
	
		
	
	
	
		
		<hr>修改日历状态
		<form action="<%=path%>/app/roommgnt/editcalstat" method="post">
			<input name="json_data" width="60" value='{account:"xbedadmin",password:"bae4cbdad9b98ab7100dd8e5c617ef65",data:[{roomId:1,date:"2015-09-21",statOper:1},{roomId:1,date:"2015-09-22",statOper:1}]}'>
			<input type="submit" value="按钮">
		</form>
		
		
	
		
		
	
	
	
	<hr>查询身份证信息 OMS用
		<form action="<%=path%>/web/storyCardAction/getStoryCard" method="post">
			<input name="json_data" width="60" value="{checkinId:289}">
			<input type="submit" value="按钮">
		</form>
		
	
		
		
		
	<hr>上传身份证3,尝试在客户端进行加密
		<form action="<%=path%>/app/lodger/upload" method="post">
			<input type="hidden" id="hiddenImgStr" value="<%=strImg %>">
			<input type="button" value="按钮" onclick="upload()">
		</form>
		
	<hr>上传身份证2
		<form action="<%=path%>/app/lodger/upload" method="post">
			<textarea rows="40" cols="80" name="json_data"><%=strImg %></textarea>
			<input type="submit" value="按钮">
		</form>
		
			
	
		
	<hr>模拟去哪儿发报文
		<form action="<%=path%>/app/sync/rooms" method="post">
			<textarea rows="40" cols="80" name="json_data" id="qunar_data">
			{
    "chainId": 1,
    "data": {
        "status": 0,
        "errMsg": "调用成功",
        "ver": "1.0",
        "data": {
            "fromTime": "2015-10-23 00:00:00",
            "orderList": [
                {
                    "orderNo": "17055509148611",
                    "channelID": "qhh",
                    "channelName": "去呼呼公寓直销订单",
                    "channelOrderNo": "17055509148611",
                    "checkInTime": "2015-10-30 14:00",
                    "checkOutTime": "2015-11-01 12:00",
                    "contactName": "PJ_test1",
                    "contactMobile": "13926415757",
                    "upDate": "2015-10-23 15:30:26",
                    "orderStatusCode": "2",
                    "orderStatusMsg": "已分房",
                    "payTypeCode": "040",
                    "payTypeMsg": "异常状态",
                    "rentMoney": "398",
                    "roomInfos": [
                        {
                            "checkInStatus": "10",
                            "checkInStatusMsg": "未入住",
                            "chkinTime": "2015-10-30 14:00",
                            "chkoutTime": "2015-11-01 12:00",
                            "customerName": "PJ_test1",
                            "customerTelNo": "13926415757",
                            "stayID": "20151023193001",
                            "realChkinTime": null,
                            "realChkoutTime": null,
                            "roomId": "8",
                            "roomPrice": "398"
                        },
                        {
                            "checkInStatus": "10",
                            "checkInStatusMsg": "未入住",
                            "chkinTime": "2015-10-30 14:00",
                            "chkoutTime": "2015-11-01 12:00",
                            "customerName": "PJ_test1",
                            "customerTelNo": "13926415757",
                            "stayID": "20151023193002",
                            "realChkinTime": null,
                            "realChkoutTime": null,
                            "roomId": "63",
                            "roomPrice": "398"
                        }
                    ]
                }
            ],
            "toTime": "2015-10-23 23:59:59"
        }
    }
}
			</textarea>
			<input type="button" value="按钮" onclick="datagram()">
		</form>
		
		
	<hr>预定后发送短信
		<form action="<%=path%>/app/ordmgnt/booksendsms" method="post">
			<input id="json_data" name="json_data" width="60" value='{orderNo:"2015092617405868201"}'>
			<input type="submit" value="按钮" >
		</form>
		
	
		
		
	
		
	<hr>早餐查用户信息/订单信息/入住单信息(使用AJAX的形式)
		<form action="<%=path%>/app/bf/get_user" method="post">
			<input type="button" value="按钮" onclick="getUser()">
		</form>
		
		
	<hr>早餐查用户信息/订单信息/入住单信息
		<form action="<%=path%>/app/bf/get_user" method="post">
			<input name="json_data" value='{"lodgerId":"2","checkinId":"294"}'>
			<input type="submit" value="按钮">
		</form>
		
		
	
		
		
	<hr>重发开门密码
		<form action="<%=path%>/app/ordmgnt/opendoorsms" method="post">
			<input name="json_data" value='{mobile:"13042014179",password:"123456",checkinId:294}'>
			<input type="submit" value="按钮">
		</form>
		
		
	<hr>点击"退款"按钮(OMS用)
		<form action="<%=path%>/app/ordmgnt/finishrefund" method="post">
			<input name="json_data" value='{orderId:791}'>
			<input type="submit" value="按钮">
		</form>
		
	
		
	
	
	
	<hr>早餐发送短信
		<form action="<%=path%>/app/bf/sendsms" method="post">
			<input name="json_data" value='{mobile:"13042014179",content:"test"}'>
			<input type="submit" value="按钮">
		</form>
		
	
		
	
	
	<hr>OMS查询发票信息
		<form action="<%=path%>/app/ordmgnt/queryinvoice" method="post">
			<input name="json_data" value='{invoiceRecId:3}'>
			<input type="submit" value="按钮">
		</form>
		
	
		
		

		
	<hr>申请在住清洁
		<form action="<%=path%>/app/sv/cleanv2" method="post">
		<input name="json_data" width="60" value='{checkinId:441,mobile:"13042014179",password:"123456",period:1}'>
			<input type="submit" value="按钮">
		</form>
		
		
	<hr>test
		<form action="<%=path%>/test/test" method="post">
			<input type="submit" value="测试">
		</form>
		
	<hr>办理入住2
		<form action="<%=path%>/app/ordmgnt/checkin" method="post">
			<input id="checkin_data" name="json_data" width="60" value='{checkinId:52,checkiner:{main:{name:"王永锋",idcardNo:"44332232323232",mobile:"13042014179",doesSendPin:1},other:[{name:"王大锤",mobile:"13431091111",idcardNo:"3444433444",doesSendPin:1},{name:"王老娘",mobile:"13431091112",idcardNo:"34444334666",doesSendPin:2}]}}'>
			<input type="button" value="按钮" onclick="checkin()">
		</form>
		
	
	
		
		
	<hr>检查是否存在相同的用户
		<form action="<%=path%>/app/lodger/checkreg" method="post">
			<input id="json_data" name="json_data" width="60" value='{mobile:"13042014179"}'>
			<input type="submit" value="按钮" >
		</form>
		
		
	
		
	<hr>去哪儿查询我们的房间的接口
		<form action="<%=path%>/app/sync/qryroom" method="post">
			<input id="json_data" name="json_data" width="60" value=''>
			<input type="submit" value="按钮" >
		</form>
		
		
		
	<hr>验证随机码
		<form action="<%=path%>/app/valid/validrandom" method="post">
			<input id="json_data" name="json_data" width="60" value='{validId:3,validVal:"075691"}'>
			<input type="submit" value="按钮" >
		</form>
	
	
	<hr>发送随机短信
		<form action="<%=path%>/app/valid/sendrandom" method="post">
			<input id="json_data" name="json_data" width="60" value='{mobile:"13042014179"}'>
			<input type="submit" value="按钮" >
		</form>
		
		
		
		
	<hr>模拟去哪儿发报文
		<form action="<%=path%>/app/sync/rooms" method="post">
			<textarea rows="40" cols="80" name="json_data" id="qunar_data">
			{"chainId":1,
"data":{
    "status": 0,
    "errMsg": "调用成功",
    "ver": "1.0",
    "data": {
        "fromTime": "2015-10-23 00:00:00",
        "orderList": [{
            "orderNo": "20151023150001",
            "channelID": "qhh",
            "channelName": "去呼呼公寓直销订单",
            "channelOrderNo": "20151023150001",
            "checkInTime": "2015-10-26 14:00",
            "checkOutTime": "2015-10-27 12:00",
            "contactName": "PJ_test1",
            "contactMobile": "13926415757",
            "upDate": "2015-10-26 15:30:26",
            "orderStatusCode": "1",
            "orderStatusMsg": "新订单",
            "payTypeCode": "040",
            "payTypeMsg": "异常状态",
            "rentMoney": "398",
            "roomInfos": [{
                "checkInStatus": "10",
                "checkInStatusMsg": "未入住",
                "chkinTime": "2015-10-26 14:00",
                "chkoutTime": "2015-10-27 12:00",
                "customerName": "PJ_test1",
                "customerTelNo": "13926415757",
                "stayID": "20151023193001",
                "realChkinTime": null,
                "realChkoutTime": null,
                "roomId": "8",
                "roomPrice": "398"
            },{
                "checkInStatus": "10",
                "checkInStatusMsg": "未入住",
                "chkinTime": "2015-10-26 14:00",
                "chkoutTime": "2015-10-27 12:00",
                "customerName": "PJ_test1",
                "customerTelNo": "13926415757",
                "stayID": "20151023193002",
                "realChkinTime": null,
                "realChkoutTime": null,
                "roomId": "14",
                "roomPrice": "398"
            }]
        },{
            "orderNo": "20151023150002",
            "channelID": "qhh",
            "channelName": "去呼呼公寓直销订单",
            "channelOrderNo": "20151023150002",
            "checkInTime": "2015-10-26 14:00",
            "checkOutTime": "2015-10-27 12:00",
            "contactName": "PJ_test2",
            "contactMobile": "15917403623",
            "upDate": "2015-10-26 15:30:26",
            "orderStatusCode": "2",
            "orderStatusMsg": "已分房",
            "payTypeCode": "040",
            "payTypeMsg": "异常状态",
            "rentMoney": "398",
            "roomInfos": [{
                "checkInStatus": "10",
                "checkInStatusMsg": "未入住",
                "chkinTime": "2015-10-26 14:00",
                "chkoutTime": "2015-10-27 12:00",
                "customerName": "PJ_test2",
                "customerTelNo": "15917403623",
                "stayID": "20151023193003",
                "realChkinTime": null,
                "realChkoutTime": null,
                "roomId": "7",
                "roomPrice": "398"
            }, {
                "checkInStatus": "10",
                "checkInStatusMsg": "未入住",
                "chkinTime": "2015-10-26 14:00",
                "chkoutTime": "2015-10-27 12:00",
                "customerName": "PJ_test2",
                "customerTelNo": "15917403623",
                "stayID": "20151023193004",
                "realChkinTime": null,
                "realChkoutTime": null,
                "roomId": "10",
                "roomPrice": "398"
                }]
        }],
        "toTime": "2015-10-26 23:59:59"
    }
}
}
			
			</textarea>
			<input type="button" value="按钮" onclick="datagram()">
		</form>
		
	
		
		
	<hr>注册用户
		<form action="<%=path%>/app/lodger/register" method="post">
			<input id="reg_date" name="json_data" value='{name:"王大锤",mobile:"13445252225",password:"123456"}'>
			<input type="button" value="按钮" onclick="register()">
		</form>
	
	
		
		
	
	
		
		
	
	<hr>lodger改密
		<form action="<%=path%>/app/lodger/chgpwd" method="post">
			<input id="json_data" name="json_data" width="60" value='{"mobile":"13042014179","password":"654321","passwordNew":"123456"}'>
			<input type="submit" value="按钮" >
		</form>
		
		
	
	
	
	
	
	
	<hr>访问图片
		<form action="<%=path%>/images/1_351_20150827_112125.jpg/400" method="post">
			<input type="submit" value="访问图片" >
		</form>
		
		
	
	<hr>删除上传图片
		<form action="<%=path%>/app/roommgnt/delpic" method="post">
			<input id="json_data" name="json_data" width="60" value='{picId:2342}'>
			<input type="submit" value="删除图片" >
		</form>
		
		
	

	
		
	
		
	
	
		
	
	
	
	
	
	
	<hr>修改日历
		<form action="<%=path%>/app/roommgnt/editcal" method="post">
			<input name="json_data" width="60" value='{roomId:1,dataSet:[{date:"2015-09-21",newPrice:38800,statOper:2},{date:"2015-10-01",newPrice:38800,statOper:2},{date:"2015-10-02",newPrice:38800,statOper:2}]}'>
			<input type="submit" value="修改日历">
		</form>
		
	
		
		
		
<hr>删除房间
<form action="<%=path%>/app/roommgnt/del" method="post">
	<input name="json_data" width="60" value="{roomId:1}">
	<input type="submit" value="删除房间">
</form>
<hr>恢复房间
<form action="<%=path%>/app/roommgnt/resetroom" method="post">
	<input name="json_data" width="60" value="{roomId:8}">
	<input type="submit" value="按钮">
</form>



 


	</div>
</body>
<script type="text/javascript">

var _context = "<%=path%>";
function addRoom() {
	var json_data = $('#adfasdfasdf').val();
	var dataMap = 'json_data='+json_data;
	json_data = encodeURI(encodeURI(json_data));
	$.ajax({
		type : "POST",
		url : _context + "/app/roommgnt/operroom",
		data : dataMap,
		success : function(jsonResult) {
			//document.write(jsonResult);
		}
	});
}

function uploadPic() {
	var form = $('#uploadpicform').val();
	
	$.ajax({
		type : "POST",
		url : _context + "/app/roommgnt/operroom",
		data : dataMap,
		success : function(jsonResult) {
			//document.write(jsonResult);
		}
	});
}


function bookRoom() {
	var jsonData = $('#book_room_json_data').val();
	var dataMap = 'json_data='+jsonData;
	dataMap = encodeURI(encodeURI(dataMap));
	$.ajax({
		type : "POST",
		url : _context + "/app/ordmgnt/book",
		data : dataMap,
		success : function(jsonResult) {
			//document.write(jsonResult);
		}
	});
}

function register() {
	var jsonData = $('#reg_date').val();
	var dataMap = 'json_data='+jsonData;
	dataMap = encodeURI(encodeURI(dataMap));
	$.ajax({
		type : "POST",
		url : _context + "/app/lodger/register",
		data : dataMap,
		success : function(jsonResult) {
			//document.write(jsonResult);
		}
	});
}

function datagram() {
	var jsonData = $('#qunar_data').val();
	var dataMap = 'json_data='+jsonData;
	//dataMap = encodeURI(encodeURI(dataMap));
	$.ajax({
		type : "POST",
		url : _context + "/app/sync/rooms",
		data : dataMap,
		success : function(jsonResult) {
			//document.write(jsonResult);
		}
	});
}

function checkin() {
	var jsonData = $('#checkin_data').val();
	var dataMap = 'json_data='+jsonData;
	dataMap = encodeURI(encodeURI(dataMap));
	$.ajax({
		type : "POST",
		url : _context + "/app/ordmgnt/checkin",
		data : dataMap,
		success : function(jsonResult) { 
			//document.write(jsonResult);
		}
	});
}


function getUser() {
	var dataMap = 'json_data='+'{"lodgerId":"2","checkinId":"294"}';// 其实是字符串
	$.ajax({
		type : "POST",
		url : _context + "/app/bf/get_user",
		data : dataMap,
		success : function(jsonResult) {
			//document.write(jsonResult);
		}
	});
}

function upload() {
	var dataMap = 'json_data='+$("#hiddenImgStr").val();// 其实是字符串
	dataMap = encodeURI(encodeURI(dataMap));
	$.ajax({
		type : "POST",
		url : _context + "/app/lodger/upload",
		data : dataMap,
		success : function(jsonResult) {
			//document.write(jsonResult);
		}
	});
}


function uploadIdCard() {
	var dataMap = 'json_data={lodgerId:2,cardNo:"445221...",password:"123456",type:1,base64code:"'+$("#hiddenImgStr").val()+'"}';// 其实是字符串
	$.ajax({
		type : "POST",
		url : _context + "/app/lodger/upload",
		data : dataMap,
		success : function(jsonResult) {
			document.write(jsonResult);
		}
	});
}


function queryrooms() {
	var dataMap = 'json_data='+$("#ttt").val();// 其实是字符串
	dataMap = encodeURI(encodeURI(dataMap));
	$.ajax({
		type : "POST",
		url : _context + "/app/ordmgnt/queryrooms",
		data : dataMap,
		success : function(jsonResult) {
			//document.write(jsonResult);
		}
	});
}
</script>
</html>