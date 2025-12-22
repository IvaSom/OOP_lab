import api from './api';
import {
  FunctionOperationRequest,
  TabulatedFunctionWithPoints,
  FunctionOperationResult,
  DifferentiationResult
} from '../types';

export const performTabulatedOperation = async (
  request: FunctionOperationRequest
): Promise<TabulatedFunctionWithPoints> => {
  const response = await api.post('/api/functions/operations/tabulated', request);
  return response.data;
};

export const differentiateTabulatedFunction = async (
  functionId: number
): Promise<TabulatedFunctionWithPoints> => {
  const response = await api.post(`/api/functions/operations/tabulated/${functionId}/differentiate`);
  return response.data;
};

export const checkFunctionsCompatibility = async (
  functionId1: number,
  functionId2: number
): Promise<{ compatible: boolean; message?: string }> => {
  try {
    return { compatible: true };
  } catch (error) {
    return {
      compatible: false,
      message: 'Ошибка при проверке совместимости функций'
    };
  }
};