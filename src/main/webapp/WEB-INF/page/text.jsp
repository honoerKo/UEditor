<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML>
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>wangEditor富文本编辑器</title>
    
	<meta charset="UTF-8">
	<meta http-equiv="Content-Type" content="multipart/form-data; charset=utf-8" />
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

	<style type="text/css">
        .topEdit {
            border: 1px solid #ccc;
        }
        .downEdit {
            border: 1px solid #ccc;
            height: 400px;
        }
        .btn{
        	margin: 0 5px;
        	height: 26px;
        	border: 1px solid #ccc;
        	background: transparent;
        }
        .float{
        	float: right;
        }
        .ob{
        	margin-left: 0;
        }
        .em{
        	margin-right: 0;
        }
        .waitLog{
        	background: white;
        	opacity: 0.7; 
		    position: fixed;   
		    top: 0px;  
		    left: 0px;
		    display: none;   
		    height: 100%;  
		    width: 100%; 
		    z-index:15000;
        }
        .waitLog img{
        	position:absolute;
        	top:50%;
        	left:50%;
        	width:33px;
        	height:33px;
        	margin-top:-15px;
        	margin-left:-15px;
        }
    </style>
  </head>
  
  <body>
  <script src="http://cdn.bootcss.com/wangeditor/2.1.20/js/lib/jquery-2.2.1.js"></script>
   	
   	<!-- title -->
   	<div style="height: 26px;width: 100%;">
   		<div style="box-sizing: border-box;width: 4%;float: left;">
   			<label for="title">标题：</label>
   		</div>
   		<div style="box-sizing: border-box;width: 96%;float: left;border: 1px solid #ccc;">
   			<input type="text" id="title" class="Title" style="border: none;width: 99%;" autofocus="autofocus" placeholder="请输入文本标题">
   		</div>
   	</div>
   	<div style="padding: 5px 0; color: #ccc"></div>
  	<!-- wangEditor富文本编辑框 -->
    <div id="menu" class="topEdit"></div> <!-- 菜单栏 -->
    <div style="padding: 5px 0; color: #ccc"></div>
    <div id="area" class="downEdit"></div>
    <!--  -->
	<div style="padding: 10px 0;">
		<button id="obtain" class="btn ob">获取html</button>
		
		<button id="emptied" class="btn float em">清空</button>
		<button id="release" class="btn float">发布</button>
	</div>	
	<div>
		<button id="query" class="btn ob">查询</button>
	</div>
	<!-- 显示栏 -->
	<div id="show" style="width: 100%;"></div> 	
	<!-- 等待提示框 -->
	<div id="WaitLog" class="waitLog">   
		<img src="./img/loading.gif" />  
	</div>
	
    <script type="text/javascript" src="./plugin/wangEditor.min.js"></script>   
    <script type="text/javascript">
    $(function(){
        var E = window.wangEditor
        var editor = new E('#menu','#area')      
        // 或者 var editor = new E( document.getElementById('editor') )
		editor.customConfig.uploadImgShowBase64 = true //使用base64保存图片
		//editor.customConfig.uploadImgMaxLength = 5 //一次最多选择五张图片,注：需添加错误提示，或者一次选择五张后不能再选择
		editor.customConfig.uploadImgMaxSize = 2*1024*1024 //图片大小限制2M,注：需添加错误提示
        editor.customConfig.debug = true //打开debug模式
        editor.create()
		
        $('#obtain').click(function () {
	        // 读取 html,包括文本和标签
	        alert(editor.txt.html())
	        /* var title = $('#title').val()
	        alert(title) */
    	})
		
		$('#emptied').click(function() {
			// 一键清空
			editor.txt.clear()
		})
		
		$('#query').click(function(){
			$('#WaitLog').show()
			//查询出最后发布的那条数据
			$.ajax({
				url : "file/query",
				type : "POST",
				dataType : "JSON",
				success : function(data){
					console.log(data)
					$('#WaitLog').hide()
					if(data.value == "1"){						
						var div = document.getElementById('show')
						div.innerHTML = "<h3>" + data.title + "</h3>" + data.msg
					}else{
						alert(data.msg)
					}
				},
				error : function(){
					$('#WaitLog').hide()
					alert("数据错误！")
				}
			})
		})
		
		$('#release').click(function(){	
			$('#WaitLog').show()
			//获取编辑的内容传回controller层
			var title = $('#title').val()
			var detail = editor.txt.html()
			$.ajax({
				url : "file/save",
				data : {"detail" : detail,"title" : title},
				type : "POST",
				dataType : "JSON",
				success : function(data){
					$('#WaitLog').hide()
					alert(data.msg)
				},
				error : function(){
					$('#WaitLog').hide()
					alert("系统错误，请刷新重试！")
				}
			})
		})
	})
    </script>
  </body>
</html>
