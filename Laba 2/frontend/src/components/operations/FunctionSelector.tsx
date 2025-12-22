import React from 'react';
import { Select } from '../ui/FormElements';
import { TabFunDTO } from '../../types';

interface FunctionSelectorProps {
  label: string;
  selectedFunctionId: number | null;
  functions: TabFunDTO[];
  onSelect: (id: number | null) => void;
  disabled?: boolean;
  required?: boolean;
  className?: string;
}

const FunctionSelector: React.FC<FunctionSelectorProps> = ({
  label,
  selectedFunctionId,
  functions,
  onSelect,
  disabled = false,
  required = false,
  className = ''
}) => {
  const handleChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const value = e.target.value;
    onSelect(value ? Number(value) : null);
  };

  return (
    <div className={className}>
      <Select
        label={label}
        value={selectedFunctionId || ''}
        onChange={handleChange}
        disabled={disabled}
        options={[
          { value: '', label: 'Выберите функцию', disabled: true },
          ...functions.map(func => ({
            value: func.id,
            label: `${func.name} (ID: ${func.id})`
          }))
        ]}
      />

      {selectedFunctionId && (
        <div className="mt-2 text-sm text-gray-500 dark:text-gray-400">
          Выбрано: {functions.find(f => f.id === selectedFunctionId)?.name}
        </div>
      )}
    </div>
  );
};

export default FunctionSelector;