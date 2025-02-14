export interface ApiResponse<T> {
  success: boolean;
  data: T;
  error?: {
    status: number;
    message: string;
  };
} 