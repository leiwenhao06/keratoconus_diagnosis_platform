import dayjs from 'dayjs';

export const GENDER_MAP: Record<string, string> = {
  Male: '男', Female: '女', Other: '其他',
};

export const EYE_SIDE_MAP: Record<string, string> = {
  left: '左眼', right: '右眼', both: '双眼',
};

export const EXAM_TYPE_MAP: Record<string, string> = {
  Pentacam: 'Pentacam 角膜地形图',
  Corvis_TBI: 'Corvis TBI 生物力学',
  Corvis_VSR: 'Corvis VSR',
  Other: '其他',
};

export const IMAGE_TYPE_MAP: Record<string, string> = {
  Pentacam: 'Pentacam', Corvis_TBI: 'Corvis TBI', Corvis_VSR: 'Corvis VSR',
  Fundus: '眼底照', Slit_Lamp: '裂隙灯', OCT: 'OCT 扫描', Other: '其他',
};

export const formatDate = (s?: string) => s ? s.substring(0, 10) : '-';

export const formatDateTime = (s?: string) =>
  s ? dayjs(s, 'YYYY-MM-DD HH:mm:ss').format('YYYY年MM月DD日 HH:mm') : '-';

export const datePickerToStr = (d: dayjs.Dayjs | null) =>
  d ? d.format('YYYY-MM-DD') : undefined;
