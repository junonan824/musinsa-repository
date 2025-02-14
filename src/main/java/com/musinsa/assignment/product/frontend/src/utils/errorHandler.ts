import { AxiosError } from 'axios';
import { ApiResponse } from '../types/api';

export const handleApiError = (error: AxiosError<ApiResponse<any>>): string => {
    if (error.response) {
        const errorResponse = error.response.data;
        if (errorResponse.error) {
            // 서버에서 보낸 에러 메시지 사용
            return errorResponse.error.message;
        }
    }
    // 기본 에러 메시지
    return '오류가 발생했습니다. 잠시 후 다시 시도해주세요.';
}; 