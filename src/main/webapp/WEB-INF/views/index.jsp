<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>어바등 텍스트 RPG</title>
	<style>
	    body {
	        background-image: url(resources/imgs/abd.jpg);
	        background-color: #00002a;
	        background-repeat : no-repeat;
	        background-size: cover;
	        
	    }
	    #start-screen, #screen {
	        height: 80vh;
	        width: 600px;
	        color: ivory;
	        font-size: 15px;
	        text-align: left;
	        padding: 30px;
	    }
	    #start-screen div { margin-top: 30px; width: 400px; }
	    #story, #message { margin-bottom: 30px; width: 400px; }
	    #screen { display: none; position:relative; }
	    #progress { 
    		display: none;
    		position:absolute;
    		background: no-repeat;
    		background-position:center;
    		background-image: url(resources/imgs/progress.gif);
    		filter: invert(100%) opacity(0.5);
	    }
	    input { margin: 20px 0;}
	    button {
	        background: transparent;
	        border: 2px solid ivory;
	        color: ivory;
	        padding: 3px 10px;
	        border-radius: 5px;
	    }
	    #monster-stat img {
	        width: 100px;
	        transition: 1s;
	    }
	    #monster-stat img:hover {
	        transform: scale(1.5);
	    }
	    #name-input {
	        background: transparent;
	        border: none;
	        height: 25px;
	        text-align: center;
	        color: ivory;
	        font-size: 15px;
	        font-weight: bold;
	    }
	</style>
</head>
<body>
	<form id="start-screen">
	    <h3>어두운 바다의 등불이 되어 - 연산호</h3>
	    <h5>팬게임 텍스트 RPG</h5>            
	    <div>당신은 한국, 미국, 캐나다, 호주, 뉴질랜드, 러시아, 일본, 중국 총 8개국이 건설한
	         해저 3000m의 북태평양해저기지(NPIUS)에 입사하였다. <br/><br/>
	         제주 해군기지에 해저기지 합격 서류를 낸 후 사흘간 배와 헬기를 통해 이동한
	         당신은 대한도에서 프리야 쿠마리를 만났다. 당신은 NEP에 가입 서명 해야한다.
	        <br/><br/>서명하시겠습니까?</div>
	    <input id="name-input" placeholder="성명"/>
	    <button id="start">서명</button>
	    <h5>* 대사 스포일러가 있습니다.</h5>
	</form>
	<div id="screen">
		<div id="progress"></div>
	    <div id="story">
	        당신은 서명을 마치고 제4해저기지로 발걸음을 향했다.
	    </div>
	    	    <div id="monster-stat">
	        <div id="monster-img" style="margin:5px;">
	            <img src="resources/imgs/sin.jpg">
	        </div>
	        <span id="monster-line"></span>
	        <br/>
	        <span id="monster-name"></span>
	        <span id="monster-hp"></span>
	        <span id="monster-att"></span>
	    </div>
	    <div id="message">
	    	<p></p>
	    </div>
	    <form id="game-menu" style="display: none;">
<!-- 	        <div id="menu-1">1.모험</div>
	        <div id="menu-2">2.카페에 쉬러가기(체력 회복)</div> -->
