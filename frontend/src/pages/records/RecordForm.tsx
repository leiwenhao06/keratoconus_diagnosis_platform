import { useState } from 'react';
import {
  Form, Input, DatePicker, Button, Card, Collapse, Space, message,
} from 'antd';
import { useNavigate, useParams } from 'react-router-dom';
import { ArrowLeftOutlined } from '@ant-design/icons';
import { recordApi } from '../../api/records';
import { datePickerToStr } from '../../utils/format';

const { TextArea } = Input;

export default function RecordForm() {
  const { patientId } = useParams<{ patientId: string }>();
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  const handleSubmit = async () => {
    setLoading(true);
    try {
      const values = await form.validateFields();
      const payload = {
        ...values,
        patientId,
        visitDate: datePickerToStr(values.visitDate),
      };

      await recordApi.create(payload);
      message.success('病历已创建');
      navigate(`/patients/${patientId}`);
    } catch (err: any) {
      if (err?.errorFields) {
        // Ant Design form validation error - already shown inline
        return;
      }
      // 其他错误已由拦截器处理
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card
      title="新增病历"
      extra={
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate(`/patients/${patientId}`)}>
          返回
        </Button>
      }
    >
      <Form form={form} layout="vertical" style={{ maxWidth: 800 }} onFinish={handleSubmit}>
        <Collapse
          defaultActiveKey={['basic', 'history', 'diagnosis']}
          items={[
            {
              key: 'basic',
              label: '基本信息',
              children: (
                <>
                  <Form.Item name="patientId" label="患者 ID">
                    <Input disabled value={patientId} />
                  </Form.Item>
                  <Form.Item
                    name="visitDate"
                    label="就诊日期"
                    rules={[{ required: true, message: '请选择就诊日期' }]}
                  >
                    <DatePicker style={{ width: '100%' }} format="YYYY-MM-DD" />
                  </Form.Item>
                  <Form.Item name="doctorName" label="医生姓名">
                    <Input placeholder="接诊医生" />
                  </Form.Item>
                </>
              ),
            },
            {
              key: 'history',
              label: '问诊信息',
              children: (
                <>
                  <Form.Item name="chiefComplaint" label="主诉">
                    <TextArea rows={2} autoSize={{ minRows: 2, maxRows: 6 }} placeholder="患者主要症状与诉求" />
                  </Form.Item>
                  <Form.Item name="presentIllness" label="现病史">
                    <TextArea rows={3} autoSize={{ minRows: 2, maxRows: 6 }} placeholder="现病史详细描述" />
                  </Form.Item>
                  <Form.Item name="pastHistory" label="既往史">
                    <TextArea rows={2} autoSize={{ minRows: 2, maxRows: 6 }} placeholder="既往病史记录" />
                  </Form.Item>
                  <Form.Item name="physicalExam" label="体格检查">
                    <TextArea rows={2} autoSize={{ minRows: 2, maxRows: 6 }} placeholder="体格检查结果" />
                  </Form.Item>
                </>
              ),
            },
            {
              key: 'diagnosis',
              label: '诊断与治疗',
              children: (
                <>
                  <Form.Item name="leftEyeDiagnosis" label="左眼诊断">
                    <Input placeholder="左眼诊断结论" />
                  </Form.Item>
                  <Form.Item name="rightEyeDiagnosis" label="右眼诊断">
                    <Input placeholder="右眼诊断结论" />
                  </Form.Item>
                  <Form.Item name="diagnosis" label="综合诊断意见">
                    <TextArea rows={2} autoSize={{ minRows: 2, maxRows: 6 }} placeholder="综合诊断" />
                  </Form.Item>
                  <Form.Item name="treatmentPlan" label="治疗方案">
                    <TextArea rows={3} autoSize={{ minRows: 2, maxRows: 6 }} placeholder="治疗方案详情" />
                  </Form.Item>
                  <Form.Item name="doctorNotes" label="医嘱/备注">
                    <TextArea rows={2} autoSize={{ minRows: 2, maxRows: 6 }} placeholder="医生备注" />
                  </Form.Item>
                </>
              ),
            },
          ]}
        />

        <div style={{ marginTop: 24 }}>
          <Space>
            <Button type="primary" htmlType="submit" loading={loading}>
              创建病历
            </Button>
            <Button onClick={() => navigate(`/patients/${patientId}`)}>取消</Button>
          </Space>
        </div>
      </Form>
    </Card>
  );
}
