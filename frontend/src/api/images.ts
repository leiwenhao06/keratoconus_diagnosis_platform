import client from './client';
import type { MedicalImage } from '../types';

const BASE = '/api/images';

export const imageApi = {
  register: (data: Omit<MedicalImage, 'imageId' | 'uploadDate' | 'imageData'>) =>
    client.post<any>(BASE, data).then(r => r.data.data as MedicalImage),

  listByPatient: (patientId: string) =>
    client.get<any>(BASE, { params: { patientId } })
      .then(r => r.data.data as MedicalImage[]),

  listByExam: (examId: number) =>
    client.get<any>(BASE, { params: { examId } })
      .then(r => r.data.data as MedicalImage[]),

  delete: (imageId: number) =>
    client.delete<any>(`${BASE}/${imageId}`).then(r => r.data),
};
