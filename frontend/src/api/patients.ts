import client from './client';
import type { ApiResponse, Patient, PatientCreateRequest } from '../types';

const BASE = '/api/patients';

export const patientApi = {
  create: (data: PatientCreateRequest) =>
    client.post<ApiResponse<Patient>>(BASE, data).then(r => r.data.data as Patient),

  list: (name?: string) =>
    client.get<ApiResponse<Patient[]>>(BASE, { params: name ? { name } : {} })
      .then(r => r.data.data ?? []),

  getById: (patientId: string) =>
    client.get<ApiResponse<Patient>>(`${BASE}/${patientId}`).then(r => r.data.data as Patient),

  update: (patientId: string, data: Partial<Patient>) =>
    client.put<ApiResponse<Patient>>(`${BASE}/${patientId}`, { ...data, patientId })
      .then(r => r.data.data as Patient),

  delete: (patientId: string) =>
    client.delete<ApiResponse<void>>(`${BASE}/${patientId}`).then(r => r.data),
};
