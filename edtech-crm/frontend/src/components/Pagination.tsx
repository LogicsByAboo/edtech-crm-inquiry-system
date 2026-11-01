import React from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';

interface PaginationProps {
  currentPage: number;
  totalPages: number;
  totalElements: number;
  pageSize: number;
  onPageChange: (newPage: number) => void;
  disabled?: boolean;
}

export const Pagination: React.FC<PaginationProps> = ({
  currentPage,
  totalPages,
  totalElements,
  pageSize,
  onPageChange,
  disabled = false,
}) => {
  if (totalPages <= 1 && totalElements === 0) {
    return null;
  }

  const startItem = totalElements === 0 ? 0 : currentPage * pageSize + 1;
  const endItem = Math.min((currentPage + 1) * pageSize, totalElements);

  return (
    <div
      style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        padding: '1rem 0',
        flexWrap: 'wrap',
        gap: '0.75rem',
      }}
    >
      <div style={{ fontSize: '0.875rem', color: 'var(--slate-500)' }}>
        Showing <strong>{startItem}</strong> - <strong>{endItem}</strong> of{' '}
        <strong>{totalElements}</strong> inquiries
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
        <button
          className="btn btn-secondary"
          style={{ padding: '0.4rem 0.75rem', fontSize: '0.85rem' }}
          onClick={() => onPageChange(currentPage - 1)}
          disabled={disabled || currentPage <= 0}
          title="Previous Page"
        >
          <ChevronLeft size={16} />
          Previous
        </button>

        <span style={{ fontSize: '0.875rem', color: 'var(--slate-600)', padding: '0 0.5rem' }}>
          Page {totalPages === 0 ? 0 : currentPage + 1} of {totalPages}
        </span>

        <button
          className="btn btn-secondary"
          style={{ padding: '0.4rem 0.75rem', fontSize: '0.85rem' }}
          onClick={() => onPageChange(currentPage + 1)}
          disabled={disabled || currentPage >= totalPages - 1}
          title="Next Page"
        >
          Next
          <ChevronRight size={16} />
        </button>
      </div>
    </div>
  );
};
