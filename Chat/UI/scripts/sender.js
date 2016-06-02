function delete_msg(elem) {
	var msg = elem.parentElement;
	msg.className = "message deleted";
	msg.getElementsByClassName('text')[0].innerHTML = 'Deleted!';
	msg.removeChild(msg.getElementsByClassName('message-edit')[0]);
	msg.removeChild(msg.getElementsByClassName('message-delete')[0]);
}

function edit_msg(elem) {
	var msg = elem.parentElement;
	msg.getElementsByClassName("edit_area")[0].className = "edit_area_on";
	var edit_field = msg.getElementsByClassName('edit-field')[0];
	edit_field.value = msg.getElementsByClassName('text')[0].innerHTML;
}

function confirm_change(elem) {
	var msg = elem.parentElement.parentElement;
	var changedText = elem.parentElement.getElementsByClassName('edit-field')[0].value;
	msg.getElementsByClassName('text')[0].innerHTML = changedText;
	elem.parentElement.className = "edit_area";
}

function send_msg() {
	if(!document.getElementById('nickname').value.localeCompare('')) {
		alert('You must enter a nickname');
	} else if(!document.getElementById('msg_area').value.localeCompare('')) {
		alert('You can not send an empty message');
	} else {
		var txt = document.getElementById('msg_area').value;
		var author = document.getElementById('nickname').value;
		var timestamp = currentTime();
		var msg = theMsg(txt, author, timestamp, uniqueId(), false);
		post(appState.mainUrl, JSON.stringify(msg), function() {
		});
	}	
}

function addMsg(msg) {
	createItem(msg);
}

function createItem(msg) {
	var newLi = createNewLi(msg);
	var newMsg = createMsg(msg);
	var avatar = createAvatar();
	var author = createAuthor(msg.author);
	var msgTxt = createMsgTxt(msg);
	var edit_area = createEditArea();
	var delBtn = createDelBtn();
	var editBtn = createEditBtn();
	
	newMsg.appendChild(avatar);
	newMsg.appendChild(author);
	newMsg.appendChild(msgTxt);
	if(!msg.isDeleted && msg.isMy_message) {
		newMsg.appendChild(delBtn);
		newMsg.appendChild(editBtn);
	}
	newMsg.appendChild(edit_area);
	newMsg.attributes['msg-id'].value = msg.id;
	newLi.appendChild(newMsg);
	document.getElementById('msg_cnt').appendChild(newLi);
	var container = document.getElementsByClassName('container')[0];
	container.scrollTop = container.scrollHeight;
	
	document.getElementById('msg_area').value='';
}

function createNewLi(msg) {
	var newLi = document.createElement('li');
	if(msg.isMy_message) 
		newLi.className = "my-message";
	newLi.setAttribute("msg-id", msg.id);
	return newLi;
}

function createMsg(msg) {
	var newMsg = document.createElement('div');
	newMsg.className = "message";
	if(msg.isDeleted) 
		newMsg.className += " deleted";

	return newMsg;
}

function createAvatar() {
	var avatar = document.createElement('div');
	avatar.className = "message-avatar";
	return avatar;
}

function createAuthor(Msgauthor) {
	var author = document.createElement('div');
	author.className = "message-author";
	author.appendChild(document.createTextNode(Msgauthor));
	return author;
}

function createMsgTxt(msg) {
	var msgTxt = document.createElement('div');
	msgTxt.className = "message-text";
	msgTxt.appendChild(createTxt(msg));
	msgTxt.appendChild(createMessageDate(msg.timestamp));
	return msgTxt;
}

function createTxt(msg) {
	var txt = document.createElement('div');
	txt.className = "text";
	if(msg.isDeleted) {
		txt.appendChild(document.createTextNode("Deleted!"));
	} else {
		txt.appendChild(document.createTextNode(msg.txt));
	}
	return txt;
}

function createMessageDate(timestamp) {
	var messageDate = document.createElement('div');
	messageDate.className = "message-date";
	messageDate.appendChild(document.createTextNode(timestamp));
	return messageDate;
}

