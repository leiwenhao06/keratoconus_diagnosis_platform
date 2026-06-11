import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Form, Input, Button, Card, Typography, message } from 'antd';
import { EyeInvisibleOutlined, EyeTwoTone, UserOutlined, LockOutlined } from '@ant-design/icons';
import { useAuthStore } from '../../stores/authStore';
import client from '../../api/client';
import type { LoginRequest, LoginResponse, ApiResponse } from '../../types';

const { Title, Text } = Typography;

export default function LoginPage() {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const setAuth = useAuthStore((s) => s.setAuth);

  const handleLogin = async (values: LoginRequest) => {
    setLoading(true);
    try {
      const { data } = await client.post<ApiResponse<LoginResponse>>(
        '/api/auth/login',
        values,
      );
      if (data.code === 200 && data.data) {
        setAuth(data.data);
        message.success('登录成功');
        navigate('/patients', { replace: true });
      } else {
        message.error(data.message || '登录失败');
      }
    } catch {
      // error handled by interceptor
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={styles.wrapper}>
      {/* Background cornea rings */}
      <div style={styles.bgRing1} />
      <div style={styles.bgRing2} />
      <div style={styles.bgRing3} />

      <Card style={styles.card} styles={{ body: styles.cardBody }}>
        {/* Logo area */}
        <div style={styles.logoArea}>
          <div style={styles.logoIcon}>
            <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
              <circle cx="24" cy="24" r="22" stroke="#0958d9" strokeWidth="2" />
              <circle cx="24" cy="24" r="14" stroke="#0958d9" strokeWidth="1.5" opacity="0.7" />
              <circle cx="24" cy="24" r="8" fill="#0958d9" opacity="0.15" />
              <circle cx="24" cy="24" r="3" fill="#0958d9" opacity="0.4" />
            </svg>
          </div>
          <Title level={3} style={styles.platformTitle}>
            圆锥角膜智能诊断平台
          </Title>
          <Text type="secondary" style={styles.subtitle}>
            Keratoconus Intelligent Diagnosis Platform
          </Text>
        </div>

        <Form<LoginRequest>
          onFinish={handleLogin}
          size="large"
          autoComplete="off"
        >
          <Form.Item
            name="username"
            rules={[{ required: true, message: '请输入用户名' }]}
          >
            <Input
              prefix={<UserOutlined style={{ color: '#bfbfbf' }} />}
              placeholder="用户名"
            />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[{ required: true, message: '请输入密码' }]}
          >
            <Input.Password
              prefix={<LockOutlined style={{ color: '#bfbfbf' }} />}
              placeholder="密码"
              iconRender={(visible) =>
                visible ? <EyeTwoTone /> : <EyeInvisibleOutlined />
              }
            />
          </Form.Item>

          <Form.Item style={{ marginBottom: 0 }}>
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              block
              style={styles.loginBtn}
            >
              登 录
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
}

const styles: Record<string, React.CSSProperties> = {
  wrapper: {
    minHeight: '100vh',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    background: 'linear-gradient(135deg, #0a1628 0%, #0d2137 30%, #0f2545 60%, #0a1929 100%)',
    position: 'relative',
    overflow: 'hidden',
  },
  bgRing1: {
    position: 'absolute',
    width: 700,
    height: 700,
    borderRadius: '50%',
    border: '1px solid rgba(9, 88, 217, 0.08)',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    pointerEvents: 'none',
  },
  bgRing2: {
    position: 'absolute',
    width: 500,
    height: 500,
    borderRadius: '50%',
    border: '1px solid rgba(9, 88, 217, 0.12)',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    pointerEvents: 'none',
  },
  bgRing3: {
    position: 'absolute',
    width: 300,
    height: 300,
    borderRadius: '50%',
    border: '1px solid rgba(9, 88, 217, 0.16)',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    pointerEvents: 'none',
  },
  card: {
    width: 420,
    borderRadius: 12,
    boxShadow: '0 20px 60px rgba(0,0,0,0.3), 0 0 0 1px rgba(255,255,255,0.05)',
    background: 'rgba(255,255,255,0.97)',
    position: 'relative',
    zIndex: 1,
  },
  cardBody: {
    padding: '40px 36px',
  },
  logoArea: {
    textAlign: 'center',
    marginBottom: 36,
  },
  logoIcon: {
    marginBottom: 16,
  },
  platformTitle: {
    margin: 0,
    fontWeight: 600,
    color: '#0a1628',
    letterSpacing: 2,
  },
  subtitle: {
    fontSize: 12,
    letterSpacing: 1,
    marginTop: 4,
    display: 'block',
  },
  loginBtn: {
    height: 44,
    fontSize: 16,
    borderRadius: 8,
    letterSpacing: 4,
    marginTop: 8,
  },
};
