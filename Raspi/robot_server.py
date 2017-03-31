#author: Luigi Vincent

import RPi.GPIO as GPIO
import video_dir
import car_dir
import motor
from socket import *
from time import ctime 

def stop:
	motor.ctrl(0)

commands = {
	'forward' : motor.forward,
	'backward' : motor.backward,
	'left' : car_dir.turn_left,
	'right' : car_dir.turn_right,
	'home' : car_dir.home,
	'stop' : stop,
	'x+' : video_dir.move_increase_x,
	'x-' : video_dir.move_decrease_x,
	'y+' : video_dir.move_increase_y,
	'y-' : video_dir.move_decrease_y,
	'xy_home' video_dir.home_x_y
}
dispatcher = {'foo': foo, 'bar': bar}
commands = ['forward', 'backward', 'left', 'right', 'stop', 'read cpu_temp', 'home', 'distance', 'x+', 'x-', 'y+', 'y-', 'xy_home']
# busnum 1 since this project uses RaspberryPi 2
busnum = 1
# Empty, so #bind can accept all valid addresses.
HOST = ''
PORT = 21505
INPUT_BUFFER = 1024
ADDR = (HOST, PORT)

server_socket = socket(AF_INET, SOCK_STREAM)
server_socket.bind(ADDR)
# Listen to only one connection at a time
server_socket.listen(1)

video_dir.setup(busnum=busnum)
car_dir.setup(busnum=busnum)
motor.setup(busnum=busnum)
video_dir.home_x_y()
car_dir.home()

while True:
	print 'Waiting for connection...'
	client_socket, addr = server_socket.accept() 
	print '...connected from :', addr
	while True:
		data = ''
		data = client_socket.recv(INPUT_BUFFER)    # Receive data sent from the client. 
		# Analyze the command received and control the car accordingly.
		if not data:
			break
		if data not in commands:
			print 'Command Error! Cannot recognize command: ' + data
		commands[data]()

server_socket.close()