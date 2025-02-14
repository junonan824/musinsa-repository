'use client';

import React from 'react';
import axios from 'axios';
import { API_BASE_URL } from '../config/api';

interface PriceInfo {
  highest: {
    brand: string;
    price: number;
  };
  lowest: {
    brand: string;
    price: number;
  };
}

const CATEGORIES = [
  { value: 'TOP', label: '상의' },
  { value: 'OUTER', label: '아우터' },
  { value: 'PANTS', label: '바지' },
  { value: 'SNEAKERS', label: '스니커즈' },
  { value: 'BAG', label: '가방' },
  { value: 'HAT', label: '모자' },
  { value: 'SOCKS', label: '양말' },
  { value: 'ACCESSORY', label: '액세서리' }
];

export default function CategoryPriceAnalysis() {
  const [selectedCategory, setSelectedCategory] = React.useState('TOP');
  const [priceInfo, setPriceInfo] = React.useState<PriceInfo | null>(null);

  React.useEffect(() => {
    fetchCategoryPriceInfo();
  }, [selectedCategory]);

  const fetchCategoryPriceInfo = async () => {
    try {
      const response = await axios.get<ApiResponse<PriceInfo>>(
        `${API_BASE_URL}/category-price-info/${selectedCategory}`
      );
      if (response.data.success) {
        setPriceInfo(response.data.data);
      }
    } catch (error) {
      console.error('Failed to fetch category price info:', error);
    }
  };

  return (
    <div className="analysis-section">
      <h3 className="analysis-title">카테고리별 최고/최저가 분석</h3>
      <div className="mb-4">
        <select
          value={selectedCategory}
          onChange={(e) => setSelectedCategory(e.target.value)}
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-black"
        >
          {CATEGORIES.map((category) => (
            <option key={category.value} value={category.value}>
              {category.label}
            </option>
          ))}
        </select>
      </div>
      {priceInfo && (
        <div className="table-container">
          <table className="analysis-table">
            <thead>
              <tr className="musinsa-table-header">
                <th className="w-1/3">구분</th>
                <th className="w-1/3">브랜드</th>
                <th className="w-1/3">가격</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>최고가</td>
                <td>{priceInfo.highest.brand}</td>
                <td>{priceInfo.highest.price.toLocaleString()}원</td>
              </tr>
              <tr>
                <td>최저가</td>
                <td>{priceInfo.lowest.brand}</td>
                <td>{priceInfo.lowest.price.toLocaleString()}원</td>
              </tr>
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
} 