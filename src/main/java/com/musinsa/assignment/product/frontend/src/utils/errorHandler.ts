import { AxiosError } from 'axios';
import { ApiResponse } from '../types/api';

export const handleApiError = (error: unknown): string => {
  if (error instanceof AxiosError && error.response) {
    const response = error.response.data as ApiResponse<any>;
    
    if (response.error) {
      if (response.error.details) {
        // Validation error handling
        return Object.values(response.error.details).join(', ');
      }
      return response.error.message;
    }
  }
  
  return '오류가 발생했습니다. 잠시 후 다시 시도해주세요.';
}; 