<!-- 	        <div id="menu-9">9.다음</div>
	        <div id="menu-3">3.종료</div> -->
	        <!-- <input id="menu-input" /> -->
	        <button id="menu-button">다음</button>
	    </form>
	    <form id="battle-menu" style="display: none;">
	        <!-- <div id="battle-1">1.대결</div> -->
	        <!-- <div id="battle-2">2.금이씨와 얘기하기(체력 +20)</div> -->
	        <!-- <div id="battle-3">2.도망</div> -->
	        <!-- <div id="battle-9">9.선택</div>
	        <input id="battle-input" /> -->
	        <button id="battle-button">선택</button>
	    </form>
	    	    <div id="hero-stat">
	        <span id="hero-name"></span>
	        <span id="hero-level"></span>
	        <span id="hero-hp"></span>
	        <span id="hero-xp"></span>
	        <span id="hero-att"></span>
	    </div>
	</div>
	<script type="text/javascript" charset="UTF-8">
		const $startScreen = document.querySelector('#start-screen');
		const $screen = document.querySelector('#screen');
		const $gameMenu = document.querySelector('#game-menu');
		const $battleMenu = document.querySelector('#battle-menu');
		const $heroName = document.querySelector('#hero-name');
		const $heroLevel = document.querySelector('#hero-level');
		const $heroHp = document.querySelector('#hero-hp');
		const $heroXp = document.querySelector('#hero-xp');
		const $heroAtt = document.querySelector('#hero-att');
		const $monsterImg = document.querySelector('#monster-img');
		const $monsterName = document.querySelector('#monster-name');
		const $monsterHp = document.querySelector('#monster-hp');
		const $monsterAtt = document.querySelector('#monster-att');
		const $monsterLine = document.querySelector('#monster-line');
		const $message = document.querySelector('#message');	
		const $messageP = document.querySelector('#message p');	
		
		var process = false;
		
		function progressOn(){
			process = true;
			setTimeout(function(){
				if(process){
					var p = document.querySelector("#progress");
					p.style.display = 'block';
					p.style.width = "370px";
					p.style.height = "300px";
				}
			},500)
		}
		
		function progressOff(){
			var p = document.querySelector("#progress");
			p.style.display = 'none';
			process = false;
		}
		
        function ajaxRequest(inputs,url){
        	progressOn()        	
        	
        	var reqJson = inputs;
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
    			      progressOff();
    			}
    	    };
    	    
    	    if(!url){
    	    	url = '/abd/gamePlay';
    	    }
    	    /* Post 방식으로 요청 */
    	    httpRequest.open('POST', url, true);//경로 잡아줌
    	    /* Response Type을 Json으로 사전 정의 */
    	    httpRequest.responseType = "json";
    	    /* 요청 Header에 컨텐츠 타입은 Json으로 사전 정의 */
    	    httpRequest.setRequestHeader('Content-Type', 'application/json');
    	    /* 정의된 서버에 Json 형식의 요청 Data를 포함하여 요청을 전송 */
    	    httpRequest.send(JSON.stringify(reqJson));
        }
        
        function afterwork(result){
        	if(result.status == 'setup'){
	        	game.createHero(result.player);
	        	
	        	ajaxRequest({status:"start"});
        	}else if(result.status == "start" || result.status == "onGoing"){
        		console.log(result);
        		if(result.sceneStatus == 'end'){
        			game.showMessage(' ');
        		}
        		
	        	if(result.event == "script"){
	                game.changeScreen('game');
	                game.clearMonster();
	                game.updateMonsterStat();
	        		
	        		game.showMessage(' ');
	        		if(result.scripts.length>0){
	        			game.showMessage(result.scripts.join("<br>"));
	        		}
			        	
	        	}else if(result.event == "justHappened"){
	                game.changeScreen('game');
	                game.clearMonster();
	                game.updateMonsterStat();
	        		
	        		game.showMessage(' ');
	        		if(result.scripts.length>0){
	        			game.showMessage(result.scripts.join("<br>"));
	        		}
			        	
	        	}else if(result.event == "play"){
	        		game.showMessage(' ');
	        		if(result.scripts.length>0){
	        			game.showMessage(result.scripts.join("<br>"));
	        		}
	        		
		        	if(result.play == "select"){
		        		var select = result.select;
		        		
		                game.changeScreen('game');
		                game.clearMonster();
		                game.updateMonsterStat();
				        
		        		var divTag = document.querySelector("#message");
				        var head = document.createElement("div");
				        head.id = "selectHead";
						head.innerText = result.select.selectHead
						divTag.append(head);
				        
		        		var selectTag = document.createElement("select");
		        		selectTag.id = "playSelect";
		        		selectTag["selectCode"] = result.selectCode;
		        		
		        		for(var idx in result.select.selectOptions){
		        			var option = document.createElement("option");
		        			var o = result.select.selectOptions[idx];
		        			option.value = o.value;
		        			option.text = o.name;
		        			selectTag.add(option);
		        		}
		        		divTag.append(selectTag);
				        	
		        	}else if(result.play == "input"){
		        		var input = result.input;
		        		if(input.status == "beforeInput"){
		        			game.changeScreen('game');
			                game.clearMonster();
			                game.updateMonsterStat();
					        
					        var divTag = document.querySelector("#message");
					        var inputTag = document.createElement("input");
					        inputTag.id = "playInput";
					        inputTag.placeholder = input.inputPlaceholer;
					        divTag.append(inputTag);
		        		}else if(input.status == "afterInput"){
		        			game.changeScreen('game');
			                game.clearMonster();
			                game.updateMonsterStat();
			        		
					        game.showMessage(input.resultTxt)
		        		}
		        	}else if(result.play == "battle"){
		        		if(result.battle.process == 'start'){
		        			gameView.setState(battleStart);
			        		gameView.setView(result);
			        		
		        		}else if(result.battle.process == 'onGoing'){
		        			gameView.setState(battleOnGoing);
		        			gameView.setView(result);
		        			
		        		}else if(!result.battle.process){
		        			//전투 이탈. 전투가 아니므로 전투 프로세스가 없음
		        			gameView.setState(battleQuit);
		        			gameView.setView(result);
		        		}
		        	}
	        	}//event 영역
        	}//status 영역
        }
        
    	let gameState = {
   			setView:function(){}
   		};
    		
   		let battleStart = {};
   		Object.setPrototypeOf(battleStart,Object.create(gameState));
   		battleStart.setView = function(result){
   			game.clearInputs();
   			game.changeScreen('battle');
   			
   			game.setHeroStatus(result.battle.player);
   			game.updateHeroStat();
   			game.createMonster(result.battle.npc);
   			game.updateMonsterStat();
   			
   			if(result.scripts.length>0){
   				game.showMessage(result.scripts.join("<br>"));
   			}
   			
   			createBattleSelect(result.battle.selectName, result.battle);		
   		}
   		
   		let battleEnd = {};
   		Object.setPrototypeOf(battleEnd,Object.create(gameState));
   		battleEnd.setView = function(result){
   			alert("전투 종료. 다음 이벤트로 진행 필요");
   		}
   		
   		let battleQuit = {};
   		Object.setPrototypeOf(battleQuit,Object.create(gameState));
   		battleQuit.setView = function(result){
   			var newResult = result.battle;
			newResult["status"] = "onGoing"
			newResult["scripts"] = [];
			afterwork(newResult);
   		}
   		
   		let battleOnGoing = {};
   		Object.setPrototypeOf(battleOnGoing,Object.create(gameState));
   		battleOnGoing.setView = function(result){
   			let battle = result.battle;
   			if(battle.selectName == "commonBattle"){
				
   				gameView.setState(battleCommon);
   				gameView.setView(result);
	        	
			}else if(battle.selectName == "battleHelp"){
				
				gameView.setState(battleHelp);
   				gameView.setView(result);
				
			}else if(battle.selectName == "pullBack"){
				
				gameView.setState(pullBack);
   				gameView.setView(result);
				
			}else if(battle.selectName == "pullBackHelp"){
				
				gameView.setState(pullBackHelp);
   				gameView.setView(result);
				
			}else if(battle.selectName == "useItem"){
				
				gameView.setState(useItem);
   				gameView.setView(result);
				
			}
   		}

   		let battleCommon = {};
   		Object.setPrototypeOf(battleCommon,Object.create(gameState));
   		battleCommon.setView = function(result){
   			if(result.battle.battleResult == 'win'){
				gameView.setState(winBattle);
				gameView.setView(result);
			}else if(result.battle.battleResult == 'defeat'){
				gameView.setState(defeatBattle);
				gameView.setView(result);
			}else{
				gameView.setState(justBattle);
				gameView.setView(result);
			}
        	createBattleSelect(result.battle.selectNext, result.battle);
   		}
   		
   		let winBattle = {};
   		Object.setPrototypeOf(winBattle,Object.create(gameState));
   		winBattle.setView = function(result){
   			var battle = result.battle;
   			//승리 메세지
			game.setMonsterStatus(battle.depeatedNpc);
			//var message = game.monster.name+"을/를 이겨 "+game.monster.xp+" 경험치를 얻었다. ";
			//if(game.hero.lev < battle.player.lev){
			//	message = `레벨업! 레벨 `+ battle.player.lev+` `;
			//}
			var message = result.scripts.join('<br>');
			
			game.clearMonster();
			game.setHeroStatus(battle.player);
        	game.updateHeroStat();
        	
			//적 생성 메세지
			game.createMonster(battle.npc);
			message = message+`무한교 신도와 마주쳤다. `+game.monster.name+`인 것 같다!`;
			game.showMessage(message);
			
			game.setMonsterStatus(battle.npc);
        	game.updateMonsterStat();
   		}   		
   		
   		let defeatBattle = {};
   		Object.setPrototypeOf(defeatBattle,Object.create(gameState));
   		defeatBattle.setView = function(result){
   			var battle = result.battle;
   			//사망
    		game.setHeroStatus(battle.player);
    		game.updateHeroStat();
    		
    		game.clearMonster();
    		game.updateMonsterStat();
    		
    		game.clearInputs();
            game.changeScreen('game');
    		alert(battle.player.lev+` 레벨에서 패배. 새 주인공을 생성하세요.`);
            game.quit();
   		}   		
   		
   		let justBattle = {};
   		Object.setPrototypeOf(justBattle,Object.create(gameState));
   		justBattle.setView = function(result){
   			var battle = result.battle;
   			
   			game.changeScreen('battle');
    		//캐릭터 상태 적용
    		game.setHeroStatus(battle.player);
        	game.updateHeroStat();
    		game.createMonster(battle.npc);
    		
       		game.showMessage(result.scripts.join("<br>"));
     		
        	game.setMonsterStatus(battle.npc);
        	game.updateMonsterStat();
        	
   		}   		
   		
   		let battleHelp = {};
   		Object.setPrototypeOf(battleHelp,Object.create(gameState));
   		battleHelp.setView = function(result){
   			var battle = result.battle;
   			
   			if(battle.selectName == battle.selectNext){
				game.showMessage(' ');
				
			}else{
				//동료와 함께 싸움
				if(battle.selectOption == 'battleWithCompany'){
					gameView.setState(battleWithCompany);
					gameView.setView(result);
				}
				//취소
				else if(battle.selectOption == 'cancelSelect'){
					gameView.setState(battleHelpCancel);
					gameView.setView(result);
					
				}
			}
			createBattleSelect(battle.selectNext, battle);
   		}   		
   		
   		
   		let battleWithCompany = {};
   		Object.setPrototypeOf(battleWithCompany,Object.create(gameState));
   		battleWithCompany.setView = function(result){
   			var battle = result.battle;
   			game.createMonster(battle.npc);
			var company = battle.company;
    		//oo이/가 적에게 xx의 데미지를 주고, yy의 데미지를 받았다
       		game.showMessage(result.scripts.join("<br>"));
    		//if(company.active == "도움 가능"){
    		//}else if(company.active == "도움 불가능"){
    		//	var message = company.name+"이(가) "+company.line;
    		//	game.showMessage(message);
    		//}
    		game.setMonsterStatus(battle.npc);
        	game.updateMonsterStat();
        	game.setHeroStatus(battle.player);
        	game.updateHeroStat();
   		}   		
   		
   		let battleHelpCancel = {};
   		Object.setPrototypeOf(battleHelpCancel,Object.create(gameState));
   		battleHelpCancel.setView = function(result){
   			game.createMonster(result.battle.npc);
			game.setHeroStatus(result.battle.player);
        	game.updateHeroStat();
			game.setMonsterStatus(result.battle.npc);
        	game.updateMonsterStat();
   		}
   		
   		let pullBack = {};
   		Object.setPrototypeOf(pullBack,Object.create(gameState));
   		pullBack.setView = function(result){
			
   			game.clearMonster();
			game.updateMonsterStat();
			if(result.battle.selectResult){
				game.showMessage(result.battle.selectResult);
			}else{
				game.showMessage(' ');
			}
			createBattleSelect(result.battle.selectNext, result.battle);
   		}
   		
   		let pullBackHelp = {};
   		Object.setPrototypeOf(pullBackHelp,Object.create(gameState));
   		pullBackHelp.setView = function(result){
   			var battle = result.battle;
   			if(battle.selectName == battle.selectNext){
    			game.showMessage(' ');
			}else{
				if(battle.selectOption == 'getHelpFromCompany'){
					
					game.showMessage(battle.company.line);
					game.setHeroStatus(battle.player);
					game.updateHeroStat();
					
				}else if(battle.selectOption == 'cancelSelect'){
					game.showMessage(' ');
				}
			}
			createBattleSelect(battle.selectNext, battle);
   		}
   		
   		let useItem = {};
   		Object.setPrototypeOf(useItem,Object.create(gameState));
   		useItem.setView = function(result){
   			var battle = result.battle;
   			if(battle.selectOption == 'usedItem'){
				
				game.showMessage(battle.selectResult);
				
				game.setHeroStatus(battle.player);
				game.updateHeroStat();
				
			}else if(battle.selectOption == 'cancelSelect'){
				game.showMessage(' ');
			}
			createBattleSelect(battle.selectNext, battle);
   		}
   		
        let gameView = {
			setState:function(state){
				this.state = state;
			},
			state:null,
			setView:function(result){
				this.state.setView(result);
			}
			
        };
        
        function createBattleSelect(id, battle){
        	id = id+"Select";
        	
        	var div = document.querySelector("#message");
			var head = document.createElement("div");
			head.id = id+"Head";
			head.innerText = battle.select.selectHead;
			div.append(head);
			
    		var select = document.createElement("select");
    		select.id = id;
    		select["selectCode"] = battle.selectCode;
    		
    		for(var idx in battle.select.selectOptions){
    			var option = document.createElement("option")
    			var o = battle.select.selectOptions[idx]
    			option.value = o.value;
    			option.text = o.name;
    			select.add(option);
    		}
    		div.append(select);
        }
		
        
        
        class Game {
        	constructor(name) {
                this.start(name);
            }
	        start(name) {
	            $gameMenu.addEventListener('submit', this.onGameMenuInput);
	            $battleMenu.addEventListener('submit', this.onBattleMenuInput);
	            this.changeScreen('game');
	            //서버와 통신으로 플레이어 캐릭터 정보 설정 및 게임상태 받아오기
	            ajaxRequest({status:"setup",name:name},'/abd/gameSetup')
	        }
	        changeScreen(screen) {
	            if(screen === 'start') {
	                $startScreen.style.display = 'block';
	                $screen.style.display = 'none';
	                $gameMenu.style.display = 'none';
	                $battleMenu.style.display = 'none';
	            } else if(screen === 'game') {
	                $startScreen.style.display = 'none';
	                $gameMenu.style.display = 'block';
	                $screen.style.display = 'block';
	                $battleMenu.style.display = 'none';
	                $monsterImg.style.display = 'none';
	            } else if(screen === 'battle') {
	                $startScreen.style.display = 'none';
	                $gameMenu.style.display = 'none';
	                $screen.style.display = 'block';
	                $battleMenu.style.display = 'block';
	                $monsterImg.style.display = 'block';
	            }
	        }
	        clearInputs() {
	            document.querySelectorAll('form').forEach((form, index) => {
	                form.reset();
	            });
	        }
	        
	        onGameMenuInput = (event) => {
                event.preventDefault();
                var inputs = {status:"onGoing",action:"event"};
            	if(document.querySelector("#playSelect")){
                	var selected = {};
            		selected["SELECT_CD"] = document.querySelector("#playSelect").selectCode;
            		selected["OPTION_SEQ"] = document.querySelector("#playSelect").value;
            		inputs["inputData"] = {selected:selected}
            	}else if(document.querySelector("#playInput")){
            		inputs["inputData"] = {userInput:document.querySelector("#playInput").value}
            	}
                ajaxRequest(inputs);
            }
	        
            onBattleMenuInput = (event) => {
                event.preventDefault();
                var inputs = {status:"onGoing",action:"event"};
            	if(document.querySelector("#commonBattleSelect")){
                	var selected = {};
            		selected["SELECT_CD"] = document.querySelector("#commonBattleSelect").selectCode;
            		selected["OPTION_SEQ"] = document.querySelector("#commonBattleSelect").value;
            		inputs["inputData"] = {selected:selected}
            	}else if(document.querySelector("#battleHelpSelect")){
                	var selected = {};
            		selected["SELECT_CD"] = document.querySelector("#battleHelpSelect").selectCode;
            		selected["OPTION_SEQ"] = document.querySelector("#battleHelpSelect").value;
            		inputs["inputData"] = {selected:selected}
            	}else if(document.querySelector("#pullBackSelect")){
                	var selected = {};
            		selected["SELECT_CD"] = document.querySelector("#pullBackSelect").selectCode;
            		selected["OPTION_SEQ"] = document.querySelector("#pullBackSelect").value;
            		inputs["inputData"] = {selected:selected}
            	}else if(document.querySelector("#pullBackHelpSelect")){
                	var selected = {};
            		selected["SELECT_CD"] = document.querySelector("#pullBackHelpSelect").selectCode;
            		selected["OPTION_SEQ"] = document.querySelector("#pullBackHelpSelect").value;
            		inputs["inputData"] = {selected:selected}
            	}else if(document.querySelector("#useItemSelect")){
                	var selected = {};
            		selected["SELECT_CD"] = document.querySelector("#useItemSelect").selectCode;
            		selected["OPTION_SEQ"] = document.querySelector("#useItemSelect").value;
            		inputs["inputData"] = {selected:selected}
            	}
                ajaxRequest(inputs);
            }
            
            createHero(heroInfo) {
            	this.hero = new Hero(this, heroInfo);
            }
            
            setHeroStatus(heroInfo){
            	this.hero.setStatus(heroInfo);
            }
            
            updateHeroStat() {
                const { hero } = this;
                if (hero === null) {
                    $heroName.textContent = '';
                    $heroLevel.textContent = '';
                    $heroHp.textContent = '';
                    $heroXp.textContent = '';
                    $heroAtt.textContent = '';
                    return;
                }
                $heroName.textContent = hero.name;
                $heroLevel.textContent = `레벨 `+hero.lev;
                $heroHp.textContent = `체력: `+ hero.hp+"/"+hero.maxHp;
                $heroXp.textContent = `경험치: `+ hero.xp+"/"+hero.reqdXp;
                $heroAtt.textContent = `공격력: `+ hero.att;
            }
            
            createMonster(monsterInfo){
            	
            	this.monster = new Monster(this, monsterInfo);
                
            	let message = `무한교 신도와 마주쳤다. `+monsterInfo.name+`인 것 같다!`;
            	this.showMessage(message);
            }
            
            clearMonster(){
            	this.monster = null;
            }
            
            setMonsterStatus(monsterInfo){
            	this.monster.setStatus(monsterInfo);
            }
            
            updateMonsterStat() {
                const { monster } = this;
                if(monster === null) {
                    $monsterName.textContent = '';
                    $monsterHp.textContent = '';
                    $monsterAtt.textContent = '';
                    $monsterLine.textContent = '';
                    return;
                }
                $monsterName.textContent = monster.name;
                $monsterHp.textContent = `체력: `+monster.hp+"/"+monster.maxHp;
                $monsterAtt.textContent = `공격력: `+monster.att;
                $monsterLine.textContent = monster.line;
            }
            
            showMessage(text) {
           		var cnt = $message.childElementCount;
           		for(var i=cnt-1; i>0 ;i--){
           			var c = $message.children[i];
           			if(c){
            			$message.removeChild(c);
           			}
           		}
           		$messageP.innerHTML = text;
            }
            
            quit() {
                this.hero = null;
                this.monster = null;
                this.updateHeroStat();
                this.updateMonsterStat();
                $gameMenu.removeEventListener('submit', this.onGameMenuInput);
                $battleMenu.removeEventListener('submit', this.onBattleMenuInput);
                this.changeScreen('start');
                game = null;
                ajaxRequest({status:'clear'});
            }
		}
        
        class Unit {
            constructor(game, name, maxHp, hp, att, xp) {
                this.game = game;
                this.name = name;
                this.maxHp = maxHp;
                this.hp = hp; 
                this.xp = xp;
                this.att = att;
            }
        }
        
        class Hero extends Unit {
            constructor(game, heroInfo) {
                super(game, heroInfo.name, heroInfo.maxHp, heroInfo.hp, heroInfo.att, heroInfo.xp);
                this.lev = heroInfo.lev;
                this.reqdXp = heroInfo.reqdXp;
            }
            
            setStatus(heroInfo){
            	if(this.lev < heroInfo.lev){
            		this.game.showMessage(`레벨업! 레벨 `+ heroInfo.lev);
            	}
                this.maxHp = heroInfo.maxHp;
                this.hp = heroInfo.hp; 
                this.xp = heroInfo.xp;
                this.att = heroInfo.att;
                this.lev = heroInfo.lev;
                this.reqdXp = heroInfo.reqdXp;
            }
        }
        
        class Monster extends Unit {
            constructor(game, monsterInfo) {
                super(game, monsterInfo.name, monsterInfo.maxHp, monsterInfo.hp, monsterInfo.att, monsterInfo.xp);
                this.line = monsterInfo.line;
            }
            
            setStatus(monsterInfo){
                this.maxHp = monsterInfo.maxHp;
                this.hp = monsterInfo.hp;
            }
        }
        //새로고침시 게임(캐릭터풀)이 살아있는 채로 플레이어만 초기되는 현상이 있어서 맨 처음에 게임을 클리어해줌
        ajaxRequest({status:'clear'});
        let game = null;
        $startScreen.addEventListener('submit', (event) => {
        	event.preventDefault();
            const name = event.target['name-input'].value;
            game = new Game(name);
        });
	</script>
</body>
</html>