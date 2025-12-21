import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { registerUser, checkLoginExists, checkEmailExists } from '../../api/authApi';
import { UserCreateDTO } from '../../types';
import { Input, Button } from '../../components/ui/FormElements';
import logo from '../../assets/logo.svg';
import { toast } from 'react-toastify';

const schema = yup.object({
  name: yup.string().required('Имя обязательно'),
  login: yup.string().required('Логин обязателен'),
  email: yup.string().email('Неверный формат email').required('Email обязателен'),
  password: yup.string()
    .min(6, 'Пароль должен содержать не менее 6 символов')
    .required('Пароль обязателен'),
  passwordConfirm: yup.string()
    .oneOf([yup.ref('password')], 'Пароли не совпадают')
    .required('Подтверждение пароля обязательно'),
}).required();

type RegisterForm = {
  name: string;
  login: string;
  email: string;
  password: string;
  passwordConfirm: string;
};

const RegisterPage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const {
    register,
    handleSubmit,
    formState: { errors },
    setError,
    clearErrors,
    watch,
  } = useForm<RegisterForm>({
    resolver: yupResolver(schema),
    mode: 'onChange',
  });

  const validateLogin = async (login: string) => {
    if (!login) return;
    try {
      const exists = await checkLoginExists(login);
      if (exists) {
        setError('login', {
          type: 'manual',
          message: 'Логин уже существует'
        });
      } else {
        clearErrors('login');
      }
    } catch (error) {
      setError('login', {
        type: 'manual',
        message: 'Ошибка при проверке логина'
      });
    }
  };

  const validateEmail = async (email: string) => {
    if (!email) return;
    try {
      const exists = await checkEmailExists(email);
      if (exists) {
        setError('email', {
          type: 'manual',
          message: 'Email уже существует'
        });
      } else {
        clearErrors('email');
      }
    } catch (error) {
      setError('email', {
        type: 'manual',
        message: 'Ошибка при проверке email'
      });
    }
  };

  useEffect(() => {
    const password = watch('password');
    const passwordConfirm = watch('passwordConfirm');
    if (password && passwordConfirm && password !== passwordConfirm) {
      setError('passwordConfirm', {
        type: 'manual',
        message: 'Пароли не совпадают'
      });
    } else {
      clearErrors('passwordConfirm');
    }
  }, [watch('password'), watch('passwordConfirm'), setError, clearErrors]);

  const onSubmit = async (data: RegisterForm) => {
    try {
      setLoading(true);
      const userData: UserCreateDTO = {
        name: data.name,
        login: data.login,
        email: data.email,
        password: data.password,
        role: 'USER'
      };
      await registerUser(userData);
      toast.success('Регистрация успешно завершена! Теперь вы можете войти в систему.');
      navigate('/login');
    } catch (error: any) {
      if (error.response?.data?.message) {
        toast.error(`Ошибка: ${error.response.data.message}`);
      } else if (error.response?.status === 400) {
        toast.error('Неверные данные регистрации');
      } else {
        toast.error('Ошибка при регистрации');
      }
      console.error('Registration error:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 dark:from-gray-900 dark:via-gray-800 dark:to-gray-900 flex items-center justify-center px-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <img src={logo} alt="Logo" className="w-16 h-16 mx-auto mb-4" />
          <h1 className="text-3xl font-bold text-white mb-2">Создание аккаунта</h1>
          <p className="text-gray-400">Заполните форму для регистрации</p>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="bg-gray-800 rounded-lg shadow-xl p-8 space-y-4">
          <Input
            label="Имя"
            type="text"
            placeholder="Ваше имя"
            {...register('name')}
            error={errors.name}
            containerClassName="mb-4"
          />

          <Input
            label="Логин"
            type="text"
            placeholder="Уникальный логин"
            {...register('login')}
            onBlur={(e) => validateLogin(e.target.value)}
            error={errors.login}
            containerClassName="mb-4"
          />

          <Input
            label="Email"
            type="email"
            placeholder="your@email.com"
            {...register('email')}
            onBlur={(e) => validateEmail(e.target.value)}
            error={errors.email}
            containerClassName="mb-4"
          />

          <Input
            label="Пароль"
            type="password"
            placeholder="Минимум 6 символов"
            {...register('password')}
            error={errors.password}
            containerClassName="mb-4"
          />

          <Input
            label="Подтверждение пароля"
            type="password"
            placeholder="Повторите пароль"
            {...register('passwordConfirm')}
            error={errors.passwordConfirm}
            containerClassName="mb-6"
          />

          <Button
            type="submit"
            disabled={loading}
            className="w-full"
          >
            {loading ? 'Регистрация...' : 'Зарегистрироваться'}
          </Button>

          <div className="text-center mt-4">
            <span className="text-gray-400">Олды тут, олды на месте? </span>
            <button
              type="button"
              onClick={() => navigate('/login')}
              className="font-medium text-primary-600 hover:text-primary-500 dark:text-primary-400"
            >
              Я олд
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default RegisterPage;
