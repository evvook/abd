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
	    #screen { display: none; }
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
	    <div id="story">
	        당신은 서명을 마치고 제4해저기지로 발걸음을 향했다.
	    </div>
	    <div id="hero-stat">
	        <span id="hero-name"></span>
	        <span id="hero-level"></span>
	        <span id="hero-hp"></span>
	        <span id="hero-xp"></span>
	        <span id="hero-att"></span>
	    </div>
	    <form id="game-menu" style="display: none;">
	        <div id="menu-1">1.모험</div>
	        <div id="menu-2">2.카페에 쉬러가기(체력 회복)</div>
	        <div id="menu-3">3.종료</div>
	        <input id="menu-input" />
	        <button id="menu-button">입력</button>
	    </form>
	    <form id="battle-menu" style="display: none;">
	        <div id="battle-1">1.대결</div>
	        <!-- <div id="battle-2">2.금이씨와 얘기하기(체력 +20)</div> -->
	        <div id="battle-3">2.도망</div>
	        <input id="battle-input" />
	        <button id="battle-button">입력</button>
	    </form>
	    <div id="message"></div>
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
		
        function ajaxRequest(inputs){
        	
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
    	    httpRequest.open('POST', '/abd/gamePlay', true);//경로 잡아줌
    	    /* Response Type을 Json으로 사전 정의 */
    	    httpRequest.responseType = "json";
    	    /* 요청 Header에 컨텐츠 타입은 Json으로 사전 정의 */
    	    httpRequest.setRequestHeader('Content-Type', 'application/json');
    	    /* 정의된 서버에 Json 형식의 요청 Data를 포함하여 요청을 전송 */
    	    httpRequest.send(JSON.stringify(reqJson));
        }
        
        function afterwork(result){
        	if(result.status == "start"){
	        	game.createHero(result.player);
        	}else if(result.status == "adventure"){
        		//상대 캐릭터 설정
        		game.createMonster(result.npc)
        		//모험 화면 업데이트
        		game.clearInputs();
        		game.changeScreen('battle');
	        	game.updateHeroStat();
        		game.updateMonsterStat();
        	}else if(result.status == "battle"){
        		//캐릭터 상태 적용
        		game.showMessage(game.hero.att+"의 데미지를 주고, "+game.monster.att+"의 데미지를 받았다.");
        		
        		game.setHeroStatus(result.player);
        		game.setMonsterStatus(result.npc);
        		//캐릭터 상태 화면 반영
	        	game.updateHeroStat();
        		game.updateMonsterStat();
        	}else if(result.status == "win"){
        		game.showMessage(game.monster.name+"을/를 이겨 "+game.monster.xp+" 경험치를 얻었다");
        		
        		game.setHeroStatus(result.player);
        		game.clearMonster();
        		
        		game.updateHeroStat();
        		game.updateMonsterStat();
        		
        		//중앙동으로 이동
        		game.changeScreen('game');
        		
        	}else if(result.status == "defeat"){
        		//사망
        		game.setHeroStatus(result.player);
        		game.updateHeroStat();
        		
        		game.clearMonster();
        		game.updateMonsterStat();
        		
        		game.clearInputs();
                game.changeScreen('game');
        		alert(result.player.lev+` 레벨에서 패배. 새 주인공을 생성하세요.`);
                game.quit();
        		
        	}else if(result.status == "goAway"){
        		//상대 캐릭터 초기화
        		//중앙동으로 이동
        		game.clearInputs();
                game.changeScreen('game');
                game.showMessage(result.line);
                
                game.setHeroStatus(result.player);
                game.updateHeroStat();
                
                game.clearMonster();
                game.updateMonsterStat();
        	}else if(result.status == "runAway"){
        		//상대 캐릭터 초기화
        		//중앙동으로 이동
                game.clearInputs();
                game.changeScreen('game');
                game.showMessage('부리나케 도망쳤다!');
                
                game.clearMonster();
                game.updateMonsterStat();
        	}
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
	            ajaxRequest({status:"start",name:name})
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
                const input = event.target['menu-input'].value;
                if(input === '1') {
                    ajaxRequest({status:"onGoing",action:"adventure"});
                    
                } else if (input === '2') { //휴식
                    ajaxRequest({status:"onGoing",action:"rest"});
                
                } else if (input === '3') { //종료
                    this.showMessage(' ');
                    this.quit();
                }
            }
	        
            onBattleMenuInput = (event) => {
                event.preventDefault();
                const input = event.target['battle-input'].value;
                if(input === '1') {
                	ajaxRequest({status:"onGoing",action:"battle"});
                }
                else if (input === '2') {
                	ajaxRequest({status:"onGoing",action:"runAway"});
                }
                
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
                
            	let message = `엔지니어 가팀과 마주쳤다. `+monsterInfo.name+`인 것 같다!`;
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
                $message.textContent = text;
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