function createEditArea() {
	var edit_area = document.createElement('div');
	edit_area.className = "edit_area";
	edit_area.appendChild(createEditAreaText());
	edit_area.appendChild(createEditAreaBtn());
	return edit_area;
}

function createEditAreaText() {
	var edit_area_text = document.createElement('input');
	edit_area_text.type = "text";
	edit_area_text.className = "edit-field";
	return edit_area_text;
}

function createEditAreaBtn() {
	var edit_area_btn = document.createElement('input');
	edit_area_btn.type = "button";
	edit_area_btn.value = "OK";
	edit_area_btn.addEventListener("click", function(event) {
		var msg = this.parentElement.parentElement;
		var txt = msg.getElementsByClassName('edit-field')[0].value;
		var author = msg.getElementsByClassName('message-author')[0].innerHTML;
		var timestamp = msg.getElementsByClassName('message-date')[0].innerHTML;
		var id = msg.attributes['msg-id'].value;
		
		put(appState.mainUrl, theMsg(txt, author, timestamp, id, false), function() {
		});
	});
	return edit_area_btn;
}

function editMsgText(msg) {
	var id = msg.parentElement.attributes['msg-id'].value;
	var changedText = msg.getElementsByClassName('edit-field')[0].value;
	for(var i = 0; i < messages.length; i++) {
		if(messages[i].id != id)
			continue;
		
		messages[i].txt = changedText;		
	}
	store(messages, "Messages");
	
	msg.getElementsByClassName('text')[0].innerHTML = changedText.replace(/([^>])\n/g, '$1<br/>');
	msg.getElementsByClassName('edit_area_on')[0].className = "edit_area";
}

function createDelBtn() {
	var delBtn = document.createElement('input');
	delBtn.type = "button";
	delBtn.addEventListener("click", function(event) {		
		var msg = this.parentElement;
		var txt = msg.getElementsByClassName('text')[0].innerHTML;
		var author = msg.getElementsByClassName('message-author')[0].innerHTML;
		var timestamp = msg.getElementsByClassName('message-date')[0].innerHTML;
		var id = msg.attributes['msg-id'].value;
		
		deleteMsg(appState.mainUrl, theMsg(txt, author, timestamp, id, false), function() {
		});	
	});
	delBtn.alt = "D";
	delBtn.className = "message-delete";
	return delBtn;
}

function addDeletedClass(msg) {
	var id = msg.parentElement.attributes['msg-id'].value;
	for(var i = 0; i < messages.length; i++) {
		if(messages[i].id != id)
			continue;
		
		messages[i].isDeleted = true;
	}
	store(messages, "Messages");
	
	msg.className = "message deleted";
	msg.getElementsByClassName('text')[0].innerHTML = 'Deleted!';
	msg.removeChild(msg.getElementsByClassName('message-edit')[0]);
	msg.removeChild(msg.getElementsByClassName('message-delete')[0]);
	msg.getElementsByClassName('edit_area_on')[0].className = "edit_area";	
}

function createEditBtn() {
	var editBtn = document.createElement('input');
	editBtn.type = "button";
	editBtn.addEventListener("click", function(event) {
		var msg = this.parentElement;
		msg.getElementsByClassName("edit_area")[0].className = "edit_area_on";
		var edit_field = msg.getElementsByClassName('edit-field')[0];
		edit_field.value = msg.getElementsByClassName('text')[0].innerHTML;
	});
	editBtn.alt = "E";
	editBtn.className = "message-edit";
	return editBtn;
}

function currentTime() {
	var date = new Date();
	var hours = date.getHours();
	var minutes = date.getMinutes();
	var seconds = date.getSeconds();
	var dat = date.getDate();
	var month = date.getMonth() + 1;
	var year = date.getFullYear();
	
	return (hours > 9 ? hours : ("0" + hours)) + ":" +
			(minutes > 9 ? minutes : ("0" + minutes)) + ":" +
			(seconds > 9 ? seconds : ("0" + seconds)) + " " +
			(dat > 9 ? dat : ("0" + dat)) + "." +
			(month > 9 ? month : ("0" + month)) + "." +
			year;
}