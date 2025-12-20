import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm, Controller } from 'react-hook-form';
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
    trigger
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

  const handleLoginChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const login = e.target.value;
    register('login').onChange(e);
    if (login.length >= 3) {
      validateLogin(login);
    }
  };

  const handleEmailChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const email = e.target.value;
    register('email').onChange(e);
    if (email.length >= 5) {
      validateEmail(email);
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
  }, [watch('password'), watch('passwordConfirm')]);
const onSubmit = async ( RegisterForm) => {
  try {
    setLoading(true);

    const userData: UserCreateDTO = {
      name: data.name,
      login: data.login,
      email: data.email,
      password: data.password,
      role: 'USER' // Явно указываем роль
    };

    await registerUser(userData);
    toast.success('Регистрация успешно завершена! Теперь вы можете войти в систему.');
    navigate('/login');
  } catch (error: any) {
    // Более детальная обработка ошибок
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
    <div className="min-h-screen flex flex-col justify-center py-12 sm:px-6 lg:px-8 bg-gray-50 dark:bg-gray-900">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <div className="flex justify-center">
          <img className="h-12 w-auto" src={logo} alt="Логотип" />
        </div>
        <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900 dark:text-white">
          Создание аккаунта
        </h2>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <div className="bg-white dark:bg-gray-800 py-8 px-4 shadow sm:rounded-lg sm:px-10">
          <form className="space-y-6" onSubmit={handleSubmit(onSubmit)}>
            <div>
              <Input
                label="Имя"
                id="name"
                type="text"
                {...register('name')}
                error={errors.name}
                containerClassName="mb-4"
              />
            </div>

            <div>
              <Input
                label="Логин"
                id="login"
                type="text"
                {...register('login', {
                  onChange: handleLoginChange,
                  onBlur: (e) => validateLogin(e.target.value)
                })}
                error={errors.login}
                containerClassName="mb-4"
              />
            </div>

            <div>
              <Input
                label="Email"
                id="email"
                type="email"
                {...register('email', {
                  onChange: handleEmailChange,
                  onBlur: (e) => validateEmail(e.target.value)
                })}
                error={errors.email}
                containerClassName="mb-4"
              />
            </div>

            <div>
              <Input
                label="Пароль"
                id="password"
                type="password"
                {...register('password')}
                error={errors.password}
                containerClassName="mb-4"
              />
            </div>

            <div>
              <Input
                label="Подтверждение пароля"
                id="passwordConfirm"
                type="password"
                {...register('passwordConfirm')}
                error={errors.passwordConfirm}
                containerClassName="mb-4"
              />
            </div>

            <div>
              <Button
                type="submit"
                className="w-full"
                loading={loading}
                disabled={loading || Object.keys(errors).length > 0}
              >
                Зарегистрироваться
              </Button>
            </div>

            <div className="text-center">
              <a
                href="#login"
                onClick={() => navigate('/login')}
                className="font-medium text-primary-600 hover:text-primary-500 dark:text-primary-400"
              >
                Уже есть аккаунт? Войти
              </a>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;