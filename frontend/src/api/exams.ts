import client from './client';
import type { ApiResponse, CornealExam } from '../types';

const BASE = '/api/exams';

export const examApi = {
  create: (data: Omit<CornealExam, 'examId' | 'createdAt'>) =>
    client.post<ApiResponse<CornealExam>>(BASE, data).then(r => r.data.data as CornealExam),

  listByPatient: (patientId: string) =>
    client.get<ApiResponse<CornealExam[]>>(BASE, { params: { patientId } })
      .then(r => r.data.data ?? []),

  getById: (examId: number) =>
    client.get<ApiResponse<CornealExam>>(`${BASE}/${examId}`)
      .then(r => r.data.data as CornealExam),

  update: (examId: number, data: Partial<CornealExam>) =>
    client.put<ApiResponse<CornealExam>>(`${BASE}/${examId}`, { ...data, examId })
      .then(r => r.data.data as CornealExam),

  delete: (examId: number) =>
    client.delete<ApiResponse<void>>(`${BASE}/${examId}`).then(r => r.data),
};
