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
  const { setUser } = useAuthStore();

  const { register, handleSubmit, formState: { errors } } = useForm<LoginForm>({
    resolver: yupResolver(schema),
  });

    const onSubmit = async (data: LoginForm) => {
      try {
        setLoading(true);

        await login(data.login, data.password);
        setAuthenticated();
        toast.success('Вход выполнен успешно!');
        navigate('/');
      } catch (error) {
        toast.error('Неверный логин или пароль');
      } finally {
        setLoading(false);
      }
    };

  const handleRegisterClick = () => {
    navigate('/register');
  };

  return (
    <div className="min-h-screen flex flex-col justify-center py-12 sm:px-6 lg:px-8 bg-gray-50 dark:bg-gray-900">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <div className="flex justify-center">
          <img className="h-12 w-auto" src={logo} alt="Логотип" />
        </div>
        <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900 dark:text-white">
          Вход в систему
        </h2>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <div className="bg-white dark:bg-gray-800 py-8 px-4 shadow sm:rounded-lg sm:px-10">
          <form className="space-y-6" onSubmit={handleSubmit(onSubmit)}>
            <div>
              <Input
                label="Логин"
                id="login"
                type="text"
                autoComplete="username"
                {...register('login')}
                error={errors.login}
                containerClassName="mb-4"
              />
            </div>

            <div>
              <Input
                label="Пароль"
                id="password"
                type="password"
                autoComplete="current-password"
                {...register('password')}
                error={errors.password}
                containerClassName="mb-4"
              />
            </div>

            <div className="flex items-center justify-between">
              <div className="text-sm">
                <a
                  href="#register"
                  onClick={handleRegisterClick}
                  className="font-medium text-primary-600 hover:text-primary-500 dark:text-primary-400"
                >
                  Зарегистрироваться
                </a>
              </div>
            </div>

            <div>
              <Button
                type="submit"
                className="w-full"
                loading={loading}
                disabled={loading}
              >
                Войти
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;