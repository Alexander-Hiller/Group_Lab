import json
import socket
from threading import Thread
import string

from flask import Flask, send_from_directory, request
from flask_socketio import SocketIO

import eventlet

eventlet.monkey_patch()

app = Flask(__name__)
socket_server = SocketIO(app)

# ** Connect to Scala TCP socket server **

scala_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
scala_socket.connect(('localhost', 65000))

delimiter = "~"
allUsers =[]

def listen_to_scala(the_socket):
    buffer = ""
    while True:
        buffer += the_socket.recv(1024).decode()
        while delimiter in buffer:
            message = buffer[:buffer.find(delimiter)]
            buffer = buffer[buffer.find(delimiter) + 1:]
            get_from_scala(message)
            for user in allUsers:
                socket_server.emit('message',message, room=user)





def get_from_scala(data):
    socket_server.emit('gameState', data, broadcast=True)


def send_to_scala(data):
    scala_socket.sendall((json.dumps(data)).encode())


Thread(target=listen_to_scala, args=(scala_socket,)).start()


# ** Setup and start Python web server **

@socket_server.on('connect')
def got_message():
    print(request.sid + " connected")
    message = {"username": request.sid, "action": "connected"}
    send_to_scala(message)
    allUsers.append(request.sid)


@socket_server.on('disconnect')
def disconnect():
    print(request.sid + " disconnected")
    message = {"username": request.sid, "action": "disconnected"}
    send_to_scala(message)
    allUsers.remove(request.sid)

@socket_server.on('test')
def test():
    print(request.sid + " initiated test")
    message = {"username": request.sid, "action": "test"}
    send_to_scala(message)

@socket_server.on('update')
def update(json):
    message = {"JSONdata": json, "action": "update"}
    send_to_scala(message)

@socket_server.on('move')
def move(name, xPos, yPos):
    message = {"name": name, "action": "move", "xPos": xPos, "yPos": yPos}
    send_to_scala(message)

@socket_server.on('rot')
def rot(name, rot):
    message = {"name": name, "action": "rot", "rot": rot}
    send_to_scala(message)

@socket_server.on('bull')
def bull(xPos,yPos,xTar,yTar,name,bullNum):
    message = {"name": name, "action": "bull", "xTar": xTar,"yTar":yTar,"bullNum":bullNum,"xPos": xPos,"yPos":yPos}
    send_to_scala(message)

@socket_server.on('newTank')
def bull(xPos,yPos,name):
    print(name + " was added to the game")
    message = {"name": name, "action": "newTank", "xPos": xPos,"yPos":yPos}
    send_to_scala(message)


@app.route('/')
def index():
    return send_from_directory('static', 'index.html')


@app.route('/<path:filename>')
def static_files(filename):
    return send_from_directory('static', filename)


print("Listening on port 60000")
socket_server.run(app, port=60000)
