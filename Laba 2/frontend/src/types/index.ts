// Пользователи
export interface UserDTO {
  id: number;
  name: string;
  login: string;
  email: string;
  role: string;
}

export interface UserCreateDTO {
  name: string;
  login: string;
  email: string;
  password: string;
  role?: string;
}

export interface UserAuthDTO {
  login: string;
  password: string;
}

// Аналитические функции
export interface AnalFunDTO {
  id: number;
  name: string;
  type: number;
}

// Точки аналитических функций
export interface AnalPointsDTO {
  id: number;
  x: number;
  y: number;
  functionId: number;
}

export interface CreateAnalPointDTO {
  x: number;
  functionId: number;
}

// Табулированные функции
export interface TabFunDTO {
  id: number;
  name: string;
}

// Точки табулированных функций
export interface TabPointsDTO {
  id: number;
  x: number;
  y: number;
  derive: number;
  functionId: number;
}

export interface TabPointsCreateDTO {
  x: number;
  y: number;
  derive: number;
  functionId: number;
}

// Композитные функции
export interface CompFunDTO {
  id: number;
  name: string;
}

// Структура композитных функций
export interface CompositeStructureDTO {
  id: number;
  compositeFunctionId: number;
  analyticFunctionId: number;
  executionOrder: number;
}

export interface CompositeStructureCreateDTO {
  compositeFunctionId: number;
  analyticFunctionId: number;
  executionOrder: number;
}

export interface CompPointsDTO {
  id: number;
  x: number;
  y: number;
  functionId: number;
}