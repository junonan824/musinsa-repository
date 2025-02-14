export interface ApiError {
  status: number;
  message: string;
  details?: Record<string, string>;
}

export interface ApiResponse<T> {
  success: boolean;
  data: T | null;
  error?: {
    status: number;
    message: string;
    details?: any;
  };
}

export interface Product {
  id: number;
  brandName: string;
  category: string;
  price: number;
} 