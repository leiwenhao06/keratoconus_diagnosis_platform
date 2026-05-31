import client from './client';
import type { MedicalRecord } from '../types';

const BASE = '/api/records';

export const recordApi = {
  create: (data: Omit<MedicalRecord, 'recordId' | 'createdAt' | 'updatedAt'>) =>
    client.post<any>(BASE, data).then(r => r.data.data as MedicalRecord),

  listByPatient: (patientId: string) =>
    client.get<any>(BASE, { params: { patientId } })
      .then(r => r.data.data as MedicalRecord[]),

  getById: (recordId: number) =>
    client.get<any>(`${BASE}/${recordId}`).then(r => r.data.data as MedicalRecord),

  update: (recordId: number, data: Partial<MedicalRecord>) =>
    client.put<any>(`${BASE}/${recordId}`, { ...data, recordId })
      .then(r => r.data.data as MedicalRecord),

  delete: (recordId: number) =>
    client.delete<any>(`${BASE}/${recordId}`).then(r => r.data),
};
