$(document).ready(() => {
    const stompClient = new StompJs.Client({
        brokerURL: 'ws://localhost:8081/chat'
    });
    stompClient.onConnect = (frame) => {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/chat', (message) => {
            respondMessage(JSON.parse(message.body).content);
        });
    };
    stompClient.onWebSocketError = (error) => {
        console.error('Error with websocket', error);
    };

    stompClient.onStompError = (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
    };
    stompClient.activate();

    function sendMessage() {
        const message = $('#input-message').val();

        $(".direct-chat-messages").append(`
        <div class="direct-chat-msg">
            <div class="direct-chat-infos clearfix">
                <span class="direct-chat-name float-left">Uno</span>
            </div>
            <div class="direct-chat-text">
                ${message}
            </div>
        </div>
    `);

        stompClient.publish({
            destination: "/app/hello",
            body: JSON.stringify({'content': $("#input-message").val()})
        });
    }

    function respondMessage(message) {
        $(".direct-chat-messages").append(`
        <div class="direct-chat-msg right">
            <div class="direct-chat-infos clearfix">
                <span class="direct-chat-name float-right">Bot</span>
            </div>
            <div class="direct-chat-text">
                ${message}
            </div>
        </div>
    `);
    }

    $(function () {
        $("#chat-form").on('submit', function (e) {
            e.preventDefault();
            sendMessage();
        });
        $("#chat-form button").click(function(e) {
            sendMessage();
        });
    });
});
