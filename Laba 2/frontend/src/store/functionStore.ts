import { create } from 'zustand';
import {
  AnalFunDTO,
  TabFunDTO,
  CompFunDTO,
  AnalPointsDTO,
  TabPointsDTO,
  CompositeStructureDTO
} from '../types';

interface FunctionState {
  analFunctions: AnalFunDTO[];
  tabFunctions: TabFunDTO[];
  compFunctions: CompFunDTO[];
  analPoints: Record<number, AnalPointsDTO[]>; // ключ - ID функции
  tabPoints: Record<number, TabPointsDTO[]>; // ключ - ID функции
  compStructures: Record<number, CompositeStructureDTO[]>; // ключ - ID композитной функции

  // Действия
  setAnalFunctions: (functions: AnalFunDTO[]) => void;
  setTabFunctions: (functions: TabFunDTO[]) => void;
  setCompFunctions: (functions: CompFunDTO[]) => void;
  setAnalPoints: (functionId: number, points: AnalPointsDTO[]) => void;
  setTabPoints: (functionId: number, points: TabPointsDTO[]) => void;
  setCompStructures: (functionId: number, structures: CompositeStructureDTO[]) => void;

  addAnalFunction: (func: AnalFunDTO) => void;
  addTabFunction: (func: TabFunDTO) => void;
  addCompFunction: (func: CompFunDTO) => void;

  removeAnalFunction: (id: number) => void;
  removeTabFunction: (id: number) => void;
  removeCompFunction: (id: number) => void;

  addAnalPoint: (functionId: number, point: AnalPointsDTO) => void;
  removeAnalPoint: (functionId: number, pointId: number) => void;

  addTabPoint: (functionId: number, point: TabPointsDTO) => void;
  removeTabPoint: (functionId: number, pointId: number) => void;

  addCompStructure: (functionId: number, structure: CompositeStructureDTO) => void;
  removeCompStructure: (functionId: number, structureId: number) => void;
}

export const useFunctionStore = create<FunctionState>((set, get) => ({
  analFunctions: [],
  tabFunctions: [],
  compFunctions: [],
  analPoints: {},
  tabPoints: {},
  compStructures: {},

  setAnalFunctions: (functions) => set({ analFunctions: functions }),
  setTabFunctions: (functions) => set({ tabFunctions: functions }),
  setCompFunctions: (functions) => set({ compFunctions: functions }),
  setAnalPoints: (functionId, points) => set(state => ({
    analPoints: { ...state.analPoints, [functionId]: points }
  })),
  setTabPoints: (functionId, points) => set(state => ({
    tabPoints: { ...state.tabPoints, [functionId]: points }
  })),
  setCompStructures: (functionId, structures) => set(state => ({
    compStructures: { ...state.compStructures, [functionId]: structures }
  })),

  addAnalFunction: (func) => set(state => ({
    analFunctions: [...state.analFunctions, func]
  })),

  addTabFunction: (func) => set(state => ({
    tabFunctions: [...state.tabFunctions, func]
  })),

  addCompFunction: (func) => set(state => ({
    compFunctions: [...state.compFunctions, func]
  })),

  removeAnalFunction: (id) => set(state => ({
    analFunctions: state.analFunctions.filter(f => f.id !== id),
    analPoints: Object.fromEntries(
      Object.entries(state.analPoints).filter(([key]) => Number(key) !== id)
    )
  })),

  removeTabFunction: (id) => set(state => ({
    tabFunctions: state.tabFunctions.filter(f => f.id !== id),
    tabPoints: Object.fromEntries(
      Object.entries(state.tabPoints).filter(([key]) => Number(key) !== id)
    )
  })),

  removeCompFunction: (id) => set(state => ({
    compFunctions: state.compFunctions.filter(f => f.id !== id),
    compStructures: Object.fromEntries(
      Object.entries(state.compStructures).filter(([key]) => Number(key) !== id)
    )
  })),

  addAnalPoint: (functionId, point) => set(state => {
    const currentPoints = state.analPoints[functionId] || [];
    return {
      analPoints: {
        ...state.analPoints,
        [functionId]: [...currentPoints, point]
      }
    };
  }),

  removeAnalPoint: (functionId, pointId) => set(state => {
    const currentPoints = state.analPoints[functionId] || [];
    return {
      analPoints: {
        ...state.analPoints,
        [functionId]: currentPoints.filter(p => p.id !== pointId)
      }
    };
  }),

  addTabPoint: (functionId, point) => set(state => {
    const currentPoints = state.tabPoints[functionId] || [];
    return {
      tabPoints: {
        ...state.tabPoints,
        [functionId]: [...currentPoints, point]
      }
    };
  }),

  removeTabPoint: (functionId, pointId) => set(state => {
    const currentPoints = state.tabPoints[functionId] || [];
    return {
      tabPoints: {
        ...state.tabPoints,
        [functionId]: currentPoints.filter(p => p.id !== pointId)
      }
    };
  }),

  addCompStructure: (functionId, structure) => set(state => {
    const currentStructures = state.compStructures[functionId] || [];
    return {
      compStructures: {
        ...state.compStructures,
        [functionId]: [...currentStructures, structure]
      }
    };
  }),

  removeCompStructure: (functionId, structureId) => set(state => {
    const currentStructures = state.compStructures[functionId] || [];
    return {
      compStructures: {
        ...state.compStructures,
        [functionId]: currentStructures.filter(s => s.id !== structureId)
      }
    };
  })
}));