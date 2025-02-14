'use client';

import React from 'react';
import axios from 'axios';
import { API_BASE_URL } from '../config/api';

interface ProductFormProps {
  onSuccess: () => void;
}

const CATEGORIES = [
  'TOP', 'OUTER', 'PANTS', 'SNEAKERS',
  'BAG', 'HAT', 'SOCKS', 'ACCESSORY'
] as const;

export default function ProductForm({ onSuccess }: ProductFormProps) {
  const [formData, setFormData] = React.useState({
    brandName: '',
    category: 'TOP' as typeof CATEGORIES[number],
    price: ''
  });

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    try {
      const response = await axios.post<ApiResponse<Product>>(`${API_BASE_URL}/products`, {
        ...formData,
        price: parseInt(formData.price)
      });
      if (response.data.success) {
        setFormData({ brandName: '', category: 'TOP', price: '' });
        onSuccess();
      }
    } catch (error) {
      console.error('Failed to create product:', error);
    }
  };

  return (
    <div className="overflow-x-auto">
      <form onSubmit={handleSubmit}>
        <table className="musinsa-table">
          <thead>
            <tr className="musinsa-table-header">
              <th className="w-1/3">브랜드명</th>
              <th className="w-1/3">카테고리</th>
              <th className="w-1/3">가격</th>
            </tr>
          </thead>
          <tbody>
            <tr className="border-b border-gray-200">
              <td className="py-4 px-6">
                <input
                  type="text"
                  value={formData.brandName}
                  onChange={(e) => setFormData({ ...formData, brandName: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-black"
                  required
                />
              </td>
              <td className="py-4 px-6">
                <select
                  value={formData.category}
                  onChange={(e) => setFormData({ ...formData, category: e.target.value as typeof CATEGORIES[number] })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-black"
                >
                  {CATEGORIES.map((category) => (
                    <option key={category} value={category}>{category}</option>
                  ))}
                </select>
              </td>
              <td className="py-4 px-6">
                <input
                  type="number"
                  value={formData.price}
                  onChange={(e) => setFormData({ ...formData, price: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-black"
                  min="0"
                  required
                />
              </td>
              <td className="py-4 px-6 text-right">
                <button
                  type="submit"
                  className="px-6 py-2 bg-black text-white rounded-md hover:bg-gray-800 transition-colors"
                >
                  등록
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </form>
    </div>
  );
} 