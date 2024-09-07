import argparse
import requests
import socket
import json

host = '127.0.0.1'
port = 3001

def send_data_tcp(host, port, data):
    try:
        client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        client_socket.connect((host, port))
        print("连接服务端成功")
    except Exception as e:
        print(f"连接服务端失败: {e}")
        return

    # 假设数据格式为json
    data_json = json.dumps(data)
    client_socket.sendall(data_json.encode())

    # 接收服务端返回的数据
    response = client_socket.recv(1024).decode()
    print("服务器响应：", response)
    client_socket.close()
    return response

def send_data_udp(host, port, data):
    try:
        client_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        print("创建UDP套接字成功")
    except Exception as e:
        print(f"创建UDP套接字失败: {e}")
        return

    # 假设数据格式为json
    data_json = json.dumps(data)
    client_socket.sendto(data_json.encode(), (host, port))
    print("发送数据成功")

    # 接收服务端返回的数据
    try:
        response, server_address = client_socket.recvfrom(1024)
        print("服务器响应：", response.decode())
        return response
    except socket.timeout:
        print("接收数据超时")
    finally:
        client_socket.close()

data = {
    "params": {
        "hello": "world"
    }
}

response = send_data_tcp(host, port, data)
