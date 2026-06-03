import client from './client';
import type { MedicalImage, UploadResult } from '../types';

const BASE = '/api/images';

/** 图片访问 URL（直接用于 <img src>） */
export const getImageUrl = (uuid: string) =>
  `/api/images/view/${uuid}`;

export const imageApi = {
  /**
   * 文件上传：FormData multipart 方式
   * 注意：必须让浏览器自动生成 Content-Type（含 boundary），不能手动设置
   */
  uploadFile: (
    file: File,
    patientId: string,
    imageType: string,
    eyeSide?: string,
    examId?: number,
  ) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('patientId', patientId);
    formData.append('imageType', imageType);
    if (eyeSide) formData.append('eyeSide', eyeSide);
    if (examId != null) formData.append('examId', String(examId));

    return client.post<any>('/api/upload', formData, {
      headers: { 'Content-Type': undefined },
    }).then(r => r?.data?.data as UploadResult);
  },

  register: (data: Omit<MedicalImage, 'imageId' | 'uploadDate' | 'imageData'>) =>
    client.post<any>(BASE, data).then(r => r?.data?.data as MedicalImage),

  listByPatient: (patientId: string) =>
    client.get<any>(BASE, { params: { patientId } })
      .then(r => r?.data?.data as MedicalImage[]),

  listByExam: (examId: number) =>
    client.get<any>(BASE, { params: { examId } })
      .then(r => r?.data?.data as MedicalImage[]),

  delete: (imageId: number) =>
    client.delete<any>(`${BASE}/${imageId}`).then(r => r?.data),
};
