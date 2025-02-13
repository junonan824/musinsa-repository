'use client';

import React from 'react';
import axios from 'axios';
import { API_BASE_URL } from '../config/api';
import CategoryPriceAnalysis from './CategoryPriceAnalysis';

interface CategoryAnalysis {
  lowestPriceByCategory: Array<{
    category: string;
    brand: string;
    price: number;
  }>;
  totalPrice: number;
}

interface BrandAnalysis {
  brand: string;
  items: Array<{
    category: string;
    price: number;
  }>;
  totalPrice: number;
  message?: string;
}

interface PriceAnalysisProps {
  refreshTrigger: number;
}

export default function PriceAnalysis({ refreshTrigger }: PriceAnalysisProps) {
  const [categoryAnalysis, setCategoryAnalysis] = React.useState<CategoryAnalysis | null>(null);
  const [brandAnalysis, setBrandAnalysis] = React.useState<BrandAnalysis | null>(null);

  React.useEffect(() => {
    fetchAnalysis();
  }, [refreshTrigger]);

  const fetchAnalysis = async () => {
    try {
      const [categoryResponse, brandResponse] = await Promise.all([
        axios.get(`${API_BASE_URL}/lowest-price-by-category`),
        axios.get(`${API_BASE_URL}/lowest-price-single-brand`)
      ]);
      setCategoryAnalysis(categoryResponse.data);
      setBrandAnalysis(brandResponse.data);
    } catch (error) {
      console.error('Failed to fetch analysis:', error);
    }
  };

  return (
    <div className="space-y-8">
      <div className="analysis-section">
        <h3 className="analysis-title">카테고리별 최저가 분석</h3>
        {categoryAnalysis && (
          <div className="table-container">
            <table className="analysis-table category-analysis-table">
              <thead>
                <tr className="musinsa-table-header">
                  <th>카테고리</th>
                  <th>브랜드</th>
                  <th>가격</th>
                </tr>
              </thead>
              <tbody>
                {categoryAnalysis.lowestPriceByCategory.map((item, index) => (
                  <tr key={index}>
                    <td>{item.category}</td>
                    <td>{item.brand}</td>
                    <td>{item.price.toLocaleString()}원</td>
                  </tr>
                ))}
                <tr className="total-row">
                  <td colSpan={2}>총액</td>
                  <td>{categoryAnalysis.totalPrice.toLocaleString()}원</td>
                </tr>
              </tbody>
            </table>
          </div>
        )}
      </div>

      <div className="analysis-section">
        <h3 className="analysis-title">단일 브랜드 최저가 분석</h3>
        {brandAnalysis && (
          brandAnalysis.message ? (
            <p className="text-red-500">{brandAnalysis.message}</p>
          ) : (
            <>
              <p className="font-semibold mb-2">브랜드: {brandAnalysis.brand}</p>
              <div className="table-container">
                <table className="analysis-table brand-analysis-table">
                  <thead>
                    <tr className="musinsa-table-header">
                      <th>카테고리</th>
                      <th>가격</th>
                    </tr>
                  </thead>
                  <tbody>
                    {brandAnalysis.items.map((item, index) => (
                      <tr key={index}>
                        <td>{item.category}</td>
                        <td>{item.price.toLocaleString()}원</td>
                      </tr>
                    ))}
                    <tr className="total-row">
                      <td>총액</td>
                      <td>{brandAnalysis.totalPrice.toLocaleString()}원</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </>
          )
        )}
      </div>

      <CategoryPriceAnalysis />
    </div>
  );
} 