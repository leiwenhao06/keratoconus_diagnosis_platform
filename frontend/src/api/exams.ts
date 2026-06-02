import client from './client';
import type { CornealExam } from '../types';

const BASE = '/api/exams';

export const examApi = {
  create: (data: Omit<CornealExam, 'examId' | 'createdAt'>) =>
    client.post<any>(BASE, data).then(r => r?.data?.data as CornealExam),

  listByPatient: (patientId: string) =>
    client.get<any>(BASE, { params: { patientId } })
      .then(r => r?.data?.data as CornealExam[]),

  getById: (examId: number) =>
    client.get<any>(`${BASE}/${examId}`).then(r => r?.data?.data as CornealExam),

  update: (examId: number, data: Partial<CornealExam>) =>
    client.put<any>(`${BASE}/${examId}`, { ...data, examId })
      .then(r => r?.data?.data as CornealExam),

  delete: (examId: number) =>
    client.delete<any>(`${BASE}/${examId}`).then(r => r?.data),
};
