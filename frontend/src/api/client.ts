import axios from 'axios';
import { message } from 'antd';
import type { ApiResponse } from '../types';

export const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? '').replace(/\/$/, '');

const client = axios.create({
  baseURL: API_BASE_URL,
  timeout: 60000,
});

// 为非 FormData 的请求统一设置 JSON Content-Type
client.interceptors.request.use((config) => {
  if (!(config.data instanceof FormData)) {
    config.headers['Content-Type'] = 'application/json';
  }
  return config;
});

client.interceptors.response.use(
  (response) => {
    const data = response?.data as ApiResponse<unknown> | undefined;
    if (!data) {
      message.error('服务器返回数据异常');
      return Promise.reject(new Error('服务器返回数据异常'));
    }
    if (data.code !== 200) {
      message.error(data.message || '操作失败');
      return Promise.reject(new Error(data.message || '操作失败'));
    }
    return response;
  },
  (error) => {
    if (error.code === 'ECONNABORTED' || error.message?.includes('timeout')) {
      message.error('请求超时，请检查网络后重试');
    } else if (error.message === 'Network Error' || !error.response) {
      message.error('网络连接失败，请检查后端服务是否启动');
    } else if (error.response?.status === 400) {
      const msg = error.response?.data?.message || '请求参数错误，请检查输入';
      message.error(msg);
    } else if (error.response?.status === 401) {
      message.error('未授权，请重新登录');
    } else if (error.response?.status === 403) {
      message.error('没有权限执行此操作');
    } else if (error.response?.status === 404) {
      message.error('请求的资源不存在');
    } else if (error.response?.status === 409) {
      const msg = error.response?.data?.message || '数据冲突，请刷新后重试';
      message.error(msg);
    } else if (error.response?.status === 500) {
      message.error('服务器内部错误，请稍后重试');
    } else if (error.response?.status) {
      message.error(`请求失败 (${error.response.status})，请稍后重试`);
    } else {
      message.error('网络连接失败，请检查后端服务是否启动');
    }
    return Promise.reject(error);
  }
);

export default client;
