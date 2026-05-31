import { useState } from 'react';
import {
  Form, Input, Select, DatePicker, InputNumber, Button, Card,
  Steps, Radio, Checkbox, Space, message, Tooltip,
} from 'antd';
import { useNavigate, useParams } from 'react-router-dom';
import { ArrowLeftOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import { examApi } from '../../api/exams';
import { datePickerToStr } from '../../utils/format';

const EXAM_TYPE_OPTIONS = [
  { label: 'Pentacam 角膜地形图', value: 'Pentacam' },
  { label: 'Corvis TBI 生物力学', value: 'Corvis_TBI' },
  { label: 'Corvis VSR', value: 'Corvis_VSR' },
  { label: '其他', value: 'Other' },
];

export default function ExamForm() {
  const { patientId } = useParams<{ patientId: string }>();
  const navigate = useNavigate();
  const [currentStep, setCurrentStep] = useState(0);
  const [includeTopography, setIncludeTopography] = useState(false);
  const [includeBiomechanics, setIncludeBiomechanics] = useState(false);
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  const handleSubmit = async () => {
    const values = await form.validateFields();
    const payload: any = {
      patientId,
      examDate: datePickerToStr(values.examDate),
      eyeSide: values.eyeSide,
      examType: values.examType,
      diagnosis: values.diagnosis || undefined,
    };

    if (includeTopography) {
      payload.topography = {};
      const topoFields = [
        'frontRf','frontRs','frontRm','frontK1','frontK2','frontKm',
        'frontQVal','frontRper','frontRmin','frontAxis','frontAstig',
        'backRf','backRs','backRm','backK1','backK2','backKm',
        'backQVal','backRper','backRmin','backAxis','backAstig',
        'pupilCenterPachyX','pupilCenterPachyY','pachyApexX','pachyApexY',
        'thinnestLocatPachyX','thinnestLocatPachyY','kMaxPachyX','kMaxPachyY',
        'corneaVolume','chamberVolume','angle','acDepth','pupilDia','iop','lensTh',
      ];
      topoFields.forEach(f => {
        if (values[f] != null) payload.topography[f] = values[f];
      });
    }

    if (includeBiomechanics) {
      payload.biomechanics = {};
      const bioFields = ['ccbi','ctbi','isValue','spA1','integrRadius','arth','daRatio','ssi'];
      bioFields.forEach(f => {
        if (values[f] != null) payload.biomechanics[f] = values[f];
      });
    }

    setLoading(true);
    try {
      await examApi.create(payload);
      message.success('检查记录已创建');
      navigate(`/patients/${patientId}`);
    } finally {
      setLoading(false);
    }
  };

  const steps = [
    {
      title: '基本信息',
      content: (
        <>
          <Form.Item name="patientId" label="患者 ID">
            <Input disabled value={patientId} />
          </Form.Item>
          <Form.Item
            name="examDate"
            label="检查日期"
            rules={[{ required: true, message: '请选择检查日期' }]}
          >
            <DatePicker style={{ width: '100%' }} format="YYYY-MM-DD" />
          </Form.Item>
          <Form.Item
            name="eyeSide"
            label="眼别"
            rules={[{ required: true, message: '请选择眼别' }]}
          >
            <Radio.Group>
              <Radio.Button value="left">左眼</Radio.Button>
              <Radio.Button value="right">右眼</Radio.Button>
              <Radio.Button value="both">双眼</Radio.Button>
            </Radio.Group>
          </Form.Item>
          <Form.Item
            name="examType"
            label="检查类型"
            rules={[{ required: true, message: '请选择检查类型' }]}
          >
            <Select options={EXAM_TYPE_OPTIONS} placeholder="选择检查类型" />
          </Form.Item>
          <Form.Item name="diagnosis" label="诊断意见">
            <Input.TextArea rows={2} placeholder="初步诊断意见（可选）" />
          </Form.Item>
        </>
      ),
    },
    {
      title: '角膜地形图',
      content: (
        <>
          <Form.Item label="包含地形图数据">
            <Checkbox
              checked={includeTopography}
              onChange={e => setIncludeTopography(e.target.checked)}
            >
              {includeTopography ? '已勾选 — 将提交地形图参数' : '勾选以输入地形图参数'}
            </Checkbox>
          </Form.Item>
          {includeTopography && (
            <>
              <Card size="small" title="前表面参数" style={{ marginBottom: 12 }}>
                <Space wrap>
                  <Form.Item name="frontK1" label="K1" style={{ width: 140 }}>
                    <InputNumber step={0.01} addonAfter="D" placeholder="平坦K" />
                  </Form.Item>
                  <Form.Item name="frontK2" label="K2" style={{ width: 140 }}>
                    <InputNumber step={0.01} addonAfter="D" placeholder="陡峭K" />
                  </Form.Item>
                  <Form.Item name="frontKm" label="Km" style={{ width: 140 }}>
                    <InputNumber step={0.01} addonAfter="D" placeholder="平均K" />
                  </Form.Item>
                  <Form.Item name="frontRf" label="Rf" style={{ width: 140 }}>
                    <InputNumber step={0.01} addonAfter="mm" />
                  </Form.Item>
                  <Form.Item name="frontRs" label="Rs" style={{ width: 140 }}>
                    <InputNumber step={0.01} addonAfter="mm" />
                  </Form.Item>
                  <Form.Item name="frontRm" label="Rm" style={{ width: 140 }}>
                    <InputNumber step={0.01} addonAfter="mm" />
                  </Form.Item>
                  <Form.Item name="frontAstig" label="Astig" style={{ width: 140 }}>
                    <InputNumber step={0.01} addonAfter="D" />
                  </Form.Item>
                  <Form.Item name="frontAxis" label="Axis" style={{ width: 140 }}>
                    <InputNumber step={0.1} addonAfter="°" />
                  </Form.Item>
                  <Form.Item name="frontQVal" label="Q值" style={{ width: 140 }}>
                    <InputNumber step={0.001} />
                  </Form.Item>
                </Space>
              </Card>
              <Card size="small" title="后表面参数" style={{ marginBottom: 12 }}>
                <Space wrap>
                  <Form.Item name="backK1" label="K1" style={{ width: 140 }}>
                    <InputNumber step={0.01} addonAfter="D" />
                  </Form.Item>
                  <Form.Item name="backK2" label="K2" style={{ width: 140 }}>
                    <InputNumber step={0.01} addonAfter="D" />
                  </Form.Item>
                  <Form.Item name="backKm" label="Km" style={{ width: 140 }}>
                    <InputNumber step={0.01} addonAfter="D" />
                  </Form.Item>
                </Space>
              </Card>
              <Card size="small" title="厚度与眼前节" style={{ marginBottom: 12 }}>
                <Space wrap>
                  <Form.Item name="thinnestLocatPachyX" label="最薄点厚度" style={{ width: 160 }}>
                    <InputNumber step={0.1} addonAfter="μm" />
                  </Form.Item>
                  <Form.Item name="corneaVolume" label="角膜容积" style={{ width: 150 }}>
                    <InputNumber step={0.01} addonAfter="mm³" />
                  </Form.Item>
                  <Form.Item name="acDepth" label="前房深度" style={{ width: 150 }}>
                    <InputNumber step={0.01} addonAfter="mm" />
                  </Form.Item>
                  <Form.Item name="iop" label="眼压" style={{ width: 140 }}>
                    <InputNumber step={0.1} addonAfter="mmHg" />
                  </Form.Item>
                  <Form.Item name="pupilDia" label="瞳孔直径" style={{ width: 150 }}>
                    <InputNumber step={0.01} addonAfter="mm" />
                  </Form.Item>
                  <Form.Item name="lensTh" label="晶状体厚度" style={{ width: 150 }}>
                    <InputNumber step={0.01} addonAfter="mm" />
                  </Form.Item>
                </Space>
              </Card>
            </>
          )}
        </>
      ),
    },
    {
      title: '生物力学',
      content: (
        <>
          <Form.Item label="包含生物力学数据">
            <Checkbox
              checked={includeBiomechanics}
              onChange={e => setIncludeBiomechanics(e.target.checked)}
            >
              {includeBiomechanics ? '已勾选 — 将提交生物力学参数' : '勾选以输入生物力学参数'}
            </Checkbox>
          </Form.Item>
          {includeBiomechanics && (
            <Card size="small" title="Corvis ST 生物力学参数">
              <Space wrap>
                <Form.Item name="ccbi" label="cCBI" style={{ width: 140 }}>
                  <InputNumber step={0.001} />
                  <Tooltip title="cCBI > 0.5 提示生物力学异常风险">
                    <QuestionCircleOutlined style={{ color: '#888', marginLeft: 4 }} />
                  </Tooltip>
                </Form.Item>
                <Form.Item name="ctbi" label="cTBI" style={{ width: 140 }}>
                  <InputNumber step={0.001} />
                  <Tooltip title="cTBI > 0.29 为圆锥角膜高度怀疑临界值">
                    <QuestionCircleOutlined style={{ color: '#888', marginLeft: 4 }} />
                  </Tooltip>
                </Form.Item>
                <Form.Item name="isValue" label="IS-Value" style={{ width: 140 }}>
                  <InputNumber step={0.001} />
                </Form.Item>
                <Form.Item name="spA1" label="SP-A1" style={{ width: 140 }}>
                  <InputNumber step={0.001} />
                </Form.Item>
                <Form.Item name="integrRadius" label="Integr Radius" style={{ width: 160 }}>
                  <InputNumber step={0.001} addonAfter="mm" />
                </Form.Item>
                <Form.Item name="arth" label="ARTh" style={{ width: 140 }}>
                  <InputNumber step={0.001} />
                </Form.Item>
                <Form.Item name="daRatio" label="DA Ratio" style={{ width: 150 }}>
                  <InputNumber step={0.001} />
                </Form.Item>
                <Form.Item name="ssi" label="SSI" style={{ width: 140 }}>
                  <InputNumber step={0.001} />
                </Form.Item>
              </Space>
            </Card>
          )}
        </>
      ),
    },
  ];

  return (
    <Card
      title="新增角膜检查"
      extra={
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate(`/patients/${patientId}`)}>
          返回
        </Button>
      }
    >
      <Steps current={currentStep} items={steps.map(s => ({ title: s.title }))} style={{ marginBottom: 24 }} />
      <Form form={form} layout="vertical" style={{ maxWidth: 800 }} initialValues={{ patientId }}>
        {steps[currentStep].content}
      </Form>
      <div style={{ marginTop: 24 }}>
        <Space>
          {currentStep > 0 && (
            <Button onClick={() => setCurrentStep(currentStep - 1)}>上一步</Button>
          )}
          {currentStep < steps.length - 1 && (
            <Button type="primary" onClick={() => setCurrentStep(currentStep + 1)}>下一步</Button>
          )}
          {currentStep === steps.length - 1 && (
            <Button type="primary" loading={loading} onClick={handleSubmit}>提交</Button>
          )}
          <Button onClick={() => navigate(`/patients/${patientId}`)}>取消</Button>
        </Space>
      </div>
    </Card>
  );
}
