import { useEffect, useState, useCallback } from 'react';
import { Table, Button, Input, Space, Popconfirm, message, Card } from 'antd';
import { PlusOutlined, SearchOutlined, DeleteOutlined, EditOutlined, EyeOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import type { ColumnsType } from 'antd/es/table';
import { patientApi } from '../../api/patients';
import type { Patient } from '../../types';
import { GENDER_MAP, formatDate } from '../../utils/format';

export default function PatientList() {
  const [patients, setPatients] = useState<Patient[]>([]);
  const [loading, setLoading] = useState(false);
  const [searchName, setSearchName] = useState('');
  const navigate = useNavigate();

  const fetchPatients = useCallback(async (name?: string) => {
    setLoading(true);
    try {
      const data = await patientApi.list(name);
      setPatients(data);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchPatients();
  }, [fetchPatients]);

  const handleSearch = useCallback(() => {
    fetchPatients(searchName || undefined);
  }, [fetchPatients, searchName]);

  const handleDelete = async (patientId: string) => {
    await patientApi.delete(patientId);
    message.success('患者已删除');
    fetchPatients(searchName || undefined);
  };

  const columns: ColumnsType<Patient> = [
    {
      title: '患者 ID',
      dataIndex: 'patientId',
      render: (id: string) => (
        <a onClick={() => navigate(`/patients/${id}`)}>{id}</a>
      ),
    },
    { title: '姓名', dataIndex: 'name' },
    {
      title: '性别',
      dataIndex: 'gender',
      render: (g: string) => GENDER_MAP[g] || '-',
    },
    {
      title: '出生日期',
      dataIndex: 'dateOfBirth',
      render: (d: string) => formatDate(d),
    },
    { title: '年龄', dataIndex: 'age', render: (a: number) => a ?? '-' },
    { title: '联系方式', dataIndex: 'contact', render: (c: string) => c || '-' },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      render: (d: string) => formatDate(d),
    },
    {
      title: '操作',
      key: 'actions',
      width: 220,
      render: (_, record) => (
        <Space>
          <Button
            type="link"
            size="small"
            icon={<EyeOutlined />}
            onClick={() => navigate(`/patients/${record.patientId}`)}
          >
            详情
          </Button>
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => navigate(`/patients/${record.patientId}/edit`)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确认删除"
            description="确认删除该患者？删除后将同时删除其所有检查记录、影像和病历，且无法恢复。"
            onConfirm={() => handleDelete(record.patientId)}
            okText="确认"
            cancelText="取消"
          >
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <Card>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
        <Space>
          <Input
            placeholder="按姓名搜索"
            prefix={<SearchOutlined />}
            value={searchName}
            onChange={e => setSearchName(e.target.value)}
            onPressEnter={handleSearch}
            allowClear
            style={{ width: 240 }}
          />
          <Button type="primary" onClick={handleSearch}>搜索</Button>
        </Space>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/patients/new')}>
          新增患者
        </Button>
      </div>
      <Table
        columns={columns}
        dataSource={patients}
        rowKey="patientId"
        loading={loading}
        pagination={{
          defaultPageSize: 10,
          showSizeChanger: true,
          showTotal: (total) => `共 ${total} 位患者`,
        }}
      />
    </Card>
  );
}
