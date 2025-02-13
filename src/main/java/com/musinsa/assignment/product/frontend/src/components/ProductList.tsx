'use client';

import React from 'react';
import axios from 'axios';
import { API_BASE_URL } from '../config/api';

const CATEGORIES = [
  'TOP',
  'OUTER',
  'PANTS',
  'SNEAKERS',
  'BAG',
  'HAT',
  'SOCKS',
  'ACCESSORY'
] as const;

interface Product {
  id: number;
  brandName: string;
  category: string;
  price: number;
}

interface PageInfo {
  products: Product[];
  currentPage: number;
  totalItems: number;
  totalPages: number;
}

interface ProductListProps {
  refreshTrigger: number;
  onDelete: () => void;
}

export default function ProductList({ refreshTrigger, onDelete }: ProductListProps) {
  const [products, setProducts] = React.useState<Product[]>([]);
  const [currentPage, setCurrentPage] = React.useState(0);
  const [totalPages, setTotalPages] = React.useState(0);
  const [pageSize] = React.useState(10);
  const [editingId, setEditingId] = React.useState<number | null>(null);
  const [editForm, setEditForm] = React.useState<Product | null>(null);

  React.useEffect(() => {
    fetchProducts();
  }, [refreshTrigger, currentPage]);

  const fetchProducts = async () => {
    try {
      const response = await axios.get<PageInfo>(
        `${API_BASE_URL}/products?page=${currentPage}&size=${pageSize}`
      );
      setProducts(response.data.products);
      setTotalPages(response.data.totalPages);
    } catch (error) {
      console.error('Failed to fetch products:', error);
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await axios.delete(`${API_BASE_URL}/products/${id}`);
      onDelete();
    } catch (error) {
      console.error('Failed to delete product:', error);
    }
  };

  const handlePageChange = (newPage: number) => {
    setCurrentPage(newPage);
  };

  const handleEdit = (product: Product) => {
    setEditingId(product.id);
    setEditForm(product);
  };

  const handleUpdate = async () => {
    if (!editForm) return;
    
    try {
      await axios.put(`${API_BASE_URL}/products/${editForm.id}`, editForm);
      setEditingId(null);
      setEditForm(null);
      onDelete(); // 목록 새로고침
    } catch (error) {
      console.error('Failed to update product:', error);
    }
  };

  return (
    <div className="space-y-4">
      <div className="table-container">
        <table className="musinsa-table">
          <thead>
            <tr className="musinsa-table-header">
              <th className="w-1/5">브랜드</th>
              <th className="w-1/5">카테고리</th>
              <th className="w-1/5">가격</th>
              <th className="w-2/5">관리</th>
            </tr>
          </thead>
          <tbody>
            {products.map((product) => (
              <tr key={product.id} className="hover:bg-gray-50 transition-colors">
                <td>
                  {editingId === product.id ? (
                    <input
                      type="text"
                      value={editForm?.brandName || ''}
                      onChange={(e) => setEditForm(prev => prev ? {...prev, brandName: e.target.value} : null)}
                      className="w-full px-2 py-1 border rounded"
                    />
                  ) : (
                    product.brandName
                  )}
                </td>
                <td>
                  {editingId === product.id ? (
                    <select
                      value={editForm?.category || 'TOP'}
                      onChange={(e) => setEditForm(prev => prev ? {...prev, category: e.target.value} : null)}
                      className="w-full px-2 py-1 border rounded"
                    >
                      {CATEGORIES.map((category) => (
                        <option key={category} value={category}>{category}</option>
                      ))}
                    </select>
                  ) : (
                    product.category
                  )}
                </td>
                <td>
                  {editingId === product.id ? (
                    <input
                      type="number"
                      value={editForm?.price || 0}
                      onChange={(e) => setEditForm(prev => prev ? {...prev, price: parseInt(e.target.value)} : null)}
                      className="w-full px-2 py-1 border rounded"
                    />
                  ) : (
                    `${product.price.toLocaleString()}원`
                  )}
                </td>
                <td className="space-x-2 text-right">
                  <button
                    onClick={() => editingId === product.id ? handleUpdate() : handleEdit(product)}
                    className="px-4 py-2 bg-black text-white rounded-md hover:bg-gray-800 transition-colors"
                  >
                    {editingId === product.id ? '수정완료' : '수정하기'}
                  </button>
                  <button
                    onClick={() => handleDelete(product.id)}
                    className="px-4 py-2 bg-black text-white rounded-md hover:bg-gray-800 transition-colors"
                  >
                    삭제
                  </button>
                </td>
              </tr>
            ))}
            {products.length === 0 && (
              <tr className="border-b-2 border-black">
                <td colSpan={4} className="py-8 text-center text-gray-500">
                  등록된 상품이 없습니다.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* Pagination */}
      <div className="pagination-container">
        <div className="flex justify-center items-center gap-2">
          <button
            onClick={() => handlePageChange(currentPage - 1)}
            disabled={currentPage === 0}
            className="px-3 py-1 border rounded disabled:opacity-50 hover:bg-gray-100 transition-all"
          >
            이전
          </button>
          
          {Array.from({ length: totalPages }, (_, i) => (
            <button
              key={i}
              onClick={() => handlePageChange(i)}
              className={`px-3 py-1 rounded transition-all duration-200 ${
                currentPage === i 
                  ? 'bg-black text-white font-bold scale-110 shadow-md' 
                  : 'border hover:bg-gray-100'
              }`}
            >
              {i + 1}
            </button>
          ))}

          <button
            onClick={() => handlePageChange(currentPage + 1)}
            disabled={currentPage === totalPages - 1}
            className="px-3 py-1 border rounded disabled:opacity-50 hover:bg-gray-100 transition-all"
          >
            다음
          </button>
        </div>
      </div>
    </div>
  );
} 