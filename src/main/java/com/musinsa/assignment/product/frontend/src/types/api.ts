export interface ApiError {
  status: number;
  message: string;
  details?: Record<string, string>;
}

export interface ApiResponse<T> {
  success: boolean;
  data: T | null;
  error?: ApiError;
}

export interface Product {
  id: number;
  brandName: string;
  category: string;
  price: number;
} 