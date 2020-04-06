var eventBus = null;
var eventBusOpen = false;

function initWs() {
    eventBus = new EventBus('/client.register');
    eventBus.onopen = function () {
        eventBusOpen = true;
        regForMessages();
    };
    eventBus.onerror = function(err) {
        eventBusOpen = false;
    };
}

function regForMessages() {
    if (eventBusOpen) {
        eventBus.registerHandler('service.ui-log', function (error, message) {
            if (message) {
                console.info('Found message: ' + message.body);
                var msgList = $("pre#log");
                msgList.html(msgList.html() + message.body + "<br/>");
            } else if (error) {
                console.error(error);
            }
        });
        eventBus.registerHandler('service.ui-lst', function (error, message) {
            if (message) {
                console.info('Found message: ' + message.body);
                var msgList = $("pre#lst");
                msgList.html(msgList.html() + message.body + "<br/>");
            } else if (error) {
                console.error(error);
            }
        });
    } else {
        console.error("Cannot register for messages; event bus is not open");
    }
}

$( document ).ready(function() {
    initWs();
    $('#orderForm').ajaxForm({
      dataType: 'json',
      success: function(res){
        alert(res['order']);
      }
    });
});

function unregister() {
    reg().subscribe(null);
}
