var socket = io.connect({transports: ['websocket']});
socket.on('gameState', parseGameState);

var canvas = document.getElementById("canvas");
var context = canvas.getContext("2d");
context.globalCompositeOperation = 'source-over';


function parseGameState(event) {

    var gameState = JSON.parse(event);

    //resets gameboard
    drawGameBoard();

    //redraws the tanks in new positions
    for (var tankMap in gameState['tanks']) {
        for (var key in tankMap) {
            placeTank(tankMap[key]['xPos'], tankMap[key]['yPos'], '#ffff00', 20);
        }
    }
}


function drawGameBoard() {
    context.clearRect(0, 0, 600, 600);
    if (canvas.getContext) {
        context.strokeRect(0, 0, 600, 600);
    }
}


function placeTank(x, y, color, size) {
    context.fillStyle = color;
    context.beginPath();
    context.arc(x, y, size, 0, 2 * Math.PI);
    context.fill();
    context.strokeStyle = '#760672';
    context.stroke();
}

