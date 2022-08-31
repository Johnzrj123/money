
//同意实名认证协议
$(function() {
	$("#agree").click(function(){
		var ischeck = document.getElementById("agree").checked;
		if (ischeck) {
			$("#btnRegist").attr("disabled", false);
			$("#btnRegist").removeClass("fail");
		} else {
			$("#btnRegist").attr("disabled","disabled");
			$("#btnRegist").addClass("fail");
		}
	});

	//手机号
	var tar_phone=0;
	$("#phone").blur(function(){
		var phone=$.trim($("#phone").val());
		//非空
		if(phone==null || phone===""){
			showError("phone","手机号不能为空");
			return;
		}
		//长度
		if(phone.length!==11){
			showError("phone","手机号长度不正确");
			return;
		}
		//格式
		if(!/^1[1-9]\d{9}$/.test(phone)){
			showError("phone","手机号格式不正确");
			return;
		}
		showSuccess("phone");
		tar_phone=1;
	});

	//姓名
	var tar_realName=0;
    $("#realName").blur(function(){
    	var realName=$("#realName").val();
		//非空
		if(realName==null || realName===""){
			showError("realName","姓名不能为空");
			return;
		}
		//格式
		if(!/^[\u4e00-\u9fa5]{0,}$/.test(realName)){
			showError("realName","姓名只能为中文");
			return;
		}
		showSuccess("realName");
		tar_realName=1;
    });

	//身份证
	var tar_idCard=0;
    $("#idCard").blur(function(){
    	var idCard=$.trim($("#idCard").val());
    	//非空
		if(idCard==null || idCard===""){
			showError("idCard","身份证号不能为空")
			return;
		}
		//长度
		if(!(idCard.length===15 || idCard.length===18)){
			showError("idCard","身份证号只能为15或18位");
			return;
		}
		//格式
		if(!/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(idCard)){
			showError("idCard","身份证格式不正确");
			return;
		}
		showSuccess("idCard");
		tar_idCard=1;
    });

	//短信验证码
	var tar_messageCode=0;
    $("#messageCode").blur(function(){
        var messageCode=$("#messageCode").val();
        //非空
        if(messageCode==null || messageCode===""){
            showError("messageCode","验证码不能为空");
            return;
        }
        //长度
        if(messageCode.length!==4){
            showError("messageCode","验证码长度不正确");
            return;
        }
        //格式
        if(!/[0-9]{4}/.test(messageCode)){
            showError("messageCode","验证码格式不正确");
            return;
        }
        hideError("messageCode");
        tar_messageCode=1;
    });

	//获取短信验证码
    $("#messageCodeBtn").click(function(){
        $("#phone").blur();
        $("#realName").blur();
        $("#idCard").blur();
        if(tar_phone===1 && tar_realName===1 && tar_idCard===1){
            var phone=$.trim($("#phone").val());
            var _this=$(this);
            if(!$(this).hasClass("on")) {
                $.get("/005-money-web/loan/page/messageCode", {"phone": phone}, function (data) {
                    alert("code:" + data.message);
                    if(data.code===0){
                        return;
                    }
                    if (data.code === 1) {
                        $.leftTime(60, function (d) {
                            if (d.status) {
                                _this.addClass("on");
                                _this.html((d.s == "00" ? "60" : d.s) + "秒后重新获取");
                            } else {
                                _this.removeClass("on");
                                _this.html("获取验证码");
                            }
                        });
                    }
                });
            }
        }
    });
	//提交数据
	$("#btnRegist").click(function(){
        var phone=$.trim($("#phone").val());
        var realName=$("#realName").val();
        var idCard=$.trim($("#idCard").val());
        var messageCode=$("#messageCode").val();
        var message={"phone":phone,"realName":realName,"idCard":idCard,"messageCode":messageCode};
        if(tar_phone===1 && tar_realName===1 && tar_idCard===1 && tar_messageCode===1) {
            $.post("/005-money-web/loan/page/realNameSubmit", message,function(data){
            	if(data.code===0){
            		alert(data.message);
            		return;
				}
				if(data.code===1){
					window.location.href=$("#ReturnUrl").val();
					//window.location.href="/005-money-web/index";
				}
			})
        }

	});
});
//打开注册协议弹层
function alertBox(maskid,bosid){
	$("#"+maskid).show();
	$("#"+bosid).show();
}
//关闭注册协议弹层
function closeBox(maskid,bosid){
	$("#"+maskid).hide();
	$("#"+bosid).hide();
}

//错误提示
function showError(id,msg) {
	$("#"+id+"Ok").hide();
	$("#"+id+"Err").html("<i></i><p>"+msg+"</p>");
	$("#"+id+"Err").show();
	$("#"+id).addClass("input-red");
}
//错误隐藏
function hideError(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id).removeClass("input-red");
}
//显示成功
function showSuccess(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").show();
	$("#"+id).removeClass("input-red");
}