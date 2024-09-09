import argparse
import requests
import socket
import json

host = '127.0.0.1'
tcp_port = 3001
udp_port = 3002
http_port = 3000


def send_data_tcp(host, port, data):
    try:
        client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        client_socket.connect((host, port))
        print("tcp连接服务端成功")
    except Exception as e:
        print(f"tcp连接服务端失败: {e}")
        return

    # 假设数据格式为json
    data_json = json.dumps(data)
    client_socket.sendall(data_json.encode())

    # 接收服务端返回的数据
    response = client_socket.recv(1024).decode()
    print("服务器响应：" + response)
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
        print("服务器响应：" + response.decode())
        return response
    except socket.timeout:
        print("接收数据超时")
    finally:
        client_socket.close()


def send_data_http(host, port, data):
    url = f"http://{host}:{port}/submitText"
    headers = {'Content-Type': 'application/json'}
    try:
        response = requests.post(url, json=data, headers=headers)
        if response.status_code == 200:
            print("http服务器响应：" + response.text)
            return response.text
        else:
            print(f"http请求失败: {response.status_code} - {response.text}")
    except Exception as e:
        print(f"http请求失败: {e}")


def op(str1, str2, protocol):
    if protocol == 'tcp':
        send_data_tcp(host, tcp_port, (str1, str2))
    elif protocol == 'udp':
        send_data_udp(host, udp_port, (str1, str2))
    elif protocol == 'http':
        send_data_http(host, http_port, {'text': str1, 'text2': str2})
    else:
        print("不支持的协议")


def file_to_string(file_path):
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        return content
    except FileNotFoundError:
        print(f"文件{file_path}不存在")
    except Exception as e:
        print(f"读取文件{file_path}失败: {e}")


def main():
    parser = argparse.ArgumentParser(description="代码相似度检测工具")
    parser.add_argument("file_path_1", type=str, help="第一个文件路径")
    parser.add_argument("file_path_2", type=str, help="第二个文件路径")
    parser.add_argument("--protocol", type=str, help="通信协议")
    args = parser.parse_args()
    file_1 = file_to_string(args.file_path_1)
    file_2 = file_to_string(args.file_path_2)
    op(file_1, file_2, args.protocol)


if __name__ == "__main__":
    main()
