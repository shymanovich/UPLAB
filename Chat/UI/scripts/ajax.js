var appState = {
	mainUrl : 'http://192.168.0.101:3001/chat'
	token : 'TE11EN'
}

function defaultErrorHandler(msg) {
	console.error(msg);
	alert(msg);
}

function isError(text) {
	if(text == "")
		return false;
	
	try {
		var obj = JSON.parse(text);
	} catch(ex) {
		return true;
	}
	return !!obj.error;
}

function get(url, continueWith) {
	ajax('GET', url, null, continueWith);
}

function post(url, data, continueWith) {
	ajax('POST', url, data, continueWith);
}

function put(url, data, continueWith) {
	ajax('PUT', url, data, continueWith);
}

function deleteMsg(url, data, continueWith) {
	ajax("DELETE", url, data, continueWith);
}

function ajax(method, url, data, continueWith)
{
	var xhr = new XMLHttpRequest();
	xhr.open(method || 'GET', url, true);
	
	xhr.onload = function() {
		if(xhr.readyState != 4)
			return;
		
		if(xhr.status != 200) {
			defaultErrorHandler('Error on the server side, response ' + xhr.status);
			return;
		}
		
		if(isError(xhr.responseText) {
			defaultErrorHandler('Error on the server side, response ' + xhr.responseText);
			return;
		}
		
		continueWith(xhr.responseText);
	}
	
	xhr.ontimeout = function() {
		defaultErrorHandler('Server timed out!');
	}
	
	xhr.onerror = function(e) {
		var errMsg = 'Server connection error!\n' +
		'\n' +
		'Check if \n' +
		'- server is active\n' +
		'- server sends header "Access-Control-Allow-Origin:*"';
		
		defaultErrorHandler(errMsg);
	}
	
	xhr.send(data);
}

function subscribe() {
	var url = appState.mainUrl + '?token=' + appState.token;
	
	get(url, function(responseText) {
		console.assert(responseText != null);
		
		var response = JSON.parse(responseText);
		
		if(response.type.localeCompare("POST") == 0) {
			appState.token = response.token;
			createAllMessages(response.messages);
		} else if(response.type.localeCompare("PUT") == 0) {
			edit(response);
		} else if(response.type.localeCompare("DELETE") == 0) {
			deleteByResponse(response);
		}
		
		subscribe();
	});
}

function edit(response) {
	var messages = document.getElementsByClassName('message');
	var msg = response.messages[0];
	
	for(var i = 0; i < messages.length; i++) {
		if(messages[i].attributes['msg-id'].value == msg.id) {
			messages[i].getElementsByClassName('text')[0].innerHTML = msg.txt;
			break;
		}
	}
}

function deleteByResponse(response) {
	var messages = document.getElementsByClassName('message');
	var msg = response.messages[0];
	
	for(var i = 0; i < messages.length; i++) {
		if(messages[i].attributes['msg-id'].value == msg.id) {
			messages[i].className = "message deleted";
			messages[i].getElementsByClassName('text').innerHTML = "Deleted!";
			messages[i].removeChild(msg.getElementsByClassName('message-edit')[0]);
			messages[i].removeChild(msg.getElementsByClassName('message-delete')[0]);
		}
	}
}