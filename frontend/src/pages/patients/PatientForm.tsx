import { useEffect, useState } from 'react';
import { Form, Input, Select, DatePicker, InputNumber, Button, Card, message, Space } from 'antd';
import { useNavigate, useParams } from 'react-router-dom';
import { ArrowLeftOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import { patientApi } from '../../api/patients';
import { datePickerToStr } from '../../utils/format';

export default function PatientForm() {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { patientId } = useParams<{ patientId: string }>();
  const isEdit = !!patientId;

  useEffect(() => {
    if (isEdit) {
      patientApi.getById(patientId).then(patient => {
        form.setFieldsValue({
          ...patient,
          dateOfBirth: patient.dateOfBirth ? dayjs(patient.dateOfBirth, 'YYYY-MM-DD') : undefined,
        });
      });
    }
  }, [patientId, isEdit, form]);

  const handleSubmit = async () => {
    const values = await form.validateFields();
    const payload = {
      ...values,
      dateOfBirth: datePickerToStr(values.dateOfBirth),
    };

    setLoading(true);
    try {
      if (isEdit) {
        await patientApi.update(patientId, payload);
        message.success('患者信息已更新');
      } else {
        await patientApi.create(payload);
        message.success('患者已创建');
      }
      navigate('/patients');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card
      title={isEdit ? '编辑患者' : '新增患者'}
      extra={
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/patients')}>
          返回列表
        </Button>
      }
    >
      <Form
        form={form}
        layout="vertical"
        style={{ maxWidth: 600 }}
        onFinish={handleSubmit}
        initialValues={{ gender: 'Male' }}
      >
        <Form.Item
          name="patientId"
          label="患者 ID"
          rules={[{ required: true, message: '请输入患者 ID' }]}
          extra="建议格式：P + 8位日期，如 P20260530"
        >
          <Input placeholder="P20260530" disabled={isEdit} />
        </Form.Item>

        <Form.Item
          name="name"
          label="姓名"
          rules={[{ required: true, message: '请输入姓名' }]}
        >
          <Input placeholder="患者姓名" />
        </Form.Item>

        <Form.Item name="gender" label="性别">
          <Select
            options={[
              { label: '男', value: 'Male' },
              { label: '女', value: 'Female' },
              { label: '其他', value: 'Other' },
            ]}
          />
        </Form.Item>

        <Form.Item
          name="dateOfBirth"
          label="出生日期"
          rules={[
            {
              validator: (_, value) => {
                if (value && value.isAfter(dayjs())) {
                  return Promise.reject('出生日期不能晚于今天');
                }
                return Promise.resolve();
              },
            },
          ]}
        >
          <DatePicker style={{ width: '100%' }} format="YYYY-MM-DD" />
        </Form.Item>

        <Form.Item name="age" label="年龄">
          <InputNumber min={0} max={150} style={{ width: '100%' }} placeholder="0-150" />
        </Form.Item>

        <Form.Item name="idCard" label="身份证号">
          <Input placeholder="18位身份证号" maxLength={18} />
        </Form.Item>

        <Form.Item name="contact" label="联系方式">
          <Input placeholder="手机号码" />
        </Form.Item>

        <Form.Item name="address" label="地址">
          <Input placeholder="联系地址" />
        </Form.Item>

        <Form.Item name="medicalHistory" label="既往病史">
          <Input.TextArea rows={3} placeholder="既往病史" />
        </Form.Item>

        <Form.Item>
          <Space>
            <Button type="primary" htmlType="submit" loading={loading}>
              {isEdit ? '保存修改' : '创建患者'}
            </Button>
            <Button onClick={() => navigate('/patients')}>取消</Button>
          </Space>
        </Form.Item>
      </Form>
    </Card>
  );
}
