<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="UTF-8"%>
<html>
<body>
<h2>Hello World!</h2>
        <input id="input_char_cd" />
        <input id="input_team_cd" />
        <button id="button" onclick="javascript:ajaxRequest();">입력</button>
        <div id="tableDiv"></div>

</body>		
	<script type="text/javascript" charset="UTF-8">
		function ajaxRequest(){
			var inputs = {CHAR_CD:document.querySelector("#input_char_cd").value,
						TEAM_CD:document.querySelector("#input_team_cd").value,};
			var reqJson = new Object();
			reqJson.inputs = inputs;
			/* 통신에 사용 될 XMLHttpRequest 객체 정의 */
			httpRequest = new XMLHttpRequest();
			httpRequest.onreadystatechange = () => {
		    	/* readyState가 Done이고 응답 값이 200일 때, 받아온 response로 처리*/
			    if (httpRequest.readyState === XMLHttpRequest.DONE) {
				    if (httpRequest.status === 200) {
				    	var result = httpRequest.response;
				    	afterwork(result);
				    } else {
				        alert('request에 뭔가 문제가 있어요.');
				    }
				}
		    };
		    
		    /* Post 방식으로 요청 */
		    httpRequest.open('POST', '/abd/getCharacterName', true);//경로 잡아줌
		    /* Response Type을 Json으로 사전 정의 */
		    httpRequest.responseType = "json";
		    /* 요청 Header에 컨텐츠 타입은 Json으로 사전 정의 */
		    httpRequest.setRequestHeader('Content-Type', 'application/json');
		    /* 정의된 서버에 Json 형식의 요청 Data를 포함하여 요청을 전송 */
		    httpRequest.send(JSON.stringify(reqJson));
		}
		
		function afterwork(result){
			var table = document.createElement('table');
			for(var idx in result){
				var  tr = document.createElement('tr');
				table.append(tr);
				var td = document.createElement('td');
				td.innerText = result[idx].CHAR_CD;
				tr.append(td);
				var td = document.createElement('td');
				td.innerText = result[idx].CHAR_NM;
				tr.append(td);
			}
			var background = document.querySelector("#tableDiv")
			if(background.childElementCount > 0){
				background.removeChild(background.firstChild);
	    	}
			background.append(table);
		}
	</script>
</html>
