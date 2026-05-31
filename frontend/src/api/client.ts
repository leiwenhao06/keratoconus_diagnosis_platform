import axios from 'axios';
import { message } from 'antd';
import type { ApiResponse } from '../types';

const client = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
});

client.interceptors.response.use(
  (response) => {
    const data: ApiResponse<unknown> = response.data;
    if (data.code !== 200) {
      message.error(data.message || '操作失败');
      return Promise.reject(new Error(data.message));
    }
    return response;
  },
  (error) => {
    if (error.response?.status === 400) {
      message.error(error.response.data?.message || '请求参数错误');
    } else if (error.response?.status === 500) {
      message.error('服务器内部错误，请稍后重试');
    } else {
      message.error('网络连接失败，请检查后端服务是否启动');
    }
    return Promise.reject(error);
  }
);

export default client;
