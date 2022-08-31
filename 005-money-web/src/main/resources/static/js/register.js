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

//注册协议确认
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



	var tar_phone=0;
	var flag=false;
	$("#phone").blur(function(){
		var phone=$.trim($("#phone").val());
		//非空验证
		if(phone==null || phone===""){
            showError("phone","号码不能为空");
            return;
		}
		//长度验证
		if(phone.length!==11){
			showError("phone","号码长度不正确");
			return;
		}
		//格式验证
		if(!/^1[1-9]\d{9}$/.test(phone)){
			showError("phone","号码格式不正确");
			return;
		}
        $.get("/005-money-web/loan/page/checkPhone",{"phone":phone},function(data){
			if(data.code===1){
				tar_phone=1;
				showSuccess("phone");
                //alert(1);
                if((tar_phone===1 && tar_loginPassword===1 && tar_messageCode===1) && flag){
					var phone=$("#phone").val();
					var loginPassword=$("#loginPassword").val();
					var messageCode=$("#messageCode").val();
					$.post("/005-money-web/loan/page/registerSubmit",{"phone":phone,"loginPassword":$.md5(loginPassword),"messageCode":messageCode},function(data){
						if(data.code===0){
							alert(data.message);
							flag=false;
						}
						if(data.code===1){
							window.location.href="/005-money-web/loan/page/realName?ReturnUrl="+$("#ReturnUrl").val();
						}
					});
				}
			}
			if(data.code===0){
				tar_phone=0;
				showError("phone",data.message);
                //alert(0);
				return;
			}
		});
        showSuccess("phone");
	});

	var tar_loginPassword=0;
	$("#loginPassword").blur(function(){
		var loginPassword=$("#loginPassword").val();
		if(loginPassword==null || loginPassword===""){
			showError("loginPassword","密码不能为空");
			return;
		}
		if(loginPassword.length<6 || loginPassword.length>16){
			showError("loginPassword","密码长度只能是[6-16]位");
			return;
		}
		if(!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(loginPassword)){
			showError("loginPassword","密码应同时包含英文和数字");
			return;
		}
		if(!/^[0-9a-zA-Z]+$/.test(loginPassword)){
			showError("loginPassword", "密码字符只可使用数字和大小写英文字母");
			return;
		}
		showSuccess("loginPassword");
        tar_loginPassword=1;
	});

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

	$("#btnRegist").click(function(){
        var ret_phone=$("#phone").blur();
        var ret_loginPassword=$("#loginPassword").blur();
        var ret_messageCode=$("#messageCode").blur();
        flag=true;
		//alert("tar_phone:"+tar_phone);
        //alert("tar_loginPassword:"+tar_loginPassword);
        /*if(tar_phone===1 && tar_loginPassword===1){
            var phone=$("#phone").val();
            var loginPassword=$("#loginPassword").val();
            $.post("/005-money-web/loan/page/registerSubmit",{"phone":phone,"loginPassword":$.md5(loginPassword)},function(data){
                if(data.code===0){
                    alert(data.message);
                }
                if(data.code===1){
                    window.location.href="/005-money-web/index";
                }
            });
        }*/
	});

	$("#messageCodeBtn").click(function(){
        $("#phone").blur();
        $("#loginPassword").blur();
        if(tar_phone===1 && tar_loginPassword===1){
            var phone=$.trim($("#phone").val());
            var _this=$(this);
            if(!$(this).hasClass("on")) {
                $.get("/005-money-web/loan/page/messageCode", {"phone": phone}, function (data) {
                    alert("code:" + data.message);
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
});
