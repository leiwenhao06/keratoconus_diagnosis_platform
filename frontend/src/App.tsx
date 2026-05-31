import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import AppLayout from './components/layout/AppLayout';
import PatientList from './pages/patients/PatientList';
import PatientDetail from './pages/patients/PatientDetail';
import PatientForm from './pages/patients/PatientForm';
import ExamForm from './pages/exams/ExamForm';
import RecordForm from './pages/records/RecordForm';

export default function App() {
  return (
    <ConfigProvider
      locale={zhCN}
      theme={{
        token: {
          colorPrimary: '#0958d9',
        },
      }}
    >
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<AppLayout />}>
            <Route index element={<Navigate to="/patients" replace />} />
            <Route path="patients" element={<PatientList />} />
            <Route path="patients/new" element={<PatientForm />} />
            <Route path="patients/:patientId" element={<PatientDetail />} />
            <Route path="patients/:patientId/edit" element={<PatientForm />} />
            <Route path="patients/:patientId/exams/new" element={<ExamForm />} />
            <Route path="patients/:patientId/records/new" element={<RecordForm />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </ConfigProvider>
  );
}
