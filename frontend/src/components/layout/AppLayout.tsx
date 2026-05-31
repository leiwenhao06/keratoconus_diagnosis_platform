import { useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { Layout, Menu } from 'antd';
import { UserOutlined, DashboardOutlined } from '@ant-design/icons';

const { Header, Sider, Content } = Layout;

const menuItems = [
  {
    key: '/patients',
    icon: <UserOutlined />,
    label: '患者管理',
  },
  {
    key: '/dashboard',
    icon: <DashboardOutlined />,
    label: '今日就诊',
  },
];

export default function AppLayout() {
  const navigate = useNavigate();
  const location = useLocation();
  const [collapsed, setCollapsed] = useState(false);

  const selectedKey = '/' + location.pathname.split('/')[1];

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider
        collapsible
        collapsed={collapsed}
        onCollapse={setCollapsed}
        theme="dark"
        width={220}
      >
        <div style={{
          height: 64,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          color: '#fff',
          fontSize: collapsed ? 14 : 16,
          fontWeight: 600,
          whiteSpace: 'nowrap',
          overflow: 'hidden',
          borderBottom: '1px solid rgba(255,255,255,0.1)',
        }}>
          {collapsed ? 'KCP' : '圆锥角膜诊断平台'}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[selectedKey]}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
        />
      </Sider>
      <Layout>
        <Header style={{
          background: '#fff',
          padding: '0 24px',
          display: 'flex',
          alignItems: 'center',
          fontSize: 14,
          color: '#666',
          boxShadow: '0 1px 4px rgba(0,0,0,0.08)',
        }}>
          {location.pathname === '/patients' && '患者列表'}
          {location.pathname === '/patients/new' && '新增患者'}
          {location.pathname.startsWith('/patients/') && location.pathname.endsWith('/edit') && '编辑患者'}
          {location.pathname.match(/^\/patients\/[^/]+$/) && '患者详情'}
          {location.pathname.includes('/exams/new') && '新增角膜检查'}
          {location.pathname.includes('/records/new') && '新增病历'}
        </Header>
        <Content style={{ margin: 24 }}>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
}
