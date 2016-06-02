var messages = [];

window.onload = function onload() {
	var conditionImg = document.getElementsByClassName('condition')[0];
	conditionImg.src = "offline.png";
	
	restore();
	subscribe();
	
	document.getElementById('nickname').value = restore("Nickname");
};

function createAllMessages(allMsgs) {
	for(var i = 0; i < allMsgs.length; i++)
		addMsg(allMsgs[i]);
}

function save_nick(elem) {
	store(elem.value, "Nickname");
}

function theMsg(txt, author, timestamp, id, isDeleted) {
	return {
		txt: txt,
		author: author,
		timestamp: timestamp,
		id: id,
		isDeleted: isDeleted
	};
}

function uniqueId() {
	var date = Date.now();
	var random = Math.random() * Math.random();

	return Math.floor(date * random).toString();
}