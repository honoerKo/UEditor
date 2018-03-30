<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" contentType="text/html; charset=utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML>
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>UEditor编辑器</title>
    
    <meta charset="UTF-8">
	<meta http-equiv="Content-Type" content="multipart/form-data; charset=utf-8" />
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
   	<script type="text/javascript" charset="utf-8" src="./utf8-jsp/ueditor.config.js"></script>
    <script type="text/javascript" charset="utf-8" src="./utf8-jsp/ueditor.all.js"></script>
    <script type="text/javascript" charset="utf-8" src="./utf8-jsp/ueditor.parse.js"></script>
    <script type="text/javascript" charset="utf-8" src="./utf8-jsp/lang/zh-cn/zh-cn.js"></script>
    <script type="text/javascript" charset="utf-8" src="./plugin/FileSaver.js"></script>
    <script type="text/javascript" charset="utf-8" src="./plugin/jQuery-2.1.4.min.js"></script>
    <script type="text/javascript" charset="utf-8" src="./plugin/jquery.wordexport.js"></script>
	
    
   	<style type="text/css">
        div{
            width:100%;
        }
        .waitLog{
		    position: fixed;   
        	background: white;
        	opacity: 0.7; 
		    top: 0px;  
		    left: 0px;
		    display: none;   
		    height: 100%;  
		    width: 100%; 
		    z-index:15000;
        }
        .waitimg{
        	position:absolute;
        	top:50%;
        	left:50%;
        	width:33px;
        	height:33px;
        	margin-top:-15px;
        	margin-left:-15px;
        }
        .showdata{
        	position:absolute;
        	display: none;
        	top: 0%;
        	left: 0%;
        	width: 100%;
        	height: 100%;
        	background: white;
      		z-index: 10000;
        }
    </style>
</head>
<body>
	<!-- title标题框 start -->
	<div style="height: 26px;width: 100%;">
   		<div style="box-sizing: border-box;width: 4%;height: 100%;float: left;">
   			<label for="title">标题：</label>
   		</div>
   		<div style="box-sizing: border-box;width: 96%;height: 100%;float: left;border: 1px solid #ccc;">
   			<input type="text" id="title" class="Title" style="border: none;outline: none;width: 99.8%;height: 91%;" autofocus="autofocus" placeholder="请输入文本标题" required="required">
   		</div>
   	</div>
   	<!-- end -->
	<div style="padding-top: 5px; color: #ccc"></div>
	<!-- UEditor编辑框 start -->
	<div id="ueditor">
	    <script id="editor" type="text/plain" style="width:100%;height:650px;"></script>
	</div>
	<!-- end -->
	<div style="padding-top: 5px; color: #ccc"></div>
	<div id="btns">
	    <div>
	        <button onclick="getContent()">获取html内容</button>
	        <button onclick="clearData()" style="float: right;">清空编辑区</button>
	        <button onclick="release()" style="float: right;margin-right: 5px;">发布</button>
	        <button onclick="query()">查询</button>     
	    </div>
	</div>
	<!-- 数据显示栏 -->
	<div id="showdiv" class="showdata">
		<div id="show" style="width: 100%;height: 95%;overflow: auto;border-bottom: 1px solid black;"></div>
		<div style="padding-top: 10px; color: #ccc"></div>
		<div style="width: 100%;height: 25px;">
			<button onclick="back()" style="height: 100%;">关闭</button>
			<button onclick="toWord()" style="height: 100%;">生成word文档</button>
		</div>
	</div>
	<!-- 等待框 -->
	<div id="WaitLog" class="waitLog">   
		<img class="waitimg" src="./img/loading.gif" />  
	</div>
	
	<!-- 测试Word -->
	<!-- <div id="word">
		<h4>测试word</h4>
		<p>测试内容</p>
	</div> -->
    
    <script type="text/javascript">
    	var ue = UE.getEditor('editor',{
    		autosave: false,
    		scaleEnabled: true,
    		allowDivTransToP: false,
    		enterTag: "br"
    	})	
		
		var title
		
		function getContent(){
			alert(ue.getContent())
		}
		function clearData(){
	        ue.setContent('')
	    }
	    function release(){
	    	var title = $('#title').val()
			var detail = ue.getContent()
			if(title == null || title == ""){
				alert("请输入标题！")
			}else{
				$('#WaitLog').show()
				$.ajax({
					url : "ueditor/save",
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
			}
			
			
	    }
	    function query(){
	    	$('#WaitLog').show()
	    	$.ajax({
				url : "ueditor/query",
				type : "POST",
				dataType : "JSON",
				success : function(data){
					//console.log(data)
					$('#WaitLog').hide()
					if(data.value == "1"){
						$('#showdiv').show()
						title = data.title					
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
	    }
	    function toWord(){
	    	$('#show').wordExport(title)	    	
	    }
	    function back(){
	    	$('#showdiv').hide()
	    }
    </script>
  </body>
</html>
