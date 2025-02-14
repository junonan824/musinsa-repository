import { AxiosError } from 'axios';
import { ApiResponse } from '../types/api';

export const handleApiError = (error: AxiosError<ApiResponse<any>>) => {
  if (error.response) {
    const errorResponse = error.response.data;
    return {
      status: errorResponse.error?.status || 500,
      message: errorResponse.error?.message || '서버 오류가 발생했습니다'
    };
  }
  return {
    status: 500,
    message: '네트워크 오류가 발생했습니다'
  };
}; 