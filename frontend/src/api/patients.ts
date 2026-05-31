import client from './client';
import type { Patient, PatientCreateRequest } from '../types';

const BASE = '/api/patients';

export const patientApi = {
  create: (data: PatientCreateRequest) =>
    client.post<any>(BASE, data).then(r => r.data.data as Patient),

  list: (name?: string) =>
    client.get<any>(BASE, { params: name ? { name } : {} })
      .then(r => r.data.data as Patient[]),

  getById: (patientId: string) =>
    client.get<any>(`${BASE}/${patientId}`).then(r => r.data.data as Patient),

  update: (patientId: string, data: Partial<Patient>) =>
    client.put<any>(`${BASE}/${patientId}`, { ...data, patientId })
      .then(r => r.data.data as Patient),

  delete: (patientId: string) =>
    client.delete<any>(`${BASE}/${patientId}`).then(r => r.data),
};
