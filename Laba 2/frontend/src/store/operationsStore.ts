import { create } from 'zustand';
import {
  performTabulatedOperation,
  differentiateTabulatedFunction
} from '../api/operationApi';
import { TabulatedFunctionWithPoints } from '../types';

interface OperationsState {
  // Состояние для операции над двумя функциями
  selectedFunction1: number | null;
  selectedFunction2: number | null;
  operationType: 'sum' | 'subtract' | 'multiply' | 'divide';

  // Состояние для дифференцирования
  selectedFunctionForDiff: number | null;

  // Результаты операций
  operationResult: TabulatedFunctionWithPoints | null;
  differentiationResult: TabulatedFunctionWithPoints | null;

  // Состояния загрузки
  isLoading: boolean;
  error: string | null;

  // Действия для выбора функций
  selectFunction1: (id: number) => void;
  selectFunction2: (id: number) => void;
  setOperationType: (type: 'sum' | 'subtract' | 'multiply' | 'divide') => void;

  // Действие для выбора функции для дифференцирования
  selectFunctionForDiff: (id: number) => void;

  // Действия для выполнения операций
  performOperation: () => Promise<void>;
  performDifferentiation: () => Promise<void>;

  // Очистка результатов
  clearResults: () => void;
  clearError: () => void;

  // Сброс выбора
  resetSelection: () => void;
}

export const useOperationsStore = create<OperationsState>((set, get) => ({
  // Начальное состояние
  selectedFunction1: null,
  selectedFunction2: null,
  operationType: 'sum',
  selectedFunctionForDiff: null,
  operationResult: null,
  differentiationResult: null,
  isLoading: false,
  error: null,

  // Действия для выбора
  selectFunction1: (id) => set({ selectedFunction1: id }),
  selectFunction2: (id) => set({ selectedFunction2: id }),
  setOperationType: (type) => set({ operationType: type }),
  selectFunctionForDiff: (id) => set({ selectedFunctionForDiff: id }),

  // Выполнение операции над двумя функциями
  performOperation: async () => {
    const { selectedFunction1, selectedFunction2, operationType } = get();

    if (!selectedFunction1 || !selectedFunction2) {
      set({ error: 'Выберите обе функции для операции' });
      return;
    }

    set({ isLoading: true, error: null });

    try {
      const request = {
        functionId1: selectedFunction1,
        functionId2: selectedFunction2,
        operation: operationType
      };

      const result = await performTabulatedOperation(request);
      set({
        operationResult: result,
        differentiationResult: null, // Очищаем другой результат
        isLoading: false
      });
    } catch (error: any) {
      const errorMessage = error.response?.data?.message ||
                          error.message ||
                          'Ошибка при выполнении операции';
      set({
        error: errorMessage,
        isLoading: false
      });
    }
  },

  // Выполнение дифференцирования
  performDifferentiation: async () => {
    const { selectedFunctionForDiff } = get();

    if (!selectedFunctionForDiff) {
      set({ error: 'Выберите функцию для дифференцирования' });
      return;
    }

    set({ isLoading: true, error: null });

    try {
      const result = await differentiateTabulatedFunction(selectedFunctionForDiff);
      set({
        differentiationResult: result,
        operationResult: null, // Очищаем другой результат
        isLoading: false
      });
    } catch (error: any) {
      const errorMessage = error.response?.data?.message ||
                          error.message ||
                          'Ошибка при дифференцировании';
      set({
        error: errorMessage,
        isLoading: false
      });
    }
  },

  // Очистка результатов
  clearResults: () => set({
    operationResult: null,
    differentiationResult: null
  }),

  clearError: () => set({ error: null }),

  // Полный сброс
  resetSelection: () => set({
    selectedFunction1: null,
    selectedFunction2: null,
    selectedFunctionForDiff: null,
    operationResult: null,
    differentiationResult: null,
    error: null
  })
}));