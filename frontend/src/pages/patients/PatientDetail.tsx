import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Card, Descriptions, Tabs, Button, Space, Spin, message, Popconfirm,
  Tag, Collapse, List, Empty, Tooltip, Upload, Modal, Select, Radio, Image,
} from 'antd';
import {
  EditOutlined, ArrowLeftOutlined, PlusOutlined,
  DeleteOutlined, InboxOutlined,
} from '@ant-design/icons';
import { patientApi } from '../../api/patients';
import { examApi } from '../../api/exams';
import { recordApi } from '../../api/records';
import { imageApi, getImageUrl } from '../../api/images';
import type { Patient, CornealExam, MedicalRecord, MedicalImage } from '../../types';
import {
  GENDER_MAP, EYE_SIDE_MAP, EXAM_TYPE_MAP,
  IMAGE_TYPE_MAP, formatDate, formatDateTime,
} from '../../utils/format';
import type { CollapseProps } from 'antd';

const ACCEPTED_IMAGE_EXTENSIONS = ['.jpg', '.jpeg', '.png', '.gif', '.bmp', '.webp', '.tiff', '.tif'];
const MAX_IMAGE_SIZE = 50 * 1024 * 1024;
type CollapseItem = NonNullable<CollapseProps['items']>[number];

const compactCollapseItems = (items: Array<CollapseItem | null>) =>
  items.filter((item): item is CollapseItem => item !== null);

