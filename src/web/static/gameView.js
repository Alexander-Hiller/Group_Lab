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
        placeTank(gameState['tanks'][tankMap]['xPos'], gameState['tanks'][tankMap]['yPos'], '#006400');
    }

    for (var bar in gameState['bars']){
        placeBar(gameState['bars'][bar]['xPos'], gameState['bars'][bar]['yPos'], getRandomColor(), gameState['bars'][bar]['xTar'], gameState['bars'][bar]['yTar'])
    }

    for (var bullMap in gameState['bulls']){
        placeBull(gameState['bulls'][bullMap]['xPos'], gameState['bulls'][bullMap]['yPos'], '#000000', 5)
    }

}


function drawGameBoard() {
    context.clearRect(0, 0, 800, 800);
    if (canvas.getContext) {
        context.strokeRect(0, 0, 800, 800);
    }
}

function placeBar(x, y, color, sizex, sizey) {
    context.fillStyle = color;
    context.fillRect(x, y, sizex, sizey);
    context.strokeStyle = '#000000';
    context.stroke();
}

function placeTank(x, y, color) {
    context.fillStyle = color;
    context.beginPath();
    context.rect(x, y, 40, 20);
    context.fill();
    context.strokeStyle = '#000000';
    context.stroke();
}

function placeBull(x, y, color, size) {
    context.fillStyle = color;
    context.beginPath();
    context.arc(x, y, size, 0, 2 * Math.PI);
    context.fill();
    context.strokeStyle = '#000000';
    context.stroke();
}

function getRandomColor() {
    var letters = '0123456789ABCDEF';
    var color = '#';
    for (var i = 0; i < 6; i++) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}


function joinGame(){
    socket.emit("newTank", "250", "350", "fuckoff")
}

// function getMouseXPos(event) {
//     var xPos = event.clientX.toString()
//     return xPos
// }
//
// function getMouseYPos(event) {
//     var yPos = event.clientY.toString()
//     return yPos
// }
//
// function fireBull(){
//     socket.emit("250", "350", getMouseXPos(), getMouseYPos(), "fuckoff", "696969")
// }
//
// document.addEventListener("click", fireBull);
