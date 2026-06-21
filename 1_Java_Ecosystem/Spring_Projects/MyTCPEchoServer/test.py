import socket
import time
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect(('127.0.0.1', 8080))
s.shutdown(socket.SHUT_WR) # Sends FIN
start_time = time.time()
while True:
    data = s.recv(1024)
    if not data:
        break
    print("Received:", data.decode('utf-8'))
print("Client closed after %.2f seconds" % (time.time() - start_time))
s.close()
