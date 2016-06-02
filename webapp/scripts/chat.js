/**
 * Created by vlad on 02.06.2016.
 */
var nickname;

window.onload = function onload() {
    var conditionImg = document.getElementsByClassName('condition')[0];
    conditionImg.src = "/images/online.png";
    var author = document.getElementById('nickname').value;
    var avatar = document.getElementById('user-avatar');
    var url = '/avatars/' + author + '.png';
    if(avatarExist(url)) {
        avatar.style.backgroundImage = "url('" + url + "')";
    }

    subscribe();
};

function theMsg(txt, author, id) {
    return {
        txt: txt,
        author: author,
        id: id,
    };
}

function theMsg(txt, author, id, timestapm, isDeleted) {
    return {
        txt: txt,
        author: author,
        id: id,
        timestamp: timestapm,
        isDeleted: isDeleted,
    };
}

function handleKeyPressMsgArea(keyCode) {
    if(keyCode == 10)
        send_msg();
}

function send_msg() {
    if(document.getElementById('msg_area').value.localeCompare('')) {
        var txt = document.getElementById('msg_area').value;
        var author = document.getElementById('nickname').value;
        var msg = theMsg(txt, author, 0);
        post(appState.mainUrl, JSON.stringify(msg), function() {
        });
    }
    document.getElementById('msg_area').value='';
}

function createAllMessages(allMsgs) {
    for(var i = 0; i < allMsgs.length; i++) {
        addMsg(theMsg(allMsgs[i].text, allMsgs[i].author, allMsgs[i].id, allMsgs[i].timestamp, allMsgs[i].isDeleted));
    }
}

function addMsg(msg) {
    createItem(msg);
}

function createItem(msg) {
    nickname = document.getElementById('nickname').value;
    var newLi = createNewLi(msg);
    var newMsg = createMsg(msg);
    var avatar = createAvatar(msg.author);
    var author = createAuthor(msg.author);
    var msgTxt = createMsgTxt(msg);
    var edit_area = createEditArea();
    var delBtn = createDelBtn();
    var editBtn = createEditBtn();

    newMsg.appendChild(avatar);
    newMsg.appendChild(author);
    newMsg.appendChild(msgTxt);
    if(msg.author.localeCompare(nickname) == 0) {
        newMsg.appendChild(delBtn);
        newMsg.appendChild(editBtn);
    }
    newMsg.appendChild(edit_area);
    newMsg.setAttribute('msg-id', msg.id);
    newLi.appendChild(newMsg);
    document.getElementById('msg_cnt').appendChild(newLi);
    if(msg.isDeleted) {
        addDeletedClass(newMsg)
    }
    var container = document.getElementsByClassName('container')[0];
    container.scrollTop = container.scrollHeight;
}

function createNewLi(msg) {
    var newLi = document.createElement('li');
    if(msg.author.localeCompare(nickname) == 0)
        newLi.className = "my-message";
    newLi.setAttribute("msg-id", msg.id);
    return newLi;
}

function createMsg(msg) {
    var newMsg = document.createElement('div');
    newMsg.className = "message";

    return newMsg;
}

function createAvatar(author) {
    var avatar = document.createElement('div');
    avatar.className = "message-avatar";
    var url = '/avatars/' + author + '.png';
    if(avatarExist(url)) {
        avatar.style.backgroundImage = "url('" + url + "')";
    }
    return avatar;
}

function avatarExist(url) {
    var img = new Image();
    img.src = url;
    return img.height != 0;
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
    txt.appendChild(document.createTextNode(msg.txt));
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
    edit_area_btn.className = "edit-button";
    edit_area_btn.addEventListener("click", function(event) {
        var msg = this.parentElement.parentElement;
        var txt = msg.getElementsByClassName('edit-field')[0].value;
        var author = msg.getElementsByClassName('message-author')[0].innerHTML;
        var id = msg.attributes['msg-id'].value;
        msg.getElementsByClassName("edit_area_on")[0].className = "edit_area";

        put(appState.mainUrl, JSON.stringify(theMsg(txt, author, id)), function() {
        });
    });
    return edit_area_btn;
}

function createDelBtn() {
    var delBtn = document.createElement('input');
    delBtn.type = "button";
    delBtn.addEventListener("click", function(event) {
        var msg = this.parentElement;
        var txt = msg.getElementsByClassName('text')[0].innerHTML;
        var author = msg.getElementsByClassName('message-author')[0].innerHTML;
        var id = msg.attributes['msg-id'].value;
        if(msg.getElementsByClassName("edit_area_on").length > 0)
            msg.getElementsByClassName("edit_area_on")[0].className = "edit_area";

        deleteMsg(appState.mainUrl, JSON.stringify(theMsg(txt, author, id)), function() {
        });
    });
    delBtn.alt = "D";
    delBtn.className = "message-delete";
    return delBtn;
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

function addDeletedClass(msg) {
    msg.className = "message deleted";
    if(msg.parentElement.className == "my-message") {
        msg.removeChild(msg.getElementsByClassName('message-edit')[0]);
        msg.removeChild(msg.getElementsByClassName('message-delete')[0]);
    }
}
