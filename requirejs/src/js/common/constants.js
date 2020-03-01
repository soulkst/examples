/*
	uglify시 properties 전체 활성화할 경우 라이브러리의 Function도 uglify 되어 정상 동작하지 않음.
	'V_' 패턴으로 변수명을 정의하여 해당 변수만 uglify가 진행된다.
*/
define(function(){
	return {
		V_message: "Hello World"
	};
});