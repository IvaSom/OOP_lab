import api from './api';
import {
  AnalFunDTO,
  TabFunDTO,
  CompFunDTO,
  AnalPointsDTO,
  TabPointsDTO,
  CompPointsDTO,
  CompositeStructureDTO,
  CreateAnalPointDTO,
  TabPointsCreateDTO,
  CompositeStructureCreateDTO
} from '../types';

// Аналитические функции
export const getAnalFunctions = async (): Promise<AnalFunDTO[]> => {
  const response = await api.get('/anal-fun');
  return response.data;
};

export const createAnalFunction = async (functionData: AnalFunDTO): Promise<AnalFunDTO> => {
  const response = await api.post('/anal-fun', functionData);
  return response.data;
};

export const updateAnalFunction = async (id: number, functionData: AnalFunDTO): Promise<AnalFunDTO> => {
  const response = await api.put(`/anal-fun/${id}`, functionData);
  return response.data;
};

export const deleteAnalFunction = async (id: number): Promise<void> => {
  await api.delete(`/anal-fun/${id}`);
};

// Точки аналитических функций
export const getAnalPointsByFunction = async (functionId: number): Promise<AnalPointsDTO[]> => {
  const response = await api.get(`/anal-points/function/${functionId}`);
  return response.data;
};

export const createAnalPoint = async (pointData: CreateAnalPointDTO): Promise<AnalPointsDTO> => {
  const response = await api.post('/anal-points', pointData);
  return response.data;
};

export const deleteAnalPoint = async (id: number): Promise<void> => {
  await api.delete(`/anal-points/${id}`);
};

// Табулированные функции
export const getTabFunctions = async (): Promise<TabFunDTO[]> => {
  const response = await api.get('/tab-fun');
  return response.data;
};

export const createTabFunction = async (functionData: TabFunDTO): Promise<TabFunDTO> => {
  const response = await api.post('/tab-fun', functionData);
  return response.data;
};

export const deleteTabFunction = async (id: number): Promise<void> => {
  await api.delete(`/tab-fun/${id}`);
};

// Точки табулированных функций
export const getTabPointsByFunction = async (functionId: number): Promise<TabPointsDTO[]> => {
  const response = await api.get(`/tab-points/function/${functionId}`);
  return response.data;
};

export const createTabPoint = async (pointData: TabPointsCreateDTO): Promise<TabPointsDTO> => {
  const response = await api.post('/tab-points', pointData);
  return response.data;
};

export const updateTabPoint = async (id: number, pointData: TabPointsCreateDTO): Promise<TabPointsDTO> => {
  const response = await api.put(`/tab-points/${id}`, pointData);
  return response.data;
};

export const deleteTabPoint = async (id: number): Promise<void> => {
  await api.delete(`/tab-points/${id}`);
};

// Композитные функции
export const getCompFunctions = async (): Promise<CompFunDTO[]> => {
  const response = await api.get('/comp-fun');
  return response.data;
};

export const createCompFunction = async (functionData: CompFunDTO): Promise<CompFunDTO> => {
  const response = await api.post('/comp-fun', functionData);
  return response.data;
};

export const deleteCompFunction = async (id: number): Promise<void> => {
  await api.delete(`/comp-fun/${id}`);
};

// Структура композитных функций
export const getCompStructuresByFunction = async (compFunId: number): Promise<CompositeStructureDTO[]> => {
  const response = await api.get(`/comp-structures/comp-fun/${compFunId}`);
  return response.data;
};

export const createCompStructure = async (structureData: CompositeStructureCreateDTO): Promise<CompositeStructureDTO> => {
  const response = await api.post('/comp-structures', structureData);
  return response.data;
};

export const deleteCompStructure = async (id: number): Promise<void> => {
  await api.delete(`/comp-structures/${id}`);
};

export const getCompPointsByFunction = async (functionId: number): Promise<CompPointsDTO[]> => {
  const response = await api.get(`/comp-points/function/${functionId}`);
  return response.data;
};

export const createCompPoint = async (pointData: { x: number; functionId: number }): Promise<CompPointsDTO> => {
  const response = await api.post('/comp-points', pointData);
  return response.data;
};

export const updateCompPoint = async (id: number, pointData: { x: number; functionId: number }): Promise<CompPointsDTO> => {
  const response = await api.put(`/comp-points/${id}`, pointData);
  return response.data;
};

export const deleteCompPoint = async (id: number): Promise<void> => {
  await api.delete(`/comp-points/${id}`);
};

// Математические операции
export const differentiateFunction = async (
  functionType: 'anal' | 'tab' | 'comp',
  functionId: number,
  step: number
): Promise<any[]> => {
  let endpoint = '';

  if (functionType === 'anal') {
    endpoint = `/math-operations/differentiate-anal/${functionId}?step=${step}`;
  } else if (functionType === 'tab') {
    endpoint = `/math-operations/differentiate-tab/${functionId}?step=${step}`;
  } else if (functionType === 'comp') {
    endpoint = `/math-operations/differentiate-comp/${functionId}?step=${step}`;
  }

  const response = await api.get(endpoint);
  return response.data;
};

export const integrateFunction = async (
  functionType: 'anal' | 'tab' | 'comp',
  functionId: number,
  threads: number
): Promise<number> => {
  let endpoint = '';

  if (functionType === 'anal') {
    endpoint = `/math-operations/integrate-anal/${functionId}?threads=${threads}`;
  } else if (functionType === 'tab') {
    endpoint = `/math-operations/integrate-tab/${functionId}?threads=${threads}`;
  } else if (functionType === 'comp') {
    endpoint = `/math-operations/integrate-comp/${functionId}?threads=${threads}`;
  }

  const response = await api.get(endpoint);
  return response.data;
};

// Операции с функциями
export const sumFunctions = async (
  function1Type: string,
  function1Id: number,
  function2Type: string,
  function2Id: number
): Promise<any> => {
  const response = await api.post('/math-operations/sum', {
    function1Type,
    function1Id,
    function2Type,
    function2Id
  });
  return response.data;
};

export const subtractFunctions = async (
  function1Type: string,
  function1Id: number,
  function2Type: string,
  function2Id: number
): Promise<any> => {
  const response = await api.post('/math-operations/subtract', {
    function1Type,
    function1Id,
    function2Type,
    function2Id
  });
  return response.data;
};