export default function PatientDetail() {
  const { patientId } = useParams<{ patientId: string }>();
  const navigate = useNavigate();
  const [patient, setPatient] = useState<Patient | null>(null);
  const [exams, setExams] = useState<CornealExam[]>([]);
  const [records, setRecords] = useState<MedicalRecord[]>([]);
  const [images, setImages] = useState<MedicalImage[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!patientId) return;
    Promise.all([
      patientApi.getById(patientId).catch(() => null),
      examApi.listByPatient(patientId).catch(() => [] as CornealExam[]),
      recordApi.listByPatient(patientId).catch(() => [] as MedicalRecord[]),
      imageApi.listByPatient(patientId).catch(() => [] as MedicalImage[]),
    ]).then(([p, e, r, i]) => {
      setPatient(p);
      setExams(e);
      setRecords(r);
      setImages(i);
    }).finally(() => setLoading(false));
  }, [patientId]);

  const handleDeleteExam = async (examId: number) => {
    try {
      await examApi.delete(examId);
      message.success('检查已删除');
      setExams(prev => prev.filter(e => e.examId !== examId));
    } catch {
      // 错误消息已由拦截器处理
    }
  };

  const handleDeleteRecord = async (recordId: number) => {
    try {
      await recordApi.delete(recordId);
      message.success('病历已删除');
      setRecords(prev => prev.filter(r => r.recordId !== recordId));
    } catch {
      // 错误消息已由拦截器处理
    }
  };

  const handleDeleteImage = async (imageId: number) => {
    try {
      await imageApi.delete(imageId);
      message.success('影像已删除');
      setImages(prev => prev.filter(i => i.imageId !== imageId));
    } catch {
      // 错误消息已由拦截器处理
    }
  };

  // ---- 图片上传 ----
  const [uploadModalOpen, setUploadModalOpen] = useState(false);
  const [uploadFile, setUploadFile] = useState<File | null>(null);
  const [uploadType, setUploadType] = useState<string>('Pentacam');
  const [uploadEyeSide, setUploadEyeSide] = useState<string>('both');
  const [uploading, setUploading] = useState(false);

  const handleUpload = async () => {
    if (!uploadFile) {
      message.warning('请先选择要上传的影像文件');
      return;
    }
    if (!patientId) {
      message.error('患者信息异常，无法上传影像');
      return;
    }
    setUploading(true);
    try {
      await imageApi.uploadFile(uploadFile, patientId, uploadType, uploadEyeSide);
      message.success('影像上传成功');
      setUploadModalOpen(false);
      setUploadFile(null);
      const list = await imageApi.listByPatient(patientId);
      setImages(list);
    } catch {
      // 错误消息已由拦截器处理
    } finally {
      setUploading(false);
    }
  };

  const uploadFileList = uploadFile
    ? [{ uid: uploadFile.name, name: uploadFile.name, status: 'done' as const }]
    : [];

  if (loading) {
    return <Spin size="large" style={{ display: 'block', margin: '100px auto' }} />;
  }

  if (!patient) {
    return <Empty description="未找到患者信息" />;
  }

  const basicInfoTab = (
    <Card extra={
      <Button icon={<EditOutlined />} onClick={() => navigate(`/patients/${patientId}/edit`)}>
        编辑
      </Button>
    }>
      <Descriptions bordered column={{ xs: 1, sm: 2, lg: 3 }}>
        <Descriptions.Item label="患者 ID">{patient.patientId}</Descriptions.Item>
        <Descriptions.Item label="姓名">{patient.name}</Descriptions.Item>
        <Descriptions.Item label="性别">{GENDER_MAP[patient.gender || ''] || '-'}</Descriptions.Item>
        <Descriptions.Item label="出生日期">{formatDate(patient.dateOfBirth)}</Descriptions.Item>
        <Descriptions.Item label="年龄">{patient.age ?? '-'}</Descriptions.Item>
        <Descriptions.Item label="身份证号">{patient.idCard || '-'}</Descriptions.Item>
        <Descriptions.Item label="联系方式">{patient.contact || '-'}</Descriptions.Item>
        <Descriptions.Item label="地址">{patient.address || '-'}</Descriptions.Item>
        <Descriptions.Item label="创建时间">{formatDateTime(patient.createdAt)}</Descriptions.Item>
        <Descriptions.Item label="更新时间">{formatDateTime(patient.updatedAt)}</Descriptions.Item>
        <Descriptions.Item label="既往病史" span={3}>{patient.medicalHistory || '-'}</Descriptions.Item>
      </Descriptions>
    </Card>
  );

  const examsTab = (
    <div>
      <div style={{ marginBottom: 16 }}>
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={() => navigate(`/patients/${patientId}/exams/new`)}
        >
          新增检查
        </Button>
      </div>
      {exams.length === 0 ? (
        <Empty description="暂无检查记录" />
      ) : (
        <List
          dataSource={exams}
          renderItem={(exam) => (
            <Card
              size="small"
              style={{ marginBottom: 12 }}
              title={
                <Space>
                  <span>{formatDate(exam.examDate)}</span>
                  <Tag color="blue">{EYE_SIDE_MAP[exam.eyeSide] || exam.eyeSide}</Tag>
                  <Tag>{EXAM_TYPE_MAP[exam.examType] || exam.examType}</Tag>
                  {exam.diagnosis && <Tag color="orange">{exam.diagnosis}</Tag>}
                </Space>
              }
              extra={
                <Popconfirm
                  title="确认删除此检查记录？"
                  onConfirm={() => exam.examId && handleDeleteExam(exam.examId)}
                  okText="确认" cancelText="取消"
                >
                  <Button size="small" danger icon={<DeleteOutlined />}>删除</Button>
                </Popconfirm>
              }
            >
              {/* Key metrics summary */}
              {(exam.topography || exam.biomechanics) && (
                <div style={{ display: 'flex', gap: 16, marginBottom: 12, flexWrap: 'wrap' }}>
                  {exam.topography?.frontKm && (
                    <Tooltip title="前表面平均K值">
                      <Card size="small" style={{ minWidth: 140, textAlign: 'center' }}>
                        <div style={{ color: '#888', fontSize: 12 }}>前表面 Km</div>
                        <div style={{ fontSize: 20, fontWeight: 600 }}>{exam.topography.frontKm} <span style={{ fontSize: 14 }}>D</span></div>
                      </Card>
                    </Tooltip>
                  )}
                  {exam.topography?.thinnestLocatPachyX && (
                    <Tooltip title="最薄点角膜厚度">
                      <Card size="small" style={{ minWidth: 140, textAlign: 'center' }}>
                        <div style={{ color: '#888', fontSize: 12 }}>最薄点厚度</div>
                        <div style={{ fontSize: 20, fontWeight: 600 }}>{exam.topography.thinnestLocatPachyX} <span style={{ fontSize: 14 }}>μm</span></div>
                      </Card>
                    </Tooltip>
                  )}
                  {exam.topography?.iop && (
                    <Tooltip title="眼压">
                      <Card size="small" style={{ minWidth: 140, textAlign: 'center' }}>
                        <div style={{ color: '#888', fontSize: 12 }}>眼压 IOP</div>
                        <div style={{ fontSize: 20, fontWeight: 600 }}>{exam.topography.iop} <span style={{ fontSize: 14 }}>mmHg</span></div>
                      </Card>
                    </Tooltip>
                  )}
                  {exam.biomechanics?.ccbi && (
                    <Tooltip title="角膜生物力学指数（cCBI > 0.5 提示异常风险）">
                      <Card size="small" style={{ minWidth: 140, textAlign: 'center', borderColor: exam.biomechanics.ccbi > 0.5 ? '#fa8c16' : undefined }}>
                        <div style={{ color: '#888', fontSize: 12 }}>cCBI</div>
                        <div style={{ fontSize: 20, fontWeight: 600, color: exam.biomechanics.ccbi > 0.5 ? '#fa8c16' : undefined }}>
                          {exam.biomechanics.ccbi}
                        </div>
                      </Card>
                    </Tooltip>
                  )}
                  {exam.biomechanics?.ctbi && (
                    <Tooltip title="角膜层析生物力学指数（cTBI > 0.29 为圆锥角膜高度怀疑临界值）">
                      <Card size="small" style={{ minWidth: 140, textAlign: 'center', borderColor: exam.biomechanics.ctbi > 0.29 ? '#fa8c16' : undefined }}>
                        <div style={{ color: '#888', fontSize: 12 }}>cTBI</div>
                        <div style={{ fontSize: 20, fontWeight: 600, color: exam.biomechanics.ctbi > 0.29 ? '#fa8c16' : undefined }}>
                          {exam.biomechanics.ctbi}
                        </div>
                      </Card>
                    </Tooltip>
                  )}
                  {exam.biomechanics?.ssi && (
                    <Tooltip title="应力-应变指数 SSI">
                      <Card size="small" style={{ minWidth: 140, textAlign: 'center' }}>
                        <div style={{ color: '#888', fontSize: 12 }}>SSI</div>
                        <div style={{ fontSize: 20, fontWeight: 600 }}>{exam.biomechanics.ssi}</div>
                      </Card>
                    </Tooltip>
                  )}
                </div>
              )}

              {/* Expandable full parameters */}
              <Collapse ghost items={compactCollapseItems([
                exam.topography ? {
                  key: 'topo',
                  label: '角膜地形图完整参数',
                  children: (
                    <Descriptions size="small" bordered column={{ xs: 1, sm: 2, md: 3 }}>
                      <Descriptions.Item label="前表面K1">{exam.topography.frontK1 ?? '-'} D</Descriptions.Item>
                      <Descriptions.Item label="前表面K2">{exam.topography.frontK2 ?? '-'} D</Descriptions.Item>
                      <Descriptions.Item label="前表面Km">{exam.topography.frontKm ?? '-'} D</Descriptions.Item>
                      <Descriptions.Item label="前表面Rf">{exam.topography.frontRf ?? '-'} mm</Descriptions.Item>
                      <Descriptions.Item label="前表面Rs">{exam.topography.frontRs ?? '-'} mm</Descriptions.Item>
                      <Descriptions.Item label="前表面Rm">{exam.topography.frontRm ?? '-'} mm</Descriptions.Item>
                      <Descriptions.Item label="前表面Q值">{exam.topography.frontQVal ?? '-'}</Descriptions.Item>
                      <Descriptions.Item label="前表面散光">{exam.topography.frontAstig ?? '-'} D</Descriptions.Item>
                      <Descriptions.Item label="前表面轴位">{exam.topography.frontAxis ?? '-'}°</Descriptions.Item>
                      <Descriptions.Item label="后表面K1">{exam.topography.backK1 ?? '-'} D</Descriptions.Item>
                      <Descriptions.Item label="后表面K2">{exam.topography.backK2 ?? '-'} D</Descriptions.Item>
                      <Descriptions.Item label="后表面Km">{exam.topography.backKm ?? '-'} D</Descriptions.Item>
                      <Descriptions.Item label="最薄点厚度">{exam.topography.thinnestLocatPachyX ?? '-'} μm</Descriptions.Item>
                      <Descriptions.Item label="角膜容积">{exam.topography.corneaVolume ?? '-'} mm³</Descriptions.Item>
                      <Descriptions.Item label="前房深度">{exam.topography.acDepth ?? '-'} mm</Descriptions.Item>
                      <Descriptions.Item label="眼压">{exam.topography.iop ?? '-'} mmHg</Descriptions.Item>
                      <Descriptions.Item label="瞳孔直径">{exam.topography.pupilDia ?? '-'} mm</Descriptions.Item>
                      <Descriptions.Item label="晶状体厚度">{exam.topography.lensTh ?? '-'} mm</Descriptions.Item>
                    </Descriptions>
                  ),
                } : null,
                exam.biomechanics ? {
                  key: 'biomech',
                  label: '生物力学完整参数',
                  children: (
                    <Descriptions size="small" bordered column={{ xs: 1, sm: 2, md: 3 }}>
                      <Descriptions.Item label="cCBI">
                        <span style={{ color: exam.biomechanics.ccbi && exam.biomechanics.ccbi > 0.5 ? '#fa8c16' : undefined }}>
                          {exam.biomechanics.ccbi ?? '-'}
                        </span>
                      </Descriptions.Item>
                      <Descriptions.Item label="cTBI">
                        <span style={{ color: exam.biomechanics.ctbi && exam.biomechanics.ctbi > 0.29 ? '#fa8c16' : undefined }}>
                          {exam.biomechanics.ctbi ?? '-'}
                        </span>
                      </Descriptions.Item>
                      <Descriptions.Item label="IS-Value">{exam.biomechanics.isValue ?? '-'}</Descriptions.Item>
                      <Descriptions.Item label="SP-A1">{exam.biomechanics.spA1 ?? '-'}</Descriptions.Item>
                      <Descriptions.Item label="Integr Radius">{exam.biomechanics.integrRadius ?? '-'}</Descriptions.Item>
                      <Descriptions.Item label="ARTh">{exam.biomechanics.arth ?? '-'}</Descriptions.Item>
                      <Descriptions.Item label="DA Ratio">{exam.biomechanics.daRatio ?? '-'}</Descriptions.Item>
                      <Descriptions.Item label="SSI">{exam.biomechanics.ssi ?? '-'}</Descriptions.Item>
                    </Descriptions>
                  ),
                } : null,
              ])} />
            </Card>
          )}
        />
      )}
    </div>
  );

  const recordsTab = (
    <div>
      <div style={{ marginBottom: 16 }}>
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={() => navigate(`/patients/${patientId}/records/new`)}
        >
          新增病历
        </Button>
      </div>
      {records.length === 0 ? (
        <Empty description="暂无病历记录" />
      ) : (
        <List
          dataSource={records}
          renderItem={(record) => (
            <Card
              size="small"
              style={{ marginBottom: 12 }}
              title={
                <Space>
                  <span>{formatDate(record.visitDate)}</span>
                  {record.doctorName && <Tag color="green">{record.doctorName}</Tag>}
                  {record.diagnosis && <Tag color="blue">{record.diagnosis}</Tag>}
                </Space>
              }
              extra={
                <Popconfirm
                  title="确认删除此病历？"
                  onConfirm={() => record.recordId && handleDeleteRecord(record.recordId)}
                  okText="确认" cancelText="取消"
                >
                  <Button size="small" danger icon={<DeleteOutlined />}>删除</Button>
                </Popconfirm>
              }
            >
              <Collapse ghost items={[
                {
                  key: 'detail',
                  label: '查看完整病历',
                  children: (
                    <Descriptions size="small" bordered column={1}>
                      <Descriptions.Item label="主诉">{record.chiefComplaint || '-'}</Descriptions.Item>
                      <Descriptions.Item label="现病史">{record.presentIllness || '-'}</Descriptions.Item>
                      <Descriptions.Item label="既往史">{record.pastHistory || '-'}</Descriptions.Item>
                      <Descriptions.Item label="体格检查">{record.physicalExam || '-'}</Descriptions.Item>
                      <Descriptions.Item label="左眼诊断">{record.leftEyeDiagnosis || '-'}</Descriptions.Item>
                      <Descriptions.Item label="右眼诊断">{record.rightEyeDiagnosis || '-'}</Descriptions.Item>
                      <Descriptions.Item label="综合诊断">{record.diagnosis || '-'}</Descriptions.Item>
                      <Descriptions.Item label="治疗方案">{record.treatmentPlan || '-'}</Descriptions.Item>
                      <Descriptions.Item label="医嘱">{record.doctorNotes || '-'}</Descriptions.Item>
                    </Descriptions>
                  ),
                },
              ]} />
            </Card>
          )}
        />
      )}
    </div>
  );

  const imagesTab = (
    <div>
      <div style={{ marginBottom: 16 }}>
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={() => setUploadModalOpen(true)}
        >
          上传影像
        </Button>
      </div>
      {images.length === 0 ? (
        <Empty description="暂无影像记录" />
      ) : (
        <List
          grid={{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 4 }}
          dataSource={images}
          renderItem={(img) => (
            <List.Item>
              <Card
                size="small"
                title={
                  <Space>
                    <Tag color="purple">{IMAGE_TYPE_MAP[img.imageType] || img.imageType}</Tag>
                    {img.eyeSide && <Tag>{EYE_SIDE_MAP[img.eyeSide]}</Tag>}
                  </Space>
                }
                extra={
                  <Popconfirm
                    title="确认删除此影像？"
                    onConfirm={() => img.imageId && handleDeleteImage(img.imageId)}
                    okText="确认" cancelText="取消"
                  >
                    <Button size="small" danger icon={<DeleteOutlined />} />
                  </Popconfirm>
                }
              >
                {img.imagePath ? (
                  <Image
                    src={getImageUrl(img.imagePath)}
                    alt={img.fileName || '医学影像'}
                    style={{ maxHeight: 200, objectFit: 'cover' }}
                    fallback="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII="
                  />
                ) : (
                  <div style={{
                    height: 120, display: 'flex', alignItems: 'center',
                    justifyContent: 'center', color: '#bbb', background: '#fafafa',
                  }}>
                    暂无预览
                  </div>
                )}
                <div style={{ fontSize: 12, color: '#888', marginTop: 8 }}>
                  <div>文件名：{img.fileName || '-'}</div>
                  <div>大小：{img.fileSize != null ? `${(img.fileSize / 1024).toFixed(1)} KB` : '-'}</div>
                  <div>上传时间：{formatDate(img.uploadDate)}</div>
                </div>
              </Card>
            </List.Item>
          )}
        />
      )}

      {/* 上传弹窗 */}
      <Modal
        title="上传医学影像"
        open={uploadModalOpen}
        onOk={handleUpload}
        onCancel={() => { setUploadModalOpen(false); setUploadFile(null); }}
        confirmLoading={uploading}
        okText="上传"
        cancelText="取消"
      >
        <Space direction="vertical" style={{ width: '100%' }} size="middle">
          <Upload.Dragger
            accept={ACCEPTED_IMAGE_EXTENSIONS.join(',')}
            maxCount={1}
            fileList={uploadFileList}
            beforeUpload={(file) => {
              const lowerName = file.name.toLowerCase();
              const extension = lowerName.includes('.') ? lowerName.slice(lowerName.lastIndexOf('.')) : '';
              if (!ACCEPTED_IMAGE_EXTENSIONS.includes(extension)) {
                message.error('不支持的文件格式，请上传 PNG、JPG、BMP、GIF、WebP 或 TIFF 图片');
                return Upload.LIST_IGNORE;
              }
              if (file.size > MAX_IMAGE_SIZE) {
                message.error('图片不能超过 50MB');
                return Upload.LIST_IGNORE;
              }
              setUploadFile(file);
              return false; // 阻止自动上传
            }}
            onRemove={() => setUploadFile(null)}
          >
            <p className="ant-upload-drag-icon">
              <InboxOutlined />
            </p>
            <p className="ant-upload-text">点击或拖拽图片文件到此区域</p>
            <p className="ant-upload-hint">支持 PNG / JPG / BMP / GIF / WebP 格式</p>
          </Upload.Dragger>

          <div>
            <span style={{ marginRight: 8 }}>影像类型：</span>
            <Select
              value={uploadType}
              onChange={setUploadType}
              style={{ width: 200 }}
              options={[
                { label: 'Pentacam 角膜地形图', value: 'Pentacam' },
                { label: 'Corvis TBI 生物力学', value: 'Corvis_TBI' },
                { label: 'Corvis VSR', value: 'Corvis_VSR' },
                { label: '眼底照', value: 'Fundus' },
                { label: '裂隙灯', value: 'Slit_Lamp' },
                { label: 'OCT 扫描', value: 'OCT' },
                { label: '其他', value: 'Other' },
              ]}
            />
          </div>

          <div>
            <span style={{ marginRight: 8 }}>眼别：</span>
            <Radio.Group value={uploadEyeSide} onChange={e => setUploadEyeSide(e.target.value)}>
              <Radio.Button value="left">左眼</Radio.Button>
              <Radio.Button value="right">右眼</Radio.Button>
              <Radio.Button value="both">双眼</Radio.Button>
            </Radio.Group>
          </div>
        </Space>
      </Modal>
    </div>
  );

  const tabItems = [
    {
      key: 'basic',
      label: '基本信息',
      children: basicInfoTab,
    },
    {
      key: 'exams',
      label: `角膜检查 (${exams.length})`,
      children: examsTab,
    },
    {
      key: 'records',
      label: `病历记录 (${records.length})`,
      children: recordsTab,
    },
    {
      key: 'images',
      label: `医学影像 (${images.length})`,
      children: imagesTab,
    },
  ];

  return (
    <div>
      <div style={{ marginBottom: 16, display: 'flex', alignItems: 'center', gap: 16 }}>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/patients')}>
          返回列表
        </Button>
        <h2 style={{ margin: 0 }}>{patient.name} - {patient.patientId}</h2>
      </div>
      <Tabs items={tabItems} />
    </div>
  );
}
