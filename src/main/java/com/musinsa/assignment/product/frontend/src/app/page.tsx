'use client';

import React from 'react';
import ProductList from '@/components/ProductList';
import ProductForm from '@/components/ProductForm';
import PriceAnalysis from '@/components/PriceAnalysis';

export default function Home() {
  const [refreshTrigger, setRefreshTrigger] = React.useState(0);
  const [activeTab, setActiveTab] = React.useState('products'); // 'products' | 'analysis'

  const handleRefresh = () => {
    setRefreshTrigger(prev => prev + 1);
  };

  return (
    <div className="layout-container">
      <header className="header-container">
        <div className="header-black-bg">
          <div className="header-content">
            <h1 className="header-title">
              MUSINSA COORDINATOR
            </h1>
            <nav className="header-nav">
              <button
                onClick={() => setActiveTab('products')}
                className={`tab-button ${activeTab === 'products' ? 'active' : ''}`}
              >
                상품 관리
              </button>
              <button
                onClick={() => setActiveTab('analysis')}
                className={`tab-button ${activeTab === 'analysis' ? 'active' : ''}`}
              >
                가격 분석
              </button>
            </nav>
          </div>
        </div>
      </header>

      <div className="content-wrapper">
        <main className="musinsa-container py-8">
          {activeTab === 'products' ? (
            <div className="space-y-8">
              <h2 className="page-title">상품 관리</h2>
              <div>
                <h3 className="section-title">상품 등록</h3>
                <ProductForm onSuccess={handleRefresh} />
              </div>
              
              <div>
                <h3 className="section-title">상품 목록</h3>
                <ProductList refreshTrigger={refreshTrigger} onDelete={handleRefresh} />
              </div>
            </div>
          ) : (
            <div>
              <h2 className="page-title">가격 분석</h2>
              <PriceAnalysis refreshTrigger={refreshTrigger} />
            </div>
          )}
        </main>
      </div>

      <footer className="bg-black text-white">
        <div className="musinsa-container py-4 text-center text-gray-300">
          © 2024 무신사 코디 서비스
        </div>
      </footer>
    </div>
  );
}
