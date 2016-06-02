/**
 * Created by vlad on 02.06.2016.
 */
var appState = {
    mainUrl : 'http://localhost:8080/chat',
    tokenMsg : 'TE11EN',
    tokenMod : 'TE11EN'
};

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

        if(isError(xhr.responseText)) {
            defaultErrorHandler('Error on the server side, response ' + xhr.responseText);
            return;
        }

        continueWith(xhr.responseText);
    };

    xhr.ontimeout = function() {
        defaultErrorHandler('Server timed out!');
    };

    xhr.onerror = function(e) {
        var errMsg = 'Server connection error!\n' +
            '\n' +
            'Check if \n' +
            '- server is active\n' +
            '- server sends header "Access-Control-Allow-Origin:*"';

        defaultErrorHandler(errMsg);
    };

    xhr.send(data);
}

function subscribe() {
    var url = appState.mainUrl + '?tokenMsg=' + appState.tokenMsg + '&tokenMod=' + appState.tokenMod;

    get(url, function(responseText) {
        console.assert(responseText != null);
        var response = JSON.parse(responseText);
        appState.tokenMsg = response.tokenMsg;
        appState.tokenMod = response.tokenMod;
        createAllMessages(response.messages);
        edit(response.modMsgs);

        subscribe();
    });
}

function edit(modMsgs) {
    var messages = document.getElementsByClassName('Message');

    for(var i = 0; i < modMsgs.length; i++) {
        for(var j = 0; j < messages.length; j++) {
            if(messages[j].attributes['msg-id'].value == modMsgs[i].id) {
                messages[j].getElementsByClassName('text')[0].innerHTML = modMsgs[i].text;
                if(modMsgs[i].isDeleted == true) {
                    addDeletedClass(messages[j]);
                }
                break;
            }
        }
    }
}