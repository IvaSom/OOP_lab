import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { login } from '../../api/authApi';
import { useAuthStore } from '../../store/authStore';
import { Input, Button } from '../../components/ui/FormElements';
import logo from '../../assets/logo.svg';
import { toast } from 'react-toastify';

const schema = yup.object({
  login: yup.string().required('Логин обязателен'),
  password: yup.string().required('Пароль обязателен'),
}).required();

type LoginForm = {
  login: string;
  password: string;
};

const LoginPage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { login: setLogin } = useAuthStore(); // ← Получаем функцию из стора

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginForm>({
    resolver: yupResolver(schema),
    mode: 'onChange',
  });

  const onSubmit = async (data: LoginForm) => {
    try {
      setLoading(true);
      const userData = await login(data);
      setLogin(userData);
      toast.success('Вы успешно вошли в систему!');
      navigate('/');
    } catch (error: any) {
      toast.error('Неверный логин или пароль');
      toast.error('Ошибка при входе');
      console.error('Login error:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 dark:from-gray-900 dark:via-gray-800 dark:to-gray-900 flex items-center justify-center px-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <img src={logo} alt="Logo" className="w-16 h-16 mx-auto mb-4" />
          <h1 className="text-3xl font-bold text-white mb-2">Вход в систему</h1>
          <p className="text-gray-400">Введите ваши учетные данные</p>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="bg-gray-800 rounded-lg shadow-xl p-8 space-y-4">
          <Input
            label="Логин"
            type="text"
            placeholder="Ваш логин"
            {...register('login')}
            error={errors.login}
            containerClassName="mb-4"
          />

          <Input
            label="Пароль"
            type="password"
            placeholder="Ваш пароль"
            {...register('password')}
            error={errors.password}
            containerClassName="mb-6"
          />

          <Button
            type="submit"
            disabled={loading}
            className="w-full"
          >
            {loading ? 'Вход...' : 'Войти'}
          </Button>

          <div className="text-center mt-4">
            <span className="text-gray-400">Вы новенький? </span>
            <button
              type="button"
              onClick={() => navigate('/register')}
              className="font-medium text-primary-600 hover:text-primary-500 dark:text-primary-400"
            >
              Стать стареньким
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default LoginPage;