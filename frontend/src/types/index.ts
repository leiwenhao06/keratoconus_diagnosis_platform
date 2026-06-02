// ========== 通用响应 ==========
export interface ApiResponse<T> {
  code: number;
  message: string;
  data?: T;
}

// ========== 患者 ==========
export type Gender = 'Male' | 'Female' | 'Other';

export interface Patient {
  patientId: string;
  name: string;
  gender?: Gender;
  dateOfBirth?: string;
  age?: number;
  idCard?: string;
  contact?: string;
  address?: string;
  medicalHistory?: string;
  createdAt?: string;
  updatedAt?: string;
}

export type PatientCreateRequest = Omit<Patient, 'createdAt' | 'updatedAt'>;

// ========== 角膜检查 ==========
export type EyeSide = 'left' | 'right' | 'both';
export type ExamType = 'Pentacam' | 'Corvis_TBI' | 'Corvis_VSR' | 'Other';

export interface CornealTopography {
  id?: number;
  examId?: number;
  frontRf?: number; frontRs?: number; frontRm?: number;
  frontK1?: number; frontK2?: number; frontKm?: number;
  frontQVal?: number; frontRper?: number; frontRmin?: number;
  frontAxis?: number; frontAstig?: number;
  backRf?: number; backRs?: number; backRm?: number;
  backK1?: number; backK2?: number; backKm?: number;
  backQVal?: number; backRper?: number; backRmin?: number;
  backAxis?: number; backAstig?: number;
  pupilCenterPachyX?: number; pupilCenterPachyY?: number;
  pachyApexX?: number; pachyApexY?: number;
  thinnestLocatPachyX?: number; thinnestLocatPachyY?: number;
  kMaxPachyX?: number; kMaxPachyY?: number;
  corneaVolume?: number; chamberVolume?: number;
  angle?: number; acDepth?: number;
  pupilDia?: number; iop?: number; lensTh?: number;
}

export interface BiomechanicalParams {
  id?: number;
  examId?: number;
  ccbi?: number;
  ctbi?: number;
  isValue?: number;
  spA1?: number;
  integrRadius?: number;
  arth?: number;
  daRatio?: number;
  ssi?: number;
}

export interface CornealExam {
  examId?: number;
  patientId: string;
  examDate: string;
  eyeSide: EyeSide;
  examType: ExamType;
  diagnosis?: string;
  createdAt?: string;
  topography?: CornealTopography;
  biomechanics?: BiomechanicalParams;
}

// ========== 医学影像 ==========
export type ImageType = 'Pentacam' | 'Corvis_TBI' | 'Corvis_VSR' | 'Fundus' | 'Slit_Lamp' | 'OCT' | 'Other';

export interface MedicalImage {
  imageId?: number;
  patientId: string;
  examId?: number;
  imageType: ImageType;
  eyeSide?: EyeSide;
  imagePath?: string;  // UUID 文件名，如 "a1b2c3d4-xxxx.png"
  imageData?: string;
  fileName?: string;
  fileSize?: number;
  uploadDate?: string;
}

export interface UploadResult {
  uuid: string;
  imageId: string;
  fileName: string;
}

// ========== 病历 ==========
export interface MedicalRecord {
  recordId?: number;
  patientId: string;
  visitDate: string;
  chiefComplaint?: string;
  presentIllness?: string;
  pastHistory?: string;
  physicalExam?: string;
  diagnosis?: string;
  leftEyeDiagnosis?: string;
  rightEyeDiagnosis?: string;
  treatmentPlan?: string;
  doctorNotes?: string;
  doctorName?: string;
  createdAt?: string;
  updatedAt?: string;
}
